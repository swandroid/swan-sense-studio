package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import interdroid.swan.crossdevice.swanplus.ProximityManagerI;
import interdroid.swan.crossdevice.swanplus.SwanUser;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swan.swansong.Expression;

/**
 * Created by vladimir on 3/9/16.
 *
 * TODO remove users from the list when they go out of range
 * TODO handle properly the cases when BT is switched on/off during usage
 * TODO connect might hang forever
 */
public class BTManager implements ProximityManagerI {

    private static final String TAG = "BTManager";
    protected final static UUID SERVICE_UUID = UUID.fromString("e2035693-b335-403f-b921-537e5ce2d27d");
    protected final static String SERVICE_NAME = "swanlake";
    public static final String ACTION_NEARBY_DEVICE_FOUND = "interdroid.swan.crossdevice.swanplus.bluetooth.ACTION_NEARBY_DEVICE_FOUND";
    private final int PEER_DISCOVERY_INTERVAL = 40000;
    private final int BLOCKED_WORKERS_CHECKING_INTERVAL = 5000;

    private Context context;
    private BTReceiver btReceiver;
    private BluetoothAdapter btAdapter;
    private ConcurrentLinkedQueue<BTRemoteExpression> evalQueue;
    private boolean busy = false;
    private Handler handler;

    private List<BTClientWorker> clientWorkers = new ArrayList<>();
    private List<BTClientWorker> waitingWorkers = new ArrayList<>();
    private List<BTServerWorker> serverWorkers = new ArrayList<>();

    private List<BluetoothDevice> nearbyDevices = new ArrayList<>();
    /** IMPORTANT the order of items in this list matters! (see SwanLakePlus) */
    private Map<String, String> registeredExpressions = new HashMap<String, String>();

    /* we schedule peer discovery to take place at regular intervals */
    Runnable nearbyPeersChecker = new Runnable() {
        public void run() {
            discoverPeers();
            handler.postDelayed(nearbyPeersChecker, PEER_DISCOVERY_INTERVAL);
        }
    };

    /* we check periodically that client threads are not blocked in connect() */
    Runnable blockedWorkersChecker = new Runnable() {
        public void run() {
            for(BTClientWorker clientWorker : clientWorkers) {
                if(!clientWorker.isConnected()) {
                    if(waitingWorkers.contains(clientWorker)) {
                        Log.e(TAG, "blocked worker found, interrupting...");
                        clientWorker.interrupt();
                    } else {
                        waitingWorkers.add(clientWorker);
                    }
                } else {
                    waitingWorkers.remove(clientWorker);
                }
            }

            handler.postDelayed(blockedWorkersChecker, BLOCKED_WORKERS_CHECKING_INTERVAL);
        }
    };

    private final Thread evalThread = new Thread() {
        @Override
        public void run() {
            try {
                while(true) {
                    while(evalQueue.isEmpty()) {
                        synchronized (this) {
                            wait();
                        }
                    }

                    BTRemoteExpression expression = removeFromQueue();
                    processExpression(expression);
                    sleep(1000);

                    while(isBusy()) {
                        synchronized (this) {
                            wait();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getName() != null && device.getName().toLowerCase().contains("swan")) {
                    Log.d(TAG, "found nearby device " + device.getName());
                    addNearbyDevice(device);

                    // code below is used by SwanLakePlus
                    Intent deviceFoundIntent = new Intent();
                    deviceFoundIntent.setAction(ACTION_NEARBY_DEVICE_FOUND);
                    context.sendBroadcast(deviceFoundIntent);
                }
            } else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                if(connState == BluetoothAdapter.STATE_ON && btReceiver == null) {
                    Log.d(TAG, "bluetooth connected, starting receiver thread...");
                    btReceiver = new BTReceiver(BTManager.this, context);
                    btReceiver.execute();
                }
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "discovery finished");

                synchronized (evalThread) {
                    evalThread.notify();
                }
            }
        }
    };

    public BTManager(Context context) {
        this.context = context;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Log.w(TAG, "Bluetooth not supported");
            return;
        }

        evalQueue = new ConcurrentLinkedQueue<BTRemoteExpression>();
        handler = new Handler();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    // TODO this is not quite OK
    public void registerService() {
        String userFriendlyName = PreferenceManager.getDefaultSharedPreferences(context).getString("name", null);

        if (userFriendlyName == null) {
            Log.e(TAG, "Name not set for device");
            return;
        }

        btAdapter.setName(userFriendlyName);
    }

    public void init() {
        registerService();
        initDiscovery();
        btAdapter.startDiscovery();
        evalThread.start();

        if(btAdapter.isEnabled()) {
            btReceiver = new BTReceiver(this, context);
            btReceiver.execute();
        }

        nearbyPeersChecker.run();
        blockedWorkersChecker.run();
    }

    public void clean() {
        disconnect();
        context.unregisterReceiver(mReceiver);
    }

    public void discoverPeers() {
        if(!btAdapter.isDiscovering()) {
            Log.d(TAG, "Discovering...");
            btAdapter.startDiscovery();
        } else {
            Log.d(TAG, "Discovery already started");
        }
    }

    public void initDiscovery() {
        if(!btAdapter.getName().toLowerCase().contains("swan")) {
            btAdapter.setName(btAdapter.getName() + "swan");
        }

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d(TAG, "device already paired: " + device.getName());
            }
        }

        if(btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(discoverableIntent);
        }
    }

    public void disconnect() {
        //TODO implement me
    }

    public void registerExpression(String id, String expression, String resolvedLocation, String action) {
        boolean addedExpr = false;

        if(resolvedLocation.equals(Expression.LOCATION_NEARBY)) {
            if (action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                registeredExpressions.put(id, expression);
            } else if (action.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                registeredExpressions.remove(id);
            } else {
                Log.e(TAG, "not a valid action");
            }

            for (BluetoothDevice device : nearbyDevices) {
                BTRemoteExpression remoteExpr = new BTRemoteExpression(id, device, expression, action);
                addToQueue(remoteExpr);
                addedExpr = true;
            }
        } else {
            BluetoothDevice device = getNearbyDeviceByName(resolvedLocation);

            if(device != null) {
                BTRemoteExpression remoteExpr = new BTRemoteExpression(id, device, expression, action);
                addToQueue(remoteExpr);
                addedExpr = true;
            } else {
                Log.e(TAG, "can't find device " + resolvedLocation + "; won't register expression");
            }
        }

        if(addedExpr) {
            synchronized (evalThread) {
                evalThread.notify();
            }
        }
    }

    private void processExpression(BTRemoteExpression remoteExpr) {
        Log.d(TAG, "processing " + remoteExpr);
        setBusy(true);

        if(remoteExpr.getAction().equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
            BTClientWorker clientWorker = new BTClientWorker(this, remoteExpr);
            clientWorkers.add(clientWorker);
            clientWorker.start();
        } else {
            send(remoteExpr.getRemoteDevice().getName(), remoteExpr.getId(),
                    remoteExpr.getAction(), remoteExpr.getExpression());
        }
    }

    /** send expression to the evaluation engine
     * TODO consider moving this to Worker base class
     * */
    protected void sendExprForEvaluation(String exprId, String exprAction, String exprSource, String exprData) {
        Intent intent = new Intent(exprAction);
        intent.setClass(context, EvaluationEngineService.class);
        intent.putExtra("id", exprId);
        intent.putExtra("source", exprSource);
        intent.putExtra("data", exprData);
        context.startService(intent);
    }

    // blocking call; use only in a separate thread
    public BluetoothSocket connect(BluetoothDevice device) {
        Log.i(TAG, "connecting to " + device.getName() + "...");
        BluetoothSocket btSocket = null;
        btAdapter.cancelDiscovery();

        try {
            btSocket = device.createInsecureRfcommSocketToServiceRecord(SERVICE_UUID);
            btSocket.connect();
            Log.i(TAG, "connected to " + device.getName());
        } catch (IOException e) {
            Log.e(TAG, "can't connect to " + device.getName() + ": " + e.getMessage());

            try {
                btSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        }

        return btSocket;
    }

    public void send(final String remoteDeviceName, final String exprId, final String exprAction, final String exprData) {
        try {
            if(exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                BTServerWorker serverWorker = getServerWorker(remoteDeviceName);

                if(serverWorker != null) {
                    serverWorker.send(exprId, exprAction, exprData);
                    return;
                }
            } else {
                BTClientWorker clientWorker = getClientWorker(remoteDeviceName);

                if(clientWorker != null) {
                    clientWorker.send(exprId, exprAction, exprData);
                    return;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "couldn't send " + exprAction + " to " + remoteDeviceName + "(id: " + exprId + ")", e);
            return;
        }

        Log.e(TAG, "couldn't send " + exprAction + " to " + remoteDeviceName + "(id: " + exprId + "): expression already processed");
    }

    private void addToQueue(BTRemoteExpression remoteExpr) {
        evalQueue.add(remoteExpr);
        Log.d(TAG, "remote expr added to queue: " + remoteExpr);
        Log.d(TAG, "[Queue] " + evalQueue);
    }

    private BTRemoteExpression removeFromQueue() {
        BTRemoteExpression remoteExpr = evalQueue.remove();
        Log.d(TAG, "remote expr removed from queue: " + remoteExpr);
        Log.d(TAG, "[Queue] " + evalQueue);
        return remoteExpr;
    }

    protected BluetoothDevice getNearbyDeviceByName(String deviceName) {
        for(BluetoothDevice device : nearbyDevices) {
            if(device.getName().equals(deviceName)) {
                return device;
            }
        }
        return null;
    }

    protected void addNearbyDevice(BluetoothDevice device) {
        if(!hasPeer(device.getName())) {
            nearbyDevices.add(device);
            registerRemoteDevice(device);
            Log.d(TAG, "added new device " + device.getName());
        } else {
            Log.d(TAG, "device " + device.getName() + " already present, won't add");
        }
    }

    private void registerRemoteDevice(BluetoothDevice device) {
        for(Map.Entry<String, String> entry : registeredExpressions.entrySet()) {
            BTRemoteExpression remoteExpr = new BTRemoteExpression(entry.getKey(), device,
                    entry.getValue(), EvaluationEngineService.ACTION_REGISTER_REMOTE);
            addToQueue(remoteExpr);
        }
    }

    @Override
    public int getPeerCount() {
        return nearbyDevices.size();
    }

    @Override
    public SwanUser getPeerAt(int position) {
        return new SwanUser(nearbyDevices.get(position).getName(), nearbyDevices.get(position).getName());
    }

    @Override
    public boolean hasPeer(String deviceName) {
        for(BluetoothDevice device : nearbyDevices) {
            if(device.getName().equals(deviceName)) {
                return true;
            }
        }
        return false;
    }

    public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;

        if(busy) {
            Log.e(TAG, "IS BUSY");
        } else {
            Log.e(TAG, "IS NOT BUSY");
        }
    }

    /**
     * we have to synchronize the methods dealing with workers, as it may happen that send() is called
     * by EvaluationEngineService right before a worker is removed
     */
    protected synchronized void workerDone(Thread worker) {
        if(worker instanceof BTClientWorker) {
            BTClientWorker clientWorker = (BTClientWorker) worker;
            BTRemoteExpression remoteExpression = clientWorker.getRemoteExpression();

            if(remoteExpression.getAction().equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                Log.d(TAG, "client worker finished processing expression " + remoteExpression.getId());

                // add expression back at the end of queue
                remoteExpression.renewId();
                addToQueue(remoteExpression);
                clientWorkers.remove(clientWorker);
                setBusy(false);

                synchronized (evalThread) {
                    evalThread.notify();
                }
            }
        } else if(worker instanceof BTServerWorker) {
            Log.d(TAG, "server worker done");
            BTServerWorker serverWorker = (BTServerWorker) worker;
            serverWorkers.remove(serverWorker);
        }
    }

    protected synchronized BTClientWorker getClientWorker(String deviceName) {
        for (BTClientWorker clientWorker : clientWorkers) {
            if (deviceName.equals(clientWorker.getRemoteDeviceName())) {
                return clientWorker;
            }
        }
        return null;
    }

    protected synchronized BTServerWorker getServerWorker(String deviceName) {
        for (BTServerWorker serverWorker : serverWorkers) {
            if (deviceName.equals(serverWorker.getRemoteDeviceName())) {
                return serverWorker;
            }
        }
        return null;
    }

    protected void addServerWorker(BTServerWorker serverWorker) {
        serverWorkers.add(serverWorker);
    }

    public Context getContext() {
        return context;
    }
}

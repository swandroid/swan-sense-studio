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
import java.util.Random;
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
 * TODO stop client workers that are blocked waiting for results
 * TODO try and test with devices close to each other and far from each other (see if there are interference issues)
 */
public class BTManager implements ProximityManagerI {

    private static final String TAG = "BTManager";
//    protected final static UUID SERVICE_UUID = UUID.fromString("e2035693-b335-403f-b921-537e5ce2d27d");
    protected final static UUID[] SERVICE_UUIDS = {
            UUID.fromString("e2035693-b335-403f-b921-537e5ce2d27d"),
//            UUID.fromString("b0ec7a42-2e19-438e-a091-d6954b999225"),
//            UUID.fromString("fc74ed56-8cf2-4eae-b609-7a32bd70183d"),
//            UUID.fromString("15e416a2-0fab-45ff-b799-b6e67b131d3a"),
//            UUID.fromString("c84a39b5-8a25-43ac-9f2d-e5f3e96f615a"),
//            UUID.fromString("803581d4-55d5-45fc-a31b-acf55052a5d7"),
//            UUID.fromString("94192877-54e0-43a3-abc5-fbf9bbc43137")
    };
    protected final static String SERVICE_NAME = "swanlake";
    public static final String ACTION_NEARBY_DEVICE_FOUND = "interdroid.swan.crossdevice.swanplus.bluetooth.ACTION_NEARBY_DEVICE_FOUND";
    public static final String ACTION_LOG_MESSAGE = "interdroid.swan.crossdevice.swanplus.bluetooth.ACTION_LOG_MESSAGE";
    private final int BLOCKED_WORKERS_CHECKING_INTERVAL = 5000;
    private final int MIN_BETWEEN_CONNECTIONS_TIMEOUT = 1000;

    private Context context;
//    private BTReceiver btReceiver;
    private List<BTReceiver> btReceivers = new ArrayList<>();
    private BluetoothAdapter btAdapter;
    private ConcurrentLinkedQueue<BTRemoteExpression> evalQueue;
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
//            handler.postDelayed(nearbyPeersChecker, PEER_DISCOVERY_INTERVAL);
        }
    };

    /* we check periodically that client threads are not blocked in connect() */
    Runnable blockedWorkersChecker = new Runnable() {
        public void run() {
            List<BTClientWorker> blockedWorkers = new ArrayList<>();

            for(BTClientWorker clientWorker : clientWorkers) {
                if(!clientWorker.isConnected()) {
                    if(waitingWorkers.contains(clientWorker)) {
                        Log.e(TAG, "blocked worker " + clientWorker + " found, interrupting...");
                        blockedWorkers.add(clientWorker);
                        waitingWorkers.remove(clientWorker);
                    } else {
                        waitingWorkers.add(clientWorker);
                    }
                } else {
                    waitingWorkers.remove(clientWorker);
                }
            }

            for(BTClientWorker clientWorker : blockedWorkers) {
                clientWorker.abort();
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

                    while(!clientWorkers.isEmpty()) {
                        synchronized (this) {
                            wait();
                        }
                    }

                    sleep(MIN_BETWEEN_CONNECTIONS_TIMEOUT + new Random().nextInt(1500));
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

                if(device.getName() != null && device.getName().contains("SWAN")) {
                    Log.d(TAG, "found nearby device " + device.getName());
                    addNearbyDevice(device);

                    // code below is used by SwanLakePlus
                    Intent deviceFoundIntent = new Intent();
                    deviceFoundIntent.setAction(ACTION_NEARBY_DEVICE_FOUND);
                    context.sendBroadcast(deviceFoundIntent);
                }
            } else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                if(connState == BluetoothAdapter.STATE_ON) {
                    Log.d(TAG, "bluetooth connected, starting receiver thread...");
//                    btReceiver.start();
                    for(BTReceiver receiver : btReceivers) {
                        receiver.start();
                    }
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
//        btReceiver = new BTReceiver(this, context, SERVICE_UUID);

        for(UUID uuid : SERVICE_UUIDS) {
            btReceivers.add(new BTReceiver(this, context, uuid));
        }

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
//            btReceiver.start();
            for(BTReceiver receiver : btReceivers) {
                receiver.start();
            }
        }

        nearbyPeersChecker.run();
//        blockedWorkersChecker.run();
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
//        if(!btAdapter.getName().toLowerCase().contains("swan")) {
//            btAdapter.setName(btAdapter.getName() + "swan");
//        }

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

        // if discovery is started, then the evaluation thread is notified when discovery is done
        if(addedExpr && !btAdapter.isDiscovering()) {
            synchronized (evalThread) {
                evalThread.notify();
            }
        }
    }

        private void processExpression(BTRemoteExpression remoteExpr) {
        Log.d(TAG, "processing " + remoteExpr);

        if(remoteExpr.getAction().equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
            BTClientWorker clientWorker = new BTClientWorker(this, remoteExpr);
            clientWorkers.add(clientWorker);
            clientWorker.start();
        } else {
            send(remoteExpr.getRemoteDevice().getName(), remoteExpr.getId(),
                    remoteExpr.getAction(), remoteExpr.getExpression());
        }
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

            // unregister expression
            if(exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                // TODO close socket here, so the other side unblocks
                Log.e(TAG, "unregistering expression " + exprId + "...");
                sendExprForEvaluation(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, remoteDeviceName, exprData);
            }

            return;
        }

        Log.e(TAG, "couldn't send " + exprAction + " to " + remoteDeviceName + "(id: " + exprId + "): expression already processed");
    }

    /**
     * send expression to the evaluation engine
     */
    protected void sendExprForEvaluation(String exprId, String exprAction, String exprSource, String exprData) {
        Intent intent = new Intent(exprAction);
        intent.setClass(getContext(), EvaluationEngineService.class);
        intent.putExtra("id", exprId);
        intent.putExtra("source", exprSource);
        intent.putExtra("data", exprData);
        getContext().startService(intent);
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
            Log.d(TAG, "added new device " + device.getName());
            bcastLogMessage("nearby device found " + device.getName());
            nearbyDevices.add(device);
            registerRemoteDevice(device);
        } else {
            Log.d(TAG, "device " + device.getName() + " already present, won't add");
        }
    }

    private void registerRemoteDevice(BluetoothDevice device) {
        boolean addedExpr = false;

        for(Map.Entry<String, String> entry : registeredExpressions.entrySet()) {
            BTRemoteExpression remoteExpr = new BTRemoteExpression(entry.getKey(), device,
                    entry.getValue(), EvaluationEngineService.ACTION_REGISTER_REMOTE);
            addToQueue(remoteExpr);
            addedExpr = true;
        }

        // if discovery is started, then the evaluation thread is notified when discovery is done
        if(addedExpr && !btAdapter.isDiscovering()) {
            synchronized (evalThread) {
                evalThread.notify();
            }
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

    /**
     * we have to synchronize the methods dealing with workers, as it may happen that send() is called
     * by EvaluationEngineService right before a worker is removed
     */
    protected synchronized void workerDone(Thread worker) {
        if(worker instanceof BTClientWorker) {
            BTClientWorker clientWorker = (BTClientWorker) worker;
            BTRemoteExpression remoteExpression = clientWorker.getRemoteExpression();

            if(remoteExpression.getAction().equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                Log.d(TAG, "client worker " + worker + "finished processing");

                // add expression back at the end of queue
                addToQueue(new BTRemoteExpression(remoteExpression));
                clientWorkers.remove(clientWorker);

                synchronized (evalThread) {
                    evalThread.notify();
                }
            }
        } else if(worker instanceof BTServerWorker) {
            Log.d(TAG, "server worker done " + worker);
            BTServerWorker serverWorker = (BTServerWorker) worker;
            serverWorkers.remove(serverWorker);

//            synchronized (btReceiver) {
//                btReceiver.notify();
//            }

            for(BTReceiver receiver : btReceivers) {
                if(receiver.getSocket() != null && receiver.getSocket().equals(serverWorker.getBtSocket())) {
                    synchronized (receiver) {
                        receiver.notify();
                    }
                    break;
                }
            }
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

    /**
     * temporary method used for debug
     */
    protected void bcastLogMessage(String message) {
        Intent logMsgIntent = new Intent();
        logMsgIntent.setAction(ACTION_LOG_MESSAGE);
        logMsgIntent.putExtra("log", message);
        context.sendBroadcast(logMsgIntent);
    }

    public Context getContext() {
        return context;
    }

}

package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import interdroid.swan.crossdevice.ProximityManagerI;
import interdroid.swan.crossdevice.wifidirect.WDSwanDevice;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swancore.swansong.Expression;

/**
 * Created by vladimir on 3/9/16.
 * <p/>
 * TODO remove users from the list when they go out of range
 * TODO handle properly the cases when BT is switched on/off during usage
 * TODO stop client workers that are blocked waiting for results
 * TODO send just one valid result in BTServerWorker
 */
public class BTManager implements ProximityManagerI {

    private static final String TAG = "BTManager";
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
    public static final String ACTION_NEARBY_DEVICE_FOUND = "interdroid.swan.crossdevice.bluetooth.ACTION_NEARBY_DEVICE_FOUND";
    public static final String ACTION_LOG_MESSAGE = "interdroid.swan.crossdevice.bluetooth.ACTION_LOG_MESSAGE";
    /* this should be configurable */
    public static final int TIME_BETWEEN_REQUESTS = 4000;
    private final int BLOCKED_WORKERS_CHECKING_INTERVAL = 5000;
    private final int PEER_DISCOVERY_INTERVAL = 60000;

    private Context context;
    private List<BTReceiver> btReceivers = new ArrayList<>();
    private BluetoothAdapter btAdapter;
    private ConcurrentLinkedQueue<Object> evalQueue;
    private Handler handler;
    private boolean discovering = false;

    private List<BTClientWorker> clientWorkers = new ArrayList<>();
    private List<BTServerWorker> serverWorkers = new ArrayList<>();
    private List<BTClientWorker> waitingWorkers = new ArrayList<>();
    private List<BTSwanDevice> nearbyDevices = new ArrayList<>();
    /**
     * IMPORTANT the order of items in this list matters! (see SwanLakePlus)
     */
    private Map<String, String> registeredExpressions = new HashMap<String, String>();

    /* we schedule peer discovery to take place at regular intervals */
    Runnable nearbyPeersChecker = new Runnable() {
        public void run() {
            Log.i(TAG, "discovery started");
            bcastLogMessage("discovery started");

            discoverPeers();
            setDiscovering(true);
        }

        @Override
        public String toString() {
            return "DiscoveryEvent";
        }
    };

    /* we check periodically that client threads are not blocked in connect() */
    @Deprecated
    Runnable blockedWorkersChecker = new Runnable() {
        public void run() {
            List<BTClientWorker> blockedWorkers = new ArrayList<>();

            for (BTClientWorker clientWorker : clientWorkers) {
                if (!clientWorker.isConnected()) {
                    if (waitingWorkers.contains(clientWorker)) {
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

            for (BTClientWorker clientWorker : blockedWorkers) {
                clientWorker.abort();
            }

            handler.postDelayed(blockedWorkersChecker, BLOCKED_WORKERS_CHECKING_INTERVAL);
        }
    };

    private final Thread evalThread = new Thread() {
        @Override
        public void run() {
            try {
                while (true) {
                    while (evalQueue.isEmpty()) {
                        synchronized (this) {
                            wait();
                        }
                    }

                    Object item = removeFromQueue();
                    processQueueItem(item);

                    while (!clientWorkers.isEmpty() || isDiscovering()) {
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

    private void scheduleQueueItem(final Object item, int timeout) {
        Log.d(TAG, "scheduled item " + item + " in " + timeout + "ms");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addToQueue(item);
                synchronized (evalThread) {
                    evalThread.notify();
                }
            }
        }, timeout);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getName() != null && device.getName().contains("SWAN")) {
                    Log.d(TAG, "found nearby device " + device.getName());
                    addNearbyDevice(new BTSwanDevice(device));

                    // code below is used by SwanLakePlus
                    Intent deviceFoundIntent = new Intent();
                    deviceFoundIntent.setAction(ACTION_NEARBY_DEVICE_FOUND);
                    context.sendBroadcast(deviceFoundIntent);
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                if (connState == BluetoothAdapter.STATE_ON) {
                    Log.d(TAG, "bluetooth connected, starting receiver thread...");
                    for (BTReceiver receiver : btReceivers) {
                        receiver.start();
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "discovery finished");
                bcastLogMessage("discovery finished");
                scheduleQueueItem(nearbyPeersChecker, PEER_DISCOVERY_INTERVAL);
                setDiscovering(false);

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

        evalQueue = new ConcurrentLinkedQueue<Object>();
        handler = new Handler();

        for (UUID uuid : SERVICE_UUIDS) {
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

        if (btAdapter.isEnabled()) {
            for (BTReceiver receiver : btReceivers) {
                receiver.start();
            }
        }

        addToQueue(nearbyPeersChecker);
        synchronized (evalThread) {
            evalThread.notify();
        }

//        blockedWorkersChecker.run();
    }

    public void clean() {
        disconnect();
        context.unregisterReceiver(mReceiver);
    }

    public void discoverPeers() {
        if (!btAdapter.isDiscovering()) {
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

        if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(discoverableIntent);
        }
    }

    public void disconnect() {
        //TODO implement me
    }

    // we have to synchronize this to make sure that it is not exectued while a client worker is removed in
    // clientWorkerDone(), which would prevent the addition of a new task in the queue
    public synchronized void registerExpression(String id, String expression, String resolvedLocation) {
        Log.d(TAG, "registering expression " + id + ": " + expression);
        boolean newTask = false;

        if (resolvedLocation.equals(Expression.LOCATION_NEARBY)) {
            registeredExpressions.put(id, expression);

            for (BTSwanDevice swanDevice : nearbyDevices) {
                swanDevice.registerExpression(id, expression);
                // if this is the first registered expression, then start a new task
                if(swanDevice.getRegisteredExpressions().size() == 1) {
                    BTRemoteEvaluationTask evalTask = new BTRemoteEvaluationTask(swanDevice);
                    addToQueue(evalTask);
                    newTask = true;
                }
            }
        } else {
            BTSwanDevice swanDevice = getNearbyDeviceByName(resolvedLocation);

            if (swanDevice != null) {
                swanDevice.registerExpression(id, expression);
                // if this is the first registered expression, then start a new task
                if(swanDevice.getRegisteredExpressions().size() == 1) {
                    BTRemoteEvaluationTask evalTask = new BTRemoteEvaluationTask(swanDevice);
                    addToQueue(evalTask);
                    newTask = true;
                }
            } else {
                Log.e(TAG, "can't find device " + resolvedLocation + "; won't register expression");
            }
        }

        if (newTask) {
            synchronized (evalThread) {
                evalThread.notify();
            }
        }
    }

    @Override
    public synchronized void unregisterExpression(String id, String expression, String resolvedLocation) {
        Log.d(TAG, "unregistering expression " + id + ": " + expression);
        registeredExpressions.remove(id);

        for(BTSwanDevice swanDevice : nearbyDevices) {
            swanDevice.unregisterExpression(id);
        }

        // terminate workers assigned to expression
        for(BTClientWorker clientWorker : clientWorkers) {
            BTRemoteExpression remoteExpression = clientWorker.getRemoteEvaluationTask().getExpressionWithBaseId(id);

            if(remoteExpression != null) {
                try {
                    clientWorker.send(remoteExpression.getId(), EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
                } catch (Exception e) {
                    Log.e(TAG, "couldn't unregister remote expression with id " + remoteExpression.getId(), e);
                }
            }
        }
    }

    private void processQueueItem(Object item) {
        Log.d(TAG, "processing " + item);

        if(item instanceof BTRemoteEvaluationTask) {
            BTRemoteEvaluationTask remoteEvalTask = (BTRemoteEvaluationTask) item;
            BTClientWorker clientWorker = new BTClientWorker(this, remoteEvalTask);

            clientWorkers.add(clientWorker);
            clientWorker.start();
        } else if(item instanceof Runnable) {
            // peer discovery
            ((Runnable) item).run();
        } else {
            Log.e(TAG, "Item can't be processed: " + item);
        }
    }

    // we synchronize this to make sure that it is not called while clientWorkerDone or serverWorkerDone are called
    public synchronized  void send(final String remoteDeviceName, final String exprId, final String exprAction, final String exprData) {
        try {
            if (exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                BTServerWorker serverWorker = getServerWorker(remoteDeviceName);

                if (serverWorker != null) {
                    serverWorker.send(exprId, exprAction, exprData);
                    return;
                }
            } else {
                BTClientWorker clientWorker = getClientWorker(remoteDeviceName);

                if (clientWorker != null) {
                    clientWorker.send(exprId, exprAction, exprData);
                    return;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "couldn't send " + exprAction + " to " + remoteDeviceName + "(id: " + exprId + "): " + e.getMessage());

            // unregister expression
            if (exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
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

    private void addToQueue(Object item) {
        evalQueue.add(item);
        Log.d(TAG, "item added to queue: " + item);
        Log.d(TAG, "[Queue] " + evalQueue);
    }

    private Object removeFromQueue() {
        Object item = evalQueue.remove();
        Log.d(TAG, "first item removed from queue: " + item);
        Log.d(TAG, "[Queue] " + evalQueue);
        return item;
    }

    protected BTSwanDevice getNearbyDeviceByName(String deviceName) {
        for (BTSwanDevice device : nearbyDevices) {
            if (device.getName().equals(deviceName)) {
                return device;
            }
        }
        return null;
    }

    protected BTSwanDevice addNearbyDevice(BTSwanDevice device) {
        if (!hasPeer(device.getName())) {
            Log.d(TAG, "added new device " + device.getName());
            bcastLogMessage("nearby device found " + device.getName());
            nearbyDevices.add(device);
            registerRemoteDevice(device);
        } else {
            Log.d(TAG, "device " + device.getName() + " already present, won't add");
        }
        return getNearbyDeviceByName(device.getName());
    }

    private void registerRemoteDevice(BTSwanDevice swanDevice) {
        if (registeredExpressions.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : registeredExpressions.entrySet()) {
            swanDevice.registerExpression(entry.getKey(), entry.getValue());
        }

        BTRemoteEvaluationTask evalTask = new BTRemoteEvaluationTask(swanDevice);
        addToQueue(evalTask);

        synchronized (evalThread) {
            evalThread.notify();
        }
    }

    @Override
    public int getPeerCount() {
        return nearbyDevices.size();
    }

    /** temporary fix */
    @Override
    public WDSwanDevice getPeerAt(int position) {
        return new WDSwanDevice(nearbyDevices.get(position).getName(), nearbyDevices.get(position).getName());
    }

    @Override
    public boolean hasPeer(String deviceName) {
        for (BTSwanDevice device : nearbyDevices) {
            if (device.getName().equals(deviceName)) {
                return true;
            }
        }
        return false;
    }

    private void scheduleEvaluationTask(BTRemoteEvaluationTask remoteEvalTask, int remoteTimeToNextReq) {
        BTSwanDevice swanDevice = remoteEvalTask.getSwanDevice();
        int timeToNextReq = TIME_BETWEEN_REQUESTS;
        Log.d(TAG, "timeToNextReq = " + timeToNextReq + "; " + "remoteTimeToNextReq = " + remoteTimeToNextReq);

        // remoteTimeToNextReq is 0 if an error occurs or if the remote device doesn't want
        // to register any expression on this device
        if (remoteTimeToNextReq > 0) {
            if (timeToNextReq == remoteTimeToNextReq) {
                if (getMacAddress().compareTo(swanDevice.getBtDevice().getAddress()) > 0) {
                    timeToNextReq++;
                } else {
                    remoteTimeToNextReq++;
                }
            }

            if (timeToNextReq > remoteTimeToNextReq) {
                swanDevice.setPendingItem(new BTPendingItem(remoteEvalTask, timeToNextReq - remoteTimeToNextReq));
                Log.d(TAG, "set pending item " + swanDevice.getPendingItem() + " for device " + swanDevice);
            } else {
                scheduleQueueItem(remoteEvalTask, timeToNextReq);
            }
        } else {
            scheduleQueueItem(remoteEvalTask, timeToNextReq);
        }
    }

    /**
     * we have to synchronize the methods dealing with workers, as it may happen that send() is called
     * by EvaluationEngineService right before a worker is removed
     */
    protected synchronized void clientWorkerDone(BTClientWorker clientWorker) {
        clientWorkerDone(clientWorker, 0);
    }

    protected synchronized void clientWorkerDone(BTClientWorker clientWorker, int remoteTimeToNextReq) {
        BTSwanDevice swanDevice = clientWorker.getSwanDevice();
        Log.d(TAG, "client worker done " + clientWorker);

        // reschedule a new task if there are registered expressions on the device
        if(!swanDevice.getRegisteredExpressions().isEmpty()) {
            BTRemoteEvaluationTask remoteEvalTask = new BTRemoteEvaluationTask(swanDevice);
            scheduleEvaluationTask(remoteEvalTask, remoteTimeToNextReq);
        }

        clientWorkers.remove(clientWorker);
        synchronized (evalThread) {
            evalThread.notify();
        }
    }

    /**
     * we have to synchronize the methods dealing with workers, as it may happen that send() is called
     * by EvaluationEngineService right before a worker is removed
     */
    protected synchronized void serverWorkerDone(BTServerWorker serverWorker) {
        Log.d(TAG, "server worker done " + serverWorker);
        BTSwanDevice swanDevice = serverWorker.getSwanDevice();
        serverWorkers.remove(serverWorker);

        if(swanDevice.getPendingItem() != null) {
            final BTPendingItem pendingItem = swanDevice.getPendingItem();
            scheduleQueueItem(pendingItem.getItem(), pendingItem.getTimeout());
            swanDevice.setPendingItem(null);
        }

        for (BTReceiver receiver : btReceivers) {
            if (receiver.getSocket() != null && receiver.getSocket().equals(serverWorker.getBtSocket())) {
                synchronized (receiver) {
                    receiver.notify();
                }
                break;
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

    public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }

    private String getMacAddress() {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
    }

    public Context getContext() {
        return context;
    }

    public boolean isDiscovering() {
        return discovering;
    }

    public void setDiscovering(boolean discovering) {
        this.discovering = discovering;
    }
}

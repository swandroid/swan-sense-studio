package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * TODO for tristate expressions sometimes there is no result
 * TODO create Logger class
 */
public class BTManager implements ProximityManagerI {

    private static final String TAG = "BTManager";
    protected final static UUID[] SERVICE_UUIDS = {
            UUID.fromString("e2035693-b335-403f-b921-537e5ce2d27d"),
            UUID.fromString("b0ec7a42-2e19-438e-a091-d6954b999225"),
            UUID.fromString("fc74ed56-8cf2-4eae-b609-7a32bd70183d"),
            UUID.fromString("15e416a2-0fab-45ff-b799-b6e67b131d3a"),
//            UUID.fromString("c84a39b5-8a25-43ac-9f2d-e5f3e96f615a"),
//            UUID.fromString("803581d4-55d5-45fc-a31b-acf55052a5d7"),
//            UUID.fromString("94192877-54e0-43a3-abc5-fbf9bbc43137")
    };
    protected final static String SERVICE_NAME = "swanlake";
    public static final String ACTION_NEARBY_DEVICE_FOUND = "interdroid.swan.crossdevice.bluetooth.ACTION_NEARBY_DEVICE_FOUND";
    public static final String ACTION_LOG_MESSAGE = "interdroid.swan.crossdevice.bluetooth.ACTION_LOG_MESSAGE";
    /* this should be configurable */
    public static final int TIME_BETWEEN_REQUESTS = 4000;
    /* set this to true if you want one connection per device, or false if you want one connection per worker */
    public static final boolean SHARED_CONNECTIONS = true;
    /* set this to true if you want only one server worker at a time, or false if you want multiple server workers in parallel */
    public static final boolean SYNC_RECEIVERS = true;
    /* set this to true if you want to prevent 2 devices connecting to each other at the same time */
    public static final boolean SYNC_DEVICES = false;
    /* set SYNC_RECEIVERS to false if you set this to true */
    public static final boolean USE_WIFI = false;
    private final int BLOCKED_WORKERS_CHECKING_INTERVAL = 10000;
    private final int PEER_DISCOVERY_INTERVAL = 60000;
    private final int MAX_CONNECTIONS = 0;
    private final boolean LOG_ONLY_CRITICAL = false;

    protected BluetoothAdapter btAdapter;
    protected ConcurrentLinkedQueue<Object> evalQueue;
    protected Handler handler;
    protected Context context;

    private WifiReceiver wifiReceiver;
    private List<BTReceiver> btReceivers = new ArrayList<>();
    private boolean discovering = false;
    private boolean restarting = false;
    private long startTime = System.currentTimeMillis();

    private List<BTClientWorker> clientWorkers = new ArrayList<>();
    private List<BTServerWorker> serverWorkers = new ArrayList<>();
    private List<BTWorker> waitingWorkers = new ArrayList<>();
    private List<BTSwanDevice> nearbyDevices = new ArrayList<>();
    private List<BTLogRecord> logRecords = new ArrayList<>();
    /**
     * IMPORTANT the order of items in this list matters! (see SwanLakePlus)
     */
    private Map<String, String> registeredExpressions = new HashMap<String, String>();

    /* we schedule peer discovery to take place at regular intervals */
    Runnable nearbyPeersChecker = new Runnable() {
        public void run() {
            log(TAG, "discovery started", Log.INFO, true);
            bcastLogMessage("discovery started");

            discoverPeers();
            setDiscovering(true);
        }

        @Override
        public String toString() {
            return "DiscoveryEvent";
        }
    };

    Runnable bluetoothRestart = new Runnable() {
        public void run() {
            log(TAG, "stopping bluetooth...", Log.INFO, true);
            bcastLogMessage("stopping bluetooth...");

            if(btAdapter.isEnabled()) {
                btAdapter.disable();
            } else {
                btAdapter.enable();
            }

            setRestarting(true);
        }

        @Override
        public String toString() {
            return "BluetoothRestartEvent";
        }
    };

    /* we check periodically that client threads are not blocked in connect() */
    Runnable blockedWorkersChecker = new Runnable() {
        public void run() {
            List<BTWorker> blockedWorkers = new ArrayList<>();
            List<BTWorker> allWorkers = new ArrayList<>();
            allWorkers.addAll(clientWorkers);
            allWorkers.addAll(serverWorkers);

            for (BTWorker worker : allWorkers) {
                if (waitingWorkers.contains(worker)) {
                    log(TAG, "blocked worker " + worker + " found, interrupting...", Log.ERROR, true);
                    blockedWorkers.add(worker);
                    waitingWorkers.remove(worker);
                } else {
                    waitingWorkers.add(worker);
                }
            }

            for (BTWorker worker : blockedWorkers) {
                worker.disconnectFromRemote();
            }

            handler.postDelayed(blockedWorkersChecker, BLOCKED_WORKERS_CHECKING_INTERVAL);
        }
    };

    protected final Thread evalThread = new Thread() {
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

                    while (!clientWorkers.isEmpty() || isDiscovering() || isRestarting()) {
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
        log(TAG, "scheduled item " + item + " in " + timeout + "ms", Log.DEBUG);

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

    //TODO move this to a separate file
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getName() != null && device.getName().contains("SWAN")) {
                    log(TAG, "found nearby device " + device.getName(), Log.DEBUG);
                    addNearbyDevice(device, null);

                    // code below is used by SwanLakePlus
                    Intent deviceFoundIntent = new Intent();
                    deviceFoundIntent.setAction(ACTION_NEARBY_DEVICE_FOUND);
                    context.sendBroadcast(deviceFoundIntent);
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                if (connState == BluetoothAdapter.STATE_ON) {
                    log(TAG, "bluetooth connected, starting receiver thread...", Log.DEBUG);
                    initDiscovery();
                    startReceivers();

                    if(isRestarting()) {
                        setRestarting(false);
                        synchronized (evalThread) {
                            evalThread.notify();
                        }
                    }
                }
                if(connState == BluetoothAdapter.STATE_OFF) {
                    log(TAG, "bluetooth connected, stopping receiver thread...", Log.DEBUG);
                    stopReceivers();

                    if(isRestarting()) {
                        btAdapter.enable();
                        log(TAG, "starting bluetooth...", Log.INFO, true);
                        bcastLogMessage("starting bluetooth...");
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                log(TAG, "discovery finished", Log.INFO, true);
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
            log(TAG, "Bluetooth not supported", Log.ERROR, true);
            return;
        }

        evalQueue = new ConcurrentLinkedQueue<Object>();
        handler = new Handler();
        wifiReceiver = new WifiReceiver(this);
    }

    public void init() {
        if (btAdapter == null) {
            log(TAG, "Bluetooth not supported", Log.ERROR, true);
            return;
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        registerService();

        evalThread.start();
        wifiReceiver.start();

        // we restart bluetooth before each run
        addToQueue(bluetoothRestart);
        addToQueue(nearbyPeersChecker);
//        addToQueue(nearbyPeersChecker);

        synchronized (evalThread) {
            evalThread.notify();
        }

        blockedWorkersChecker.run();
    }

    // TODO this is not quite OK
    public void registerService() {
        String userFriendlyName = PreferenceManager.getDefaultSharedPreferences(context).getString("name", null);

        if (userFriendlyName == null) {
            log(TAG, "Name not set for device", Log.ERROR, true);
            return;
        }

        btAdapter.setName(userFriendlyName);
    }

    private void startReceivers() {
        for (UUID uuid : SERVICE_UUIDS) {
            BTReceiver btReceiver = new BTReceiver(this, uuid);
            btReceivers.add(btReceiver);
            btReceiver.start();
        }
    }

    private void stopReceivers() {
        for (BTReceiver receiver : btReceivers) {
            receiver.abort();
        }
        btReceivers.clear();
    }

    @Override
    public void clean() {
        for(Map.Entry<String, String> entry : registeredExpressions.entrySet()) {
            unregisterExpression(entry.getKey(), entry.getValue(), null);
        }

        disconnect();
        context.unregisterReceiver(mReceiver);
    }

    public void disconnect() {
        for(BTSwanDevice swanDevice : nearbyDevices) {
            if (swanDevice.isConnectedToRemote()) {
                swanDevice.getConnection().disconnect();
            }
        }
        for(BTReceiver receiver : btReceivers) {
            receiver.abort();
        }
        wifiReceiver.abort();
    }

    public void discoverPeers() {
        if (!btAdapter.isDiscovering()) {
            log(TAG, "Discovering...", Log.DEBUG);
            btAdapter.startDiscovery();
        } else {
            log(TAG, "Discovery already started", Log.DEBUG);
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

    // we have to synchronize this to make sure that it is not exectued while a client worker is removed in
    // clientWorkerDone(), which would prevent the addition of a new task in the queue
    public synchronized void registerExpression(String id, String expression, String resolvedLocation) {
        log(TAG, "registering expression " + id + ": " + expression, Log.DEBUG);
        boolean newTask = false;

        if (resolvedLocation.equals(Expression.LOCATION_NEARBY)) {
            registeredExpressions.put(id, expression);

            for (BTSwanDevice swanDevice : nearbyDevices) {
                swanDevice.registerExpression(id, expression);
                // if this is the first registered expression, then start a new task
                if(swanDevice.getRegisteredExpressions().size() == 1) {
                    BTRemoteEvaluationTask evalTask = new BTRemoteEvaluationTask(swanDevice);
//                    addToQueue(evalTask);
                    scheduleEvaluationTask(evalTask, TIME_BETWEEN_REQUESTS);
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
//                    addToQueue(evalTask);
                    scheduleEvaluationTask(evalTask, TIME_BETWEEN_REQUESTS);
                    newTask = true;
                }
            } else {
                log(TAG, "can't find device " + resolvedLocation + "; won't register expression", Log.ERROR);
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
        log(TAG, "unregistering expression " + id + ": " + expression, Log.DEBUG);
        registeredExpressions.remove(id);

        for(BTSwanDevice swanDevice : nearbyDevices) {
            swanDevice.unregisterExpression(id);
        }

        if(registeredExpressions.isEmpty()) {
            printLogs();
            disconnect();
        }
    }

    // we synchronize this to make sure that a client worker is not added while a connection is killed in cleanupConnections()
    protected synchronized void processQueueItem(Object item) {
        log(TAG, "processing " + item, Log.DEBUG, true);

        if(item instanceof BTRemoteEvaluationTask) {
            BTRemoteEvaluationTask remoteEvalTask = (BTRemoteEvaluationTask) item;
            updateEvaluationTask(remoteEvalTask);

            if(remoteEvalTask.hasExpressions()) {
                BTClientWorker clientWorker = new BTClientWorker(this, remoteEvalTask);
                addClientWorker(clientWorker);
                clientWorker.start();
            }
        } else if(item instanceof Runnable) {
            // peer discovery or restart bluetooth
            ((Runnable) item).run();
        } else {
            log(TAG, "Item can't be processed: " + item, Log.ERROR);
        }
    }

    protected void updateEvaluationTask(BTRemoteEvaluationTask remoteEvalTask) {
        List<BTRemoteExpression> toRemove = new ArrayList<BTRemoteExpression>();

        for(BTRemoteExpression expression : remoteEvalTask.getExpressions()) {
            if(!registeredExpressions.containsKey(expression.getBaseId())) {
                toRemove.add(expression);
            }
        }

        remoteEvalTask.getExpressions().removeAll(toRemove);
    }

    // we synchronize this to make sure that it is not called while clientWorkerDone or serverWorkerDone are called
    public synchronized void send(final String remoteDeviceName, final String exprId, final String exprAction, final String exprData) {
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
            log(TAG, "couldn't send " + exprAction + " to " + remoteDeviceName + "(id: " + exprId + "): " + e.getMessage(), Log.ERROR, true);

            // unregister expression
            if (exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                // TODO close socket here, so the other side unblocks
                log(TAG, "unregistering expression " + exprId + "...", Log.ERROR);
                sendExprForEvaluation(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, remoteDeviceName, exprData);
            }

            return;
        }

        log(TAG, "couldn't send " + exprAction + " to " + remoteDeviceName + "(id: " + exprId + "): expression already processed", Log.ERROR);
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

    protected void addToQueue(Object item) {
        evalQueue.add(item);
        log(TAG, "item added to queue: " + item, Log.DEBUG);
        log(TAG, "[Queue] " + evalQueue, Log.DEBUG);
    }

    private Object removeFromQueue() {
        Object item = evalQueue.remove();
        log(TAG, "first item removed from queue: " + item, Log.DEBUG);
        log(TAG, "[Queue] " + evalQueue, Log.DEBUG);
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

    protected BTSwanDevice getNearbyDeviceByIp(String deviceIp) {
        for (BTSwanDevice device : nearbyDevices) {
            if (device.getIpAddress().equals(deviceIp)) {
                return device;
            }
        }
        return null;
    }

    protected BTSwanDevice addNearbyDevice(BluetoothDevice btDevice, BTConnection btConnection) {
        BTSwanDevice swanDevice = getNearbyDeviceByName(btDevice.getName());

        if (swanDevice == null) {
            log(TAG, "added new device " + btDevice.getName(), Log.DEBUG, true);
            bcastLogMessage("nearby device found " + btDevice.getName());
            swanDevice = new BTSwanDevice(btDevice, this, btConnection);
            nearbyDevices.add(swanDevice);
            registerRemoteDevice(swanDevice);
        } else {
            log(TAG, "device " + btDevice.getName() + " already present, won't add", Log.DEBUG);
            if(btConnection != null && btConnection.isConnected()) {
                log(TAG, "updated connection for " + btDevice.getName(), Log.DEBUG);
                swanDevice.setConnection(btConnection);
            }
        }
        return swanDevice;
    }

    private void registerRemoteDevice(BTSwanDevice swanDevice) {
        if (registeredExpressions.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : registeredExpressions.entrySet()) {
            swanDevice.registerExpression(entry.getKey(), entry.getValue());
        }

        BTRemoteEvaluationTask evalTask = new BTRemoteEvaluationTask(swanDevice);
//        addToQueue(evalTask);
        scheduleEvaluationTask(evalTask, TIME_BETWEEN_REQUESTS);

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
        if(!SYNC_DEVICES) {
            remoteTimeToNextReq = 0;
        }

        BTSwanDevice swanDevice = remoteEvalTask.getSwanDevice();
        int timeToNextReq = TIME_BETWEEN_REQUESTS;
        log(TAG, "timeToNextReq = " + timeToNextReq + "; " + "remoteTimeToNextReq = " + remoteTimeToNextReq, Log.DEBUG);

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
                log(TAG, "set pending item " + swanDevice.getPendingItem() + " for device " + swanDevice, Log.DEBUG);
            } else {
                scheduleQueueItem(remoteEvalTask, timeToNextReq);
            }
        } else {
            scheduleQueueItem(remoteEvalTask, timeToNextReq);
        }
    }

    private void cleanupConnections() {
        if(!SHARED_CONNECTIONS) {
            return;
        }

        log(TAG, "cleaning up connections...", Log.INFO, true);
        List<BTSwanDevice> idleDevices = new ArrayList<BTSwanDevice>();
        int connectedDevices = 0;

        for(BTSwanDevice swanDevice : nearbyDevices) {
            if(swanDevice.isConnectedToRemote()) {
                connectedDevices++;

                if (swanDevice.getClientWorker() == null && swanDevice.getServerWorker() == null) {
                    idleDevices.add(swanDevice);
                }
            }
        }

        log(TAG, "found " + connectedDevices + " connected devices; max is " + MAX_CONNECTIONS, Log.DEBUG);

        for(BTSwanDevice swanDevice : idleDevices) {
            if(connectedDevices > MAX_CONNECTIONS) {
                swanDevice.getConnection().disconnect();
                connectedDevices--;
            } else if(USE_WIFI && swanDevice.getConnection() instanceof BTConnection) {
                // we use the bluetooth connection only for the initial exchange of IPs, so we can close it now
                swanDevice.getConnection().disconnect();
            } else {
                break;
            }
        }
    }

    /**
     * we have to synchronize the methods dealing with workers, as it may happen that send() is called
     * by EvaluationEngineService right before a worker is removed
     */
    protected synchronized void clientWorkerDone(BTClientWorker clientWorker, int remoteTimeToNextReq) {
        BTSwanDevice swanDevice = clientWorker.getSwanDevice();
        BTLogRecord workerLog = clientWorker.getLogRecord();
        log(TAG, "client worker done " + clientWorker + " in " + workerLog.totalDuration
                + "ms (comm time = " + (workerLog.totalDuration - workerLog.swanDuration) + "ms)", Log.DEBUG, true);

        // reschedule a new task if there are registered expressions on the device
        if(!swanDevice.getRegisteredExpressions().isEmpty()) {
            BTRemoteEvaluationTask remoteEvalTask = new BTRemoteEvaluationTask(swanDevice);
            scheduleEvaluationTask(remoteEvalTask, remoteTimeToNextReq);
        }

        clientWorkers.remove(clientWorker);
        waitingWorkers.remove(clientWorker);
        logRecords.add(clientWorker.getLogRecord());
        cleanupConnections();

        synchronized (evalThread) {
            evalThread.notify();
        }
    }

    /**
     * we have to synchronize the methods dealing with workers, as it may happen that send() is called
     * by EvaluationEngineService right before a worker is removed
     * also, cleanupConnections() call in this method shouldn't overlap with processQueueItem()
     */
    protected synchronized void serverWorkerDone(BTServerWorker serverWorker) {
        log(TAG, "server worker done " + serverWorker, Log.DEBUG, true);
        BTSwanDevice swanDevice = serverWorker.getSwanDevice();
        serverWorkers.remove(serverWorker);
        logRecords.add(serverWorker.getLogRecord());

        // if crashed, the other side will reschedule a request, so we wait for it and don't reschedule
        if(swanDevice.getPendingItem() != null) {
            final BTPendingItem pendingItem = swanDevice.getPendingItem();
            scheduleQueueItem(pendingItem.getItem(), pendingItem.getTimeout());
            swanDevice.setPendingItem(null);
        }

        cleanupConnections();

        if(SYNC_RECEIVERS) {
            for (BTReceiver receiver : btReceivers) {
                if (receiver.getSocket() != null && receiver.getSocket().equals(((BTConnection)serverWorker.getConnection()).getBtSocket())) {
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

    protected void addClientWorker(BTClientWorker clientWorker) {
        log(TAG, "client worker added to pool: " + clientWorker, Log.DEBUG, true);
        clientWorkers.add(clientWorker);
    }

    protected void addServerWorker(BTServerWorker serverWorker) {
        log(TAG, "server worker added to pool: " + serverWorker, Log.DEBUG, true);
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

    private void printLogs() {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
        String logSuffix = sdf.format(new Date());
        File logsDir = new File(context.getExternalFilesDir(null), "logs");
        File logFile = new File(logsDir, "log-" + logSuffix);
        logsDir.mkdirs();

        double reqCount = 0;
        double failedReq = 0;
        double failedRate = 0;
        double avgReqTime = 0;
        double avgConnTime = 0;
        double avgCommTime = 0;
        double avgSwanTime = 0;
        double successCount = 0;

        for(BTLogRecord logRec : logRecords) {
            // we log only logs by client workers
            if(logRec.client) {
                reqCount++;

                if(logRec.failed) {
                    failedReq++;
                } else {
                    successCount++;
                    avgReqTime += logRec.totalDuration;
                    avgConnTime += logRec.connDuration;
                    avgCommTime += logRec.totalDuration - logRec.swanDuration;
                    avgSwanTime += logRec.swanDuration;

                    sb.append((int)successCount + "\t" + logRec.toString() + "\n");
                }
            }
        }

        if(reqCount > 0) {
            failedRate = failedReq / reqCount;
            avgReqTime = avgReqTime / successCount;
            avgConnTime = avgConnTime / successCount;
            avgCommTime = avgCommTime / successCount;
            avgSwanTime = avgSwanTime / successCount;
        }

        try {
            FileWriter fw = new FileWriter(logFile);
            fw.append("\n# phones = " + (nearbyDevices.size() + 1));
            fw.append("\n# shared connections = " + SHARED_CONNECTIONS);
            fw.append("\n# max connections = " + MAX_CONNECTIONS);
            fw.append("\n# sample interval = " + TIME_BETWEEN_REQUESTS);
            fw.append("\n# sync receivers = " + SYNC_RECEIVERS);
            fw.append("\n# receivers = " + btReceivers.size());
            fw.append("\n# wifi enabled = " + USE_WIFI);
            fw.append("\n\n# failedRate = " + failedRate);
            fw.append("\n# avgReqTime = " + avgReqTime);
            fw.append("\n# avgConnTime = " + avgConnTime);
            fw.append("\n# avgCommTime = " + avgCommTime);
            fw.append("\n# avgSwanTime = " + avgSwanTime);
            fw.append("\n\n# Idx\t" + BTLogRecord.printHeader());
            fw.append("\n\n" + sb.toString());
            fw.close();

            logRecords.clear();
            startTime = 0;

            MediaScannerConnection.scanFile(context, new String[]{ logFile.getAbsolutePath() }, null, null);
            log(TAG, "log printed", Log.INFO, true);
        } catch (IOException e) {
            log(TAG, "couldn't write log", Log.ERROR, true, e);
        }
    }

    protected void log(String tag, String msg, int level) {
        log(tag, msg, level, false, null);
    }

    protected void log(String tag, String msg, int level, boolean critical) {
        log(tag, msg, level, critical, null);
    }

    protected void log(String tag, String msg, int level, Exception e) {
        log(tag, msg, level, false, e);
    }

    protected void log(String tag, String msg, int level, boolean critical, Exception e) {
        if(LOG_ONLY_CRITICAL && !critical) {
            return;
        }

        switch (level) {
            case Log.DEBUG: if(e != null) Log.d(tag, msg, e); else Log.d(tag, msg); break;
            case Log.ERROR: if(e != null) Log.e(tag, msg, e); else Log.e(tag, msg); break;
            case Log.INFO: if(e != null) Log.i(tag, msg, e); else Log.i(tag, msg); break;
            case Log.WARN: if(e != null) Log.w(tag, msg, e); else Log.w(tag, msg); break;
        }
    }

    public long getStartTime() {
        return startTime;
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

    public boolean isRestarting() {
        return restarting;
    }

    public void setRestarting(boolean restarting) {
        this.restarting = restarting;
    }
}

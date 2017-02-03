package interdroid.swan.crossdevice.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import interdroid.swan.crossdevice.bluetooth.BTManager;
import interdroid.swan.crossdevice.bluetooth.BTRemoteEvaluationTask;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SensorInfo;
import interdroid.swancore.swansong.Expression;

/**
 * Created by vladimir on 9/23/16.
 *
 * TODO set characteristics properties accordingly
 * TODO send numbers instead of strings whenever possible
 * TODO optimization: use one server worker per sensor/valuepath combination
 * TODO decouple this class from BTManager
 * TODO fix register/unregister for a specific bluetooth ID
 * TODO app crashes when BLE disconnects
 * TODO check if adapter is enabled
 */
public class BLEManager extends BTManager {

    private static final String TAG = "BLEManager";
    private static final int SCAN_PERIOD = 20000;
    protected static final UUID SWAN_SERVICE_UUID = UUID.fromString("11060915-f0e9-43b8-82b3-c3609d14313f");
    protected static final UUID SWAN_CHAR_REGISTER_UUID = UUID.fromString("ad847b73-3ce5-4b75-9330-6c952fa6f830");
    protected static final UUID SWAN_CHAR_UNREGISTER_UUID = UUID.fromString("06ad4ac5-ad7e-4884-ab2c-26d91faf4d42");

    private List<BLEClientWorker> clientWorkers = new ArrayList<>();
    private List<BLEServerWorker> serverWorkers = new ArrayList<>();
    private HashMap<UUID, String> uuidSensorMap = new HashMap<>();
    private LinkedBlockingQueue<Runnable> execQueue = new LinkedBlockingQueue<>();
    private BluetoothGattServer bleServer;
    private BluetoothManager btManager;

    private Thread bleExecThread = new Thread() {
        @Override
        public void run() {
            while(true) {
                try {
                    Runnable task = execQueue.take();
                    task.run();
                    sleep(250); // give some time to the bluetooth adapter to finish the task
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();

            if (device.getName() != null && device.getName().contains("SWAN")) {
//                log(TAG, "found nearby device " + device.getName(), Log.DEBUG);
                addNearbyDevice(device, null);
            }
        }
    };

    private BluetoothGattServerCallback bleServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.i(TAG, getDeviceName(device) + ": onConnectionStateChange: status = " + status + ", state = " + newState);
            // TODO handle here connections/disconnections
        }

        @Override
        public void onCharacteristicReadRequest(final BluetoothDevice device, final int requestId, int offset, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.i(TAG, "received read request from " + device.getName());

            enqueueTask(new Runnable() {
                @Override
                public void run() {
                    // it's very important to send this this empty response, otherwise further write operations won't work on the other side
                    bleServer.sendResponse(device, requestId, 0, 0, characteristic.getValue());
                }
            });

            BLEServerWorker serverWorker = new BLEServerWorker(BLEManager.this, device, characteristic);
            serverWorker.start();
            addServerWorker(serverWorker);
        }

        @Override
        public void onCharacteristicWriteRequest(final BluetoothDevice device, final int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            UUID sensorValuePathUuid = bytesToUuid(value);
            Log.i(TAG, device.getName() + ": received write request for: " + uuidSensorMap.get(sensorValuePathUuid));

            if(characteristic.getUuid().equals(SWAN_CHAR_REGISTER_UUID)) {
                if (bleServer.getService(sensorValuePathUuid) == null) {
                    BluetoothGattService service = new BluetoothGattService(sensorValuePathUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);
                    BluetoothGattCharacteristic newCharacteristic =
                            new BluetoothGattCharacteristic(sensorValuePathUuid,
                                    BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                                    BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
                    service.addCharacteristic(newCharacteristic);
                    bleServer.addService(service);
                }

                enqueueTask(new Runnable() {
                    @Override
                    public void run() {
                        //TODO here we can send back "not available" if the phone doesn't have the requested sensor
                        bleServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, "service added".getBytes());
                    }
                });
            } else if(characteristic.getUuid().equals(SWAN_CHAR_UNREGISTER_UUID)) {
                ArrayList<BLEServerWorker> workersDone = new ArrayList<>();

                for(BLEServerWorker serverWorker : serverWorkers) {
                    if(serverWorker.getCharacteristic().getUuid().equals(sensorValuePathUuid) && serverWorker.getDevice().equals(device)) {
                        serverWorker.stop();
                        workersDone.add(serverWorker);
                    }
                }

                for(BLEServerWorker serverWorker : workersDone) {
                    BLEManager.this.serverWorkerDone(serverWorker);
                }

                //TODO remove the service if no one is using it anymore
            }
        }
    };

    public BLEManager(Context context) {
        super(context);
        btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    @Override
    public void init() {
        if (btAdapter == null) {
            log(TAG, "Bluetooth not supported", Log.ERROR, true);
            return;
        }

        if(btAdapter.isMultipleAdvertisementSupported()) {
            initServer();
            advertise();
        } else {
            log(TAG, "BLE advertising not supported", Log.ERROR, true);
        }

        // initialize sensors uuids
        for(SensorInfo sensor : ExpressionManager.getSensors(context)) {
            for(String valuePath : sensor.getValuePaths()) {
                String sensorValuePath = sensor.getEntity() + ":" + valuePath;
                UUID sensorValuePathUuid = UUID.nameUUIDFromBytes(sensorValuePath.getBytes());
                uuidSensorMap.put(sensorValuePathUuid, sensorValuePath);
            }
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

//        addToQueue(nearbyPeersChecker);
        discoverPeers();
        bleExecThread.start();
        evalThread.start();

//        addToQueue(bluetoothRestart);

        synchronized (evalThread) {
            evalThread.notify();
        }
    }

    private void initServer() {
        BluetoothGattService service = new BluetoothGattService(SWAN_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic charRegister =
                new BluetoothGattCharacteristic(SWAN_CHAR_REGISTER_UUID,
                        BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(charRegister);

        BluetoothGattCharacteristic charUnregister =
                new BluetoothGattCharacteristic(SWAN_CHAR_UNREGISTER_UUID,
                        BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(charUnregister);

        bleServer = btManager.openGattServer(context, bleServerCallback);
        bleServer.addService(service);
    }

    private void advertise() {
        BluetoothLeAdvertiser bleAdvertiser = btAdapter.getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
                .build();
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();
        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e("BLE", "Advertising onStartFailure: " + errorCode);
                super.onStartFailure(errorCode);
            }
        };

        bleAdvertiser.startAdvertising(settings, data, advertisingCallback);
    }

    @Override
    public void discoverPeers() {
        // Stops scanning after a pre-defined scan period.
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                btAdapter.getBluetoothLeScanner().stopScan(scanCallback);
//                log(TAG, "discovery finished", Log.INFO, true);
//                bcastLogMessage("discovery finished");
//
////                handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        discoverPeers();
////                    }
////                }, PEER_DISCOVERY_INTERVAL);
//            }
//        }, SCAN_PERIOD);

        btAdapter.getBluetoothLeScanner().startScan(scanCallback);
        log(TAG, "discovery started", Log.INFO, true);
        bcastLogMessage("discovery started");
    }

    protected void enqueueTask(Runnable task) {
        try {
            execQueue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected synchronized void processQueueItem(Object item) {
        log(TAG, "processing " + item, Log.DEBUG, true);

        if(item instanceof BTRemoteEvaluationTask) {
            BTRemoteEvaluationTask remoteEvalTask = (BTRemoteEvaluationTask) item;
            updateEvaluationTask(remoteEvalTask);

            if(remoteEvalTask.hasExpressions()) {
                BLEClientWorker clientWorker = new BLEClientWorker(this, remoteEvalTask);
                clientWorker.start();
                addClientWorker(clientWorker);
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if(item instanceof Runnable) {
            // peer discovery or restart bluetooth
            ((Runnable) item).run();
        } else {
            log(TAG, "Item can't be processed: " + item, Log.ERROR);
        }
    }

    @Override
    public synchronized void unregisterExpression(String id, String expression, String resolvedLocation) {
        ArrayList<BLEClientWorker> workersDone = new ArrayList<>();
        super.unregisterExpression(id, expression, resolvedLocation);

        if(resolvedLocation.equals(Expression.LOCATION_NEARBY)) {
            for (BLEClientWorker clientWorker : clientWorkers) {
                if(clientWorker.unregisterExpression(id)) {
                    workersDone.add(clientWorker);
                }
            }
        } else {
            //TODO
        }

        for(BLEClientWorker clientWorker : workersDone) {
            clientWorkerDone(clientWorker);
        }
    }

    public static UUID bytesToUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    protected void addClientWorker(BLEClientWorker clientWorker) {
        log(TAG, "client worker added to pool: " + clientWorker, Log.DEBUG, true);
        clientWorkers.add(clientWorker);
    }

    protected void addServerWorker(BLEServerWorker serverWorker) {
        log(TAG, "server worker added to pool: " + serverWorker, Log.DEBUG, true);
        serverWorkers.add(serverWorker);
    }

    protected void clientWorkerDone(BLEClientWorker clientWorker) {
        log(TAG, "client worker done " + clientWorker, Log.DEBUG, true);
        clientWorkers.remove(clientWorker);
    }

    protected void serverWorkerDone(BLEServerWorker serverWorker) {
        log(TAG, "server worker done " + serverWorker, Log.DEBUG, true);
        serverWorkers.remove(serverWorker);
    }

    protected String getSensorForUuid(UUID uuid) {
        return uuidSensorMap.get(uuid);
    }

    protected BluetoothGattServer getBleServer() {
        return bleServer;
    }

    public String getDeviceName(BluetoothDevice device) {
        if(device.getName() != null) {
            return device.getName();
        }
        return device.getAddress();
    }

}

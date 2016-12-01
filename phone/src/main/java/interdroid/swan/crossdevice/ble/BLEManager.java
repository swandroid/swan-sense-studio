package interdroid.swan.crossdevice.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

import interdroid.swan.crossdevice.bluetooth.BTManager;
import interdroid.swan.crossdevice.bluetooth.BTRemoteEvaluationTask;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SensorInfo;

/**
 * Created by vladimir on 9/23/16.
 *
 * TODO set characteristics properties accordingly
 * TODO send numbers instead of strings whenever possible
 */
public class BLEManager extends BTManager {

    private static final String TAG = "BLEManager";
    private static final int SCAN_PERIOD = 5000;
    private static final int CHARACTERISTIC_CHANGE_INTERVAL = 5000;
    protected static final UUID SWAN_SERVICE_UUID = UUID.fromString("11060915-f0e9-43b8-82b3-c3609d14313f");
    protected static final UUID SWAN_REQUEST_UUID = UUID.fromString("ad847b73-3ce5-4b75-9330-6c952fa6f830");
    protected static final UUID SWAN_SENSOR_VALUE_UUID = UUID.fromString("c13e4329-fd71-4962-8cf3-7062317978ea");

    private HashMap<UUID, String> uuidSensorMap = new HashMap<>();
    private BluetoothGattServer bleServer;
    private BluetoothManager btManager;
    private long startTime;

    /* we schedule peer discovery to take place at regular intervals */
    private Runnable nearbyPeersChecker = new Runnable() {
        public void run() {
            log(TAG, "discovery started", Log.INFO, true);
            bcastLogMessage("discovery started");

            discoverPeers();
            setDiscovering(true);
        }

        @Override
        public String toString() {
            return "BLEDiscoveryEvent";
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();

            if (device.getName() != null && device.getName().contains("SWAN")) {
                log(TAG, "found nearby device " + device.getName(), Log.DEBUG);
                addNearbyDevice(device, null);
            }
        }
    };

    private BluetoothGattServerCallback bleServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.i(TAG, "[server] onConnectionStateChange: status = " + status + ", state = " + newState);
            // TODO handle here connections/disconnections
        }

        @Override
        public void onCharacteristicReadRequest(final BluetoothDevice device, int requestId, int offset, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.i(TAG, "received read request from " + device.getName());

            bleServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, characteristic.getValue());
            BLEServerWorker serverWorker = new BLEServerWorker(BLEManager.this, device, characteristic);
            serverWorker.start();
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            String reqSensorValuePath = new String(value);
            Log.i(TAG, "received request from " + device.getName() + ": " + reqSensorValuePath);

            UUID serviceUuid = UUID.nameUUIDFromBytes(reqSensorValuePath.getBytes());
            BluetoothGattService service = new BluetoothGattService(serviceUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

            UUID serviceCharacteristicUuid = UUID.nameUUIDFromBytes(reqSensorValuePath.getBytes());
            BluetoothGattCharacteristic newCharacteristic =
                    new BluetoothGattCharacteristic(serviceCharacteristicUuid,
                            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                            BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
            service.addCharacteristic(newCharacteristic);
            bleServer.addService(service); // TODO check if service doesn't exist already

            bleServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, "service added".getBytes());
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
            advertise();
            initServer();
//            initReceiver();
        } else {
            log(TAG, "BLE advertising not supported", Log.ERROR, true);
        }

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

        addToQueue(nearbyPeersChecker);
        evalThread.start();

        synchronized (evalThread) {
            evalThread.notify();
        }
    }

    private void initReceiver() {
        new Thread() {
            @Override
            public void run() {
                BluetoothServerSocket serverSocket = null;
                try {
                    serverSocket = btAdapter.listenUsingInsecureRfcommWithServiceRecord(BTManager.SERVICE_NAME, SWAN_SERVICE_UUID);
                    BluetoothSocket btSocket = serverSocket.accept();

                    if (btSocket != null) {
                        OutputStream os = btSocket.getOutputStream();
                        ObjectOutputStream outStream = new ObjectOutputStream(os);
                        InputStream is = btSocket.getInputStream();
                        ObjectInputStream inStream = new ObjectInputStream(is);

                        while (true) {
                            Object test = inStream.readObject();
                            Log.d(TAG, "[server bt] received " + test);
                            outStream.writeObject(test);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initServer() {
        BluetoothGattService service = new BluetoothGattService(SWAN_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristic =
                new BluetoothGattCharacteristic(SWAN_REQUEST_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(characteristic);

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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btAdapter.getBluetoothLeScanner().stopScan(scanCallback);

                log(TAG, "discovery finished", Log.INFO, true);
                bcastLogMessage("discovery finished");

                setDiscovering(false);
                synchronized (evalThread) {
                    evalThread.notify();
                }
            }
        }, SCAN_PERIOD);

        btAdapter.getBluetoothLeScanner().startScan(scanCallback);

//        btAdapter.startDiscovery();
    }

    @Override
    protected synchronized void processQueueItem(Object item) {
        log(TAG, "processing " + item, Log.DEBUG, true);

        if(item instanceof BTRemoteEvaluationTask) {
            BTRemoteEvaluationTask remoteEvalTask = (BTRemoteEvaluationTask) item;
            updateEvaluationTask(remoteEvalTask);

            if(remoteEvalTask.hasExpressions()) {
//                processEvalTask(remoteEvalTask);
                BLEClientWorker clientWorker = new BLEClientWorker(this, remoteEvalTask);
                clientWorker.start();
            }
        } else if(item instanceof Runnable) {
            // peer discovery or restart bluetooth
            ((Runnable) item).run();
        } else {
            log(TAG, "Item can't be processed: " + item, Log.ERROR);
        }
    }

    @Deprecated
    private void processEvalTask(BTRemoteEvaluationTask remoteEvalTask) {
        BluetoothDevice device = remoteEvalTask.getSwanDevice().getBtDevice();
        startTime = System.currentTimeMillis();
//        device.connectGatt(context, false, bleClientCallback);
//        connect(device);
    }

    protected String getSensorForUuid(UUID uuid) {
        return uuidSensorMap.get(uuid);
    }

    protected BluetoothGattServer getBleServer() {
        return bleServer;
    }

    private void connect(BluetoothDevice device) {
        BluetoothSocket btSocket = null;

        try {
            btSocket = device.createInsecureRfcommSocketToServiceRecord(SWAN_SERVICE_UUID);
            btSocket.connect();
            Log.d(TAG, "[client bt] conn time = " + (System.currentTimeMillis() - startTime) + " ms)");

            OutputStream os = btSocket.getOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream(os);
            InputStream is = btSocket.getInputStream();
            ObjectInputStream inStream = new ObjectInputStream(is);

            startTime = System.currentTimeMillis();
            outStream.writeObject("Hi there!");

            while(true) {
                Object test = inStream.readObject();
                Log.d(TAG, "[client bt] received " + test + " (" + (System.currentTimeMillis() - startTime) + " ms)");
                Thread.sleep(2000);
                startTime = System.currentTimeMillis();
                outStream.writeObject(test);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

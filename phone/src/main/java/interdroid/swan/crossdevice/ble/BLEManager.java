package interdroid.swan.crossdevice.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

import interdroid.swan.crossdevice.bluetooth.BTManager;
import interdroid.swan.crossdevice.bluetooth.BTRemoteEvaluationTask;

/**
 * Created by vladimir on 9/23/16.
 */

public class BLEManager extends BTManager {

    private static final String TAG = "BLEManager";
    private static final int SCAN_PERIOD = 5000;
    private static final int CHARACTERISTIC_CHANGE_INTERVAL = 5000;
    private static final UUID LIGHT_CHARACTERISTIC_UUID = UUID.fromString("ad847b73-3ce5-4b75-9330-6c952fa6f830");
    private static final UUID LIGHT_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID SWAN_SERVICE_UUID = UUID.fromString("11060915-f0e9-43b8-82b3-c3609d14313f");

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
            // TODO register sensor
            bleServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, characteristic.getValue());

//            Runnable demoCharacteristicChange = new Runnable() {
//                public void run() {
//                    log(TAG, "[server] characteristic changed", Log.INFO, true);
//
//                    characteristic.setValue(characteristic.getStringValue(0) + "+");
//                    bleServer.notifyCharacteristicChanged(device, characteristic, false);
//                    handler.postDelayed(this, CHARACTERISTIC_CHANGE_INTERVAL);
//                }
//            };
//            handler.postDelayed(demoCharacteristicChange, CHARACTERISTIC_CHANGE_INTERVAL);
        }
    };

    private BluetoothGattCallback bleClientCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, "[client] onConnectionStateChange: status = " + status + ", state = " + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "[client] conn time = " + (System.currentTimeMillis() - startTime) + " ms");
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BluetoothGattService service = gatt.getService(SWAN_SERVICE_UUID);

            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(LIGHT_CHARACTERISTIC_UUID);
                gatt.setCharacteristicNotification(characteristic, true);
                gatt.readCharacteristic(characteristic);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "[client] onCharacteristicRead: " + characteristic.getStringValue(0) + " (" + (System.currentTimeMillis() - startTime) + " ms)");
            super.onCharacteristicRead(gatt, characteristic, status);

            if(LIGHT_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                try { Thread.sleep(2000); } catch (InterruptedException e) {}
                startTime = System.currentTimeMillis();
                gatt.readCharacteristic(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            if(LIGHT_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                Log.d(TAG, "[client] onCharacteristicChanged: " + characteristic.getStringValue(0));
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
            advertise();
            initServer();
//            initReceiver();
        } else {
            log(TAG, "BLE advertising not supported", Log.ERROR, true);
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

        BluetoothGattCharacteristic lightCharacteristic =
                new BluetoothGattCharacteristic(LIGHT_CHARACTERISTIC_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
        lightCharacteristic.setValue("Hi there!");
        service.addCharacteristic(lightCharacteristic);

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
                processEvalTask(remoteEvalTask);
            }
        } else if(item instanceof Runnable) {
            // peer discovery or restart bluetooth
            ((Runnable) item).run();
        } else {
            log(TAG, "Item can't be processed: " + item, Log.ERROR);
        }
    }

    private void processEvalTask(BTRemoteEvaluationTask remoteEvalTask) {
        BluetoothDevice device = remoteEvalTask.getSwanDevice().getBtDevice();
        startTime = System.currentTimeMillis();
        device.connectGatt(context, false, bleClientCallback);
//        connect(device);
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

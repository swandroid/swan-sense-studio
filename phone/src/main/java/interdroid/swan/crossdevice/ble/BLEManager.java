package interdroid.swan.crossdevice.ble;

import android.bluetooth.BluetoothDevice;
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
import android.content.Intent;
import android.util.Log;

import java.util.UUID;

import interdroid.swan.crossdevice.bluetooth.BTManager;
import interdroid.swan.crossdevice.bluetooth.BTRemoteEvaluationTask;

/**
 * Created by vladimir on 9/23/16.
 */

public class BLEManager extends BTManager {

    private static final String TAG = "BLEManager";
    private static final int SCAN_PERIOD = 60000;
    private static final UUID LIGHT_CHARACTERISTIC_UUID = UUID.fromString("ad847b73-3ce5-4b75-9330-6c952fa6f830");

    private BluetoothGattServer bleServer;
    private BluetoothManager btManager;

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
            Log.i(TAG, "onConnectionStateChange: status = " + status + ", state = " + newState);
            // TODO handle here connections/disconnections
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            // TODO register sensor
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
        if(!btAdapter.isMultipleAdvertisementSupported()) {
            log(TAG, "BLE advertising not supported", Log.ERROR, true);
            return;
        }

        advertise();
        initServer();
        evalThread.start();
        addToQueue(nearbyPeersChecker);

        synchronized (evalThread) {
            evalThread.notify();
        }
    }

    private void initServer() {
        BluetoothGattService service =new BluetoothGattService(BTManager.SERVICE_UUIDS[0], BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic lightCharacteristic =
                new BluetoothGattCharacteristic(LIGHT_CHARACTERISTIC_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
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
        // TODO
    }
}

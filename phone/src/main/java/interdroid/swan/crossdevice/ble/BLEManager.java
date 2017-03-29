package interdroid.swan.crossdevice.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import interdroid.swan.crossdevice.bluetooth.BTManager;
import interdroid.swan.crossdevice.bluetooth.BTRemoteEvaluationTask;
import interdroid.swan.crossdevice.bluetooth.BTRemoteExpression;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SensorInfo;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.SensorValueExpression;

/**
 * Created by vladimir on 9/23/16.
 *
 * TODO set characteristics properties accordingly
 * TODO send numbers instead of strings whenever possible
 * TODO optimization: use one server worker per sensor/valuepath combination
 * TODO decouple this class from BTManager
 * TODO fix register/unregister for a specific bluetooth ID
 * TODO app crashes when BLE disconnects
 * TODO check if adapter is enabled at start
 * TODO recognize Swan devices by UUID instead of device name
 * TODO send new notification only after the previous one has been acknowledged by onNotificationSent()
 */
public class BLEManager extends BTManager {

    private static final String TAG = "BLEManager";
    private static final int SCAN_PERIOD = 20000;
    protected static final UUID SWAN_SERVICE_UUID = UUID.fromString("11060915-f0e9-43b8-82b3-c3609d14313f");
    protected static final UUID SWAN_CHAR_UNREGISTER_UUID = UUID.fromString("06ad4ac5-ad7e-4884-ab2c-26d91faf4d42");
    protected static final UUID NOTIFY_DESC_UUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");
    // send sensor values in push mode or pull mode
    protected static final boolean PUSH_MODE = true;
    public static final int TIME_BETWEEN_REQUESTS = 1000;

    private List<BLEClientWorker> clientWorkers = new ArrayList<>();
    private List<BLEServerWorker> serverWorkers = new ArrayList<>();
    private HashMap<UUID, String> uuidSensorMap = new HashMap<>();
    private LinkedBlockingQueue<Runnable> execQueue = new LinkedBlockingQueue<>();
    private BluetoothGattServer bleServer;
    private BluetoothManager btManager;

    Runnable initTask = new Runnable() {
        public void run() {
            discoverPeers();

            if(btAdapter.isMultipleAdvertisementSupported()) {
                try {
                    initServer();
                    advertise();
                } catch (ExpressionParseException e) {
                    log(TAG, "couldn't initialize BLE server", Log.ERROR, true);
                }
            } else {
                log(TAG, "BLE advertising not supported", Log.ERROR, true);
            }
        }

        @Override
        public String toString() {
            return "DiscoveryEvent";
        }
    };

    private AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.e("BLE", "Advertising onStartSuccess: " + settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e("BLE", "Advertising onStartFailure: " + errorCode);
        }
    };

    private AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(true)
            .build();

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BLEManager.this.context);

            if (device.getName() != null && device.getName().contains("SWAN")) {
                addNearbyDevice(device, null);

                if(result.getScanRecord().getServiceUuids() != null) { // sometimes this is null
                    for (ParcelUuid parcelUuid : result.getScanRecord().getServiceUuids()) {
                        UUID sensorValuePathUuid = parcelUuid.getUuid();
                        String sensorEntity = getSensorForUuid(sensorValuePathUuid).split(":")[0];

                        // check if the service isn't started and if the sensor is shareable
                        if (bleServer.getService(sensorValuePathUuid) == null && sharedPref.getBoolean("sharing." + sensorEntity, false)) {
                            Log.d(TAG, "service " + getSensorForUuid(sensorValuePathUuid) + " not present, adding...");
                            BluetoothGattService service = new BluetoothGattService(sensorValuePathUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);
                            BluetoothGattCharacteristic newCharacteristic =
                                    new BluetoothGattCharacteristic(sensorValuePathUuid,
                                            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                                            BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
                            BluetoothGattDescriptor desc = new BluetoothGattDescriptor(NOTIFY_DESC_UUID,
                                    BluetoothGattDescriptor.PERMISSION_WRITE | BluetoothGattDescriptor.PERMISSION_READ);
                            newCharacteristic.addDescriptor(desc);
                            service.addCharacteristic(newCharacteristic);
                            bleServer.addService(service);
                        }
                    }
                }
            }
        }
    };

    private BluetoothGattServerCallback bleServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.w(TAG, getDeviceName(device) + ": connection changed (status = " + status + ", state = " + newState + ")");

            if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                ArrayList<BLEServerWorker> workersDone = new ArrayList<>();

                for(BLEServerWorker serverWorker : serverWorkers) {
                    if(serverWorker.getDevice().equals(device)) {
                        serverWorker.stop();
                        workersDone.add(serverWorker);
                    }
                }

                for(BLEServerWorker serverWorker : workersDone) {
                    BLEManager.this.serverWorkerDone(serverWorker);
                }
            }

            // TODO handle here connections/disconnections for client workers
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);

            BLEServerWorker serverWorker = new BLEServerWorker(BLEManager.this, device, characteristic, requestId, offset);
            serverWorker.start();
            addServerWorker(serverWorker);
        }

        @Override
        public void onCharacteristicWriteRequest(final BluetoothDevice device, final int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            UUID sensorValuePathUuid = bytesToUuid(value);
            Log.w(TAG, getDeviceName(device) + ": received write request for: " + uuidSensorMap.get(sensorValuePathUuid));

            if(characteristic.getUuid().equals(SWAN_CHAR_UNREGISTER_UUID)) {
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

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);

            if(descriptor.getUuid().equals(BLEManager.NOTIFY_DESC_UUID)) {
                BLEServerWorker serverWorker = new BLEServerWorker(BLEManager.this, device, descriptor.getCharacteristic());
                serverWorker.start();
                addServerWorker(serverWorker);
            }

            bleServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);

            if(status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, getDeviceName(device) + ": notification sent succesfully");
            } else {
                Log.e(TAG, getDeviceName(device) + ": notification couldn't be sent");
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

        Log.d(TAG, "starting as " + btAdapter.getName());
        bcastLogMessage("starting as " + btAdapter.getName());

        // initialize sensors uuids
        for(SensorInfo sensor : ExpressionManager.getSensors(context)) {
            for(String valuePath : sensor.getValuePaths()) {
                String sensorValuePath = sensor.getEntity() + ":" + valuePath;
                UUID sensorValuePathUuid = getUuidForSensorValuePath(sensorValuePath);
                uuidSensorMap.put(sensorValuePathUuid, sensorValuePath);
            }
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        evalThread.start();

        addToQueue(bluetoothRestart);
        addToQueue(initTask);

        synchronized (evalThread) {
            evalThread.notify();
        }
    }

    private void initServer() {
        BluetoothGattService service = new BluetoothGattService(SWAN_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic charUnregister =
                new BluetoothGattCharacteristic(SWAN_CHAR_UNREGISTER_UUID,
                        BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(charUnregister);

        bleServer = btManager.openGattServer(context, bleServerCallback);
        bleServer.addService(service);
    }

    private void advertise() throws ExpressionParseException {
        final BluetoothLeAdvertiser bleAdvertiser = btAdapter.getBluetoothLeAdvertiser();
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder().setIncludeDeviceName(true);

        for(Map.Entry<String, String> expressionEntry : registeredExpressions.entrySet()) {
            SensorValueExpression svExpression = (SensorValueExpression) ExpressionFactory.parse(expressionEntry.getValue());
            String sensorValuePath = svExpression.getEntity() + ":" + svExpression.getValuePath();
            UUID serviceUuid = getUuidForSensorValuePath(sensorValuePath);
            dataBuilder.addServiceUuid(new ParcelUuid(serviceUuid));
        }

        bleAdvertiser.startAdvertising(advertiseSettings, dataBuilder.build(), advertisingCallback);
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
        log(TAG, "discovery started", Log.DEBUG, true);
        bcastLogMessage("discovery started");
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
        } else if(item instanceof Runnable) {
            // peer discovery or restart bluetooth
            ((Runnable) item).run();
        } else {
            log(TAG, "Item can't be processed: " + item, Log.ERROR);
        }
    }

    @Override
    public synchronized void registerExpression(String id, String expression, String resolvedLocation) {
        super.registerExpression(id, expression, resolvedLocation);
        //TODO readvertise expression
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

    protected UUID getUuidForSensorValuePath(String sensorValuePath) {
        UUID uuid = UUID.nameUUIDFromBytes(sensorValuePath.getBytes());
        String shortUuid = uuid.toString().split("-")[1];
        return UUID.fromString("00002902-0000-1000-8000-00805F9B34FB".replace("2902", shortUuid));
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
        log(TAG, "client worker added to pool: " + clientWorker, Log.INFO, true);
        clientWorkers.add(clientWorker);
    }

    protected void addServerWorker(BLEServerWorker serverWorker) {
        log(TAG, "server worker added to pool: " + serverWorker.getDeviceName(), Log.WARN, true);
        serverWorkers.add(serverWorker);
    }

    protected void clientWorkerDone(BLEClientWorker clientWorker) {
        log(TAG, "client worker done " + clientWorker, Log.INFO, true);
        clientWorkers.remove(clientWorker);
    }

    protected void serverWorkerDone(BLEServerWorker serverWorker) {
        log(TAG, "server worker done " + serverWorker, Log.WARN, true);
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

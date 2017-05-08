package interdroid.swan.crossdevice.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import interdroid.swan.crossdevice.bluetooth.BTRemoteEvaluationTask;
import interdroid.swan.crossdevice.bluetooth.BTRemoteExpression;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * Created by vladimir on 11/25/2016.
 */

public class BLEClientWorker {

    private static final String TAG = "BLEClientWorker";

    private BLEManager bleManager;
    private BTRemoteEvaluationTask remoteEvaluationTask;
    private BluetoothGatt gatt;
    private int connectionState = BluetoothProfile.STATE_DISCONNECTED;
    private ArrayList<UUID> foundServices = new ArrayList<>();

    private long startTime;
    private long connTime;
    private long discoveryTime;

    public BLEClientWorker(BLEManager bleManager, BTRemoteEvaluationTask remoteEvaluationTask) {
        this.bleManager = bleManager;
        this.remoteEvaluationTask = remoteEvaluationTask;
    }

    public void start() {
        final BluetoothDevice device = remoteEvaluationTask.getSwanDevice().getBtDevice();
        Log.i(TAG, "connecting to " + device.getName() + "...");
        bleManager.bcastLogMessage("connecting to " + device.getName() + "...");
        startTime = System.currentTimeMillis();
        execThread.start();
        device.connectGatt(bleManager.getContext(), false, bleClientCallback);
    }

    private ConcurrentLinkedQueue<Runnable> execQueue = new ConcurrentLinkedQueue<>();
    private long startTimeLastExecItem;
    private boolean processing = false;

    public static class ExecWriteDesc implements Runnable {
        private BluetoothGattDescriptor descriptor;
        private BluetoothGatt gatt;

        public ExecWriteDesc(BluetoothGattDescriptor descriptor, BluetoothGatt gatt) {
            this.descriptor = descriptor;
            this.gatt = gatt;
        }

        @Override
        public void run() {
            gatt.writeDescriptor(descriptor);
        }
    }

    public static class ExecReadChar implements Runnable {
        private BluetoothGattCharacteristic characteristic;
        private BluetoothGatt gatt;

        public ExecReadChar(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
            this.characteristic = characteristic;
            this.gatt = gatt;
        }

        @Override
        public void run() {
            gatt.readCharacteristic(characteristic);
        }
    }

    public static class ExecAddService implements Runnable {
        BluetoothGattServer bleServer;
        BluetoothGattService service;

        public ExecAddService(BluetoothGattServer bleServer, BluetoothGattService service) {
            this.bleServer = bleServer;
            this.service = service;
        }

        @Override
        public void run() {
            bleServer.addService(service);
        }
    }

    protected final Thread execThread = new Thread() {
        @Override
        public void run() {
            try {
                while (true) {
                    while (execQueue.isEmpty() || processing) {
                        synchronized (this) {
                            wait();
                        }
                    }

                    Runnable item = execQueue.remove();
                    startTimeLastExecItem = System.currentTimeMillis();
                    item.run();
                    setProcessing(true);
                }
            } catch (Exception e) {
                Log.e(TAG, "error in exec thread", e);
            }
        }
    };

    public void setProcessing(boolean processing) {
        this.processing = processing;
        synchronized (execThread) {
            execThread.notify();
        }
    }

    protected void addExecQueueItem(Runnable item) {
        execQueue.add(item);
        synchronized (execThread) {
            execThread.notify();
        }
    }

    private BluetoothGattCallback bleClientCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, remoteEvaluationTask.getSwanDevice() + ": onConnectionStateChange: status = " + status + ", state = " + newState);
            BLEClientWorker.this.gatt = gatt;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "connected to " + remoteEvaluationTask.getSwanDevice() + ", discovering services...");
                bleManager.bcastLogMessage("connected to " + remoteEvaluationTask.getSwanDevice());
                gatt.discoverServices();
                connectionState = BluetoothProfile.STATE_CONNECTED;
                connTime = System.currentTimeMillis();
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "disconnected from " + remoteEvaluationTask.getSwanDevice());
                bleManager.bcastLogMessage("disconnected from " + remoteEvaluationTask.getSwanDevice());
                gatt.close();

                if(connectionState != BluetoothProfile.STATE_DISCONNECTING) {
                    // if not disconnected on demand, then retry to connect
                    start();
                } else {
                    bleManager.clientWorkerDone(BLEClientWorker.this);
                }
                //TODO unregister expressions
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            discoveryTime = System.currentTimeMillis();
            boolean allServicesDiscovered = true;

            // we make a copy of the original expressions list to avoid ConcurrentModificationException that occurs
            // when we iterate over the expressions while at the same time expressions are removed in onReceive()
            List<BTRemoteExpression> remoteExpressions = new ArrayList<>(remoteEvaluationTask.getExpressions());

            for(BTRemoteExpression remoteExpression : remoteExpressions) {
                try {
                    SensorValueExpression expression = (SensorValueExpression) remoteExpression.getExpression();
                    String sensorValuePath = expression.getEntity() + ":" + expression.getValuePath();
                    UUID serviceUuid = bleManager.getUuidForSensorValuePath(sensorValuePath);
                    BluetoothGattService service = gatt.getService(serviceUuid);

                    if(service != null) {
                        if(!foundServices.contains(serviceUuid)) {
                            Log.i(TAG, remoteEvaluationTask.getSwanDevice() + ": found service " + sensorValuePath);
                            final BluetoothGattCharacteristic characteristic = service.getCharacteristic(serviceUuid);

                            if (characteristic != null) {
                                Log.d(TAG, "value for service " + sensorValuePath + " = " + characteristic.getStringValue(0));
                                if (BLEManager.PUSH_MODE) {
                                    gatt.setCharacteristicNotification(characteristic, true);
                                    gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BLEManager.NOTIFY_DESC_UUID);
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                                    bleManager.addExecQueueItem(new BLEManager.ExecWriteDesc(descriptor, gatt));
                                    addExecQueueItem(new ExecWriteDesc(descriptor, gatt));
                                } else {
                                    Log.d(TAG, "requesting service " + sensorValuePath + "...");
                                    foundServices.add(serviceUuid);
//                                    bleManager.addExecQueueItem(new BLEManager.ExecReadChar(characteristic, gatt));
                                    addExecQueueItem(new ExecReadChar(characteristic, gatt));
                                }
                            } else {
                                Log.i(TAG, remoteEvaluationTask.getSwanDevice() + ": characteristic for service " + sensorValuePath + " not found, retrying...");
                                allServicesDiscovered = false;
                            }
                        }
                    } else {
                        Log.i(TAG, remoteEvaluationTask.getSwanDevice() + ": service " + sensorValuePath + " not found, retrying...");
                        allServicesDiscovered = false;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "cannot process remote expression", e);
                }
            }

            if(!allServicesDiscovered) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gatt.discoverServices();
                    }
                }, 2000);
            }
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
//            long reqDuration = System.currentTimeMillis() - bleManager.getStartTimeLastExecItem();
            long reqDuration = System.currentTimeMillis() - startTimeLastExecItem;
//            bleManager.setProcessing(false);
            setProcessing(false);
            Log.d(TAG, "request took " + reqDuration + "ms");

            if(status == BluetoothGatt.GATT_SUCCESS) {
                BLELogRecord bleLogRecord = new BLELogRecord(startTime, bleManager.getStartTime(), reqDuration,
                        connTime, discoveryTime, remoteEvaluationTask.getExpressionIds().size(), false);
                bleManager.addLogRecord(bleLogRecord);
                onCharacteristicChanged(gatt, characteristic);
            } else {
                BLELogRecord bleLogRecord = new BLELogRecord(startTime, bleManager.getStartTime(), reqDuration,
                        connTime, discoveryTime, remoteEvaluationTask.getExpressionIds().size(), true);
                bleManager.addLogRecord(bleLogRecord);
                Log.e(TAG, "couldn't read characteristic");
            }

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
//                    bleManager.addExecQueueItem(new BLEManager.ExecReadChar(characteristic, gatt));
                    addExecQueueItem(new ExecReadChar(characteristic, gatt));
                }
            }, BLEManager.TIME_BETWEEN_REQUESTS);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            try {
                String sensorValuePath = bleManager.getSensorForUuid(characteristic.getUuid());
                String sensor = sensorValuePath.split(":")[0];
                String valuePath = sensorValuePath.split(":")[1];

                Log.i(TAG, remoteEvaluationTask.getSwanDevice() + ": new result for " + sensorValuePath + ": " + characteristic.getStringValue(0));

                for (BTRemoteExpression remoteExpr : remoteEvaluationTask.getExpressions()) {
                    SensorValueExpression expression = (SensorValueExpression) remoteExpr.getExpression();

                    if (sensor.equals(expression.getEntity()) && valuePath.equals(expression.getValuePath())) {
                        TimestampedValue tValue = new TimestampedValue(characteristic.getStringValue(0));
                        Result result = new Result(new TimestampedValue[]{tValue}, tValue.getTimestamp());

                        bleManager.sendExprForEvaluation(remoteExpr.getBaseId(), EvaluationEngineService.ACTION_NEW_RESULT_REMOTE,
                                remoteEvaluationTask.getSwanDevice().getName(), Converter.objectToString(result));

                    }

                }
            } catch (Exception e) {
                Log.e(TAG, "couldn't get new value for characteristic");
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG, remoteEvaluationTask.getSwanDevice() + ": wrote characteristic " + characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i(TAG, remoteEvaluationTask.getSwanDevice() + ": descriptor wrote succesfully");
//            bleManager.setProcessing(false);
            setProcessing(false);
        }
    };

    /**
     * return true if client worker finished all expressions
     */
    public boolean unregisterExpression(String exprId) {
        BTRemoteExpression toRemove = null;

        for(BTRemoteExpression remoteExpression : remoteEvaluationTask.getExpressions()) {
            if(remoteExpression.getBaseId().equals(exprId) && gatt != null) {
                SensorValueExpression expression = (SensorValueExpression) remoteExpression.getExpression();
                String sensorValuePath = expression.getEntity() + ":" + expression.getValuePath();
                UUID serviceUuid = UUID.nameUUIDFromBytes(sensorValuePath.getBytes());

                // TODO check here if connection has failed, otherwise it crashes
                BluetoothGattService service = gatt.getService(BLEManager.SWAN_SERVICE_UUID);
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(BLEManager.SWAN_CHAR_UNREGISTER_UUID);
                characteristic.setValue(bleManager.uuidToBytes(serviceUuid));

                gatt.writeCharacteristic(characteristic);

                toRemove = remoteExpression;
                break;
            }
        }

        if(toRemove != null) {
            remoteEvaluationTask.removeExpression(toRemove);

            if(remoteEvaluationTask.getExpressions().isEmpty()) {
                if(gatt != null) {
                    connectionState = BluetoothProfile.STATE_DISCONNECTING;
                    gatt.close();
                }
                return true;
            }
        }

        return false;
    }
}

package interdroid.swan.crossdevice.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEClientWorker {

    private static final String TAG = "BLEClientWorker";

    private BLEManager bleManager;
    private BTRemoteEvaluationTask remoteEvaluationTask;
    private BluetoothGatt gatt;

    private BluetoothGattCallback bleClientCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, "onConnectionStateChange: status = " + status + ", state = " + newState);
            BLEClientWorker.this.gatt = gatt;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            // we make a copy of the original expressions list to avoid ConcurrentModificationException that occurs
            // when we iterate over the expressions while at the same time expressions are removed in onReceive()
            List<BTRemoteExpression> remoteExpressions = new ArrayList<>(remoteEvaluationTask.getExpressions());

            for(BTRemoteExpression remoteExpression : remoteExpressions) {
                try {
                    SensorValueExpression expression = (SensorValueExpression) remoteExpression.getExpression();
                    String sensorValuePath = expression.getEntity() + ":" + expression.getValuePath();
                    UUID serviceUuid = UUID.nameUUIDFromBytes(sensorValuePath.getBytes());
                    BluetoothGattService service = gatt.getService(serviceUuid);

                    if(service != null) {
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(serviceUuid);
                        gatt.setCharacteristicNotification(characteristic, true);
                        gatt.readCharacteristic(characteristic);
                    } else {
                        service = gatt.getService(BLEManager.SWAN_SERVICE_UUID);
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(BLEManager.SWAN_CHAR_REGISTER_UUID);
                        characteristic.setValue(bleManager.uuidToBytes(serviceUuid));
                        gatt.writeCharacteristic(characteristic);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "cannot process remote expression", e);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            try {
                String sensorValuePath = bleManager.getSensorForUuid(characteristic.getUuid());
                String sensor = sensorValuePath.split(":")[0];
                String valuePath = sensorValuePath.split(":")[1];

                for (BTRemoteExpression remoteExpr : remoteEvaluationTask.getExpressions()) {
                    SensorValueExpression expression = (SensorValueExpression) remoteExpr.getExpression();

                    if (sensor.equals(expression.getEntity()) && valuePath.equals(expression.getValuePath())) {
                        TimestampedValue tValue = new TimestampedValue(Double.valueOf(characteristic.getStringValue(0)));
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
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if(characteristic.getUuid().equals(BLEManager.SWAN_CHAR_REGISTER_UUID)) {
                gatt.discoverServices();
            }
        }
    };

    public BLEClientWorker(BLEManager bleManager, BTRemoteEvaluationTask remoteEvaluationTask) {
        this.bleManager = bleManager;
        this.remoteEvaluationTask = remoteEvaluationTask;
    }

    public void start() {
        BluetoothDevice device = remoteEvaluationTask.getSwanDevice().getBtDevice();
        device.connectGatt(bleManager.getContext(), false, bleClientCallback);
    }

    public void unregisterExpression(String exprId) {
        BTRemoteExpression toRemove = null;

        for(BTRemoteExpression remoteExpression : remoteEvaluationTask.getExpressions()) {
            if(remoteExpression.getBaseId().equals(exprId) && gatt != null) {
                SensorValueExpression expression = (SensorValueExpression) remoteExpression.getExpression();
                String sensorValuePath = expression.getEntity() + ":" + expression.getValuePath();
                UUID serviceUuid = UUID.nameUUIDFromBytes(sensorValuePath.getBytes());

                BluetoothGattService service = gatt.getService(BLEManager.SWAN_SERVICE_UUID);
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(BLEManager.SWAN_CHAR_UNREGISTER_UUID);
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
                    gatt.close();
                }
                bleManager.clientWorkerDone(this);
            }
        }
    }
}

package interdroid.swan.crossdevice.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.HistoryReductionMode;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * Created by vladzy on 11/25/2016.
 */

public class BLEServerWorker {

    private static final String TAG = "BLEServerWorker";

    private BLEManager bleManager;
    private BluetoothDevice device;
    private BluetoothGattCharacteristic characteristic;
    private static int curExpressionId = 0;

    private synchronized int nextExpressionId() {
        return curExpressionId++;
    }

    public BLEServerWorker(BLEManager bleManager, BluetoothDevice device, BluetoothGattCharacteristic characteristic) {
        this.bleManager = bleManager;
        this.device = device;
        this.characteristic = characteristic;
    }

    public void start() {
        try {
            String sensorValuePath = bleManager.getSensorForUuid(characteristic.getUuid());
            String sensorEntity = sensorValuePath.split(":")[0];
            String valuePath = sensorValuePath.split(":")[1];
            SensorValueExpression expression = new SensorValueExpression(Expression.LOCATION_SELF,
                    sensorEntity, valuePath, null, HistoryReductionMode.DEFAULT_MODE.ANY, 1000, null);

            ExpressionManager.registerValueExpression(bleManager.getContext(), nextExpressionId() + "", expression, new ValueExpressionListener() {
                @Override
                public void onNewValues(String id, TimestampedValue[] newValues) {
                    if (newValues != null && newValues.length > 0) {
                        String value = newValues[0].getValue().toString();
                        Log.d(TAG, "got new value for expression: " + value);
                        characteristic.setValue(value);
                        bleManager.getBleServer().notifyCharacteristicChanged(device, characteristic, false);
                    }
                }
            });
        } catch(Exception e) {
            Log.e(TAG, "error registering expression", e);
        }
    }
}

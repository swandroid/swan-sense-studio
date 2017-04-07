package interdroid.swan.crossdevice.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Looper;
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
    private static int curExpressionId = 0;
    private static double curVal = 0;

    private BLEManager bleManager;

    private BluetoothDevice device;
    private BluetoothGattCharacteristic characteristic;
    private String expressionId;
    private int requestId; // needed for pull method
    private int offset; // needed for pull method

    private static synchronized int nextExpressionId() {
        return curExpressionId++;
    }

    // constructor for push mode
    public BLEServerWorker(BLEManager bleManager, BluetoothDevice device, BluetoothGattCharacteristic characteristic) {
        this.bleManager = bleManager;
        this.device = device;
        this.characteristic = characteristic;
    }

    // constructor for pull mode
    public BLEServerWorker(BLEManager bleManager, BluetoothDevice device, BluetoothGattCharacteristic characteristic, int requestId, int offset) {
        this(bleManager, device, characteristic);
        this.requestId = requestId;
        this.offset = offset;
    }

    public void start() {
        try {
            final String sensorValuePath = bleManager.getSensorForUuid(characteristic.getUuid());
            String sensorEntity = sensorValuePath.split(":")[0];
            String valuePath = sensorValuePath.split(":")[1];
            SensorValueExpression expression = new SensorValueExpression(Expression.LOCATION_SELF,
                    sensorEntity, valuePath, null, HistoryReductionMode.DEFAULT_MODE.ANY, 1000, null);
            expressionId = nextExpressionId() + "";
            Log.d(TAG, getDeviceName() + ": started server worker for service " + sensorValuePath);

            ExpressionManager.registerValueExpression(bleManager.getContext(), expressionId, expression, new ValueExpressionListener() {
                @Override
                public void onNewValues(String id, TimestampedValue[] newValues) {
                    if (newValues != null && newValues.length > 0) {
                        try {
                            final String value = "" + newValues[0].getValue().toString();
                            characteristic.setValue(value);
                            Log.w(TAG, getDeviceName() + ": sending value for " + sensorValuePath + "(" + expressionId + ") to remote: " + value);

                            if(BLEManager.PUSH_MODE) {
                                if (!bleManager.getBleServer().notifyCharacteristicChanged(device, characteristic, false)) {
                                    Log.w(TAG, getDeviceName() + ": couldn't send notification for new value");
                                }

                                stop();
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        start();
                                    }
                                }, BLEManager.TIME_BETWEEN_REQUESTS);
                            } else {
                                bleManager.getBleServer().sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value.getBytes());
                                stop();
                                bleManager.serverWorkerDone(BLEServerWorker.this);
                            }
                        } catch(Exception e) {
                            Log.e(TAG, getDeviceName() + ": couldn't send new value to remote");
                        }
                    }
                }
            });
        } catch(Exception e) {
            Log.e(TAG, "error registering expression", e);
        }
    }

    public void stop() {
        Log.d(TAG, "stopping");
        ExpressionManager.unregisterExpression(bleManager.getContext(), expressionId);
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getDeviceName() {
        if(device.getName() != null) {
            return device.getName();
        }
        return device.getAddress();
    }
}

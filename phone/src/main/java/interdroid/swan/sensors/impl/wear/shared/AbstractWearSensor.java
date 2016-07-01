package interdroid.swan.sensors.impl.wear.shared;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import interdroid.swancore.shared.SensorConstants;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swan.sensors.impl.wear.shared.data.SensorDataPoint;
import interdroid.swan.sensors.impl.wear.shared.data.SensorNames;
import interdroid.swan.sensors.impl.wear.shared.data.WearSensor;

/**
 * Created by Veaceslav Munteanu on 3/14/16.
 */
public abstract class AbstractWearSensor extends AbstractSwanSensor {

    final String ABSTRACT_SENSOR = "Abstract Sensor";
    protected HashMap<String, Integer> valuePathMappings = new HashMap<>();
    protected String sensor_name = ABSTRACT_SENSOR;

    protected int sensorId = -1;
    ArrayList<String> ids = new ArrayList<>();
    ReentrantLock lock = new ReentrantLock();

    private SensorUpdate updateReceiver = new SensorUpdate();

    private class SensorUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(RemoteSensorManager.UPDATE_MESSAGE)) {
                Bundle extra = intent.getExtras();
                //String who = extra.getString("sensor");
                WearSensor s = (WearSensor) extra.getSerializable(SensorConstants.SENSOR_OBJECT);
                SensorDataPoint d = (SensorDataPoint) extra.getSerializable(SensorConstants.SENSOR_DATA);
                if (s.getName().equals(sensor_name)) {

                    long now = acceptSensorReading();
                    if(now < 0)
                        return;
                    Log.d(TAG, "Got update+++++++++++++++++++++" + s.getName());
                    float[] dataz = d.getValues();
                    for (Map.Entry<String, Integer> data : valuePathMappings.entrySet()) {
                        for (String id : ids) {
                            putValueTrimSize(data.getKey(), id, System.currentTimeMillis(), dataz[data.getValue()]);
                        }
                    }
                }

            }

        }
    }

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {


        if (valuePathMappings.isEmpty()) {
            Log.w(ABSTRACT_SENSOR, "You need to set the path mappings");
            return;
        }

        if (sensorId == -1) {
            Log.w(ABSTRACT_SENSOR, "You need to specify the sensor id");
            return;
        }

        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        sensor_name = SensorNames.getInstance().getName(sensorId);

        configuration.putInt(SensorConstants.SENSOR_ID, sensorId);
        configuration.putInt(SensorConstants.ACCURACY, getSensorDelay());


        lock.lock();
        if (ids.isEmpty()) {
            RemoteSensorManager.getInstance(this).startMeasurement(configuration);
        }
        ids.add(id);
        lock.unlock();

        Log.d(TAG, "Configuration " + configuration.toString());
        registerReceiver(updateReceiver, new IntentFilter(RemoteSensorManager.UPDATE_MESSAGE));

    }

    @Override
    public void unregister(String id) {
        lock.lock();
        ids.remove(id);

        Bundle configuration = new Bundle();
        configuration.putInt(SensorConstants.SENSOR_ID, sensorId);

        if (ids.isEmpty())
            RemoteSensorManager.getInstance(this).stopMeasurement(configuration);
        lock.unlock();

        unregisterReceiver(updateReceiver);
    }

    @Override
    public void onConnected() {

    }
}

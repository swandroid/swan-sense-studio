package interdroid.swan.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swancore.sensors.AbstractSwanSensorBase;

public class HeartRateSensor extends AbstractSwanSensorBase {
    public static final String TAG = "HeartRateSensor";

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return 1;
        }

    }

    /**
     * Value of ACCURACY must be one of SensorManager.SENSOR_STATUS_ACCURACY_*
     */
    public static final String ACCURACY = "accuracy";

    public static final String HEARTRATE_FIELD = "heart_rate";

    private Sensor heartrateSensor;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_HEART_RATE) {
                currentConfiguration.putInt(ACCURACY, accuracy);
            }
        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                long now = acceptSensorReading();
                if (now >= 0) {
                    Log.d(TAG, "onSensorChanged: " + now + " val " + event.values[0]);
                    putValueTrimSize(HEARTRATE_FIELD, null, now, event.values[0]);
                }
            }
        }
    };

    @Override
    public String[] getValuePaths() {
        return new String[]{ HEARTRATE_FIELD };
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putInt(DELAY, normalizeSensorDelay(SensorManager.SENSOR_DELAY_NORMAL));
        DEFAULT_CONFIGURATION.putInt(ACCURACY, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Light Sensor";
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_HEART_RATE);
        if (sensorList.size() > 0) {
            heartrateSensor = sensorList.get(0);
        } else {
            Toast.makeText(getApplicationContext(), "No heartrate Sensor found on device!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No heartrateSensor found on device!");
        }
    }

    @Override
    public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        updateDelay();
    }

    private void updateDelay() {
        sensorManager.unregisterListener(sensorEventListener);

        int delay = getSensorDelay();
        if (delay >= 0) {
            sensorManager.registerListener(sensorEventListener, heartrateSensor, delay);
            Log.d(TAG, "delay set to " + delay);
        }

    }

    @Override
    public final void unregister(String id) {
        updateDelay();
    }

    @Override
    public final void onDestroySensor() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroySensor();
    }

    @Override
    public float getCurrentMilliAmpere() {
        return heartrateSensor.getPower();
    }
}

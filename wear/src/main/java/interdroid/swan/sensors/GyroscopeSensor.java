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

public class GyroscopeSensor extends AbstractSwanSensorBase {

    public static final String TAG = "GyroscopeSensor";

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
//            return R.xml.gyroscope_preferences;
//        }

    }

    /**
     * Value of ACCURACY must be one of SensorManager.SENSOR_STATUS_ACCURACY_*
     */
    public static final String ACCURACY = "accuracy";

    public static final String X_FIELD = "x";
    public static final String Y_FIELD = "y";
    public static final String Z_FIELD = "z";


    private Sensor gyroscope;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                currentConfiguration.putInt(ACCURACY, accuracy);
            }
        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                long now = acceptSensorReading();
                if (now >= 0) {
                    Log.d(TAG, "onSensorChanged: " + now + " val " +
                            event.values[0] + " " + event.values[1] + " " +
                            event.values[2]);
                    for (int i = 0; i < 3; i++) {
                        putValueTrimSize(VALUE_PATHS[i], null, now,
                                event.values[i]);
                    }
                }
            }
        }
    };

    @Override
    public String[] getValuePaths() {
        return new String[]{X_FIELD, Y_FIELD, Z_FIELD};
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putInt(DELAY, normalizeSensorDelay(SensorManager.SENSOR_DELAY_NORMAL));
        DEFAULT_CONFIGURATION.putInt(ACCURACY, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Gyroscope Sensor";
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        if (sensorList.size() > 0) {
            gyroscope = sensorList.get(0);
        } else {
            Toast.makeText(getApplicationContext(), "No gyroscope found on device", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No gyroscope found on device!");
        }
    }

    @Override
    public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        Log.d(TAG, "Starting gyroscope++++");
        updateDelay();
    }

    private void updateDelay() {
        sensorManager.unregisterListener(sensorEventListener);

        int delay = getSensorDelay();
        if (delay >= 0) {
            sensorManager.registerListener(sensorEventListener, gyroscope, delay);
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
        return gyroscope.getPower();
    }
}

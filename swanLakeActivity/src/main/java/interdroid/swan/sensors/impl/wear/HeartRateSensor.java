package interdroid.swan.sensors.impl.wear;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import interdroid.swan.R;
import interdroid.swan.sensordashboard.shared.SensorConstants;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.impl.wear.shared.AbstractWearSensor;

/**
 * Created by veaceslav on 2/22/16.
 */
public class HeartRateSensor extends AbstractWearSensor {

    public static final String TAG = "HeartRateSensor";

    public static final String VALUE_PATH = "heart_rate";

    /**
     * Value of ACCURACY must be one of SensorManager.SENSOR_DELAY_*
     */

    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.wear_heartrate_preferences;
        }

    }

    @Override
    public void initDefaultConfiguration(Bundle defaults) {
        Log.d("Heart Rate Sensor", "Init default configuration");
        defaults.putInt(SensorConstants.ACCURACY,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        SENSOR_NAME = "Wear Heart Rate Sensor";
        sensorId = Sensor.TYPE_HEART_RATE;
        valuePathMappings.put(VALUE_PATH, 0);
        Log.d("Heart RATE", "Register++++++++++++++++++++++++++++++++");

        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
    }


    @Override
    public String[] getValuePaths() {
        return new String[]{"heart_rate"};
    }

    ;


}

package interdroid.swan.sensors.impl.wear;

import android.hardware.Sensor;
import android.os.Bundle;

import interdroid.swan.R;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.impl.wear.shared.AbstractWearSensor;

/**
 * Created by Veaceslav Munteanu on 14-March-16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class WearStepCounterSensor extends AbstractWearSensor {
    public static final String TAG = "Wear Step Counter Sensor";

    /**
     * Step counter
     */
    public static final String STEP_COUNTER = "step_counter";

    public static class ConfigurationActivity extends AbstractConfigurationActivity {
        @Override
        public final int getPreferencesXML() {
            return R.xml.stepcounter_preferences;
        }
    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        SENSOR_NAME = "Wear Step Counter Sensor";
        sensorId = Sensor.TYPE_STEP_COUNTER;
        valuePathMappings.put(STEP_COUNTER, 0);
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{STEP_COUNTER};
    }
}

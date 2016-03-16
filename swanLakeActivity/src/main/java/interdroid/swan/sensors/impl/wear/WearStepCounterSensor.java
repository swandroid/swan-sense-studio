package interdroid.swan.sensors.impl.wear;

import android.os.Bundle;

import java.io.IOException;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.impl.wear.shared.AbstractWearSensor;

/**
 * Created by Veaceslav Munteanu on 14-March-16.
 * @email veaceslav.munteanu90@gmail.com
 */
public class WearStepCounterSensor extends AbstractWearSensor{
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
    public void register(String id, String valuePath, Bundle configuration) throws IOException {
        SENSOR_NAME = "Wear Step Counter Sensor";
        sensor_name = "Step Counter";
        valuePathMappings.put(STEP_COUNTER, 0);
        super.register(id, valuePath, configuration);
    }
    @Override
    public String[] getValuePaths() {
        return new String[] { STEP_COUNTER };
    }
}

package interdroid.swan.sensors.impl.wear;

/**
 * Created by slavik on 7/7/16.
 */

import android.os.Bundle;

import interdroid.swan.R;
import interdroid.swan.sensors.impl.wear.shared.AbstractWearSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swancore.shared.SensorConstants;


/**
 * Created by Veaceslav Munteanu on 14-March-16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class WearTestSensor extends AbstractWearSensor {

    public static final String ZERO_FIELD = "zero";
    public static final String ONE_FIELD = "one";
    public static final String ALTERNATE_FIELD = "alternate_test";

    public static final String TAG = "TestSensor";

    /**
     * The configuration activity for this sensor.
     *
     * @author Veaceslav Munteanu
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.wear_test_preferences;
        }

    }

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        SENSOR_NAME = "Wear Test Sensor";
        sensorId = SensorConstants.TEST_SENSOR_ID;
        valuePathMappings.put(ZERO_FIELD, 0);
        valuePathMappings.put(ONE_FIELD, 1);
        valuePathMappings.put(ALTERNATE_FIELD, 2);

        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{ZERO_FIELD, ONE_FIELD, ALTERNATE_FIELD};
    }
}


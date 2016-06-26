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
public class WearGravitySensor extends AbstractWearSensor {

    public static final String X_FIELD = "x";
    public static final String Y_FIELD = "y";
    public static final String Z_FIELD = "z";

    public static class ConfigurationActivity extends AbstractConfigurationActivity {
        @Override
        public final int getPreferencesXML() {
            return R.xml.gravity_preferences;
        }
    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        SENSOR_NAME = "Wear Gravity Sensor";

        sensorId = Sensor.TYPE_GRAVITY;
        valuePathMappings.put(X_FIELD, 0);
        valuePathMappings.put(Y_FIELD, 1);
        valuePathMappings.put(Z_FIELD, 2);

        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{X_FIELD, Y_FIELD, Z_FIELD};
    }
}

package interdroid.swan.actuator.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import interdroid.swan.actuator.Actuator;
import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * An {@link Actuator} that changes the brightness of the phone screen to the specified value.
 */
public class BrightnessActuator extends Actuator {

    private static final String TAG = BrightnessActuator.class.getSimpleName();

    public static final String ENTITY = "brightness";

    private static final String[] KEYS = new String[]{"value"};

    private static final String[] PATHS = new String[]{"set"};

    private final int brightness;

    private ContentResolver cr;

    /**
     * Create a {@link BrightnessActuator} object.
     *
     * @param context    the context
     * @param brightness the brightness value to set, should be between 0 and 255 inclusive.
     */
    private BrightnessActuator(Context context, int brightness) {
        this.brightness = brightness;
        cr = context.getContentResolver();
    }

    @Override
    public void performAction(Context context, TimestampedValue[] newValues) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(context)) {
            Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
        } else {
            Log.w(TAG, "android.permission.WRITE_SETTINGS permission not granted, please grant the permission in app settings!");
        }
    }

    public static class Factory implements Actuator.Factory {

        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            int brightness = Integer.parseInt(expression.getConfiguration().getString("value"));
            return new BrightnessActuator(context, brightness);
        }
    }

    public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[]{"255"};
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            return ENTITY;
        }
    }
}

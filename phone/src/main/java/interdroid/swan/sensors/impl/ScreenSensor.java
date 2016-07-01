package interdroid.swan.sensors.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;

import interdroid.swan.R;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * A sensor for if the screen is on or off.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public class ScreenSensor extends AbstractSwanSensor {
    public static final String TAG = "ScreenSensor";

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.screen_preferences;
        }

    }

    /**
     * Is screen on field.
     */
    public static final String IS_SCREEN_ON_FIELD = "is_screen_on";

    /**
     * The receiver of screen information.
     */
    private BroadcastReceiver screenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            long now = System.currentTimeMillis();

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                putValueTrimSize(IS_SCREEN_ON_FIELD, null, now, "false");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                putValueTrimSize(IS_SCREEN_ON_FIELD, null, now, "true");
            }
        }

    };

    @Override
    public final String[] getValuePaths() {
        return new String[]{IS_SCREEN_ON_FIELD};
    }

    @Override
    public void initDefaultConfiguration(final Bundle defaults) {
    }

    @Override
    public final void onConnected() {
        SENSOR_NAME = "Screen Sensor";
    }

    @Override
    public final void register(final String id, final String valuePath,
                               final Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        if (registeredConfigurations.size() == 1) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            putValueTrimSize(IS_SCREEN_ON_FIELD, null, System.currentTimeMillis(), pm.isScreenOn());

            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(screenReceiver, filter);
        }
    }

    @Override
    public final void unregister(final String id) {
        if (registeredConfigurations.size() == 0) {
            unregisterReceiver(screenReceiver);
        }
    }

    @Override
    public final void onDestroySensor() {
        unregisterReceiver(screenReceiver);
        super.onDestroySensor();
    }

}

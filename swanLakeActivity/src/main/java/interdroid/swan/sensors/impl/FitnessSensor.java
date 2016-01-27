package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Created by vladimir on 1/27/16.
 */
public class FitnessSensor extends AbstractSwanSensor {

    public static final String TAG = "Fitness";

    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.fitness_preferences;
        }

    }

    /*Value path */
    public static final String AVG_SPEED = "avg_speed";

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void register(final String id, final String valuePath, Bundle configuration) throws IOException {
        new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    long start = System.currentTimeMillis();
                    putValueTrimSize(valuePath, id, start, 10);
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }.start();
    }

    @Override
    public void unregister(String id) {

    }

    @Override
    public String[] getValuePaths() {
        return new String[] { AVG_SPEED };
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Fitness";
        Log.e(TAG, "No fitness sensor found on device!");
    }
}

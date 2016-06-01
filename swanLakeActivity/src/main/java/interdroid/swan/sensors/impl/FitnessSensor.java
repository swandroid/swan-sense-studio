package interdroid.swan.sensors.impl;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.crossdevice.swanplus.FitnessBroadcastReceiver;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Created by vladimir on 1/27/16.
 */
public class FitnessSensor extends AbstractSwanSensor {

    public static final String TAG = "Fitness";
    public static final String ACTION_SEND_FITNESS_DATA = "interdroid.swan.crossdevice.swanplus.ACTION_SEND_FITNESS_DATA";
    public static final String ACTION_REQ_FITNESS_DATA = "interdroid.swan.crossdevice.swanplus.ACTION_REQUEST_FITNESS_DATA";

    /*Value path */
    public static final String AVG_SPEED = "avg_speed";

    private Map<String, FitnessDataPoller> activeThreads = new HashMap<String, FitnessDataPoller>();

    public class FitnessDataPoller extends Thread {

        private Bundle configuration;
        private String valuePath;
        private String id;

        FitnessDataPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;

            BroadcastReceiver fitnessBcastReceiver = new FitnessBroadcastReceiver(this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_SEND_FITNESS_DATA);
            registerReceiver(fitnessBcastReceiver, intentFilter);
        }

        public void updateValues(String value) {
            putValueTrimSize(valuePath, id, System.currentTimeMillis(), value);
        }

        public void run() {
            while (!isInterrupted()) {
                Intent intent = new Intent();
                intent.setAction(ACTION_REQ_FITNESS_DATA);
                sendBroadcast(intent);
                Log.d(TAG, "sent speed req");

                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public static class ConfigurationActivity extends AbstractConfigurationActivity {
        @Override
        public int getPreferencesXML() {
            return R.xml.fitness_preferences;
        }
    }

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void register(final String id, final String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        FitnessDataPoller fdp = new FitnessDataPoller(id, valuePath, configuration);
        activeThreads.put(id, fdp);
        fdp.start();
    }

    @Override
    public void unregister(String id) {
        activeThreads.remove(id).interrupt();
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{AVG_SPEED};
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Fitness";
        Log.e(TAG, "No fitness sensor found on device!");
    }

    @Override
    public void onDestroySensor() {
        for (FitnessDataPoller fdp : activeThreads.values()) {
            fdp.interrupt();
        }
        super.onDestroySensor();
    }
}

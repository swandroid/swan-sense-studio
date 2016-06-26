package interdroid.swan.sensors.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

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
    BroadcastReceiver fitnessBcastReceiver;

    public class FitnessBroadcastReceiver extends BroadcastReceiver {

        FitnessSensor.FitnessDataPoller fitnessDataPoller;

        public FitnessBroadcastReceiver(FitnessSensor.FitnessDataPoller fitnessDataPoller) {
            this.fitnessDataPoller = fitnessDataPoller;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (FitnessSensor.ACTION_SEND_FITNESS_DATA.equals(action)) {
                String avgSpeed = intent.getStringExtra("avg_speed");
                Log.d(TAG, "fitness receiver: " + avgSpeed);
                fitnessDataPoller.updateValues(avgSpeed);
            }
        }
    }

    public class FitnessDataPoller extends Thread {

        private Bundle configuration;
        private String valuePath;
        private String id;

        FitnessDataPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;

            fitnessBcastReceiver = new FitnessBroadcastReceiver(this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_SEND_FITNESS_DATA);
            registerReceiver(fitnessBcastReceiver, intentFilter);
        }

        public void updateValues(String value) {
            Log.d(TAG, "valuePath = " + valuePath);
            Log.d(TAG, "value = " + value);
            putValueTrimSize(valuePath, id, System.currentTimeMillis(), value);
        }

        public void run() {
            while(true) {
                Intent intent = new Intent();
                intent.setAction(ACTION_REQ_FITNESS_DATA);
                sendBroadcast(intent);
                Log.d(TAG, "sent speed req");

                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    break;
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
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        FitnessDataPoller fdp = new FitnessDataPoller(id, valuePath, configuration);
        activeThreads.put(id, fdp);
        fdp.start();

        Log.d(TAG, "registering new FitnessDataPoller; active threads = " + activeThreads.size());
    }

    @Override
    public void unregister(String id) {
        activeThreads.remove(id).interrupt();
        unregisterReceiver(fitnessBcastReceiver);
        Log.d(TAG, "unregistering FitnessDataPoller; active threads = " + activeThreads.size());
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

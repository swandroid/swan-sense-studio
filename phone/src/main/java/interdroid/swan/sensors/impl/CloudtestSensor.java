package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;


public class CloudtestSensor extends AbstractSwanSensor {

    public static final String TAG = "Cloudtest";

    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.cloudtest_preferences;
        }

    }


    /*Configuration */
    public static final String DELAY = "delay";
    public static final int DEFAULT_SAMPLE_INTERVAL = 1000;


    protected static final int HISTORY_SIZE = 10;


    private Map<String, CloudtestPoller> activeThreads = new HashMap<String, CloudtestPoller>();


    @Override
    public String[] getValuePaths() {
        return new String[]{"value"};
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putInt(DELAY, DEFAULT_SAMPLE_INTERVAL);
    }


    @Override
    public void onConnected() {
        SENSOR_NAME = "cloudtest";
        //Log.e(TAG, "No Cloudtest found on device!");
    }

    @Override
    public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        CloudtestPoller cloudtestPoller = new CloudtestPoller(id, valuePath,
                configuration);
        activeThreads.put(id, cloudtestPoller);
        cloudtestPoller.start();
    }

    @Override
    public final void unregister(String id) {
        activeThreads.remove(id).interrupt();
    }

    class CloudtestPoller extends Thread {

        private Bundle configuration;
        private String valuePath;
        private String id;

        int i=0;

        CloudtestPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;
        }

        public void run() {
            while (!isInterrupted()) {
                long start = System.currentTimeMillis();


                    i ^= 1;

                    putValueTrimSize(valuePath, id, start, (double)i);

                try {
                    Thread.sleep(configuration.getInt(DELAY,mDefaultConfiguration.getInt(DELAY)));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

    }

    @Override
    public void onDestroySensor() {
        for (CloudtestPoller cloudtestPoller : activeThreads.values()) {
            cloudtestPoller.interrupt();
        }
        super.onDestroySensor();
    }

    ;

}

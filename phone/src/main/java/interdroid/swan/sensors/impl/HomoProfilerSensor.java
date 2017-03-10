package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

/**
 * Created by Roshan Bharath Das on 12/12/16.
 */

public class HomoProfilerSensor extends AbstractSwanSensor {


    public static final String TAG = "HomoProfiler";
    public static final String VALUE = "value";
    public static final String DELAY = "delay";

    //public static int noOfTimes = 0;
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.homoprofiler_preferences;
        }

    }

    private Map<String, HomoProfilerSensor.HomoProfilerPoller> activeThreads = new HashMap<>();


    @Override
    public String[] getValuePaths() {
        return new String[]{VALUE};
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putLong(DELAY, 1000);
    }


    @Override
    public void onConnected() {
        SENSOR_NAME = "HomoProfiler";
    }

    @Override
    public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        Log.e("Roshan","Registering homo rpofiler");
        HomoProfilerPoller homoProfilerPoller = new HomoProfilerPoller(id, valuePath,
                configuration);
        activeThreads.put(id, homoProfilerPoller);
        homoProfilerPoller.start();
    }

    @Override
    public final void unregister(String id) {
        //Log.e("Roshan","SWAN Phone Communication"+noOfTimes);
        activeThreads.remove(id).interrupt();
        //noOfTimes=0;
    }

    class HomoProfilerPoller extends Thread {


        private Bundle configuration;
        private String valuePath;
        private String id;

        private int delay = 1000;

        Random randomNumber = new Random();
        double randomDouble=0.0;

        HomoProfilerPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;

            if (this.configuration.containsKey(DELAY)) {
                delay = configuration.getInt(DELAY);
            }


        }

        public void run() {
            while (!isInterrupted()) {
                long start = System.currentTimeMillis();

                randomDouble = 0 + (20 - 0) * randomNumber.nextDouble();

                Log.d("Roshan", "Double value = "+randomDouble);

                putValueTrimSize(valuePath, id, start, randomDouble);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Log.d(TAG, e.toString());
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroySensor() {
        for (HomoProfilerPoller profilerPoller : activeThreads.values()) {
            profilerPoller.interrupt();
        }
        super.onDestroySensor();
    }
}

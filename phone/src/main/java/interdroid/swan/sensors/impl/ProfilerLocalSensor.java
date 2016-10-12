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

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

public class ProfilerLocalSensor extends AbstractSwanSensor {

    public static final String TAG = "Profiler";
    public static final String VALUE = "value";
    public static final String CASE = "case";
    public static final String DELAY = "delay";

    public static int noOfTimes = 0;
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.profiler_preferences;
        }

    }

    private Map<String, ProfilerPoller> activeThreads = new HashMap<>();


    @Override
    public String[] getValuePaths() {
        return new String[]{VALUE};
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putInt(CASE, 0);
        DEFAULT_CONFIGURATION.putLong(DELAY, 1000);
    }


    @Override
    public void onConnected() {
        SENSOR_NAME = "Profiler";
    }

    @Override
    public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        ProfilerPoller profilerPoller = new ProfilerPoller(id, valuePath,
                configuration);
        activeThreads.put(id, profilerPoller);
        profilerPoller.start();
    }

    @Override
    public final void unregister(String id) {
        Log.e("Roshan","SWAN Phone Communication"+noOfTimes);
        activeThreads.remove(id).interrupt(); 
        noOfTimes=0;
    }

    class ProfilerPoller extends Thread {
        public static final String SAMPLE_INTERVAL = "sample_interval";

      //  private static final String BASE_URL = "http://gps.buienradar.nl/getrr.php?lat=52.3&lon=4.87";

        private static final String BASE_URL = "https://thingspeak.com/channels/45572/field/3.json/";
        private Bundle configuration;
        private String valuePath;
        private String id;

        private int i = 0;
        private int caseScenario = 0;
        private int delay = 1000;

        ProfilerPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;
        }

        public void run() {
            while (!isInterrupted()) {
                long start = System.currentTimeMillis();

                if (configuration.containsKey(CASE)) {
                    caseScenario = configuration.getInt(CASE);
                }

                if (configuration.containsKey(DELAY)) {
                    delay = configuration.getInt(DELAY);
                }

                noOfTimes++;

                String jsonData = "";
                try {
                    String line;
                    URLConnection conn = new URL(BASE_URL).openConnection();
                    BufferedReader r = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    // String line = r.readLine();

                    while ((line = r.readLine()) != null) {
                        jsonData += line + "\n";
                    }


                    JSONObject jsonObject = new JSONObject(jsonData);
                    Object result = null;
                    int length = 0;

                    length = jsonObject.getJSONArray("feeds").length();
                    result = jsonObject.getJSONArray("feeds").getJSONObject(length - 1).get("field3");


                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (caseScenario == 0) {
                    if (i == 0) {
                        i = 2;
                    } else {
                        i = 0;
                    }
                } else {
                    if (i == 0) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                }
                putValueTrimSize(valuePath, id, start, i);

                try {
                    Thread.sleep(Math.max(0,
                            configuration.getLong(SAMPLE_INTERVAL,
                                    delay)
                                    + start - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    Log.d(TAG, e.toString());
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroySensor() {
        for (ProfilerPoller profilerPoller : activeThreads.values()) {
            profilerPoller.interrupt();
        }
        super.onDestroySensor();
    }
}

package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Created by Roshan Bharath Das on 05/11/15.
 */
public class OVSensor extends AbstractSwanSensor {


    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.ov_preferences;
        }

    }


    public static final String SAMPLE_INTERVAL = "sample_interval";
    public static final long DEFAULT_SAMPLE_INTERVAL = 5 * 60 * 1000;

    public static final String API_BASE_URL = "http://v0.ovapi.nl/";

    public static final String LINE = "line";

    public static final String STOPAREACODE = "stopareacode";

    public static final String VALUE = "value";


    private Map<String, GVBPoller> activeThreads = new HashMap<String, GVBPoller>();


    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putLong(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);

    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {// throws IOException {


        GVBPoller gvbPoller = new GVBPoller(id, valuePath,
                configuration);
        activeThreads.put(id, gvbPoller);
        gvbPoller.start();

    }


    @Override
    public void unregister(String id) {

        activeThreads.remove(id).interrupt();

    }


    class GVBPoller extends Thread {

        private Bundle configuration;
        private String valuePath;
        private String id;

        GVBPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;
        }

        public void run() {
            while (!isInterrupted()) {
                long start = System.currentTimeMillis();

                String url = API_BASE_URL + valuePath + "/" + configuration.get(VALUE);

                try {
                    JSONObject data = JsonReader.readJsonFromUrl(url);

                    putValueTrimSize(valuePath, id, start, data.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    Thread.sleep(DEFAULT_SAMPLE_INTERVAL);
                } catch (InterruptedException e) {

                }
            }
        }


    }


    public static class JsonReader {

        private static String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }
    }


    @Override
    public String[] getValuePaths() {
        return new String[]{LINE, STOPAREACODE};
    }

    @Override
    public void onConnected() {

    }


}




package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swancore.models.RainPrediction;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;


public class RainSensor extends AbstractSwanSensor {

    public static final String TAG = "Rain";

    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.rain_preferences;
        }

    }

    /*Value path */
    public static final String RAIN_PREDICTION = "rain_prediction";


    /*Configuration */
    public static final String SAMPLE_INTERVAL = "sample_interval";
    public static final long DEFAULT_SAMPLE_INTERVAL = 5 * 60 * 1000;   // default update frequency is 5 minutes
    public static final String LATITUDE = "latitude";
    public static final double DEFAULT_LATITUDE = 52.3;
    public static final String LONGITUDE = "longitude";
    public static final double DEFAULT_LONGITUDE = 4.886;

    /* Weather URL */
    private static final String BASE_URL = "http://gpsgadget.buienradar.nl/data/raintext?lat=%s&lon=%s";
    /* Output :
     * 000|16:25
	 * 000|16:30 
	 * 000|16:35 
	 * 000|16:40
	 * ---
	 * ---
	 * 000|18:25
	 */

    private Map<String, RainPoller> activeThreads = new HashMap<String, RainPoller>();

    private volatile int registered = 0;
    private volatile int sensed = 0;

    @Override
    public String[] getValuePaths() {
        return new String[]{RAIN_PREDICTION};
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putLong(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);
        DEFAULT_CONFIGURATION.putDouble(LATITUDE, DEFAULT_LATITUDE);
        DEFAULT_CONFIGURATION.putDouble(LONGITUDE, DEFAULT_LONGITUDE);
    }


    @Override
    public void onConnected() {
        SENSOR_NAME = "Rain";
    }

    @Override
    public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        RainPoller rainPoller = new RainPoller(id, valuePath, configuration);
        activeThreads.put(id, rainPoller);
        rainPoller.start();

        Log.d(getClass().getSimpleName(), "Registered = " + (++registered));
    }

    @Override
    public final void unregister(String id) {
        activeThreads.remove(id).interrupt();
    }

    class RainPoller extends Thread {

        private Bundle configuration;
        private String valuePath;
        private String id;

        RainPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;
        }

        public void run() {
            while (!isInterrupted()) {

                String url = String.format(BASE_URL, configuration.get(LATITUDE),
                        configuration.get(LONGITUDE));
//                Log.d(getClass().getSimpleName(), url);

                RainPrediction rainPrediction = new RainPrediction(Double.valueOf((String) configuration.get(LATITUDE)),
                        Double.valueOf((String) configuration.get(LONGITUDE)));

                String line;
                long startTime = System.currentTimeMillis();
                BufferedReader bufferedReader = null;

                try {
                    URLConnection conn = new URL(url).openConnection();
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    Log.d(getClass().getSimpleName(), "Total elapsed http request/response time in milliseconds: " + elapsedTime);
                    Log.d(getClass().getSimpleName(), "Sensed = " + (++sensed));

                } catch (Exception e) {
                    bufferedReader = retry(url);
                }

                if (bufferedReader == null) {
                    putValueTrimSize(configuration, id, System.currentTimeMillis(), rainPrediction);
                    return;
                }

                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        float value = convertValueToMMPerHr(Integer.parseInt(line.substring(0, 3)));
                        String time = line.substring(4);
                        //Log.d(getClass().getSimpleName(), "new rain value: " + value + " " + time);
                        rainPrediction.addRainValue(time, value);
                    }
                    putValueTrimSize(configuration, id, System.currentTimeMillis(), rainPrediction);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    Thread.sleep(DEFAULT_SAMPLE_INTERVAL);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        private BufferedReader retry(String url) {
            Log.d(getClass().getSimpleName(), "Retry for url = " + url);

            long startTime = System.currentTimeMillis();
            BufferedReader bufferedReader = null;

            try {
                URLConnection conn = new URL(url).openConnection();
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                long elapsedTime = System.currentTimeMillis() - startTime;
                Log.d(getClass().getSimpleName(), "Total elapsed http request/response time in milliseconds: " + elapsedTime);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Log.d(getClass().getSimpleName(), url);
                long elapsedTime = System.currentTimeMillis() - startTime;
                Log.d(getClass().getSimpleName(), "Total elapsed http request/response time in milliseconds: " + elapsedTime);
            } finally {
                Log.d(getClass().getSimpleName(), "Sensed = " + (++sensed));
            }
            return bufferedReader;
        }

        /*
        Based on lat lon coordinates, you can deposit two hours to fetch ahead in text form. 0 dry, 255 is heavy rain.
        Mm / hour = 10 ^ ((value -109) / 32) So 77 = 0.1 mm / hour
         */
        private float convertValueToMMPerHr(int value) {
            if (value == 0)
                return 0;
            return (float) (Math.pow(10, (value - 109) / 32.0));
        }
    }

    @Override
    public void onDestroySensor() {
        for (RainPoller rainPoller : activeThreads.values()) {
            rainPoller.interrupt();
        }
        super.onDestroySensor();
    }
}

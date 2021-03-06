package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

public class LogCatSensor extends AbstractSwanSensor {

    private static final String TAG = "LogCat Sensor";

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.logcat_preferences;
        }

    }

    public static final String LOG_FIELD = "log";

    public static final String LOGCAT_PARAMETERS = "logcat_parameters";

    public static final String DEFAULT_LOGCAT_PARAMETERS = "*:I";

    private Map<String, LogcatPoller> activeThreads = new HashMap<String, LogcatPoller>();

    @Override
    public String[] getValuePaths() {
        return new String[]{LOG_FIELD};
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putString(LOGCAT_PARAMETERS,
                DEFAULT_LOGCAT_PARAMETERS);
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "LogCat Sensor";
    }

    @Override
    public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        Log.d(TAG, "Logcat got registration for: " + id + ", conf: " + configuration.getString(LOGCAT_PARAMETERS));
        LogcatPoller logcatPoller = new LogcatPoller(id, valuePath,
                configuration);
        activeThreads.put(id, logcatPoller);
        logcatPoller.start();
    }

    @Override
    public final void unregister(String id) {
        activeThreads.remove(id).terminate();
    }

    class LogcatPoller extends Thread {

        private String id;
        private Bundle configuration;
        private String valuePath;
        private Process process;

        LogcatPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;
        }

        public void terminate() {
            process.destroy();
        }

        public void run() {
            new Thread() {
                public void run() {
                    String parameters = configuration
                            .getString(LOGCAT_PARAMETERS);
                    if (parameters == null) {
                        parameters = mDefaultConfiguration
                                .getString(LOGCAT_PARAMETERS);
                    }
                    try {
                        process = Runtime
                                .getRuntime()
                                .exec(new String[]{"/system/bin/sh", "-c",
                                        "/system/bin/logcat -s " + parameters});
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("line: " + line);
                            long now = System.currentTimeMillis();
                            putValueTrimSize(valuePath, id, now, line);
                        }
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }

    }

    @Override
    public void onDestroySensor() {
        for (LogcatPoller logcatPoller : activeThreads.values()) {
            logcatPoller.terminate();
        }
        super.onDestroySensor();
    }

    ;

}

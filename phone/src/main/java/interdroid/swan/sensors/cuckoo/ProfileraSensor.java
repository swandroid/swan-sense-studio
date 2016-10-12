package interdroid.swan.sensors.cuckoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import interdroid.swan.R;
import interdroid.swan.cuckoo_sensors.CuckooPoller;
import interdroid.swan.sensors.AbstractCuckooSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

/**
 * Created by Roshan Bharath Das on 07/10/16.
 */
public class ProfileraSensor extends AbstractCuckooSensor {

        public static final String VALUE = "value";
        public static final String TIMESTAMP = "ts";
        public static final String ACTION_NEW_VALUE = "new_value";

        /**
         * The configuration activity for this sensor.
         */
        public static class ConfigurationActivity extends
                AbstractConfigurationActivity {

            @Override
            public final int getPreferencesXML() {
                return R.xml.profiler_preferences;
            }

        }

        @Override
        public CuckooPoller getPoller() {
            return new ProfileraPoller();
        }

        @Override
        public String getGCMSenderId() {
            return "251697980958";
            //throw new RuntimeException("<EMPTY FOR GIT>");
        }

        @Override
        public String getGCMApiKey() {
            return "AIzaSyCJoZbF36XS-2I83oe5ahuoEBRuqcU1u7M";
            //return "AIzaSyCHqnp0RwLhVUkX6MWJBW_5hfbKB93ynQ8";
            //throw new RuntimeException("<EMPTY FOR GIT>");
        }

        @Override
        public void registerReceiver() {
            SENSOR_NAME = "Profiler Sensor";

            IntentFilter filter = new IntentFilter(ACTION_NEW_VALUE);
            filter.addCategory(getPackageName());
            registerReceiver(mReceiver = new BroadcastReceiver() {
                private static final String TAG = "profilerSensorReceiver";

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.hasExtra(VALUE)) {
                        try {
                            int newVal = Integer.valueOf(intent.getExtras().getString(VALUE));
                            Log.d(TAG, newVal + "");
                            long ts = Long.valueOf(intent.getExtras().getString(TIMESTAMP));
                            putValueTrimSize(VALUE, null, ts, newVal);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    //   setResultCode(Activity.RESULT_OK);
                }
            }, filter);
        }

        @Override
        public void initDefaultConfiguration(Bundle defaults) {

        }

        @Override
        public String[] getValuePaths() {
            return new String[]{VALUE};
        }

}
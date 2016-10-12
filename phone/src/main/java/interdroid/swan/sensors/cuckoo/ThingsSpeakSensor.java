package interdroid.swan.sensors.cuckoo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import interdroid.swan.R;
import interdroid.swan.cuckoo_sensors.CuckooPoller;
import interdroid.swan.sensors.AbstractCuckooSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

/**
 * A sensor for expected rain in the Netherlands
 *
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 */
public class ThingsSpeakSensor extends AbstractCuckooSensor {

    /**
     * The configuration activity for this sensor.
     */
    public static class ConfigurationActivity
            extends AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.things_speak_preferences;
        }

    }

    public static final String VALUE = "value";
    public static final String VALUE_PATH = "value_path";
    public static final String TIMESTAMP = "ts";


    @Override
    public final String[] getValuePaths() {
        return new String[]{"a", "b", "c", "d", "e", "f", "g", "i", "j", "k", "n"};
    }

    @Override
    public void initDefaultConfiguration(final Bundle defaults) {
    }

    @Override
    public final ThingsSpeakPoller getPoller() {
        return new ThingsSpeakPoller();
    }

    @Override
    public String getGCMSenderId() {
        return "251697980958";
    }

    @Override
    public String getGCMApiKey() {
        return "AIzaSyCJoZbF36XS-2I83oe5ahuoEBRuqcU1u7M";
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
        filter.addCategory(getPackageName());
        registerReceiver(mReceiver = new BroadcastReceiver() {
            private static final String TAG = "rainSensorReceiver";

            @Override
            public void onReceive(Context context, Intent intent) {
                String vpath = null;
                if (intent.hasExtra(VALUE_PATH)) {
                    vpath = intent.getStringExtra(VALUE_PATH);

                    if (intent.hasExtra(VALUE)) {
                        try {
                            Double newVal = Double.valueOf((String) intent.getExtras().get(VALUE));
                            Log.d(TAG, newVal + "");
                            long ts = Long.valueOf(intent.getExtras().getString(TIMESTAMP));
                            putValueTrimSize(vpath, null, ts, newVal);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }
            }
        }, filter, "com.google.android.c2dm.permission.SEND", null);
    }
}
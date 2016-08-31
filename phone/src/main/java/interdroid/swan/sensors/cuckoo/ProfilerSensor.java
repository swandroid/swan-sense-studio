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

public class ProfilerSensor  extends AbstractCuckooSensor {

    public static final String VALUE = "value";

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
         return new ProfilerPoller();
    }

    @Override
    public String getGCMSenderId() {
        return "314238823080";
        //throw new RuntimeException("<EMPTY FOR GIT>");
    }

    @Override
    public String getGCMApiKey() {
        return "AIzaSyCHqnp0RwLhVUkX6MWJBW_5hfbKB93ynQ8";
        //throw new RuntimeException("<EMPTY FOR GIT>");
    }

    @Override
    public void registerReceiver() {
        IntentFilter filter = new IntentFilter(
                "com.google.android.c2dm.intent.RECEIVE");
        filter.addCategory(getPackageName());
        registerReceiver(mReceiver = new BroadcastReceiver() {
            private static final String TAG = "profilerSensorReceiver";

            @Override
            public void onReceive(Context context, Intent intent) {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                String messageType = gcm.getMessageType(intent);
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                        .equals(messageType)) {
                    Log.d(TAG, "Received update but encountered send error.");
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                        .equals(messageType)) {
                    Log.d(TAG, "Messages were deleted at the server.");
                } else {
                    if (intent.hasExtra(VALUE)) {
                        int newVal = Integer.valueOf(intent.getExtras().getString(VALUE));
                        Log.d(TAG, newVal+"");
                        putValueTrimSize(VALUE, null, System.currentTimeMillis(), newVal);
                    }
                }
                setResultCode(Activity.RESULT_OK);
            }
        }, filter, "com.google.android.c2dm.permission.SEND", null);
    }

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public String[] getValuePaths() {
        return new String[]{ VALUE };
    }
}

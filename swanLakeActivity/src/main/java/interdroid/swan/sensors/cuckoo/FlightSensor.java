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

public class FlightSensor extends AbstractCuckooSensor {

    private static final String SOURCE = "source";
    private static final String DESTINATION = "destination";
    private static final String DATE = "flight_date";
    private static final String MAX_STOPS = "max_stops";
    private static final String MAX_PRICE = "max_price";
    private static final String MAX_CONNECTION_DURATION = "max_connection_duration";
    private static final String FLIGHT_CABIN_TYPE = "flight_cabin_type";
    private static final String PRICE = "price";

    /**
     * The configuration activity for this sensor.
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.flight_preferences;
        }

    }

    @Override
    public CuckooPoller getPoller() {
        return new FlightPoller();
    }

    @Override
    public String getGCMSenderId() {
        throw new RuntimeException("<EMPTY FOR GIT>");
    }

    @Override
    public String getGCMApiKey() {
        throw new RuntimeException("<EMPTY FOR GIT>");
    }

    @Override
    public void registerReceiver() {
        IntentFilter filter = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
        filter.addCategory(getPackageName());

        registerReceiver(mReceiver = new BroadcastReceiver() {
            private static final String TAG = "flightSensorReceiver";

            @Override
            public void onReceive(Context context, Intent intent) {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                String messageType = gcm.getMessageType(intent);
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    Log.d(TAG, "Received update but encountered send error.");
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                    Log.d(TAG, "Messages were deleted at the server.");
                } else {
                    if (intent.hasExtra(PRICE)) {
                        putValueTrimSize(PRICE, null, System.currentTimeMillis(), intent.getExtras().getString(PRICE));
                    }
                    // TODO
                    // Send push notification to the phone or email
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
        return new String[] {PRICE};
    }
}

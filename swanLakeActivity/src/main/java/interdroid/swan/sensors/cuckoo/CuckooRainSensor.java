package interdroid.swan.sensors.cuckoo;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractCuckooSensor;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import interdroid.swan.cuckoo_sensors.CuckooPoller;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.gms.gcm.GoogleCloudMessaging; // link to android library: google-play-services_lib

/**
* A sensor for expected rain in the Netherlands
*
* @author roelof &lt;rkemp@cs.vu.nl&gt;
*
*/
public class CuckooRainSensor extends AbstractCuckooSensor {

	/**
	* The configuration activity for this sensor.
	*/
	public static class ConfigurationActivity
		extends AbstractConfigurationActivity {

		@Override
		public final int getPreferencesXML() {
			return R.xml.cuckoo_rain_preferences;
		}

	}

	/**
	* The lat configuration.
	*/
	public static final String LAT_CONFIG = "lat";

	/**
	* The lon configuration.
	*/
	public static final String LON_CONFIG = "lon";

	/**
	* The window configuration.
	*/
	public static final String WINDOW_CONFIG = "window";

	/**
	* The expected field.
	*/
	public static final String EXPECTED_FIELD = "expected_mm";

	@Override
	public final String[] getValuePaths() {
		return new String[] { EXPECTED_FIELD };
	}

	@Override
	public void initDefaultConfiguration(final Bundle defaults) {
	}


	/**
	* Data Storage Helper Method.
	* @param expected value for expected
	*/
	private void storeReading(int expected) {
		putValueTrimSize(EXPECTED_FIELD, null, System.currentTimeMillis(), expected);
	}

	/**
	* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	* Sensor Specific Implementation
	* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	*/

	@Override
	public final CuckooPoller getPoller() {
		return new CuckooRainPoller();
	}

	@Override
	public String getGCMSenderId() {
		throw new RuntimeException("<EMPTY FOR GIT>");
	}

	@Override
	public String getGCMApiKey() {
		throw new RuntimeException("<EMPTY FOR GIT>");
	}
	
	public void registerReceiver() {
		IntentFilter filter = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
		filter.addCategory(getPackageName());
		registerReceiver(mReceiver = new BroadcastReceiver() {
			private static final String TAG = "rainSensorReceiver";

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
					if (intent.hasExtra(EXPECTED_FIELD)) {
						storeReading(intent.getExtras().getInt("expected"));
					}
				}
				setResultCode(Activity.RESULT_OK);
			}
	}, filter, "com.google.android.c2dm.permission.SEND", null);
	}
}
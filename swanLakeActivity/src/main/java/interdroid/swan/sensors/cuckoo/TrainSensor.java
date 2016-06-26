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
 * A sensor for departure times of trains in the Netherlands
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class TrainSensor extends AbstractCuckooSensor {

	/**
	 * The configuration activity for this sensor.
	 */
	public static class ConfigurationActivity extends
			AbstractConfigurationActivity {

		@Override
		public final int getPreferencesXML() {
			return R.xml.train_preferences;
		}

	}

	/**
	 * The from configuration.
	 */
	public static final String FROM_CONFIG = "from";

	/**
	 * The to configuration.
	 */
	public static final String TO_CONFIG = "to";

	/**
	 * The type configuration.
	 */
	public static final String TYPE_CONFIG = "type";

	/**
	 * The time configuration.
	 */
	public static final String TIME_CONFIG = "time";

	/**
	 * The departure field.
	 */
	public static final String DEPARTURE_FIELD = "departure";

	@Override
	public final String[] getValuePaths() {
		return new String[] { DEPARTURE_FIELD };
	}

	@Override
	public void initDefaultConfiguration(final Bundle defaults) {
		defaults.putString(TYPE_CONFIG, "Intercity");
	}

	/**
	 * Data Storage Helper Method.
	 * 
	 * @param departure
	 *            value for departure
	 */
	private void storeReading(String departure) {
		putValueTrimSize(DEPARTURE_FIELD, null, System.currentTimeMillis(), departure);
	}

	/**
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Sensor Specific Implementation
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	 */

	@Override
	public final CuckooPoller getPoller() {
		return new TrainPoller();
	}

	@Override
	public String getGCMSenderId() {
		throw new java.lang.RuntimeException("<put your gcm project id here>");
	}

	@Override
	public String getGCMApiKey() {
		throw new java.lang.RuntimeException("<put your gcm api key here>");
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter(
				"com.google.android.c2dm.intent.RECEIVE");
		filter.addCategory(getPackageName());
		registerReceiver(mReceiver = new BroadcastReceiver() {
			private static final String TAG = "trainSensorReceiver";

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
					if (intent.hasExtra(DEPARTURE_FIELD)) {
						storeReading(intent.getExtras().getString("departure"));
					}
				}
				setResultCode(Activity.RESULT_OK);
			}
		}, filter, "com.google.android.c2dm.permission.SEND", null);
	}
}
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
 * A sensor for news in the Netherlands
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class NewsSensor extends AbstractCuckooSensor {

	/**
	 * The configuration activity for this sensor.
	 */
	public static class ConfigurationActivity extends
			AbstractConfigurationActivity {

		@Override
		public final int getPreferencesXML() {
			return R.xml.news_preferences;
		}

	}

	/**
	 * The category configuration.
	 */
	public static final String CATEGORY_CONFIG = "category";

	/**
	 * The recent field.
	 */
	public static final String RECENT_FIELD = "recent";

	@Override
	public final String[] getValuePaths() {
		return new String[] { RECENT_FIELD };
	}

	@Override
	public void initDefaultConfiguration(final Bundle defaults) {
		defaults.putString(CATEGORY_CONFIG, "algemeen");
	}

	/**
	 * Data Storage Helper Method.
	 * 
	 * @param recent
	 *            value for recent
	 */
	private void storeReading(String recent) {
		putValueTrimSize(RECENT_FIELD, null, System.currentTimeMillis(), recent);
	}

	/**
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Sensor Specific Implementation
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	 */

	@Override
	public final CuckooPoller getPoller() {
		return new NewsPoller();
	}

	@Override
	public String getGCMSenderId() {
		throw new RuntimeException("EMPTY FOR GIT");
	}

	@Override
	public String getGCMApiKey() {
		throw new RuntimeException("EMPTY FOR GIT");
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter(
				"com.google.android.c2dm.intent.RECEIVE");
		filter.addCategory(getPackageName());
		registerReceiver(mReceiver = new BroadcastReceiver() {
			private static final String TAG = "newsSensorReceiver";

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
					if (intent.hasExtra(RECENT_FIELD)) {
						storeReading(intent.getExtras().getString("recent"));
					}
				}
				setResultCode(Activity.RESULT_OK);
			}
		}, filter, "com.google.android.c2dm.permission.SEND", null);
	}
}
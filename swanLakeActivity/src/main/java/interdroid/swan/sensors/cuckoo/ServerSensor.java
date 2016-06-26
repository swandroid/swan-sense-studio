package interdroid.swan.sensors.cuckoo;

import interdroid.swan.R;
import interdroid.swan.cuckoo_sensors.CuckooPoller;
import interdroid.swan.sensors.AbstractCuckooSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * A sensor for the return http status of a web server
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class ServerSensor extends AbstractCuckooSensor {

	/**
	 * The configuration activity for this sensor.
	 */
	public static class ConfigurationActivity extends
			AbstractConfigurationActivity {

		@Override
		public final int getPreferencesXML() {
			return R.xml.cuckoo_server_preferences;
		}

	}

	/**
	 * The url configuration.
	 */
	public static final String URL_CONFIG = "url";

	/**
	 * The timeout configuration.
	 */
	public static final String TIMEOUT_CONFIG = "timeout";

	/**
	 * The http_status field.
	 */
	public static final String HTTP_STATUS_FIELD = "http_status";

	@Override
	public final String[] getValuePaths() {
		return new String[] { HTTP_STATUS_FIELD };
	}

	@Override
	public void initDefaultConfiguration(final Bundle defaults) {
		defaults.putLong(TIMEOUT_CONFIG, 1000);
	}

	/**
	 * Data Storage Helper Method.
	 * 
	 * @param http_status
	 *            value for http_status
	 */
	private void storeReading(String http_status) {
		putValueTrimSize(HTTP_STATUS_FIELD, null, System.currentTimeMillis(), http_status);
	}

	/**
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Sensor Specific Implementation
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	 */

	@Override
	public final CuckooPoller getPoller() {
		return new ServerPoller();
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

			private static final String TAG = "ServerSensorReceiver";

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
					storeReading(/*(Long) makeTyped(*/intent
							.getStringExtra(HTTP_STATUS_FIELD));
				}
				setResultCode(Activity.RESULT_OK);
			}

		}, filter, "com.google.android.c2dm.permission.SEND", null);
	}

}
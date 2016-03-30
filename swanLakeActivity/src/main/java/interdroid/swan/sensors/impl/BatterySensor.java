package interdroid.swan.sensors.impl;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

/**
 * A sensor for battery temperature, level and voltage.
 * 
 * @author nick &lt;palmer@cs.vu.nl&gt;
 * 
 */
public class BatterySensor extends AbstractSwanSensor {

	public static final String TAG = "BatterySensor";
		
	/**
	 * The configuration activity for this sensor.
	 * 
	 * @author nick &lt;palmer@cs.vu.nl&gt;
	 * 
	 */
	public static class ConfigurationActivity extends
			AbstractConfigurationActivity {
		
		@Override
		public final int getPreferencesXML() {
			return R.xml.battery_preferences;
		}
	}
	
	/**
	 * The level field.
	 */
	public static final String LEVEL_FIELD = "level";
	/**
	 * The voltage field.
	 */
	public static final String VOLTAGE_FIELD = "voltage";
	/**
	 * The temperature field.
	 */
	public static final String TEMPERATURE_FIELD = "temperature";
	/**
	 * The plugged field
	 */
	public static final String PLUGGED_FIELD = "plugged";
	
	/**
	 * The pluggedText field
	 */
	public static final String STATUS_TEXT_FIELD = "status_text";

	/**
	 * The receiver for battery events.
	 */
	private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				long now = System.currentTimeMillis();
				Log.d(TAG, "New level: " + intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));
				
				putValueTrimSize(LEVEL_FIELD, null, now, intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));
				putValueTrimSize(TEMPERATURE_FIELD, null, now, intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0));
				putValueTrimSize(VOLTAGE_FIELD, null, now, intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0));
				putValueTrimSize(PLUGGED_FIELD, null, now, intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));
				putValueTrimSize(STATUS_TEXT_FIELD, null, now, intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));
			}
		}
	};

	@Override
	public final String[] getValuePaths() {
		return new String[] { TEMPERATURE_FIELD, LEVEL_FIELD, VOLTAGE_FIELD,
				PLUGGED_FIELD, STATUS_TEXT_FIELD };
	}

	@Override
	public void initDefaultConfiguration(final Bundle defaults) {
	}

	@Override
	public void onConnected() {
		SENSOR_NAME = "Battery Sensor";	
	}

	@Override
	public final void register(final String id, final String valuePath,
			final Bundle configuration, final Bundle httpConfiguration) {
		super.register(id,valuePath,configuration,httpConfiguration);

		if (registeredConfigurations.size() == 1) {
			registerReceiver(batteryReceiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
		}
	}

	@Override
	public final void unregister(final String id) {
		if (registeredConfigurations.size() == 0) {
			unregisterReceiver(batteryReceiver);
		}
	}

	@Override
	public final void onDestroySensor() {		
		if (registeredConfigurations.size() > 0) {
			unregisterReceiver(batteryReceiver);
		}
		super.onDestroySensor();
	}
}

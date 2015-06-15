package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;


public class JsonSensor extends AbstractSwanSensor {
	
	public static final String TAG = "Json";
	
	public static class ConfigurationActivity extends
			AbstractConfigurationActivity {

		@Override
		public int getPreferencesXML() {
			return R.xml.json_preferences;
		}

	}

	/*Value path */
	public static final String EXPECTED_MM = "expected_mm";	
	
	
	/*Configuration */
	public static final String SAMPLE_INTERVAL = "sample_interval";
	public static final long DEFAULT_SAMPLE_INTERVAL = 5 * 60 * 1000;
	
	protected static final int HISTORY_SIZE = 10;


	private Map<String, JsonPoller> activeThreads = new HashMap<String, JsonPoller>();


	@Override
	public String[] getValuePaths() {
		return new String[] { EXPECTED_MM };
	}

	@Override
	public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
		DEFAULT_CONFIGURATION.putLong(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);
	}


	@Override
	public void onConnected() {
		 SENSOR_NAME = "Rain";
		 Log.e(TAG, "No rain sensor found on device!");	
	}

	@Override
	public final void register(String id, String valuePath, Bundle configuration) {
		JsonPoller jsonPoller = new JsonPoller(id, valuePath,
				configuration);
		activeThreads.put(id, jsonPoller);
		jsonPoller.start();
	}

	@Override
	public final void unregister(String id) {
		activeThreads.remove(id).interrupt();
	}

	class JsonPoller extends Thread {

		private Bundle configuration;
		private String valuePath;
		private String id;

		JsonPoller(String id, String valuePath, Bundle configuration) {
			this.id = id;
			this.configuration = configuration;
			this.valuePath = valuePath;
		}

		public void run() {
			while (!isInterrupted()) {
				long start = System.currentTimeMillis();

				//TODO: download the json

				//TODO: this is where optimization can be achieved by downloading once and share multiple values in one go
				//This can maybe be achieved by giving a message to the threads that the data is available


				try {
					Thread.sleep(Math.max(
							0,
							configuration.getLong(SAMPLE_INTERVAL,
									mDefaultConfiguration
											.getLong(SAMPLE_INTERVAL))
									+ start - System.currentTimeMillis()));
				} catch (InterruptedException e) {
				}
				putValueTrimSize(valuePath, id, start, new Integer(15));
			}
		}
		
	}

	@Override
	public void onDestroySensor() {
		for (JsonPoller jsonPoller : activeThreads.values()) {
			jsonPoller.interrupt();
		}
		super.onDestroySensor();
	};

}

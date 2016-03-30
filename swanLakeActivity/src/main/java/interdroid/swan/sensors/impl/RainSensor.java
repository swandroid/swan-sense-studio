package interdroid.swan.sensors.impl;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;


public class RainSensor extends AbstractSwanSensor {
	
	public static final String TAG = "Rain";
	
	public static class ConfigurationActivity extends
			AbstractConfigurationActivity {

		@Override
		public int getPreferencesXML() {
			return R.xml.rain_preferences;
		}

	}

	/*Value path */
	public static final String EXPECTED_MM = "expected_mm";	
	
	
	/*Configuration */
	public static final String SAMPLE_INTERVAL = "sample_interval";
	public static final long DEFAULT_SAMPLE_INTERVAL = 5 * 60 * 1000;
	public static final String LATITUDE = "latitude";
	public static final double DEFAULT_LATITUDE = 52.3;
	public static final String LONGITUDE = "longitude";
	public static final double DEFAULT_LONGITUDE = 4.886;
	
	/* Weather URL */
	private static final String BASE_URL = "http://gps.buienradar.nl/getrr.php?lat=%s&lon=%s";
	/* Output : 
	 * 000|16:25 
	 * 000|16:30 
	 * 000|16:35 
	 * 000|16:40
	 * ---
	 * ---
	 * 000|18:25
	 */
	
	
	protected static final int HISTORY_SIZE = 10;


	private Map<String, RainPoller> activeThreads = new HashMap<String, RainPoller>();


	@Override
	public String[] getValuePaths() {
		return new String[] { EXPECTED_MM };
	}

	@Override
	public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
		DEFAULT_CONFIGURATION.putLong(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);
		DEFAULT_CONFIGURATION.putDouble(LATITUDE, DEFAULT_LATITUDE);
		DEFAULT_CONFIGURATION.putDouble(LONGITUDE, DEFAULT_LONGITUDE);
	}


	@Override
	public void onConnected() {
		 SENSOR_NAME = "Rain";
		 Log.e(TAG, "No rain sensor found on device!");	
	}

	@Override
	public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration) {
		super.register(id,valuePath,configuration,httpConfiguration);

		RainPoller rainPoller = new RainPoller(id, valuePath,
				configuration);
		activeThreads.put(id, rainPoller);
		rainPoller.start();
	}

	@Override
	public final void unregister(String id) {
		activeThreads.remove(id).interrupt();
	}

	class RainPoller extends Thread {

		private Bundle configuration;
		private String valuePath;
		private String id;

		RainPoller(String id, String valuePath, Bundle configuration) {
			this.id = id;
			this.configuration = configuration;
			this.valuePath = valuePath;
		}

		public void run() {
			while (!isInterrupted()) {
				long start = System.currentTimeMillis();
				
				String url = String.format(BASE_URL, configuration.get(LATITUDE),
						configuration.get(LONGITUDE));
				
				
				try {
					URLConnection conn = new URL(url).openConnection();
					BufferedReader r = new BufferedReader(new InputStreamReader(
							conn.getInputStream()));
					String line = r.readLine();
					Log.e(TAG,"Rain Sensor Value: "+Integer.parseInt(line.substring(0, 3)));
					float value = convertValueToMMPerHr(Integer.parseInt(line.substring(0, 3)));
					putValueTrimSize(valuePath, id, start, value);
					
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				

				try {
					Thread.sleep(Math.max(
							0,
							configuration.getLong(SAMPLE_INTERVAL,
									mDefaultConfiguration
											.getLong(SAMPLE_INTERVAL))
									+ start - System.currentTimeMillis()));
				} catch (InterruptedException e) {
				}
			}
		}

		private float convertValueToMMPerHr(int value) {
			float result = (float) (Math.pow(10, (value - 109) / 32.0));
			return result;
		}
		
	}

	@Override
	public void onDestroySensor() {
		for (RainPoller rainPoller : activeThreads.values()) {
			rainPoller.interrupt();
		}
		super.onDestroySensor();
	};

}

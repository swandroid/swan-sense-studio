package interdroid.swan.sensors.cuckoo;

import interdroid.swan.cuckoo_sensors.CuckooPoller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * A sensor for expected rain in the Netherlands
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class CuckooRainPoller implements CuckooPoller {

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

	private static final String BASE_URL = "http://gps.buienradar.nl/getrr.php?lat=%s&lon=%s";
	private static final int SAMPLE_LENGTH = 5;

	@Override
	public Map<String, Object> poll(String valuePath,
			Map<String, Object> configuration) {
		Map<String, Object> result = new HashMap<>();
		String url = String.format(BASE_URL, configuration.get(LAT_CONFIG),
				configuration.get(LON_CONFIG));
		String window = (String) configuration.get(WINDOW_CONFIG);
		int minutes = window == null ? 0 : Integer.parseInt(window.split(":")[1]);
		int hours = window == null ? 0 : Integer.parseInt(window.split(":")[0]);
		int nrSamples = hours * 12 + minutes / SAMPLE_LENGTH;
		float fractionOfLastSample = minutes % SAMPLE_LENGTH
				/ (float) SAMPLE_LENGTH;
		float mm = 0;
		BufferedReader r = null;

		try {
			URLConnection conn = new URL(url).openConnection();
			r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			for (int i = 0; i < nrSamples; i++) {
				String line = r.readLine();
				if (line == null) {
					break;
				} else {
					Date date = new Date();
					date.setHours(Integer.parseInt(line.substring(4, 6)));
					date.setMinutes(Integer.parseInt(line.substring(7, 9)));
					date.setSeconds(0);
					if (date.after(new Date())) {
						mm += convertValueToMM(
								Integer.parseInt(line.substring(0, 3)), 5);
					} else {
						nrSamples++;
					}
				}
			}
			String line = r.readLine();
			if (line != null) {
				Date date = new Date();
				date.setHours(Integer.parseInt(line.substring(4, 6)));
				date.setMinutes(Integer.parseInt(line.substring(7, 9)));
				date.setSeconds(0);
				if (date.after(new Date())) {
					mm += (fractionOfLastSample * convertValueToMM(
							Integer.parseInt(line.substring(0, 3)), 5));
				}
			}
		} catch (Exception e) {
			// ignore
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		result.put(EXPECTED_FIELD, mm);
		return result;
	}

	private float convertValueToMM(int value, int minutes) {
		return (float) ((Math.pow(10, (value - 109) / 32.0) / 60.0) * minutes);
	}

	@Override
	public long getInterval(Map<String, Object> configuration, boolean remote) {
		if (remote) {
			return 60000;
		} else {
			return 5 * 60000;
		}
	}
}
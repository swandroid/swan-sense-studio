package interdroid.swan.sensors.cuckoo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.cuckoo_sensors.CuckooPoller;

/**
 * A sensor for expected rain in the Netherlands
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class ThingsSpeakPoller implements CuckooPoller {

	/**
	 * The expected field.
	 */
	public static final String VALUE = "value";
	public static final String VALUE_PATH = "value_path";
	public static final String DELAY = "delay";

	private static final String BASE_URL = "http://fs0.das5.cs.vu.nl:3000/channels/%s/field/1.json";

	@Override
	public Map<String, Object> poll(String valuePath,
			Map<String, Object> configuration) {
		Map<String, Object> result = new HashMap<>();

		int ascii = (int)valuePath.charAt(0);
		int channel_id = ascii - 91;
		if(valuePath.contentEquals("n")){
			channel_id = 13;
		}

		String url = String.format(BASE_URL, String.valueOf(channel_id));

		long now = System.currentTimeMillis();

		String jsonData ="";

		try {
			String line;
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");

			BufferedReader r = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			while ((line = r.readLine()) != null) {
				jsonData += line + "\n";
			}


			System.out.println(jsonData);

			try {

				JSONObject jsonObject = new JSONObject(jsonData);

				double data;
				int length;


				length = jsonObject.getJSONArray("feeds").length();
				data = Double.valueOf((String) jsonObject.getJSONArray("feeds").getJSONObject(length-1).get("field1"));


				System.out.println("dataaaaa "+data+ "length"+length +"valuepath"+valuePath);

				result.put(VALUE, data);


			} catch (JSONException e) {
				e.printStackTrace();
			}


		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		return result;
	}

	@Override
	public long getInterval(Map<String, Object> configuration, boolean remote) {
		if (configuration != null && configuration.containsKey(DELAY))
			return (long) configuration.get(DELAY);
		return 1000;
	}
}
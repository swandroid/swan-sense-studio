package interdroid.swan.sensors.cuckoo;

import android.annotation.SuppressLint;
import interdroid.swan.cuckoo_sensors.CuckooPoller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * A sensor for alarm in the Netherlands
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class AlarmPoller implements CuckooPoller {

	/**
	 * The region configuration.
	 */
	public static final String REGION_CONFIG = "region";

	/**
	 * The type configuration.
	 */
	public static final String TYPE_CONFIG = "type";

	/**
	 * The recent field.
	 */
	public static final String RECENT_FIELD = "recent";

	@SuppressLint("DefaultLocale")
	@Override
	public Map<String, Object> poll(String valuePath,
			Map<String, Object> configuration) {
		Map<String, Object> result = new HashMap<String, Object>();
		// put your polling code here
		String region = (String) configuration.get("region");
		String type = (String) configuration.get("type");
		String suffix;
		if (region.equals("all") && type.equals("")) {
			suffix = "all.rss";
		} else if (region.equals("all")) {
			suffix = "discipline/" + type + ".rss";
		} else if (type.equals("")) {
			suffix = "region/"
					+ region.replace(" ", "-").replace("--", "-").toLowerCase()
					+ ".rss";
		} else {
			suffix = "region/"
					+ region.replace(" ", "-").replace("--", "-").toLowerCase()
					+ "/" + type + ".rss";
		}
		String url = "http://alarmeringen.nl/feeds/" + suffix;
		BufferedReader reader = null;
		try {
			URLConnection connection = new URL(url).openConnection();
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			String reply = "";
			while ((line = reader.readLine()) != null) {
				reply += line;
			}
			reader.close();
			String[] items = reply.split("<item>");
			String recent = items[1];
			recent = recent.substring(
					recent.indexOf("<title>") + "<title>".length(),
					recent.indexOf("</title>"));
			result.put("recent", recent);
		} catch (IOException e) {
			// ignore
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@Override
	public long getInterval(Map<String, Object> configuration, boolean remote) {
		if (remote) {
			return 30000; // twice every minute
		} else {
			return 6 * 60000; // 6 min.
		}
	}
}
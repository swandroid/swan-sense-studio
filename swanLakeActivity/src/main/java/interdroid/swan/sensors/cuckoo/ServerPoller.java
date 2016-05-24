package interdroid.swan.sensors.cuckoo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.cuckoo_sensors.CuckooPoller;


/**
 * A sensor for the return http status of a web server
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class ServerPoller implements CuckooPoller {

	@Override
	public Map<String, Object> poll(String valuePath,
			Map<String, Object> configuration) {
		Map<String, Object> newValues = new HashMap<String, Object>();
		// valuePath can be ignored, we just have one
		long connectionTimeOut = (Long) configuration.get("timeout");
		String serverURL = ((String) configuration.get("url")).replace("'", "");

		try {
			HttpURLConnection http = (HttpURLConnection) new URL(serverURL).openConnection();
			http.setConnectTimeout((int)connectionTimeOut);
			System.out.println("New status code: " + http.getResponseCode());
			newValues.put("http_status", http.getResponseCode());
		}  catch (IOException e) {
			// ignore
		}
		return newValues;
	}

	@Override
	public long getInterval(Map<String, Object> configuration, boolean remote) {
		if (remote) {
			// every second
			return 1000;
		} else {
			// every 10 minutes
			return 10 * 60000;
		}
	}

}
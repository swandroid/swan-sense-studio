package interdroid.swan.sensors.cuckoo;

import interdroid.swan.cuckoo_sensors.CuckooPoller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

/**
 * A sensor for news in the Netherlands
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class NewsPoller implements CuckooPoller {

	/**
	 * The category configuration.
	 */
	public static final String CATEGORY_CONFIG = "category";

	/**
	 * The recent field.
	 */
	public static final String RECENT_FIELD = "recent";

	@Override
	public Map<String, Object> poll(String valuePath,
			Map<String, Object> configuration) {
		Map<String, Object> result = new HashMap<String, Object>();
		String category = (String) configuration.get("category");
		String suffix = category + ".rss";
		String url = "http://nu.nl/feeds/rss/" + suffix;
		HttpParams httpParams = new BasicHttpParams();
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = null;
			String reply = "";
			while ((line = reader.readLine()) != null) {
				reply += line;
			}
			reader.close();
			String[] items = reply.split("<item>");
			String recent = null;
			long timestamp = Long.MIN_VALUE;
			// Mon, 08 Jul 2013 11:39:04 +0200
			SimpleDateFormat df = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
			for (int i = 1; i < items.length; i++) {
				String current = items[1];
				try {
					long currentTime = df.parse(
							current.substring(current.indexOf("<pubDate>")
									+ "<pubDate>".length(),
									current.indexOf("</pubDate>"))).getTime();

					if (currentTime > timestamp) {
						recent = current.substring(current.indexOf("<title>")
								+ "<title>".length(),
								current.indexOf("</title>"));
						timestamp = currentTime;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			result.put("recent", recent);
		} catch (ClientProtocolException e) {
			// ignore
		} catch (IOException e) {
			// ignore
		}
		return result;
	}

	@Override
	public long getInterval(Map<String, Object> configuration, boolean remote) {
		if (remote) {
			return 60000;
		} else {
			return 10000;
		}
	}
}
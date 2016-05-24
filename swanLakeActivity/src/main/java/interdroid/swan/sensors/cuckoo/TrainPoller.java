package interdroid.swan.sensors.cuckoo;

import interdroid.swan.cuckoo_sensors.CuckooPoller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

/**
 * A sensor for departure times of trains in the Netherlands
 * 
 * @author roelof &lt;rkemp@cs.vu.nl&gt;
 * 
 */
public class TrainPoller implements CuckooPoller {

	/**
	 * The from configuration.
	 */
	public static final String FROM_CONFIG = "from";

	/**
	 * The to configuration.
	 */
	public static final String TO_CONFIG = "to";

	/**
	 * The type configuration.
	 */
	public static final String TYPE_CONFIG = "type";

	/**
	 * The time configuration.
	 */
	public static final String TIME_CONFIG = "time";

	/**
	 * The departure field.
	 */
	public static final String DEPARTURE_FIELD = "departure";

	@Override
	public Map<String, Object> poll(String valuePath,
			Map<String, Object> configuration) {
		Map<String, Object> result = new HashMap<String, Object>();
		String fromStation = (String) configuration.get(FROM_CONFIG);
		String toStation = (String) configuration.get(TO_CONFIG);
		String time = (String) configuration.get(TIME_CONFIG);
//		HttpParams httpParams = new BasicHttpParams();
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String date = new SimpleDateFormat("dd-MM").format(new Date(System
				.currentTimeMillis()));
		
		String url = "http://mobiel.ns.nl/planner.action?from=" + fromStation
				+ "&to=" + toStation + "&date=" + date + "&time="
				+ time + "&departure=true&planroute=Journey+advice"; 
		System.out.println(url);
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = null;
			String departureTimeString = "";
			int delay = 0;
			while ((line = reader.readLine()) != null) {
				System.out.println("response+ " + line);
				if (line.contains("<b>D&#160;")) {
					departureTimeString = line.substring("<b>D&#160;".length())
							.replace("</b>", "");
					reader.readLine();
					line = reader.readLine();
					if (line.contains("+&#160;")) {
						Pattern pattern = Pattern.compile(Pattern
								.quote("+&#160;"));
						delay = Integer.parseInt(pattern.split(line)[1]
								.split(" min")[0]);
					}
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.HOUR_OF_DAY,
							Integer.parseInt(departureTimeString.split(":")[0]));
					calendar.set(Calendar.MINUTE,
							Integer.parseInt(departureTimeString.split(":")[1]));
					calendar.set(Calendar.MILLISECOND, 0);
					calendar.roll(Calendar.MINUTE, delay);
					result.put(DEPARTURE_FIELD, calendar.getTimeInMillis());
					break;
				}
			}
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
			return 10000; // 10 sec
		} else {
			return 5 * 60 * 1000; // 5 min
		}
	}
}
package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;


public class RSSSensor extends AbstractSwanSensor {
	
	public static final String TAG = "RSS";
	
	public static class ConfigurationActivity extends
			AbstractConfigurationActivity {

		@Override
		public int getPreferencesXML() {
			return R.xml.rss_preferences;
		}

	}

	/*Value path */
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
    public static final String BOTH = "both";


	/*Configuration */
	public static final String SAMPLE_INTERVAL = "sample_interval";
	public static final int DEFAULT_SAMPLE_INTERVAL = 5 * 60;
	public static final String URL_CONFIGURATION = "url";
	public static final String DEFAULT_URL = "";

	protected static final int HISTORY_SIZE = 10;


	private Map<String, RSSPoller> activeThreads = new HashMap<String, RSSPoller>();

    private long mStart;


	@Override
	public String[] getValuePaths() {
		return new String[] { TITLE, DESCRIPTION, BOTH };
	}

	@Override
	public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
		DEFAULT_CONFIGURATION.putInt(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);
		DEFAULT_CONFIGURATION.putString(URL_CONFIGURATION, DEFAULT_URL);
	}


	@Override
	public void onConnected() {
		 SENSOR_NAME = "RSS";
		 Log.e(TAG, "No RSS sensor found on device!");
	}

	@Override
	public final void register(String id, String valuePath, Bundle configuration) {
        RSSPoller rssPoller = new RSSPoller(id, valuePath,
				configuration);
		activeThreads.put(id, rssPoller);
        rssPoller.start();
	}

	@Override
	public final void unregister(String id) {
		activeThreads.remove(id).interrupt();
	}

	class RSSPoller extends Thread {

		private Bundle configuration;
		private String valuePath;
		private String id;

        RSSPoller(String id, String valuePath, Bundle configuration) {
			this.id = id;
			this.configuration = configuration;
			this.valuePath = valuePath;
		}

		public void run() {
			while (!isInterrupted()) {
                mStart = System.currentTimeMillis();

                //TODO: save parsed xml to cache and check in if one already exists that can now be used
                //TODO: get correct url from configuration
                doGetRequest("https://www.androidpit.com/feed/main.xml", valuePath, id);



                try {
					Thread.sleep(Math.max(
							0,
							configuration.getInt(SAMPLE_INTERVAL,
                                    mDefaultConfiguration
                                            .getInt(SAMPLE_INTERVAL)) * 1000
									+ mStart - System.currentTimeMillis()));
				} catch (InterruptedException e) {
				}
			}
		}

	}

	@Override
	public void onDestroySensor() {
		for (RSSPoller rssPoller : activeThreads.values()) {
            rssPoller.interrupt();
		}
		super.onDestroySensor();
	};

    private class RSSItem {
        public String title;
        public String description;

        public RSSItem(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    private void doGetRequest(String url, final String valuePath, final String id) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        parseRSS(response, valuePath, id);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void parseRSS(String rss, String valuePath, String id) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(rss));
            processList(readRss(xpp), valuePath, id);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
            e.printStackTrace();
        }
	}

    private List<RSSItem> readRss(XmlPullParser parser) throws XmlPullParserException, IOException
             {
        List<RSSItem> items = new ArrayList<>();
         int eventType = parser.getEventType();
         Log.i("TAG", "The event type is: " + eventType);

         while (eventType != XmlPullParser.START_DOCUMENT) {
             eventType = parser.next();
             Log.i("TAG", "The event type is: " + eventType);
         }
         while (eventType != XmlPullParser.START_TAG) {
             eventType = parser.next();
             Log.i("TAG", "The event type is: " + eventType);
         }
        parser.require(XmlPullParser.START_TAG, null, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("channel")) {
                items.addAll(readChannel(parser));
            } else {
                skip(parser);
            }
        }
        return items;
    }

    private List<RSSItem> readChannel(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        List<RSSItem> items = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return items;
    }

    private RSSItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        String title = "";
        String description = "";
        parser.require(XmlPullParser.START_TAG, null, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else {
                skip(parser);
            }
        }
        return new RSSItem(title, description);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    // Processes description tags in the feed.
    private String readDescription(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "description");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "description");
        return title;
    }

    private String readText(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private void processList(List<RSSItem> rssItems, String valuePath, String id) {
        if (valuePath.equals(TITLE)) {
            for (int i = 0; i < rssItems.size(); i++) {
                putValueTrimSize(valuePath, id, mStart, rssItems.get(i).title);
            }
        } else if (valuePath.equals(DESCRIPTION)) {
            for (int i = 0; i < rssItems.size(); i++) {
                putValueTrimSize(valuePath, id, mStart, rssItems.get(i).description);
            }
        } else if (valuePath.equals(BOTH)) {
            for (int i = 0; i < rssItems.size(); i++) {
                putValueTrimSize(valuePath, id, mStart, rssItems.get(i).title + " " + rssItems.get(i).description);
            }
        }
        //Log.d(TAG, "title: " + rssItems.get(0).title);
        //putValueTrimSize(valuePath, id, mStart, rssItems.get(0).title);
        //putValueTrimSize(valuePath, id, mStart, rssItems.get(0).title);
        //TODO: check for value path
    }

}

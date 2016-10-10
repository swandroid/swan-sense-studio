package interdroid.swan.sensors.impl;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.rss_sensor.activities.RssMainActivity;
import interdroid.swan.rss_sensor.cache.RssSensorCache;
import interdroid.swan.rss_sensor.pojos.RssItem;
import interdroid.swan.rss_sensor.pojos.RssRequestComplete;
import interdroid.swan.rss_sensor.pojos.RssSensorRequest;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;


public class RssSensor extends AbstractSwanSensor {
	
	public static final String TAG = "RssSensor";

    private static final int REQUEST_CODE_RSS = 5124;

    private WifiManager.WifiLock mLock;

	public static class ConfigurationActivity extends
			AbstractConfigurationActivity {

		@Override
		public int getPreferencesXML() {
			return R.xml.rss_preferences;
		}

        @Override
        public void startActivity(Intent intent) {
            //Preference preference = findPreference("json_configuration");
            //getSharedPreferences()
            super.startActivityForResult(intent, REQUEST_CODE_RSS);
        }

        @Override
        protected void onActivityResult(int reqCode, int resCode, Intent data) {
            if (reqCode == REQUEST_CODE_RSS) {
                if (resCode == RESULT_OK) {
                    //preferences
                    //Preference preference = findPreference("json_configuration");
                    //preference.getEditor().putString("json_configuration", data.getStringExtra(SelectionActivity.REQUEST_EXTRA_RESULT)).apply();

                    //Full
                    Preference preference = findPreference("rss_configuration_full");
                    Log.d(TAG, "rssPreference: " + data.getStringExtra(RssMainActivity.REQUEST_EXTRA_RESULT_FULL));
                    preference.getEditor().putString("rss_configuration_full", data.getStringExtra(RssMainActivity.REQUEST_EXTRA_RESULT_FULL)).apply();
                }
                // should be getting called now
            }
        }

	}

	/*Value path */
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
    public static final String BOTH = "both";
    public static final String COUNT = "count";
    public static final String PRESENT = "present";


	/*Configuration */
	public static final String SAMPLE_INTERVAL = "sample_interval";
	public static final int DEFAULT_SAMPLE_INTERVAL = 5 * 60 * 1000;
//	public static final String URL_CONFIGURATION = "url";
//	public static final String DEFAULT_URL = "";
    public static final String RSS_CONFIGURATION_FULL = "rss_configuration_full";
    public static final String DEFAULT_RSS_CONFIGURATION_FULL = "";

	protected static final int HISTORY_SIZE = 10;


	private Map<String, RSSPoller> activeThreads = new HashMap<String, RSSPoller>();

    private long mStart;


	@Override
	public String[] getValuePaths() {
		return new String[] { TITLE, DESCRIPTION, BOTH, COUNT, PRESENT };
	}

	@Override
	public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
		DEFAULT_CONFIGURATION.putInt(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);
		DEFAULT_CONFIGURATION.putString(RSS_CONFIGURATION_FULL, DEFAULT_RSS_CONFIGURATION_FULL);
	}


	@Override
	public void onConnected() {
		 SENSOR_NAME = "RSS";
		 Log.e(TAG, "No RSS sensor found on device!");
//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        mLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "LockTag");
//        mLock.acquire();
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
        Log.d(TAG, "unregister sensor");
        RSSPoller rssPoller = activeThreads.remove(id);
        rssPoller.destroyPoller();
        rssPoller.interrupt();
//		activeThreads.remove(id).interrupt();
	}

	class RSSPoller extends Thread implements RssSensorRequest.RssSensorRequestListener {

		private Bundle configuration;
		private String valuePath;
		private String id;

        private RssRequestComplete rssRequestComplete;
        private RssSensorRequest rssSensorRequest;

        private boolean interrupted;

        RSSPoller(String id, String valuePath, Bundle configuration) {
			this.id = id;
			this.configuration = configuration;
			this.valuePath = valuePath;
            interrupted = false;
		}

		public void run() {
			while (!interrupted) {
                mStart = System.currentTimeMillis();

                Log.d(TAG, "rssStart: " + mStart);

                Log.d(TAG, "RssRequestComplete: " + rssRequestComplete);
                if (rssRequestComplete == null) {
                    String rssConfigurationFull = configuration.getString(RSS_CONFIGURATION_FULL);
                    Log.d(TAG, "RssRequestComplete: " + rssConfigurationFull);
                    if (rssConfigurationFull != null && !rssConfigurationFull.isEmpty()) {
                        rssRequestComplete = new Gson().fromJson(rssConfigurationFull, RssRequestComplete.class);
//                        mJsonRequestInfo = new JsonRequestInfo(jsonRequestComplete);
//                        mPathToValue = jsonRequestComplete.pathToValue;
                    } else {
                        //no value to get
                    }
                }

                //TODO: ook zonder cache de responses bewaren (zodat alleen de nieuwste doorgegeven worden), om het verschil te kunnen bekijken
                //TODO: get correct url from configuration, al gedaan denk
                //doGetRequest(rssRequestComplete.url, valuePath, id, rssRequestComplete);
                int sampleRate = configuration.getInt(SAMPLE_INTERVAL,
                        mDefaultConfiguration.getInt(SAMPLE_INTERVAL)) * 1000;
                if (rssSensorRequest == null) {
                    rssSensorRequest = new RssSensorRequest(rssRequestComplete, id, sampleRate, this);
                }

                Log.d(TAG, "Add request to RssSensorCache");
                RssSensorCache.getInstance(getApplicationContext()).addRequestToQueueSynchronized(rssSensorRequest);

                try {
					Thread.sleep(Math.max(
							0,
                            sampleRate)
									+ mStart - System.currentTimeMillis());
				} catch (InterruptedException e) {
                    Log.w(TAG, "interrupted");
                    interrupted = true;
                    break;
				}
			}
            Log.w(TAG, "isInterrupted after: " + isInterrupted());
//            destroyPoller(); //Remove the sensor from the queue if it starts once more
		}

        @Override
        public void onResult(List<RssItem> rssItemList) {
            Log.d(TAG, "result: " + rssItemList);
            processList(rssItemList, valuePath, id, rssRequestComplete);
        }

        public void destroyPoller() {
            interrupted = true;
//            rssSensorRequest.listener = null;
            RssSensorCache.getInstance(getApplicationContext()).removeSensorFromCacheSynchronized(rssSensorRequest);
        }
    }

	@Override
	public void onDestroySensor() {
		for (RSSPoller rssPoller : activeThreads.values()) {
            rssPoller.destroyPoller();
            rssPoller.interrupt();
		}
		super.onDestroySensor();
//        mLock.release();
	};

    private void doGetRequest(String url, final String valuePath, final String id,
                              final RssRequestComplete rssRequestComplete) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        parseRSS(response, valuePath, id, rssRequestComplete);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void parseRSS(String rss, String valuePath, String id, RssRequestComplete rssRequestComplete) {
        StringReader in = new StringReader(rss);
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(in);
            processList(readRss(xpp), valuePath, id, rssRequestComplete);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
	}

    private List<RssItem> readRss(XmlPullParser parser) throws XmlPullParserException, IOException
             {
        List<RssItem> items = new ArrayList<>();
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

    private List<RssItem> readChannel(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        List<RssItem> items = new ArrayList<>();
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

    private RssItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
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
        return new RssItem(title, description);
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

    private void processList(List<RssItem> rssItems, String valuePath, String id,
                             RssRequestComplete rssRequestComplete) {
        if (valuePath.equals(TITLE)) {
            for (int i = 0; i < rssItems.size(); i++) {
                putValueTrimSize(valuePath, id, mStart, rssItems.get(i).title);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (valuePath.equals(DESCRIPTION)) {
            for (int i = 0; i < rssItems.size(); i++) {
                putValueTrimSize(valuePath, id, mStart, rssItems.get(i).description);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (valuePath.equals(BOTH)) {
            for (int i = 0; i < rssItems.size(); i++) {
                putValueTrimSize(valuePath, id, mStart, rssItems.get(i).title + " " + rssItems.get(i).description);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (valuePath.equals(COUNT)) {
            for (int i = 0; i < rssItems.size(); i++) {
                StringBuilder sb = new StringBuilder(rssItems.get(i).title);
                sb.append(" ");
                sb.append(rssItems.get(i).description);
                String[] rssItemStrings = sb.toString().toLowerCase().split(" ");
                String word = rssRequestComplete.word.toLowerCase();
                int count = 0;
                for (int j = 0; j < rssItemStrings.length; j++) {
                    if (rssItemStrings[j].contains(word)) {
                        count ++;
                    }
                }
                putValueTrimSize(valuePath, id, mStart, count);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (valuePath.equals(PRESENT)) {
            for (int i = 0; i < rssItems.size(); i++) {
                StringBuilder sb = new StringBuilder(rssItems.get(i).title);
                sb.append(" ");
                sb.append(rssItems.get(i).description);
                String rssItemString = sb.toString().toLowerCase();
                if (rssItemString.contains(rssRequestComplete.word.toLowerCase())) {
                    putValueTrimSize(valuePath, id, mStart, 1);
                } else {
                    putValueTrimSize(valuePath, id, mStart, 0);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //Log.d(TAG, "title: " + rssItems.get(0).title);
        //putValueTrimSize(valuePath, id, mStart, rssItems.get(0).title);
        //putValueTrimSize(valuePath, id, mStart, rssItems.get(0).title);
        //TODO: check for value path
    }

}

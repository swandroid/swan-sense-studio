package interdroid.swan.rss_sensor.cache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import interdroid.swan.rss_sensor.pojos.RssItem;
import interdroid.swan.rss_sensor.pojos.RssSensorRequest;
import interdroid.swan.rss_sensor.pojos.RssSensorResponse;
import interdroid.swan.rss_sensor.pojos.RssUrlResponse;

/**
 * Created by steven on 05/11/15.
 */
public class RssSensorCache {

    private static final String TAG = RssSensorCache.class.getSimpleName();

    private static final int CACHE_TIME_DIVIDER = 10; //1/CACHE_TIME_DIVIDER = is caching time allowed as new response;

    private static RssSensorCache sInstance;

    private Context mContext;

    private Queue<RssSensorRequest> mRequestQueue;
    private List<RssSensorResponse> mSensorResponseCache;
    private List<RssUrlResponse> mUrlResponseCache;

    private RssSensorCache(Context context) {
        mContext = context;
    }

    public static RssSensorCache getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RssSensorCache(context);
        }
        return sInstance;
    }

    public synchronized void addRequestToQueue(RssSensorRequest rssSensorRequest) {
        if (mRequestQueue == null) {
            mRequestQueue = new LinkedList<>();
        }
        if (mRequestQueue.offer(rssSensorRequest) && mRequestQueue.size() == 1) {
            doRequest(rssSensorRequest);
        }
    }

    private synchronized void removeRequestFromQueue() {
        mRequestQueue.poll();
    }

    private synchronized void checkForNextRequest() {
        RssSensorRequest rssSensorRequest = mRequestQueue.peek();
        if (rssSensorRequest != null) {
            doRequest(rssSensorRequest);
        }
    }

    private void doRequest(RssSensorRequest rssSensorRequest) {
        if (mUrlResponseCache == null) {
            mUrlResponseCache = new ArrayList<>();
        }
        for (int i = 0; i < mUrlResponseCache.size(); i++) {
            if (mUrlResponseCache.get(i).urlId == rssSensorRequest.sensorUrlId) {
                //Update url if necessary
                RssUrlResponse rssUrlResponse = mUrlResponseCache.get(i);
                if (!rssUrlResponse.urlString.equals(rssSensorRequest.sensorUrl)) {
                    rssUrlResponse.urlString = rssSensorRequest.sensorUrl;
                    doGetRequest(rssSensorRequest);
                } else {
                    long timePaseSinceLastResponse = System.currentTimeMillis() - rssUrlResponse.responseTime;
                    if (timePaseSinceLastResponse < rssSensorRequest.sampleRate / CACHE_TIME_DIVIDER) {
                        List<RssItem> rssItemList = updateResponseWithSensorCache(rssSensorRequest, rssUrlResponse.rssItemList);
                        if (rssSensorRequest.listener != null) {
                            rssSensorRequest.listener.onResult(rssItemList);
                        }
                    } else {
                        doGetRequest(rssSensorRequest);
                    }
                }
            }
        }
        //TODO: check if we need to do a get or cached request

    }

    private List<RssItem> updateResponseWithSensorCache(RssSensorRequest rssSensorRequest, List<RssItem> rssItemList) {
        String sensorId = rssSensorRequest.sensorId;
        if (mSensorResponseCache == null) {
            mSensorResponseCache = new ArrayList<>();
        }
        for (int i = 0; i < mSensorResponseCache.size(); i++) {
            if (mSensorResponseCache.get(i).sensorId.equals(sensorId)) {
                removeRssItemsIfExists(mSensorResponseCache.get(i).rssItemList, rssItemList);
                mSensorResponseCache.get(i).rssItemList = rssItemList;
                return rssItemList;
            }
        }
        RssSensorResponse rssSensorResponse = new RssSensorResponse();
        rssSensorResponse.sensorId = sensorId;
        rssSensorResponse.urlId = rssSensorRequest.sensorUrlId;
        rssSensorResponse.urlString = rssSensorRequest.sensorUrl;
        rssSensorResponse.rssItemList = rssItemList;
        mSensorResponseCache.add(rssSensorResponse);
        return rssItemList;
    }

    private void removeRssItemsIfExists(List<RssItem> rssCachedItemList, List<RssItem> rssItemList) {
        for (int i = 0; i < rssCachedItemList.size(); i++) {
            removeRssItemIfExists(rssCachedItemList.get(i), rssItemList);
        }
    }

    private void removeRssItemIfExists(RssItem rssCachedItem, List<RssItem> rssItemList) {
        for (int i = 0; i < rssItemList.size(); i++) {
            if (rssCachedItem.equals(rssItemList.get(i))) {
                rssItemList.remove(i);
                return;
            }
        }
    }

    public void removeSensorFromCache() {

    }

    public void doGetRequest(final RssSensorRequest rssSensorRequest) {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, rssSensorRequest.sensorUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        parseRSS(response, rssSensorRequest, System.currentTimeMillis());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void parseRSS(String rss, RssSensorRequest rssSensorRequest, long currentTime) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(rss));

            List<RssItem> rssItemList = readRss(xpp);
            putResultsInUrlCache(rssSensorRequest, rssItemList, currentTime);
            updateResponseWithSensorCache(rssSensorRequest, rssItemList);
            
            if (rssSensorRequest.listener != null) {
                rssSensorRequest.listener.onResult(rssItemList);
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    private void putResultsInUrlCache(RssSensorRequest rssSensorRequest, List<RssItem> rssItemList, long currentTime) {
        List<RssItem> rssItemListCopy = new ArrayList<>(rssItemList.size());
        for (int i = 0; i < rssItemList.size(); i++) {
            RssItem rssItem = new RssItem(rssItemList.get(i).title, rssItemList.get(i).description);
            rssItemListCopy.add(rssItem);
        }
        for (int i = 0; i < mUrlResponseCache.size(); i++) {
            if (mUrlResponseCache.get(i).urlId == rssSensorRequest.sensorUrlId) {
                RssUrlResponse rssUrlResponse = mUrlResponseCache.get(i);
                rssUrlResponse.responseTime = currentTime;
                rssUrlResponse.urlString = rssSensorRequest.sensorUrl;
                rssUrlResponse.rssItemList = rssItemListCopy;
                return;
            }
        }
    }

    private List<RssItem> readRss(XmlPullParser parser) throws XmlPullParserException, IOException {
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

}

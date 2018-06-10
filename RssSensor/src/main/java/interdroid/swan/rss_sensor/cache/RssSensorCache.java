package interdroid.swan.rss_sensor.cache;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

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
import interdroid.swan.rss_sensor.volley.VolleySingleton;

/**
 * Created by steven on 05/11/15.
 */
public class RssSensorCache {

    private static final String TAG = RssSensorCache.class.getSimpleName();

    private static final float CACHE_TIME_DIVIDER = 1.9f; //1/CACHE_TIME_DIVIDER = is caching time allowed as new response;
    private static final int TEST = 219;

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

    public synchronized void addRequestToQueueSynchronized(final RssSensorRequest rssSensorRequest) {
        if (mRequestQueue == null) {
            mRequestQueue = new LinkedList<>();
        }
        if (mRequestQueue.offer(rssSensorRequest) && mRequestQueue.size() == 1) {
            doRequest(rssSensorRequest);
        }
    }

    private synchronized void removeFromQueueAndCheckForNextRequestSynchronized() {
        mRequestQueue.poll();
        RssSensorRequest rssSensorRequest = mRequestQueue.peek();
        if (rssSensorRequest != null) {
            doRequest(rssSensorRequest);
        }
    }

    /**
     * Do a request
     * First check if the cache can satisfy the request
     * If the cache cannot satisfy the request, do a request to the url
     * @param rssSensorRequest the request information
     */
    private synchronized void doRequest(RssSensorRequest rssSensorRequest) {
        if (mUrlResponseCache == null) { //Create the url response cache if it doesn't exists
            mUrlResponseCache = new ArrayList<>();
        }
        for (int i = 0; i < mUrlResponseCache.size(); i++) {
            if (mUrlResponseCache.get(i).urlId == rssSensorRequest.sensorUrlId) { //There is already an (old) response for this request
                //Update url if necessary
                RssUrlResponse rssUrlResponse = mUrlResponseCache.get(i);
                if (rssUrlResponse.lastUpdate < rssSensorRequest.lastUpdate) {
                    rssUrlResponse.urlString = rssSensorRequest.sensorUrl;
                    rssUrlResponse.lastUpdate = rssSensorRequest.lastUpdate;
                    doGetRequest(rssSensorRequest);
                } else {
                    long timePastSinceLastResponse = System.currentTimeMillis() - rssUrlResponse.responseTime;
                    if (timePastSinceLastResponse < (int) (rssSensorRequest.sampleRate / CACHE_TIME_DIVIDER)) {
                        List<RssItem> rssItemList = updateResponseWithSensorCache(rssSensorRequest, getCopyOfRssItemList(rssUrlResponse.rssItemList));
                        if (rssSensorRequest.listener != null) {
                            rssSensorRequest.listener.onResult(rssItemList);
                        }
                        removeFromQueueAndCheckForNextRequestSynchronized();
                    } else {
                        if (rssSensorRequest.lastUpdate < rssUrlResponse.lastUpdate) {
                            rssSensorRequest.sensorUrl = rssUrlResponse.urlString;
                            rssSensorRequest.lastUpdate = rssUrlResponse.lastUpdate;
                        }
                        doGetRequest(rssSensorRequest);
                    }
                }
                return;
            }
        }

        //No (old) response was found, do a request to the url
        doGetRequest(rssSensorRequest);
    }

    private synchronized List<RssItem> updateResponseWithSensorCache(RssSensorRequest rssSensorRequest, List<RssItem> rssItemList) {
        if (mSensorResponseCache == null) { //Create the sensor response cache if it doesn't exists
            mSensorResponseCache = new ArrayList<>();
        }
        String sensorId = rssSensorRequest.sensorId;
        for (int i = 0; i < mSensorResponseCache.size(); i++) {
            //Check if there is an older response for this sensor
            if (mSensorResponseCache.get(i).sensorId.equals(sensorId)) {
                //Check if the url isn't changed
                if (mSensorResponseCache.get(i).urlId == rssSensorRequest.sensorUrlId) {
                    List<RssItem> rssItemListCopy = getCopyOfRssItemList(rssItemList);
                    //Remove RSS items that where found in a previous request
                    removeRssItemsIfExists(mSensorResponseCache.get(i).rssItemList, rssItemList);
                    //Put the received results in the cache
                    mSensorResponseCache.get(i).rssItemList = rssItemListCopy;
                    //Return the stripped response
                    return rssItemList;
                } else {
                    removeSensorFromCacheSynchronized(rssSensorRequest);
                }
            }
        }

        //There was no old response (or the old response was removed), create a new response
        return addNewSensorResponseToCache(rssSensorRequest, rssItemList);
    }

    private List<RssItem> getCopyOfRssItemList(List<RssItem> rssItemList) {
        List<RssItem> rssItemListCopy = new ArrayList<>(rssItemList.size());
        for (int i = 0; i < rssItemList.size(); i++) {
            rssItemListCopy.add(rssItemList.get(i).clone());
        }
        return rssItemListCopy;
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

    public synchronized void removeSensorFromCacheSynchronized(RssSensorRequest rssSensorRequest) {
        for (int i = 0; i < mSensorResponseCache.size(); i++) {
            if (mSensorResponseCache.get(i).sensorId.equals(rssSensorRequest.sensorId)) {
                mSensorResponseCache.remove(i);
            }
        }
        //Check if there are still sensors left with the same urlId
        for (int i = 0; i < mSensorResponseCache.size(); i++) {
            if (mSensorResponseCache.get(i).urlId == rssSensorRequest.sensorUrlId) {
                return;
            }
        }
        //If there are no sensors left with the same urlId, remove the urlId from the cache
        for (int i = 0; i < mUrlResponseCache.size(); i++) {
            if (mUrlResponseCache.get(i).urlId == rssSensorRequest.sensorUrlId) {
                mUrlResponseCache.remove(i);
                return;
            }
        }
    }

    private synchronized List<RssItem> addNewSensorResponseToCache(RssSensorRequest rssSensorRequest, List<RssItem> rssItemList) {
        RssSensorResponse rssSensorResponse = new RssSensorResponse();
        rssSensorResponse.sensorId = rssSensorRequest.sensorId;
        rssSensorResponse.urlId = rssSensorRequest.sensorUrlId;
        rssSensorResponse.urlString = rssSensorRequest.sensorUrl;
        rssSensorResponse.rssItemList = rssItemList;
        mSensorResponseCache.add(rssSensorResponse);
        return rssItemList;
    }

    private void doGetRequest(final RssSensorRequest rssSensorRequest) {
        VolleySingleton volleySingleton = VolleySingleton.getInstance(mContext.getApplicationContext());
        RequestQueue queue = volleySingleton.getRequestQueue();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, rssSensorRequest.sensorUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseRSS(response, rssSensorRequest, System.currentTimeMillis());
                        removeFromQueueAndCheckForNextRequestSynchronized();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "error response:" + error.getMessage());
                removeFromQueueAndCheckForNextRequestSynchronized();
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
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

    private synchronized void putResultsInUrlCache(RssSensorRequest rssSensorRequest, List<RssItem> rssItemList, long currentTime) {
        List<RssItem> rssItemListCopy = getCopyOfRssItemList(rssItemList);
        for (int i = 0; i < mUrlResponseCache.size(); i++) {
            if (mUrlResponseCache.get(i).urlId == rssSensorRequest.sensorUrlId) {
                RssUrlResponse rssUrlResponse = mUrlResponseCache.get(i);
                rssUrlResponse.responseTime = currentTime;
                rssUrlResponse.urlString = rssSensorRequest.sensorUrl;
                rssUrlResponse.rssItemList = rssItemListCopy;
                return;
            }
        }
        mUrlResponseCache.add(new RssUrlResponse(rssSensorRequest.sensorUrlId, rssSensorRequest.sensorUrl, currentTime,
                rssItemListCopy, rssSensorRequest.lastUpdate));
    }

    private List<RssItem> readRss(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<RssItem> items = new ArrayList<>();
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.START_DOCUMENT) {
            eventType = parser.next();
        }
        while (eventType != XmlPullParser.START_TAG) {
            eventType = parser.next();
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
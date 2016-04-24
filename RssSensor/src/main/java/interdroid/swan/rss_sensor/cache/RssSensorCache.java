package interdroid.swan.rss_sensor.cache;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private ExecutorService mThreadPool;

    private long mTotalResponseSize = 0;
    private long mTotalResponseSizeWithoutCache = 0;
    private int mNumberOfResponses = 0;
    private int mNumberOfVolleyErrors = 0;
    private int mNumberOfEncodingErrors = 0;
    private String mLastVolleyError = "";
    private int mNumberOfRequests1 = 0;
    private int mNumberOfRequests2 = 0;

    private RssSensorCache(Context context) {
        mContext = context;
    }

    public static RssSensorCache getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RssSensorCache(context);
        }
        return sInstance;
    }

    /**
     * Add a request from a RssSensor to the cache Queue
     * @param rssSensorRequest the request information to add to the queue
     */
    public void addRequestToQueue(final RssSensorRequest rssSensorRequest) {
//        addRequestToQueueSynchronized(rssSensorRequest);
//        if (mRequestQueue == null) {
//            mRequestQueue = new LinkedList<>();
//        }
//        if (mRequestQueue.offer(rssSensorRequest) && mRequestQueue.size() == 1) {
//            doRequest(rssSensorRequest);
//        }
        if (mThreadPool == null) {
            mThreadPool = Executors.newCachedThreadPool();
        }
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                addRequestToQueueSynchronized(rssSensorRequest);
            }
        });
    }

    public synchronized void addRequestToQueueSynchronized(final RssSensorRequest rssSensorRequest) {
//        if (mThreadPool == null) {
//            mThreadPool = Executors.newCachedThreadPool();
//        }
//        mThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
                if (rssSensorRequest.sensorUrlId == 1) {
                    mNumberOfRequests1 += 1;
                } else if (rssSensorRequest.sensorUrlId == 2) {
                    mNumberOfRequests2 += 1;
                }
                Log.d(TAG, "addRequestToQueue: " + rssSensorRequest.sensorId);
                if (mRequestQueue == null) {
                    mRequestQueue = new LinkedList<>();
                }
                Log.d(TAG, "mRequestQueue.size(): " + mRequestQueue.size());
                new ExportStringRequestToFile().execute(hasInternetConnection() + "", rssSensorRequest.sensorUrlId + "", mRequestQueue.size() + "");
                if (mRequestQueue.offer(rssSensorRequest) && mRequestQueue.size() == 1) {
                    Log.d(TAG, "mRequestQueue.size() after adding size == 1: " + mRequestQueue.size());
                    doRequest(rssSensorRequest);
                }
                Log.d(TAG, "mRequestQueue.size() after adding: " + mRequestQueue.size());
//            }
//        });
    }

//    /**
//     * Remove the oldest request from the queue
//     */
//    private synchronized void removeOldestRequestFromQueue() {
//        Log.d(TAG, "removeOldestRequestFromQueue: " + mRequestQueue.size());
//
//    }

    /**
     * Check if there are requests left in the queue
     */
    private void removeFromQueueAndCheckForNextRequest() {
//        mRequestQueue.poll();
//        RssSensorRequest rssSensorRequest = mRequestQueue.peek();
//        if (rssSensorRequest != null) {
//            doRequest(rssSensorRequest);
//        }

//        mThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
                removeFromQueueAndCheckForNextRequestSynchronized();
//            }
//        });
    }

    private synchronized void removeFromQueueAndCheckForNextRequestSynchronized() {
//        mThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
                Log.d(TAG, "checkForNextRequest: " + mRequestQueue.size());
                mRequestQueue.poll();
                RssSensorRequest rssSensorRequest = mRequestQueue.peek();
                if (rssSensorRequest != null) {
                    doRequest(rssSensorRequest);
                }
//            }
//        });
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
                    doGetRequest(rssSensorRequest, 187);
                } else {
                    long timePastSinceLastResponse = System.currentTimeMillis() - rssUrlResponse.responseTime;
                    if (timePastSinceLastResponse < rssSensorRequest.sampleRate / CACHE_TIME_DIVIDER) {
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
                        doGetRequest(rssSensorRequest, 201);
                    }
                }
                return;
            }
        }
        //No (old) response was found, do a request to the url
        doGetRequest(rssSensorRequest, 208);
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

    public void removeSensorFromCache(final RssSensorRequest rssSensorRequest) {
        Log.w(TAG, "removeSensorFromCache");
        //Remove the sensor from the sensor cache
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                removeSensorFromCacheSynchronized(rssSensorRequest);
            }
        });
    }

    public synchronized void removeSensorFromCacheSynchronized(RssSensorRequest rssSensorRequest) {
        Log.d(TAG, "removeSensorFromCache");

        new ExportDataToCsv().execute();

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

    private class ExportStringRequestToFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "SwanRssLogs");
                Log.w(TAG, "Create directory");
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Log.e(TAG, "Directory not created");
                    }
                }

                Log.d(TAG, "storagelocation: " + Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
                FileWriter f = new FileWriter(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                                + "/SwanRssLogs/all_responses.txt", true);
                f.write("Request" + System.currentTimeMillis() + "\n");
                f.write("Has internet connection" + params[0] + "\n");
                f.write("Request id: " + params[1] + "\n");
                f.write("Number of requests in queue before adding: " + params[2] + "\n");
                f.close();
            } catch (IOException e) {
                System.out.println(
                        "Failed to create file: " + System.currentTimeMillis() + ".csv");
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }

    private class ExportDoGetRequestToFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "SwanRssLogs");
                Log.w(TAG, "Create directory");
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Log.e(TAG, "Directory not created");
                    }
                }

                Log.d(TAG, "storagelocation: " + Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
                FileWriter f = new FileWriter(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                                + "/SwanRssLogs/all_responses.txt", true);
                f.write("Line where get request is called: " + params[0] + "\n");
                f.close();
            } catch (IOException e) {
                System.out.println(
                        "Failed to create file: " + System.currentTimeMillis() + ".csv");
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }

    private class ExportStringToFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "SwanRssLogs");
                Log.w(TAG, "Create directory");
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Log.e(TAG, "Directory not created");
                    }
                }

                Log.d(TAG, "storagelocation: " + Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
                FileWriter f = new FileWriter(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                                + "/SwanRssLogs/all_responses.txt", true);
                f.write("Response" + System.currentTimeMillis() + "\n");
                f.write(params[0] + "\n");
                f.close();
            } catch (IOException e) {
                System.out.println(
                        "Failed to create file: " + System.currentTimeMillis() + ".csv");
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }

    private class ExportSizeToFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "SwanRssLogs");
                Log.w(TAG, "Create directory");
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Log.e(TAG, "Directory not created");
                    }
                }

                Log.d(TAG, "storagelocation: " + Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
                FileWriter f = new FileWriter(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                                + "/SwanRssLogs/all_response_sizes.txt", true);
                f.write("Response" + System.currentTimeMillis() + "\n");
                f.write(params[0] + "\n");
                f.close();
            } catch (IOException e) {
                System.out.println(
                        "Failed to create file: all_response_sizes.txt");
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }

    public boolean hasInternetConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected())
        {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected())
        {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
        {
            return true;
        }
        return false;
    }


    private class ExportDataToCsv extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "SwanRssLogs");
                Log.w(TAG, "Create directory");
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Log.e(TAG, "Directory not created");
                    }
                }

                Log.d(TAG, "storagelocation: " + Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
                FileWriter f = new FileWriter(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                                + "/SwanRssLogs/" + System.currentTimeMillis() + ".csv", true);
                f.write("timestamp;numberOfResponses;totalResponseSize;totalResponseSizeWithoutCache\n");
                f.write(System.currentTimeMillis() + ";" + mNumberOfResponses + ";" + mTotalResponseSize + ";" + mTotalResponseSizeWithoutCache + "\n");
                f.write("requests1;requests2\n");
                f.write(mNumberOfRequests1 + ";" + mNumberOfRequests2 + "\n");
                f.write("numberOfVolleyErrors;numberOfEncodingErrors\n");
                f.write(mNumberOfVolleyErrors + ";" + mNumberOfEncodingErrors + "\n");
                f.write(mLastVolleyError);
                f.close();
            } catch (IOException e) {
                System.out.println(
                        "Failed to create file: " + System.currentTimeMillis() + ".csv");
                e.printStackTrace();
                return null;
            }

            return null;
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

    private void doGetRequest(final RssSensorRequest rssSensorRequest, int line) {
        new ExportDoGetRequestToFile().execute(line + "");
        RequestQueue queue = Volley.newRequestQueue(mContext);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, rssSensorRequest.sensorUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "response:" + response);
                        int responseSize = 0;
                        try {
                            responseSize = response.getBytes("UTF-8").length;
                        } catch (UnsupportedEncodingException e) {
                            mNumberOfEncodingErrors += 1;
                            e.printStackTrace();
                        }
//                        mTotalResponseSize += responseSize;
//                        mNumberOfResponses += 1;
                        Log.w(TAG, "response size: " + responseSize);
                        Log.w(TAG, "Total response size: " + mTotalResponseSize);
                        Log.w(TAG, "Total with cache repsonse size: " + mTotalResponseSizeWithoutCache);
                        parseRSS(response, rssSensorRequest, System.currentTimeMillis());
                        new ExportStringToFile().execute(response);
                        new ExportSizeToFile().execute(responseSize + "");
                        removeFromQueueAndCheckForNextRequestSynchronized();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mNumberOfVolleyErrors += 1;
                mLastVolleyError = error.getMessage();
                new ExportStringToFile().execute(mLastVolleyError);
                new ExportSizeToFile().execute("response error");
                removeFromQueueAndCheckForNextRequestSynchronized();
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mTotalResponseSize += response.data.length;
                if (!response.notModified) {
                    mTotalResponseSizeWithoutCache += response.data.length;
                } else {
                    mTotalResponseSizeWithoutCache += new PrettyPrintingMap<String, String>(response.headers).toString().getBytes().length;
                }
                mNumberOfResponses += 1;
                return super.parseNetworkResponse(response);
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public class PrettyPrintingMap<K, V> {
        private Map<K, V> map;

        public PrettyPrintingMap(Map<K, V> map) {
            this.map = map;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<K, V> entry = iter.next();
                sb.append(entry.getKey());
                sb.append('=').append('"');
                sb.append(entry.getValue());
                sb.append('"');
                if (iter.hasNext()) {
                    sb.append(',').append(' ');
                }
            }
            return sb.toString();

        }
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
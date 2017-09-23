package interdroid.swan.jsonsensor.cache;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import interdroid.swan.jsonsensor.pojos.JsonItem;
import interdroid.swan.jsonsensor.pojos.JsonRequestInfo;
import interdroid.swan.jsonsensor.pojos.JsonResponse;
import interdroid.swan.jsonsensor.pojos.JsonSensorRequest;
import interdroid.swan.jsonsensor.pojos.Parameter;
import interdroid.swan.jsonsensor.volley.VolleySingleton;

/**
 * Created by steven on 31/01/16.
 */
public class JsonSensorCache {
    //TODO: remove a sensor from response cache if no other sensor uses this, started implementing, see TODO further below
    //TODO: save results to the cache

    private static final String TAG = JsonSensorCache.class.getSimpleName();

    private static final float CACHE_TIME_DIVIDER = 1.9f; //1/CACHE_TIME_DIVIDER = is caching time allowed as new response;
    private static final int TEST = 92;

    private static JsonSensorCache sInstance;

    private Context mContext;

    private Queue<JsonSensorRequest> mRequestQueue;
    private List<JsonResponse> mResponseCache;

    private long mTotalResponseSize = 0;
    private long mTotalResponseSizeWithoutCache = 0;
    private long mLastResponseSize = 0;
    private int mNumberOfResponses = 0;
    private int mNumberOfResponsesFromCache = 0;
    private int mNumberOfVolleyErrors = 0;
    private int mNumberOfEncodingErrors = 0;
    private String mLastVolleyError = "";
    private int mNumberOfRequests = 0;


    public static JsonSensorCache getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new JsonSensorCache(context);
        }
        return sInstance;
    }

    public JsonSensorCache(Context context) {
        mContext = context;
    }

    /**
     * Add a request from a JsonSensor to the cache Queue
     * @param jsonSensorRequest the request information to add to the queue
     */
    public synchronized void addRequestToQueue(JsonSensorRequest jsonSensorRequest) {
        mNumberOfRequests += 1;

        if (mRequestQueue == null) {
            mRequestQueue = new LinkedList<>();
        }
        if (mRequestQueue.offer(jsonSensorRequest) && mRequestQueue.size() == 1) {
            doRequest(jsonSensorRequest);
        }
    }

    /**
     * Check if there are requests left in the queue
     */
    private synchronized void removeFromQueueAndCheckForNextRequest() {
        mTotalResponseSizeWithoutCache += mLastResponseSize;
        mRequestQueue.poll();
        JsonSensorRequest jsonSensorRequest = mRequestQueue.peek();
        if (jsonSensorRequest != null) {
            doRequest(jsonSensorRequest);
        }
    }

    /**
     * Do a request
     * First check if the cache can satisfy the request
     * If the cache cannot satisfy the request, do a request to the url
     * @param jsonSensorRequest the request information
     */
    private synchronized void doRequest(JsonSensorRequest jsonSensorRequest) {
        if (mResponseCache == null) { //Create the response cache if it doesn't exists
            mResponseCache = new ArrayList<>();
        }
        for (int i = 0; i < mResponseCache.size(); i++) {
            if (mResponseCache.get(i).jsonRequestInfo.id == jsonSensorRequest.jsonRequestInfo.id) { //There is already an (old) response for this request
                //Update request if necessary
                JsonResponse jsonResponse = mResponseCache.get(i);
                if (jsonResponse.lastRequestUpdate < jsonSensorRequest.jsonRequestInfo.lastUpdate) { //Check if the request was updated (new url)
                    JsonRequestInfo jsonRequestInfo = jsonSensorRequest.jsonRequestInfo;
                    jsonResponse.jsonRequestInfo = jsonRequestInfo.cloneForCache();
                    jsonResponse.lastRequestUpdate = jsonRequestInfo.lastUpdate;
                    doGetOrPostRequest(jsonSensorRequest);
                } else {
                    long timePastSinceLastResponse = System.currentTimeMillis() - jsonResponse.responseTime;
                    if (timePastSinceLastResponse < jsonSensorRequest.sampleRate / CACHE_TIME_DIVIDER) {
                        JsonItem jsonItem = jsonResponse.jsonItem.clone();
                        if (jsonSensorRequest.listener != null) {
                            jsonSensorRequest.listener.onResult(jsonItem);
                        }
                        mNumberOfResponsesFromCache += 1;
                        removeFromQueueAndCheckForNextRequest();
                    } else {
                        if (jsonSensorRequest.jsonRequestInfo.lastUpdate < jsonResponse.lastRequestUpdate) {
                            jsonSensorRequest.jsonRequestInfo = jsonResponse.jsonRequestInfo.cloneForCache();
                        }
                        doGetOrPostRequest(jsonSensorRequest);
                    }
                }
                return;
            }
        }
        //No (old) response was found, do a request to the url
        doGetOrPostRequest(jsonSensorRequest);
    }

    private void doGetOrPostRequest(JsonSensorRequest jsonSensorRequest) {
        if (jsonSensorRequest.jsonRequestInfo.requestType.equals(JsonRequestInfo.GET)) {
            doGetRequest(jsonSensorRequest);
        } else if (jsonSensorRequest.jsonRequestInfo.requestType.equals(JsonRequestInfo.POST)) {
            doPostRequest(jsonSensorRequest);
        } else {
            removeFromQueueAndCheckForNextRequest();
        }
    }

    private void doGetRequest(final JsonSensorRequest jsonSensorRequest) {
        VolleySingleton volleySingleton = VolleySingleton.getInstance(mContext.getApplicationContext());
        RequestQueue queue = volleySingleton.getRequestQueue();
//        RequestQueue queue = Volley.newRequestQueue(mContext);

        String url = jsonSensorRequest.jsonRequestInfo.url;
        ArrayList<Parameter> parameters = jsonSensorRequest.jsonRequestInfo.parameterList;

        if (parameters != null && parameters.size() > 0) {
            if (!url.contains("?")) {
                url += "?";
            }
            url += parameters.get(0).name + "=" + parameters.get(0).value;
            int size = parameters.size();
            for (int i = 1; i < size; i++) {
                url += "&" + parameters.get(i).name + "=" + parameters.get(i).value;
            }
        }

        url += "&call=" + mNumberOfRequests
                + "&test=" + TEST + "&requestTime=" + System.currentTimeMillis();
        Log.w(TAG, "url: " + url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int responseSize = 0;
                        try {
                            responseSize = response.getBytes("UTF-8").length;
                        } catch (UnsupportedEncodingException e) {
                            mNumberOfEncodingErrors += 1;
                            e.printStackTrace();
                        }

                        JsonItem jsonItem = parseJson(response);
                        JsonResponse jsonResponse =
                                new JsonResponse(jsonSensorRequest.jsonRequestInfo.cloneForCache(),
                                        System.currentTimeMillis(), jsonItem);
                        updateResponseCache(jsonResponse);
                        Log.i(TAG, response);
                        if (jsonSensorRequest.listener != null) {
                            jsonSensorRequest.listener.onResult(jsonItem);
                        }

                        removeFromQueueAndCheckForNextRequest();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "error response:" + error.getMessage());
                mNumberOfVolleyErrors += 1;
                mLastVolleyError = error.getMessage();
//                new ExportStringToFile().execute(mLastVolleyError);
//                new ExportSizeToFile().execute("response error");
                removeFromQueueAndCheckForNextRequest();
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mTotalResponseSize += response.data.length;
                mLastResponseSize = response.data.length;
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

    private void doPostRequest(final JsonSensorRequest jsonSensorRequest) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);

        JsonRequestInfo jsonRequestInfo = jsonSensorRequest.jsonRequestInfo;
        String url = jsonRequestInfo.url;

        final Map<String, String> parameters = new Hashtable<>();
        if (jsonRequestInfo.parameterList != null) {
            int size = jsonRequestInfo.parameterList.size();
            for (int i = 0; i < size; i++) {
                Parameter parameter = jsonRequestInfo.parameterList.get(i);
                parameters.put(parameter.name, parameter.value);
            }
        }

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonItem jsonItem = parseJson(response);
                        if (jsonSensorRequest.listener != null) {
                            jsonSensorRequest.listener.onResult(jsonItem);
                        }
                        removeFromQueueAndCheckForNextRequest();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                removeFromQueueAndCheckForNextRequest();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                return parameters;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private synchronized void updateResponseCache(JsonResponse jsonResponse) {
        if (mResponseCache == null) {
            mResponseCache = new ArrayList<>();
        }

        //TODO: Check if url changed etc.
        boolean foundInCache = false;
        for (int i = 0; i < mResponseCache.size(); i++) {
            if (mResponseCache.get(i).jsonRequestInfo.id == jsonResponse.jsonRequestInfo.id) {
                mResponseCache.remove(i);
                mResponseCache.add(i, jsonResponse);
                foundInCache = true;
            }
        }

        if (!foundInCache) {
            mResponseCache.add(jsonResponse);
        }
    }

    private JsonItem parseJson(String response) {
        JsonItem jsonItem = new JsonItem("root");
        try {
            Log.w(TAG, "object");
            JSONObject jsonObject = new JSONObject(response);
            Iterator<String> strings = jsonObject.keys();
            ArrayList<JsonItem> jsonItems = new ArrayList<>();
            while (strings.hasNext()) {
                String key = strings.next();
                JsonItem jsonItemChild = new JsonItem(key);
                jsonItems.add(jsonItemChild);
                getNextItem(jsonObject, key, jsonItemChild);
            }
            jsonItem.jsonItems = jsonItems;
        } catch (JSONException e) {
            try {
                Log.w(TAG, "array");
                JSONArray jsonArray = new JSONArray(response);
                getNextJsonArray(jsonArray, jsonItem);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        return jsonItem;

        //TODO: move back to JsonActivity
//        if (jsonItem.jsonItems != null) {
//            mAdapterHeader.addJsonItem(jsonItem);
//            mAdapter.setJsonItems(jsonItem.jsonItems);
//        }
    }

    /*private String getWebString(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    private class DownloadFilesTask extends AsyncTask<Void, Void, JsonItem> {
        protected JsonItem doInBackground(Void... urls) {
            String jsonString;
            JsonItem jsonItem = new JsonItem("root");
            try {
                //jsonString = getWebString("http://www.mylaps.com/api/practicelocations?sport=IceSkating&country=&trackstatus=active");
                //jsonString = getWebString("http://api.openweathermap.org/data/2.5/weather?q=Amsterdam,nl/");
                jsonString = getWebString("http://www.trafficlink-online.nl/trafficlinkdata/wegdata/IDPA_ParkingLocation.GeoJSON");

                //jsonString = getWebString("https://www.bitstamp.net/api/ticker/");
                //jsonString = getWebString("http://jsonip.com");
                //jsonString = getWebString("http://www.telize.com/geoip");
                //jsonString = getWebString("https://qrng.anu.edu.au/API/jsonI.php?length=1&type=uint8"); //Documentation: http://qrng.anu.edu.au/API/api-demo.php
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> strings = jsonObject.keys();
                ArrayList<JsonItem> jsonItems = new ArrayList<>();
                while(strings.hasNext()) {
                    String key = strings.next();
                    JsonItem jsonItemChild = new JsonItem(key);
                    jsonItems.add(jsonItemChild);
                    getNextItem(jsonObject, key, jsonItemChild);
                }
                jsonItem.jsonItems = jsonItems;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonItem;
        }

        protected void onPostExecute(JsonItem jsonItem) {
            if (jsonItem.jsonItems != null) {
                mAdapterHeader.addJsonItem(jsonItem);
                mAdapter.setJsonItems(jsonItem.jsonItems);
            }
        }
    }*/

    private void getNextItem(JSONObject jsonObject, String key, JsonItem jsonItem) {
        try {
            JSONObject jsonSubObject = jsonObject.getJSONObject(key);
            JsonItem jsonChildItem = new JsonItem(key);
            jsonItem.jsonItem = jsonChildItem;
            getNextJsonObject(jsonSubObject, jsonChildItem);
        } catch (JSONException e) {
            //Log.e(TAG, "Not an JSONObject");
            //e.printStackTrace();
            try {
                JSONArray jsonSubArray = jsonObject.getJSONArray(key);
                getNextJsonArray(jsonSubArray, jsonItem);
            } catch (JSONException e1) {
                //Log.e(TAG, "Not an JSONArray");
                try {
                    jsonItem.stringItem = jsonObject.getString(key);
                } catch (JSONException e2) {
                    //Log.e(TAG, "Not a String");
                }
            }
        }
    }

    private void getNextJsonObject(JSONObject jsonObject, JsonItem jsonItem) {
        Iterator<String> strings = jsonObject.keys();
        ArrayList<JsonItem> jsonItems = new ArrayList<>();
        while(strings.hasNext()) {
            String childKey = strings.next();
            JsonItem jsonItemChild = new JsonItem(childKey);
            jsonItems.add(jsonItemChild);
            getNextItem(jsonObject, childKey, jsonItemChild);
        }
        jsonItem.jsonItems = jsonItems;
    }

    private void getNextJsonArray(JSONArray jsonArray, JsonItem jsonItem) {
        ArrayList<JsonItem> jsonItems = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Object object = jsonArray.get(i);
                JsonItem jsonItemChild = new JsonItem("" + i);
                if (object instanceof JSONObject) {
                    Log.d(TAG, "object jsonItem: " + jsonItem.key);
                    JsonItem jsonItemSubChild = new JsonItem("" + i);
                    jsonItemChild.jsonItem = jsonItemSubChild;
                    getNextJsonObject((JSONObject) object, jsonItemSubChild);
                } else if (object instanceof JSONArray){
                    Log.d(TAG, "array jsonItem: " + jsonItem.key);
                    getNextJsonArray((JSONArray) object, jsonItemChild);
                } else {
                    jsonItemChild.stringItem = object.toString();
                }
                jsonItems.add(jsonItemChild);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jsonItem.jsonItems = jsonItems;
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

                Log.w(TAG, "storagelocation: " + Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
                FileWriter f = new FileWriter(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                                + "/SwanRssLogs/" + System.currentTimeMillis() + "_" + TEST + ".csv", true);
                f.write("timestamp;numberOfResponses;numberOfResponsesFromCache;totalResponseSize;totalResponseSizeWithoutCache\n");
                f.write(System.currentTimeMillis() + ";" + mNumberOfResponses + ";" + mNumberOfResponsesFromCache + ";" + mTotalResponseSize + ";" + mTotalResponseSizeWithoutCache + "\n");
//                f.write("requests1;requests2\n");
//                f.write(mNumberOfRequests1 + ";" + mNumberOfRequests2 + "\n");
                f.write("numberOfVolleyErrors;numberOfEncodingErrors\n");
                f.write(mNumberOfVolleyErrors + ";" + mNumberOfEncodingErrors + "\n");
                if (mLastVolleyError != null) {
                    f.write(mLastVolleyError);
                }
                f.close();
            } catch (IOException e) {
                System.out.println(
                        "Failed to create file: " + System.currentTimeMillis() + "_" + TEST + ".csv");
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }

    public synchronized void removeSensorFromCacheSynchronized(JsonSensorRequest jsonSensorRequest) {
        Log.w(TAG, "removeSensorFromCache");

        new ExportDataToCsv().execute();

        //TODO find a way to check if there are others using this cache.
        for (int i = 0; i < mResponseCache.size(); i++) {
            if (mResponseCache.get(i).jsonRequestInfo.id == jsonSensorRequest.jsonRequestInfo.id) {
                mResponseCache.remove(i);
            }
        }



    }

//    private synchronized List<RssItem> addNewSensorResponseToCache(RssSensorRequest rssSensorRequest, List<RssItem> rssItemList) {
//        RssSensorResponse rssSensorResponse = new RssSensorResponse();
//        rssSensorResponse.sensorId = rssSensorRequest.sensorId;
//        rssSensorResponse.urlId = rssSensorRequest.sensorUrlId;
//        rssSensorResponse.urlString = rssSensorRequest.sensorUrl;
//        rssSensorResponse.rssItemList = rssItemList;
//        mSensorResponseCache.add(rssSensorResponse);
//        return rssItemList;
//    }
}

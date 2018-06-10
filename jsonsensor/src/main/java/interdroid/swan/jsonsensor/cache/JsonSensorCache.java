package interdroid.swan.jsonsensor.cache;

import android.content.Context;
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
    private static final String TAG = JsonSensorCache.class.getSimpleName();

    private static final float CACHE_TIME_DIVIDER = 1.9f; //1/CACHE_TIME_DIVIDER = is caching time allowed as new response;

    private static JsonSensorCache sInstance;

    private Context mContext;

    private Queue<JsonSensorRequest> mRequestQueue;
    private List<JsonResponse> mResponseCache;

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

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                removeFromQueueAndCheckForNextRequest();
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
                JSONArray jsonArray = new JSONArray(response);
                getNextJsonArray(jsonArray, jsonItem);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        return jsonItem;
    }

    private void getNextItem(JSONObject jsonObject, String key, JsonItem jsonItem) {
        try {
            JSONObject jsonSubObject = jsonObject.getJSONObject(key);
            JsonItem jsonChildItem = new JsonItem(key);
            jsonItem.jsonItem = jsonChildItem;
            getNextJsonObject(jsonSubObject, jsonChildItem);
        } catch (JSONException e) {
            try {
                JSONArray jsonSubArray = jsonObject.getJSONArray(key);
                getNextJsonArray(jsonSubArray, jsonItem);
            } catch (JSONException e1) {
                try {
                    jsonItem.stringItem = jsonObject.getString(key);
                } catch (JSONException e2) {
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
                    JsonItem jsonItemSubChild = new JsonItem("" + i);
                    jsonItemChild.jsonItem = jsonItemSubChild;
                    getNextJsonObject((JSONObject) object, jsonItemSubChild);
                } else if (object instanceof JSONArray) {
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

    public synchronized void removeSensorFromCacheSynchronized(JsonSensorRequest jsonSensorRequest) {
        for (int i = 0; i < mResponseCache.size(); i++) {
            if (mResponseCache.get(i).jsonRequestInfo.id == jsonSensorRequest.jsonRequestInfo.id) {
                mResponseCache.remove(i);
            }
        }
    }
}

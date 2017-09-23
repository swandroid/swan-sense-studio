package interdroid.swan.sensors.impl;

import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.jsonsensor.activities.SelectionActivity;
import interdroid.swan.jsonsensor.cache.JsonSensorCache;
import interdroid.swan.jsonsensor.pojos.JsonItem;
import interdroid.swan.jsonsensor.pojos.JsonPathType;
import interdroid.swan.jsonsensor.pojos.JsonRequestComplete;
import interdroid.swan.jsonsensor.pojos.JsonRequestInfo;
import interdroid.swan.jsonsensor.pojos.JsonSensorRequest;
import interdroid.swan.jsonsensor.pojos.PathToValue;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;


public class JsonSensor extends AbstractSwanSensor {
	
	public static final String TAG = "Json";
	
	public static class ConfigurationActivity extends
			AbstractConfigurationActivity {

        private static final int REQUEST_CODE_JSON = 5123;

		@Override
		public int getPreferencesXML() {
			return R.xml.json_preferences;
		}

        @Override
        public void startActivity(Intent intent) {
            //Preference preference = findPreference("json_configuration");
            //getSharedPreferences()
            super.startActivityForResult(intent, REQUEST_CODE_JSON);
        }

        @Override
        protected void onActivityResult(int reqCode, int resCode, Intent data) {
            if (reqCode == REQUEST_CODE_JSON) {
                if (resCode == RESULT_OK) {
                    //preferences
                    //Preference preference = findPreference("json_configuration");
                    //preference.getEditor().putString("json_configuration", data.getStringExtra(SelectionActivity.REQUEST_EXTRA_RESULT)).apply();

                    //Full
                    Preference preference = findPreference("json_configuration_full");
                    preference.getEditor().putString("json_configuration_full", data.getStringExtra(SelectionActivity.REQUEST_EXTRA_RESULT_FULL)).apply();
                }
                // should be getting called now
            }
        }

	}

	/*Value path */
	public static final String VALUE = "value";
	
	
	/*Configuration */
	public static final String SAMPLE_INTERVAL = "sample_interval";
	public static final int DEFAULT_SAMPLE_INTERVAL = 5 * 60;
    //public static final String JSON_CONFIGURATION = "json_configuration";
    //public static final String DEFAULT_JSON_CONFIGURATION = "";
    public static final String JSON_CONFIGURATION_FULL = "json_configuration_full";
    public static final String DEFAULT_JSON_CONFIGURATION_FULL = "";

	protected static final int HISTORY_SIZE = 10;


	private Map<String, JsonPoller> activeThreads = new HashMap<String, JsonPoller>();


	@Override
	public String[] getValuePaths() {
		return new String[] { VALUE };
	}

	@Override
	public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
		DEFAULT_CONFIGURATION.putInt(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);
        //DEFAULT_CONFIGURATION.putString(JSON_CONFIGURATION, DEFAULT_JSON_CONFIGURATION);
        DEFAULT_CONFIGURATION.putString(JSON_CONFIGURATION_FULL, DEFAULT_JSON_CONFIGURATION_FULL);
	}


	@Override
	public void onConnected() {
        SENSOR_NAME = "Json";
        Log.e(TAG, "No json sensor found on device!");
	}

	@Override
	public final void register(String id, String valuePath, Bundle configuration) {
		JsonPoller jsonPoller = new JsonPoller(id, valuePath,
				configuration);
		activeThreads.put(id, jsonPoller);
		jsonPoller.start();
	}

	@Override
	public final void unregister(String id) {
        JsonPoller jsonPoller = activeThreads.remove(id);
        jsonPoller.destroyPoller();
        jsonPoller.interrupt();
//		activeThreads.remove(id).interrupt();
	}

	class JsonPoller extends Thread implements JsonSensorRequest.JsonRequestListener {

		private Bundle configuration;
		private String valuePath;
		private String id;

        private JsonRequestInfo mJsonRequestInfo;
        private PathToValue mPathToValue;
        private long mStart;
        private JsonSensorRequest mJsonSensorRequest;

        private boolean interrupted;

		JsonPoller(String id, String valuePath, Bundle configuration) {
			this.id = id;
			this.configuration = configuration;
            this.valuePath = valuePath;
        }

        public void run() {
			while (!interrupted) {
                mStart = System.currentTimeMillis();
                Log.d("JsonSensor", "testtimer start: " + mStart);

                if (mPathToValue == null) {
//                    String jsonConfiguration = configuration.getString(JSON_CONFIGURATION);
//                    if (!jsonConfiguration.isEmpty()) {
//                        String[] jsonConfigurations = jsonConfiguration.split(",");
//                        try {
//                            int keyId = Integer.parseInt(jsonConfigurations[0]);
//                            int valueId = Integer.parseInt(jsonConfigurations[1]);
//                            List<JsonRequestInfo> jsonRequestInfoList = JsonSensorSettings.getInstance().getJsonRequestList().jsonRequestInfoList;
//                            for (int i = 0; i < jsonRequestInfoList.size(); i++) {
//                                JsonRequestInfo jsonRequestInfo = jsonRequestInfoList.get(i);
//                                if (jsonRequestInfo.id == keyId) {
//                                    mJsonRequestInfo = jsonRequestInfo;
//                                    List<PathToValue> pathToValueList = jsonRequestInfo.pathToValueList;
//                                    Log.d("JsonSensor", "size: " + pathToValueList.size());
//                                    for (int j = 0; j < pathToValueList.size(); j++) {
//                                        PathToValue pathToValue = pathToValueList.get(j);
//                                        if (pathToValue.id == valueId) {
//                                            mPathToValue = pathToValue;
//                                        }
//                                    }
//                                }
//                            }
//                            //TODO
//                        } catch (NumberFormatException e) {
//
//                        }
//                    } else {
//                        //No value to get
//                    }
                    String jsonConfigurationFull = configuration.getString(JSON_CONFIGURATION_FULL);
                    if (jsonConfigurationFull != null && !jsonConfigurationFull.isEmpty()) {
                        JsonRequestComplete jsonRequestComplete = new Gson().fromJson(jsonConfigurationFull, JsonRequestComplete.class);
                        mJsonRequestInfo = new JsonRequestInfo(jsonRequestComplete);
                        mPathToValue = jsonRequestComplete.pathToValue;
                    } else {
                        //No value to get
                    }
                }

                long end = System.currentTimeMillis();
                Log.d("JsonSensor", "testtimer end: " + end);

                int sampleRate = configuration.getInt(SAMPLE_INTERVAL,
                        mDefaultConfiguration.getInt(SAMPLE_INTERVAL)) * 1000;
                if (mJsonSensorRequest == null) {
                    mJsonSensorRequest = new JsonSensorRequest(id, mJsonRequestInfo, sampleRate, this);
                }

                if (mPathToValue != null) {
                    JsonSensorCache.getInstance(getApplicationContext()).addRequestToQueue(mJsonSensorRequest);
//                    doGetRequest(mJsonRequestInfo.url);
                }

				//TODO: download the json

				//TODO: this is where optimization can be achieved by downloading once and share multiple values in one go
				//This can maybe be achieved by giving a message to the threads that the data is available

                //putValueTrimSize(valuePath, id, start, new Integer(15));

				try {
					Thread.sleep(Math.max(
							0,
							sampleRate)
									+ mStart - System.currentTimeMillis());
				} catch (InterruptedException e) {
                    interrupted = true;
                    break;
				}
			}
		}

        @Override
        public void onResult(JsonItem jsonItem) {
            if (jsonItem.jsonItems != null) {
                walkToValue(jsonItem);
            }
        }

        private void doGetRequest(String url) {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseJson(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

        private void parseJson(String response) {
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
                e.printStackTrace();
            }

            if (jsonItem.jsonItems != null) {
                walkToValue(jsonItem);
            }
        }

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

        private void walkToValue(JsonItem jsonItem) {
            List<JsonPathType> jsonPathTypes = mPathToValue.jsonPathTypes;
            int size = jsonPathTypes.size();
            for (int i = 0; i < size; i++) {
                JsonPathType jsonPathType = jsonPathTypes.get(i);
                if (i == 0) {
                    jsonItem = getJsonItemRootObject(jsonItem, jsonPathType);
                } else {
                    int type = jsonPathTypes.get(i - 1).type;
                    switch (type) {
                        case JsonPathType.JSON_TYPE_ARRAY:
                            jsonItem = jsonItem.jsonItems.get(jsonPathType.index);
                            break;
                        case JsonPathType.JSON_TYPE_OBJECT:
                            jsonItem = getJsonItemObject(jsonItem, jsonPathType);
                            break;
                    /*case JsonPathType.JSON_TYPE_STRING:

                        break;*/
                    }
                }
            }
            try {
                Long longValue = Long.parseLong(jsonItem.stringItem);
                putValueTrimSize(valuePath, id, mStart, longValue);
            } catch (NumberFormatException e1) {
                try {
                    Double doubleValue = Double.parseDouble(jsonItem.stringItem);
                    putValueTrimSize(valuePath, id, mStart, doubleValue);
                } catch (NumberFormatException e2) {
                    putValueTrimSize(valuePath, id, mStart, jsonItem.stringItem);
                }
            }
        }

        private JsonItem getJsonItemRootObject(JsonItem jsonItem, JsonPathType jsonPathType) {
            for (int j = 0; j < jsonItem.jsonItems.size(); j++) {
                if (jsonItem.jsonItems.get(j).key.equals(jsonPathType.key)) {
                    return jsonItem.jsonItems.get(j);
                }
            }
            return jsonItem;
        }

        private JsonItem getJsonItemObject(JsonItem jsonItem, JsonPathType jsonPathType) {
            List<JsonItem> jsonItems = jsonItem.jsonItem.jsonItems;
            for (int j = 0; j < jsonItems.size(); j++) {
                if (jsonItems.get(j).key.equals(jsonPathType.key)) {
                    return jsonItems.get(j);
                }
            }
            return jsonItem;
        }

        public void destroyPoller() {
            interrupted = true;
//            rssSensorRequest.listener = null;
            JsonSensorCache.getInstance(getApplicationContext()).removeSensorFromCacheSynchronized(mJsonSensorRequest);
        }
    }

	@Override
	public void onDestroySensor() {
		for (JsonPoller jsonPoller : activeThreads.values()) {
            jsonPoller.destroyPoller();
			jsonPoller.interrupt();
		}
		super.onDestroySensor();
	};

}

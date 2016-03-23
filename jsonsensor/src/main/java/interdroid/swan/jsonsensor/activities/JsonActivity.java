package interdroid.swan.jsonsensor.activities;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import interdroid.swan.jsonsensor.JsonSensorSettings;
import interdroid.swan.jsonsensor.R;
import interdroid.swan.jsonsensor.adapters.JsonHeaderListAdapter;
import interdroid.swan.jsonsensor.adapters.JsonListAdapter;
import interdroid.swan.jsonsensor.pojos.JsonItem;
import interdroid.swan.jsonsensor.pojos.JsonPathType;
import interdroid.swan.jsonsensor.pojos.JsonRequestInfo;
import interdroid.swan.jsonsensor.pojos.JsonRequestList;
import interdroid.swan.jsonsensor.pojos.Parameter;
import interdroid.swan.jsonsensor.pojos.PathToValue;


public class JsonActivity extends BaseActivity {

    private static final String TAG = JsonActivity.class.getSimpleName();

    public static final String EXTRA_JSON_REQUEST_INFO = "extra_json_request_info";

    private RecyclerView mRecyclerViewHeader;
    private RecyclerView.LayoutManager mLayoutManagerHeader;
    private JsonHeaderListAdapter mAdapterHeader;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private JsonListAdapter mAdapter;

    private JsonRequestInfo mJsonRequestInfo;
    //private OkHttpClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        getViews();

        mJsonRequestInfo = getIntent().getParcelableExtra(EXTRA_JSON_REQUEST_INFO);
        ArrayList<Parameter> parameterList = mJsonRequestInfo.parameterList;
        if (mJsonRequestInfo.requestType.equals("GET")) {
            doGetRequest(mJsonRequestInfo.url, parameterList);
        } else {
            Map<String, String> parameters = new Hashtable<>();
            if (parameterList != null) {
                int size = mJsonRequestInfo.parameterList.size();
                for (int i = 0; i < size; i++) {
                    Parameter parameter = parameterList.get(i);
                    parameters.put(parameter.name, parameter.value);
                }
            }

            doPostRequest(mJsonRequestInfo.url, parameters);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_json;
    }

    private void getViews() {
        //Header recycler view
        mRecyclerViewHeader = (RecyclerView) findViewById(R.id.json_activity_recyclerview_header);

        mLayoutManagerHeader = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewHeader.setLayoutManager(mLayoutManagerHeader);

        mAdapterHeader = new JsonHeaderListAdapter(mOnJsonHeaderItemClickListener);
        mRecyclerViewHeader.setAdapter(mAdapterHeader);

        //Main recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.json_activity_recyclerview);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new JsonListAdapter(mOnJsonItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void doGetRequest(String url, ArrayList<Parameter> parameters) {
        RequestQueue queue = Volley.newRequestQueue(this);

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
                        parseJson(response);
                        Log.i(TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void doPostRequest(String url, final Map<String, String> params) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void parseJson(String response) {
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

        if (jsonItem.jsonItems != null) {
            mAdapterHeader.addJsonItem(jsonItem);
            mAdapter.setJsonItems(jsonItem.jsonItems);
        }
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

    private JsonHeaderListAdapter.OnJsonItemClickListener mOnJsonHeaderItemClickListener = new JsonHeaderListAdapter.OnJsonItemClickListener() {
        @Override
        public void onJsonItemClicked(JsonItem jsonItem) {
            if (jsonItem.jsonItems != null) {
                mAdapter.setJsonItems(jsonItem.jsonItems);
            } else if (jsonItem.jsonItem != null && jsonItem.jsonItem.jsonItems != null) {
                mAdapter.setJsonItems(jsonItem.jsonItem.jsonItems);
            }
        }
    };

    private JsonListAdapter.OnJsonItemClickListener mOnJsonItemClickListener = new JsonListAdapter.OnJsonItemClickListener() {
        @Override
        public void onJsonItemClicked(JsonItem jsonItem) {
            if (jsonItem.jsonItem != null && jsonItem.jsonItem.jsonItems != null) { //Json object
                mAdapterHeader.addJsonItem(jsonItem.jsonItem);
                mAdapter.setJsonItems(jsonItem.jsonItem.jsonItems);
                jsonItem.jsonItem.type = JsonItem.JSON_TYPE_OBJECT;
                Log.d(TAG, "object");
            } else if (jsonItem.jsonItems != null) { //Json array
                mAdapterHeader.addJsonItem(jsonItem);
                mAdapter.setJsonItems(jsonItem.jsonItems);
                jsonItem.type = JsonItem.JSON_TYPE_ARRAY;
                Log.d(TAG, "array");
            } else if (jsonItem.stringItem != null) {
                showDialog(jsonItem);
            }
        }
    };

    private void showDialog(final JsonItem jsonValueItem) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //alert.setTitle("Title");
        alert.setMessage(R.string.label_enter_name);

        // Set an EditText view to get user input
        FrameLayout frameLayout = (FrameLayout) View.inflate(this, R.layout.dialog_enter_name, null);
        final EditText input = (EditText) frameLayout.findViewById(R.id.dialog_enter_name_input);
        //final EditText input = new EditText(this);
        input.setText(jsonValueItem.key);
        alert.setView(frameLayout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                createPathToValue(jsonValueItem, value);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void createPathToValue(JsonItem jsonValueItem, String name) {
        ArrayList<JsonItem> jsonItems = mAdapterHeader.getJsonItems();
        PathToValue pathToValue = new PathToValue(name);
        boolean isArray = false;
        for (int i = 1; i < jsonItems.size(); i++) {
            Log.d(TAG, "key: " + i + " " + jsonItems.get(i).key);
            JsonItem jsonItem = jsonItems.get(i);

            JsonPathType jsonPathType;
            String key = jsonItem.key;
            if (isArray) {
                jsonPathType = new JsonPathType(Integer.parseInt(key));
            } else {
                jsonPathType = new JsonPathType(key);
            }

            Log.d(TAG, "jsonItem: " + jsonItem.jsonItem + ", jsonType: " + jsonItem.type);
            if (jsonItem.type == JsonItem.JSON_TYPE_OBJECT/*jsonItem.jsonItem != null && jsonItem.jsonItem.jsonItems != null*/) {
                Log.d(TAG, "object");
                jsonPathType.type = jsonItem.type;
                isArray = false;
            } else if (jsonItem.type == JsonItem.JSON_TYPE_ARRAY/*jsonItem.jsonItems != null*/) {
                Log.d(TAG, "array");
                jsonPathType.type = jsonItem.type;
                isArray = true;
            }
            pathToValue.jsonPathTypes.add(jsonPathType);
        }
        JsonPathType jsonPathType;
        String key = jsonValueItem.key;
        if (isArray) {
            jsonPathType = new JsonPathType((Integer.parseInt(key)));
        } else {
            jsonPathType = new JsonPathType(key);
        }
        jsonPathType.type = JsonPathType.JSON_TYPE_STRING;
        pathToValue.jsonPathTypes.add(jsonPathType);

        savePathToValue(pathToValue);

        //mJsonRequestInfo.pathToValueList.add(pathToValue);

    }

    private void savePathToValue(PathToValue pathToValue) {
        JsonRequestList jsonRequestList = JsonSensorSettings.getInstance(getApplicationContext()).getJsonRequestList();
        List<JsonRequestInfo> jsonRequestInfoList = jsonRequestList.jsonRequestInfoList;
        int id = 0;
        for (int i = 0; i < jsonRequestInfoList.size(); i++) {
            JsonRequestInfo jsonRequestInfo = jsonRequestInfoList.get(i);
            if (jsonRequestInfo.id == mJsonRequestInfo.id) {
                jsonRequestInfo.maxPathToValueId += 1;
                pathToValue.id = jsonRequestInfo.maxPathToValueId;
                id = jsonRequestInfo.maxPathToValueId;
                jsonRequestInfo.pathToValueList.add(pathToValue);
                JsonSensorSettings.getInstance(getApplicationContext()).setJsonRequestList(jsonRequestList);
                Intent intent = new Intent();
                intent.putExtra(SelectionActivity.REQUEST_EXTRA_RESULT, id);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

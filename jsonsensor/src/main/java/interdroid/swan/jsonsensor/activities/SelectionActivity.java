package interdroid.swan.jsonsensor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import interdroid.swan.jsonsensor.JsonSensorSettings;
import com.liutoapps.android.jsonsensor.R;
import interdroid.swan.jsonsensor.pojos.JsonItem;
import interdroid.swan.jsonsensor.pojos.JsonPathType;
import interdroid.swan.jsonsensor.pojos.JsonRequestInfo;
import interdroid.swan.jsonsensor.pojos.PathToValue;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by steven on 04/06/15.
 */
public class SelectionActivity extends BaseActivity {

    private static final String TAG = SelectionActivity.class.getSimpleName();

    private static final int REQUEST_CODE_ENDPOINT = 1001;
    private static final int REQUEST_CODE_VALUE = 1002;

    public static final String REQUEST_EXTRA_RESULT = "result";

    private View mEndpoint;
    private TextView mEndpointName;
    private TextView mEndpointExtras;
    private View mValue;
    private TextView mValueName;
    private TextView mValuePath;
    private View mDividerView;
    private FloatingActionButton mExecute;
    private TextView mResult;
    private ProgressBar mProgress;

    private int mJsonRequestKeyId;
    private int mJsonRequestValueId;

    private JsonRequestInfo mJsonRequestInfo;
    private PathToValue mPathToValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(0);

        getViews();
    }

    private void getViews() {
        mEndpoint = findViewById(R.id.selection_endpoint);
        mEndpoint.setOnClickListener(mOnEndpointClickListener);
        mEndpointName = (TextView) findViewById(R.id.selection_endpoint_name);
        mEndpointExtras = (TextView) findViewById(R.id.selection_endpoint_extras);
        mValue = findViewById(R.id.selection_value);
        mValueName = (TextView) findViewById(R.id.selection_value_name);
        mValuePath = (TextView) findViewById(R.id.selection_value_path);
        mValue.setOnClickListener(mOnValueClickListener);
        mDividerView = findViewById(R.id.selection_divider_view);
        mExecute = (FloatingActionButton) findViewById(R.id.selection_fab);
        mExecute.setOnClickListener(mOnExecuteClickListener);
        mResult = (TextView) findViewById(R.id.selection_result);
        mProgress = (ProgressBar) findViewById(R.id.selection_progress);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_selection;
    }

    private View.OnClickListener mOnEndpointClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SelectionActivity.this, JsonInputActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ENDPOINT);
        }
    };

    private View.OnClickListener mOnValueClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SelectionActivity.this, PathToValueListActivity.class);
            intent.putExtra(JsonActivity.EXTRA_JSON_REQUEST_INFO, mJsonRequestInfo);
            startActivityForResult(intent, REQUEST_CODE_VALUE);
        }
    };

    private View.OnClickListener mOnExecuteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            downloadData();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE_ENDPOINT) {
            if (resultCode == RESULT_OK) {
                mJsonRequestKeyId = data.getIntExtra(REQUEST_EXTRA_RESULT, 0);
                List<JsonRequestInfo> jsonRequestInfoList = JsonSensorSettings.getInstance().getJsonRequestList().jsonRequestInfoList;
                int size = jsonRequestInfoList.size();
                for (int i = 0; i < size; i++) {
                    if (jsonRequestInfoList.get(i).id == mJsonRequestKeyId) {
                        mJsonRequestInfo = jsonRequestInfoList.get(i);
                        mEndpointName.setText(mJsonRequestInfo.name);
                        mEndpointExtras.setText(mJsonRequestInfo.requestType);
                        mValueName.setText(R.string.selection_click_for_value);
                        mValuePath.setText("");
                        mPathToValue = null;
                        mValue.setVisibility(View.VISIBLE);
                        mDividerView.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_VALUE) {
            if (resultCode == RESULT_OK) {
                mJsonRequestValueId = data.getIntExtra(REQUEST_EXTRA_RESULT, 0);
                List<PathToValue> pathToValueList = mJsonRequestInfo.pathToValueList;
                int size = pathToValueList.size();
                for (int i = 0; i < size; i++) {
                    if (pathToValueList.get(i).id == mJsonRequestValueId) {
                        mPathToValue = pathToValueList.get(i);
                        mValueName.setText(mPathToValue.name);
                        mValuePath.setText(getPathString(mPathToValue));
                        mExecute.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }
        }
    }

    private String getPathString(PathToValue pathToValue) {
        StringBuilder sb = new StringBuilder();
        List<JsonPathType> jsonPathTypes = pathToValue.jsonPathTypes;
        int size = jsonPathTypes.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append("/");
            }
            if (jsonPathTypes.get(i).key == null) {
                sb.append(jsonPathTypes.get(i).index);
            } else {
                sb.append(jsonPathTypes.get(i).key);
            }
        }
        return sb.toString();
    }

    private void downloadData() {
        doGetRequest(mJsonRequestInfo.url);
        mResult.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
    }

    private void doGetRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

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
                mProgress.setVisibility(View.GONE);
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
        mProgress.setVisibility(View.GONE);
        mResult.setVisibility(View.VISIBLE);
        mResult.setText(jsonItem.stringItem);
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

}

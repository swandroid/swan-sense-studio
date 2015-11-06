package interdroid.swan.jsonsensor;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interdroid.swan.jsonsensor.pojos.JsonRequestInfo;
import interdroid.swan.jsonsensor.pojos.JsonRequestList;

/**
 * Created by steven on 19/05/15.
 */
public class JsonSensorSettings {

    private static final String KEY_JSON_REQUEST_LIST = "key_json_request_list";

    private static JsonSensorSettings sInstance;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SharedPreferences mSharedPreferences;

    private JsonRequestList mJsonRequestList;

    private JsonSensorSettings(Context context) {
        mExecutor = Executors.newSingleThreadExecutor();
        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(JsonSensorApp.getInstance());
        mSharedPreferences = context.getSharedPreferences("JsonSensor", Context.MODE_PRIVATE);

        loadData();
    }

    public static JsonSensorSettings getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new JsonSensorSettings(context);
        }
        return sInstance;
    }

    private void loadData() {
        String jsonRequestListString = mSharedPreferences.getString(KEY_JSON_REQUEST_LIST, null);
        if (jsonRequestListString == null) {
            mJsonRequestList = new JsonRequestList();
            mJsonRequestList.jsonRequestInfoList.add(new JsonRequestInfo(0, "Add new request"));
        } else {
            mJsonRequestList = new Gson().fromJson(jsonRequestListString, JsonRequestList.class);
        }
    }

    private void persistString(final String key, final String value) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(key, value);
                editor.commit();
            }
        });
    }

    public void setJsonRequestList(JsonRequestList jsonRequestList) {
        mJsonRequestList = jsonRequestList;
        persistString(KEY_JSON_REQUEST_LIST, new Gson().toJson(mJsonRequestList));
    }

    public JsonRequestList getJsonRequestList() {
        return mJsonRequestList;
    }
}

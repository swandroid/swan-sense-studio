package interdroid.swan.rss_sensor;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interdroid.swan.rss_sensor.pojos.RssRequestInfo;

/**
 * Created by steven on 11/10/15.
 */
public class RssSensorSettings {

    private static final String KEY_RSS_REQUEST_URLS = "key_rss_request_urls";
    private static final String KEY_RSS_REQUEST_STRINGS = "key_rss_request_strings";

    private static RssSensorSettings sInstance;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SharedPreferences mSharedPreferences;

    private ArrayList<RssRequestInfo> mRssRequestUrls;
    private ArrayList<String> mRssRequestStrings;

    private RssSensorSettings() {
        mExecutor = Executors.newSingleThreadExecutor();
        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(JsonSensorApp.getInstance());
        mSharedPreferences = RssSensorApp.getInstance().getSharedPreferences("RssSensor", Context.MODE_PRIVATE);

        loadData();
    }

    public static RssSensorSettings getInstance() {
        if (sInstance == null) {
            sInstance = new RssSensorSettings();
        }
        return sInstance;
    }

    private void loadData() {
        String rssRequestUrls = mSharedPreferences.getString(KEY_RSS_REQUEST_URLS, null);
        if (rssRequestUrls == null) {
            mRssRequestUrls = new ArrayList<String>();
            mRssRequestUrls.add("Add new request");
        } else {
            Type listObject = new TypeToken<List<String>>(){}.getType();
            mRssRequestUrls = new Gson().fromJson(rssRequestUrls, listObject);
        }

        String rssRequestStrings = mSharedPreferences.getString(KEY_RSS_REQUEST_STRINGS, null);
        if (rssRequestStrings == null) {
            mRssRequestStrings = new ArrayList<String>();
            mRssRequestStrings.add("Add new string");
        } else {
            Type listObject = new TypeToken<List<String>>(){}.getType();
            mRssRequestStrings = new Gson().fromJson(rssRequestStrings, listObject);
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

    public void setRssRequestUrls(ArrayList<String> rssRequestUrls) {
        mRssRequestUrls = rssRequestUrls;
        persistString(KEY_RSS_REQUEST_URLS, new Gson().toJson(mRssRequestUrls));
    }

    public ArrayList<String> getRssRequestUrls() {
        return mRssRequestUrls;
    }
}

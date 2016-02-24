package interdroid.swan.rss_sensor.activities;

import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import interdroid.swan.rss_sensor.R;
import interdroid.swan.rss_sensor.RssSensorSettings;
import interdroid.swan.rss_sensor.pojos.RssRequestComplete;
import interdroid.swan.rss_sensor.pojos.RssRequestInfo;

public class RssMainActivity extends BaseActivity {

    private static final String TAG = RssMainActivity.class.getSimpleName();
    public static final String REQUEST_EXTRA_RESULT_FULL = "result_full";

    private Spinner mNameSpinner;
    private ArrayAdapter<RssRequestInfo> mSelectionAdapter;
    private EditText mNameEditText;
    private EditText mUrlEditText;
    private Spinner mStringSpinner;
    private ArrayAdapter<String> mStringSelectionAdapter;
    private EditText mStringEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        getViews();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private void getViews() {
        mNameSpinner = (Spinner) findViewById(R.id.rss_input_name_spinner);

        ArrayList<RssRequestInfo> rssRequestUrls = RssSensorSettings.getInstance(getApplicationContext()).getRssRequestUrls();
        mSelectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rssRequestUrls);
        mNameSpinner.setAdapter(mSelectionAdapter);
        mNameSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

        mNameEditText = (EditText) findViewById(R.id.rss_input_name);
        mUrlEditText = (EditText) findViewById(R.id.rss_input_url);
        mStringSpinner = (Spinner) findViewById(R.id.rss_input_string_spinner);

        ArrayList<String> rssRequestStrings = RssSensorSettings.getInstance(getApplicationContext()).getRssRequestStrings();
        mStringSelectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rssRequestStrings);
        mStringSpinner.setAdapter(mStringSelectionAdapter);
        mStringSpinner.setOnItemSelectedListener(mOnStringItemSelectedListener);

        mStringEditText = (EditText) findViewById(R.id.rss_input_string);
    }

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            RssRequestInfo rssRequestInfo = mSelectionAdapter.getItem(position);
            if (!rssRequestInfo.name.equals(getString(R.string.rss_spinner_add_new_request))) {
                mNameEditText.setText(rssRequestInfo.name);
                mUrlEditText.setText(rssRequestInfo.url);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener mOnStringItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String word = mStringSelectionAdapter.getItem(position);
            if (!word.equals(getString(R.string.rss_spinner_add_new_word))) {
                mStringEditText.setText(word);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rss_sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int menuId = item.getItemId();

        if (menuId == R.id.action_save) {
            saveRssInfo();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveRssInfo() {
        RssRequestInfo rssRequestInfo = getAndSaveRssRequestInfo();
        if (rssRequestInfo == null) {
            return;
        }

        Intent intent = new Intent();
        String rssRequestFull = new Gson().toJson(
                new RssRequestComplete(rssRequestInfo, getAndSaveWord()));
        Log.d(TAG, rssRequestFull);
        intent.putExtra(REQUEST_EXTRA_RESULT_FULL, rssRequestFull);
        setResult(RESULT_OK, intent);
        finish();
    }

    private RssRequestInfo getAndSaveRssRequestInfo() {
        RssRequestInfo rssRequestInfo = null;
        ArrayList<RssRequestInfo> rssRequestList = RssSensorSettings.getInstance(getApplicationContext()).getRssRequestUrls();
        if (((RssRequestInfo)mNameSpinner.getSelectedItem()).name.equals(getString(R.string.rss_spinner_add_new_request))) {
            String name = mNameEditText.getText().toString();
            if (name.length() < 1) {
                mNameEditText.setError("Name cannot be empty");
                return null;
            }
            rssRequestInfo = new RssRequestInfo(getNewId(rssRequestList), name);
            rssRequestList.add(rssRequestInfo);
        } else {
            boolean foundRssRequestInfo = false;
            for (int i = 0; i < rssRequestList.size(); i++) {
                if (((RssRequestInfo)mNameSpinner.getSelectedItem()).id == rssRequestList.get(i).id) {
                    rssRequestInfo = rssRequestList.get(i);
                    foundRssRequestInfo = true;
                    break;
                }
            }
            if (!foundRssRequestInfo) {
                String name = mNameEditText.getText().toString();
                rssRequestInfo = new RssRequestInfo(getNewId(rssRequestList), name);
                rssRequestList.add(rssRequestInfo);
            }
        }

        rssRequestInfo.url = mUrlEditText.getText().toString();
        rssRequestInfo.lastUpdate = System.currentTimeMillis();

        RssSensorSettings.getInstance(getApplicationContext()).setRssRequestUrls(rssRequestList);

        return rssRequestInfo;
    }

    private int getNewId(ArrayList<RssRequestInfo> rssRequestList) {
        int maxId = 0;
        for (int i = 0; i < rssRequestList.size(); i++) {
            if (rssRequestList.get(i).id > maxId) {
                maxId = rssRequestList.get(i).id;
            }
        }
        return maxId + 1;
    }

    private String getAndSaveWord() {
        String word = mStringEditText.getText().toString();
        if (word.length() < 1) {
            return "";
        }
        ArrayList<String> rssRequestStrings = RssSensorSettings.getInstance(getApplicationContext()).getRssRequestStrings();
        if (((String)mStringSpinner.getSelectedItem()).equals(getString(R.string.rss_spinner_add_new_word))) {
            for (int i = 0; i < rssRequestStrings.size(); i++) {
                if (rssRequestStrings.get(i).equals(word)) {
                    mStringSpinner.setSelection(i);
                }
            }
        } else {
            rssRequestStrings.remove(mStringSpinner.getSelectedItemPosition());
            rssRequestStrings.add(mStringSpinner.getSelectedItemPosition(), word);
        }
        RssSensorSettings.getInstance(getApplicationContext()).setRssRequestStrings(rssRequestStrings);
        return word;
    }
}

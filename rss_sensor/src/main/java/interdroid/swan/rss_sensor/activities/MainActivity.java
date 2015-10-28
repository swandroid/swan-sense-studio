package interdroid.swan.rss_sensor.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.liutoapps.android.rss_sensor.R;

import java.util.ArrayList;

import interdroid.swan.rss_sensor.RssSensorSettings;

public class MainActivity extends AppCompatActivity {

    private Spinner mNameSpinner;
    private ArrayAdapter<>
    private EditText mNameEditText;
    private EditText mUrlEditText;
    private Spinner mStringSpinner;
    private EditText mStringEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getViews();
    }

    private void getViews() {
        mNameSpinner = (Spinner) findViewById(R.id.rss_input_name_spinner);

        ArrayList<String> rssRequestUrls = RssSensorSettings.getInstance().getRssRequestUrls();
        mSelectionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jsonRequestList.jsonRequestInfoList);
        mSelectionSpinner.setAdapter(mSelectionAdapter);
        mSelectionSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

        mNameEditText = (EditText) findViewById(R.id.rss_input_name);
        mUrlEditText = (EditText) findViewById(R.id.rss_input_url);
        mStringSpinner = (Spinner) findViewById(R.id.rss_input_string_spinner);
        mStringEditText = (EditText) findViewById(R.id.rss_input_string);
    }
}

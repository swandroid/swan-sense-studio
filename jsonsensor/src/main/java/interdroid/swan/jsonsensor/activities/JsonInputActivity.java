package interdroid.swan.jsonsensor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import interdroid.swan.jsonsensor.JsonSensorSettings;
import interdroid.swan.jsonsensor.R;
import interdroid.swan.jsonsensor.adapters.ParameterListAdapter;
import interdroid.swan.jsonsensor.pojos.JsonRequestInfo;
import interdroid.swan.jsonsensor.pojos.JsonRequestList;
import interdroid.swan.jsonsensor.pojos.Parameter;


public class JsonInputActivity extends BaseActivity {

    private static final String TAG = JsonInputActivity.class.getSimpleName();

    private Spinner mSelectionSpinner;
    private ArrayAdapter<JsonRequestInfo> mSelectionAdapter;
    private EditText mName;
    private EditText mUrl;
    private Spinner mRequestTypeSpinner;
    private EditText mParamName;
    private EditText mParamValue;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ParameterListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        getViews();

        //startActivity(new Intent(JsonInputActivity.this, JsonActivity.class));
    }

    private void getViews() {
        mSelectionSpinner = (Spinner) findViewById(R.id.json_input_selection_spinner);
        //TODO: adapter from preferences if possible
        JsonRequestList jsonRequestList = JsonSensorSettings.getInstance().getJsonRequestList();
        mSelectionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, jsonRequestList.jsonRequestInfoList);
        mSelectionSpinner.setAdapter(mSelectionAdapter);
        mSelectionSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
        mName = (EditText) findViewById(R.id.json_input_name);
        mUrl = (EditText) findViewById(R.id.json_input_url);
        mRequestTypeSpinner = (Spinner) findViewById(R.id.json_input_request_type_spinner);
        ArrayAdapter requestTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.input_array_request, android.R.layout.simple_spinner_dropdown_item);
        mRequestTypeSpinner.setAdapter(requestTypeAdapter);
        mParamName = (EditText) findViewById(R.id.json_input_param_name);
        mParamValue = (EditText) findViewById(R.id.json_input_param_value);

        //Main recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.json_input_recycler);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ParameterListAdapter(mOnParameterClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            JsonRequestInfo jsonRequestInfo = mSelectionAdapter.getItem(position);
            if (jsonRequestInfo.id != 0) {
                mName.setText(jsonRequestInfo.name);
                mUrl.setText(jsonRequestInfo.url);
                mRequestTypeSpinner.setSelection(jsonRequestInfo.requestType.equals("GET") ? 0 : 1);
                mAdapter.setParameters(jsonRequestInfo.parameterList);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private ParameterListAdapter.OnParameterClickListener mOnParameterClickListener = new ParameterListAdapter.OnParameterClickListener() {
        @Override
        public void onParameter(Parameter parameter) {

        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_json_input;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_json_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int menuId = item.getItemId();

        if (menuId == R.id.action_save) {
            JsonRequestInfo jsonRequestInfo;
            JsonRequestList jsonRequestList = JsonSensorSettings.getInstance().getJsonRequestList();
            if (((JsonRequestInfo)mSelectionSpinner.getSelectedItem()).id == 0) {
                int id = jsonRequestList.maxId += 1;
                jsonRequestList.maxId = id;
                String name = mName.getText().toString();
                jsonRequestInfo = new JsonRequestInfo(id, name);
                jsonRequestList.jsonRequestInfoList.add(jsonRequestInfo);
            } else {
                jsonRequestInfo = jsonRequestList.jsonRequestInfoList.get(mSelectionSpinner.getSelectedItemPosition());
                jsonRequestInfo.name = mName.getText().toString();
            }

            jsonRequestInfo.url = mUrl.getText().toString();
            jsonRequestInfo.parameterList = mAdapter.getParamterList();

            JsonSensorSettings.getInstance().setJsonRequestList(jsonRequestList);

            Intent intent = new Intent();
            intent.putExtra(SelectionActivity.REQUEST_EXTRA_RESULT, jsonRequestInfo.id);
            setResult(RESULT_OK, intent);
            finish();

            /*Intent intent = new Intent(JsonInputActivity.this, PathToValueListActivity.class);
            intent.putExtra(JsonActivity.EXTRA_JSON_REQUEST_INFO, jsonRequestInfo);
            //intent.putExtra(JsonActivity.EXTRA_URL, jsonRequestInfo.url);
            startActivity(intent);
            break;*/
        }
        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}

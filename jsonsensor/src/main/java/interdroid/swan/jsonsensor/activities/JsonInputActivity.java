package interdroid.swan.jsonsensor.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private Button mAddParameterButton;
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
        JsonRequestList jsonRequestList = JsonSensorSettings.getInstance(getApplicationContext()).getJsonRequestList();
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

        mAddParameterButton = (Button) findViewById(R.id.json_input_button_add_parameter);
        mAddParameterButton.setOnClickListener(onAddParameterClickListener);

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

    private View.OnClickListener onAddParameterClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            showDialog();
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

    private void showDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //alert.setTitle("Title");
        alert.setMessage(R.string.input_label_param_title);

        // Set an EditText view to get user input
        LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.dialog_parameter, null);
        final EditText nameEditText = (EditText) layout.findViewById(R.id.param_input_param_name);
        final EditText valueEditText = (EditText) layout.findViewById(R.id.param_input_param_value);
        //nameEditText.setText(jsonValueItem.key);
        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = nameEditText.getText().toString();
                String value = valueEditText.getText().toString();
                mAdapter.addParameter(new Parameter(name, value));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
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
            JsonRequestList jsonRequestList = JsonSensorSettings.getInstance(getApplicationContext()).getJsonRequestList();
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
            jsonRequestInfo.lastUpdate = System.currentTimeMillis();

            JsonSensorSettings.getInstance(getApplicationContext()).setJsonRequestList(jsonRequestList);

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

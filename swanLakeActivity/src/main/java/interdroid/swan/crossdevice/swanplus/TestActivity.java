package interdroid.swan.crossdevice.swanplus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import interdroid.swan.ExpressionManager;
import interdroid.swan.R;
import interdroid.swan.SensorInfo;
import interdroid.swan.SwanException;
import interdroid.swan.ValueExpressionListener;
import interdroid.swan.crossdevice.swanplus.bluetooth.BTManager;
import interdroid.swan.swansong.ExpressionFactory;
import interdroid.swan.swansong.ExpressionParseException;
import interdroid.swan.swansong.TimestampedValue;
import interdroid.swan.swansong.ValueExpression;

public class TestActivity extends Activity {

    private static final String TAG = "TestSensorApp";
    SensorInfo swanSensor;

    /* name of the sensor */
//    final String SENSOR_NAME = "fitness";
    final String SENSOR_NAME = "light";

    /* random id */
    public final int REQUEST_CODE = 123;

    TextView tv = null;
    String mExpression;
    private boolean mRegistered = false;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BTManager.ACTION_LOG_MESSAGE.equals(action)) {
                String message = intent.getStringExtra("log");
                TextView tv = (TextView) findViewById(R.id.result);
                tv.setText(message + "\n" + tv.getText());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_main);
        tv = (TextView) findViewById(R.id.textView1);

        IntentFilter intentFilter = new IntentFilter(BTManager.ACTION_LOG_MESSAGE);
        registerReceiver(mReceiver, intentFilter);
//        initialize();
//        testSensor();
    }

    public void testSensor() {
//        String myExpression = "SWAN1@fitness:avg_speed{ANY,0}";
        mExpression = "NEARBY@light:lux{ANY,0}";
//        registerSWANSensor(myExpression);
    }

    public void initialize() {

        try {
            swanSensor = ExpressionManager.getSensor(this, SENSOR_NAME);
        } catch (SwanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		/* start activity for configuring sensor */
        startActivityForResult(swanSensor.getConfigurationIntent(), REQUEST_CODE);

    }

    public void registerExpression(View view) {
        EditText usernameEdit = (EditText) findViewById(R.id.username);
        String connectTo = usernameEdit.getText().toString();

        if (connectTo.trim().isEmpty()) {
            connectTo = "NEARBY";
        }

//        mExpression = connectTo + "@light:lux";
        mExpression = connectTo + "@fitness:avg_speed$server_storage=false{ANY,0}";

        if (!mRegistered) {
            registerSWANSensor(mExpression);
        } else {
            Log.d(TAG, "Already registered");
        }
    }

    public void unregisterExpression(View view) {
        if (mRegistered) {
            unregisterSWANSensor();
        } else {
            Log.d(TAG, "Already unregistered");
        }
    }

    public void testBind(View view) {
    }

    /* Invoked on pressing back key from the sensor configuration activity */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (REQUEST_CODE == requestCode) {
                mExpression = data.getStringExtra("Expression");
                    /*Based on sensor configuration an expression will be created*/
                Log.d(TAG, "expression: " + mExpression);
            }
        }

    }

    /* Register expression to SWAN */
    private void registerSWANSensor(String myExpression) {
        try {
            ExpressionManager.registerValueExpression(this, String.valueOf(REQUEST_CODE),
                    (ValueExpression) ExpressionFactory.parse(myExpression),
                    new ValueExpressionListener() {

                        /* Registering a listener to process new values from the registered sensor*/
                        @Override
                        public void onNewValues(String id, TimestampedValue[] arg1) {
                            if (arg1 != null && arg1.length > 0) {
                                String value = arg1[0].getValue().toString();
                                tv.setText("Value = " + value);

                            } else {
                                tv.setText("Value = null");
                            }

                        }
                    });
            mRegistered = true;
        } catch (SwanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExpressionParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    /* Unregister expression from SWAN */
    private void unregisterSWANSensor() {
        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE));
        mRegistered = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
//        unregisterSWANSensor();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSWANSensor();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
//        if (id == R.id.action_disconnect) {
//
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}


package interdroid.swan.crossdevice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swan.R;
import interdroid.swancore.swanmain.SensorInfo;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swanmain.TriStateExpressionListener;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swan.crossdevice.bluetooth.BTManager;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.TriState;
import interdroid.swancore.swansong.TriStateExpression;
import interdroid.swancore.swansong.ValueExpression;

public class TestActivity extends Activity {

    private static final String TAG = "BTTestSensorApp";
    SensorInfo swanSensor;

    /* name of the sensor */
//    final String SENSOR_NAME = "fitness";
    final String SENSOR_NAME = "light";

    /* random id */
    public final int REQUEST_CODE_1 = 123;
    public final int REQUEST_CODE_2 = 234;
    public final int REQUEST_CODE_3 = 345;
    public final int REQUEST_CODE_4 = 456;
    public final int REQUEST_CODE_5 = 567;
    public final int REQUEST_CODE_6 = 678;
    public final int REQUEST_CODE_7 = 789;

    TextView tv = null;
    String mExpression;
    private boolean mRegistered = false;
    private Handler handler;

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
        handler = new Handler();

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
        startActivityForResult(swanSensor.getConfigurationIntent(), REQUEST_CODE_1);

    }

    /* button handler */
    public void registerExpression(View view) {
        EditText usernameEdit = (EditText) findViewById(R.id.username);
        String connectTo = usernameEdit.getText().toString();

        if (connectTo.trim().isEmpty()) {
            connectTo = "NEARBY";
        }

        final String expression1 = connectTo + "@light:lux";
//        final String expression2 = connectTo + "@movement:x";
//        final String expression3 = connectTo + "@gyroscope:x";
//        final String expression4 = connectTo + "@magnetometer:x";
//        final String expression5 = connectTo + "@battery:level";
//        final String expression6 = connectTo + "@proximity:distance";
//        final String expression7 = connectTo + "@sound:rms?audio_format=2#sample_interval=2000$server_storage=false{ANY,0}";
//        String expression3 = connectTo + "@light:lux > 10.0";
//        mExpression = connectTo + "@fitness:avg_speed$server_storage=false{ANY,0}";

        if (!mRegistered) {
            registerSWANSensor(expression1, REQUEST_CODE_1);
//            registerSWANSensor(expression2, REQUEST_CODE_2);
//            registerSWANSensor(expression3, REQUEST_CODE_3);
//            registerSWANSensor(expression4, REQUEST_CODE_4);
//            registerSWANSensor(expression5, REQUEST_CODE_5);
//            registerSWANSensor(expression6, REQUEST_CODE_6);
//            registerSWANSensor(expression7, REQUEST_CODE_7);
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    registerSWANSensor(expression2, REQUEST_CODE_2);
//                }
//            }, 30000);

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

    /* Invoked on pressing back key from the sensor configuration activity */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (REQUEST_CODE_1 == requestCode) {
                mExpression = data.getStringExtra("Expression");
                    /*Based on sensor configuration an expression will be created*/
                Log.d(TAG, "expression: " + mExpression);
            }
        }

    }

    /* Register expression to SWAN */
    private void registerSWANSensor(String myExpression, int requestCode) {
        try {
            ExpressionManager.registerValueExpression(this, String.valueOf(requestCode),
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
//            ExpressionManager.registerTriStateExpression(this, String.valueOf(requestCode),
//                    (TriStateExpression) ExpressionFactory.parse(myExpression),
//                    new TriStateExpressionListener() {
//
//                        @Override
//                        public void onNewState(String id, long timestamp, TriState newState) {
//                            tv.setText("Value = " + newState);
//                        }
//                    });
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
        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE_1));
        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE_2));
        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE_3));
        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE_4));
        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE_5));
        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE_6));
        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE_7));
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


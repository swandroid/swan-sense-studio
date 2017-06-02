package interdroid.swan.crossdevice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

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
    public final int REQUEST_CODE_0 = 012;
    public final int REQUEST_CODE_1 = 123;
    public final int REQUEST_CODE_2 = 234;
    public final int REQUEST_CODE_3 = 345;
    public final int REQUEST_CODE_4 = 456;
    public final int REQUEST_CODE_5 = 567;
    public final int REQUEST_CODE_6 = 678;
    public final int REQUEST_CODE_7 = 789;
    public final int REQUEST_CODE_8 = 890;
    public final int REQUEST_CODE_9 = 901;
    public final int REQUEST_CODE_BEACON = 888;

    TextView tv = null;
    String mExpression;
    private boolean mRegistered = false;
    private Handler handler;
    ArrayList<String> registeredExpr = new ArrayList<>();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BTManager.ACTION_LOG_MESSAGE.equals(action)) {
                String message = intent.getStringExtra("log");
                TextView tv = (TextView) findViewById(R.id.result);
                tv.setText(message + "\n" + tv.getText());
            } else if(BTManager.ACTION_LOG_METRICS.equals(action)) {
                String message = intent.getLongExtra("time", 0) + "\n" +
                        intent.getStringExtra("sourceMac") + "\n" +
                        intent.getStringExtra("destinationMac") + "\n" +
                        intent.getDoubleExtra("reqCount", 0) + "\n" +
                        intent.getDoubleExtra("failedReqCount", 0) + "\n" +
                        intent.getDoubleExtra("avgReqTime", 0) + "\n" +
                        intent.getDoubleExtra("avgConnTime", 0) + "\n" +
                        intent.getDoubleExtra("avgCommTime", 0) + "\n" +
                        intent.getDoubleExtra("avgSwanTime", 0) + "\n" +
                        intent.getDoubleExtra("avgDataTransferred", 0);
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
        intentFilter.addAction(BTManager.ACTION_LOG_METRICS);
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

    public void registerExpressionSelf(View view) {
        EditText usernameEdit = (EditText) findViewById(R.id.username);
        usernameEdit.setText("self");
        registerExpression(view);
//        testLocation();
    }

    private void testLocation() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d(TAG, "got new location: " + location.getLatitude() + ", " + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    /* button handler */
    public void registerExpression(View view) {
        EditText usernameEdit = (EditText) findViewById(R.id.username);
        String connectTo = usernameEdit.getText().toString();

        if (connectTo.trim().isEmpty()) {
            connectTo = "NEARBY";
        }

        final String expression0 = connectTo + "@cloudtest:value?delay='1000'{MIN,1000}";
        final String expression1 = connectTo + "@light:lux";
        final String expression2 = connectTo + "@movement:x";
        final String expression3 = connectTo + "@gyroscope:x";
        final String expression4 = connectTo + "@magnetometer:x";
        final String expression5 = connectTo + "@battery:level";
        final String expression6 = connectTo + "@proximity:distance";
        final String expression7 = connectTo + "@pressure:pressure";
        final String expression8 = connectTo + "@sound:rms?audio_format=2#sample_interval=100$server_storage=false{ANY,0}";
        final String expression9 = connectTo + "@location:latitude?provider='gps'";
//        final String beaconExpression = connectTo + "@beacon_discovery:estimotenearable{ANY,0}";
//        String expression3 = connectTo + "@light:lux > 10.0";
//        mExpression = connectTo + "@fitness:avg_speed$server_storage=false{ANY,0}";

        if (!mRegistered) {
//            registerSWANSensor(expression0, REQUEST_CODE_0);
            registerSWANSensor(expression1, REQUEST_CODE_1);
//            registerSWANSensor(expression2, REQUEST_CODE_2);
//            registerSWANSensor(expression3, REQUEST_CODE_3);
//            registerSWANSensor(expression4, REQUEST_CODE_4);
//            registerSWANSensor(expression5, REQUEST_CODE_5);
//            registerSWANSensor(expression6, REQUEST_CODE_6);
//            registerSWANSensor(expression7, REQUEST_CODE_7);
//            registerSWANSensor(expression8, REQUEST_CODE_8);
//            registerSWANSensor(expression9, REQUEST_CODE_9);
//            registerSWANSensor(beaconExpression, REQUEST_CODE_BEACON);
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
            registeredExpr.add(String.valueOf(requestCode));

            ExpressionManager.registerValueExpression(this, String.valueOf(requestCode),
                    (ValueExpression) ExpressionFactory.parse(myExpression),
                    new ValueExpressionListener() {
                        /* Registering a listener to process new values from the registered sensor*/
                        @Override
                        public void onNewValues(String id, TimestampedValue[] arg1) {
                            if (arg1 != null && arg1.length > 0) {
                                String value = arg1[0].getValue().toString();
                                tv.setText("Value = " + value);
                                Log.d(TAG, "got new value = " + value);
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
        for(String regExprId : registeredExpr) {
            ExpressionManager.unregisterExpression(this, regExprId);
        }

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


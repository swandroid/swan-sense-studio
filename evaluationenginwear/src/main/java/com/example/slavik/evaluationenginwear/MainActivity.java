package com.example.slavik.evaluationenginwear;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import interdroid.swan.swanmain.ExpressionManager;
import interdroid.swan.swanmain.SensorInfo;
import interdroid.swan.swanmain.SwanException;
import interdroid.swan.swanmain.ValueExpressionListener;
import interdroid.swan.swansong.ExpressionFactory;
import interdroid.swan.swansong.ExpressionParseException;
import interdroid.swan.swansong.TimestampedValue;
import interdroid.swan.swansong.ValueExpression;

public class MainActivity extends Activity {

    private TextView mTextView;

    private static final String TAG = "AccelerometerApp";
    SensorInfo swanSensor;

    /* name of the sensor */
    final String SENSOR_NAME = "movement";

    /* random id */
    public final int REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.textView1);
                initialize();
            }
        });


    }


    public void initialize(){

        //mTextView = (TextView) findViewById(R.id.textView1);

        try {
            swanSensor = ExpressionManager.getSensor(MainActivity.this, SENSOR_NAME);
        } catch (SwanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        registerSWANSensor("self@movement:x{ANY,1000}");

    }


    /* Register expression to SWAN */
    private void registerSWANSensor(final String myExpression){

        try {
            ExpressionManager.registerValueExpression(this, String.valueOf(REQUEST_CODE),
                    (ValueExpression) ExpressionFactory.parse(myExpression),
                    new ValueExpressionListener() {

                        /* Registering a listener to process new values from the registered sensor*/
                        @Override
                        public void onNewValues(String id,
                                                TimestampedValue[] arg1) {
                            if (arg1 != null && arg1.length > 0) {
                                String value = arg1[0].getValue().toString();
                                mTextView.setText("Value = "+value);

                            } else {
                                mTextView.setText("Value = null");
                            }

                        }
                    });
        } catch (SwanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExpressionParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    /* Unregister expression from SWAN */
    private void unregisterSWANSensor(){


        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE));

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("WEAR", "++++++++++++++++++++++= On pause called");
        unregisterSWANSensor();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("WEAR", "++++++++++++++++++++++= On destroy called");
        unregisterSWANSensor();

    }
}

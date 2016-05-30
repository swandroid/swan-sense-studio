package com.example.slavik.evaluationengineapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SensorInfo;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.ValueExpression;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AccelerometerApp";
    SensorInfo swanSensor;

    /* name of the sensor */
    final String SENSOR_NAME = "movement";

    /* random id */
    public final int REQUEST_CODE = 123;

    TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

    }


    public void initialize(){

        tv = (TextView) findViewById(R.id.textView1);

        try {
            swanSensor = ExpressionManager.getSensor(MainActivity.this, SENSOR_NAME);
        } catch (SwanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        registerSWANSensor("self@movement:x{ANY,1000}");

    }


    /* Register expression to SWAN */
    private void registerSWANSensor(String myExpression){

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
                                tv.setText("Value = "+value);

                            } else {
                                tv.setText("Value = null");
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
        unregisterSWANSensor();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSWANSensor();

    }



}

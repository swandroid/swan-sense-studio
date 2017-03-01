package interdroid.swan.crossdevice.queueapp;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;

import interdroid.swan.R;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.ValueExpression;

public class QueuyActivity extends Activity {

    private static final String TAG = "BLEQueuyApp";
    private final int REQUEST_DISCOVERY = 888;
    private final int REQUEST_DISTANCE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queuy);
//        String discoveryExpression = "self@beacon_discovery:estimotenearable{ANY,0}";
        String discoveryExpression = "self@beaconQueue:waitingTime{ANY,0}";

        try {
            ExpressionManager.registerValueExpression(this, String.valueOf(REQUEST_DISCOVERY),
                    (ValueExpression) ExpressionFactory.parse(discoveryExpression),
                new ValueExpressionListener() {
                    /* Registering a listener to process new values from the registered sensor*/
                    @Override
                    public void onNewValues(String id, TimestampedValue[] arg1) {
                        if (arg1 != null && arg1.length > 0) {
                            String value = arg1[0].getValue().toString();
                            Log.w(TAG, "found beacon with id " + value);
//                            ExpressionManager.unregisterExpression(QueuyActivity.this, String.valueOf(REQUEST_DISCOVERY));
//                            getDistanceToBeacon(value);
                        } else {
                            Log.w(TAG, "value is null");
                        }

                    }
                });
        } catch (SwanException e) {
            e.printStackTrace();
        } catch (ExpressionParseException e) {
            e.printStackTrace();
        }
    }

    private void getDistanceToBeacon(String beaconId) {
        String distanceExpression = beaconId + "@beaconDistance:distance{ANY,0}";

        try {
            ExpressionManager.registerValueExpression(this, String.valueOf(REQUEST_DISTANCE),
                    (ValueExpression) ExpressionFactory.parse(distanceExpression),
                new ValueExpressionListener() {
                    /* Registering a listener to process new values from the registered sensor*/
                    @Override
                    public void onNewValues(String id, TimestampedValue[] arg1) {
                        if (arg1 != null && arg1.length > 0) {
                            String value = arg1[0].getValue().toString();
                            Log.w(TAG, "distance to beacon = " + value);
                        } else {
                            Log.w(TAG, "value is null");
                        }

                    }
                });
        } catch (SwanException e) {
            e.printStackTrace();
        } catch (ExpressionParseException e) {
            e.printStackTrace();
        }
    }
}

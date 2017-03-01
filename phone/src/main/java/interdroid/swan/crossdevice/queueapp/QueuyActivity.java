package interdroid.swan.crossdevice.queueapp;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

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
    private final int REQUEST_QUEUE_LOCAL = 888;
    private final int REQUEST_QUEUE_REMOTE = 999;

    private TextView tvWaitingTime = null;
    private TextView tvMaxWaitingTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queuy);
        String queueLocalExpression = "self@beaconQueue:waitingTime{ANY,0}";
        String queueRemoteExpression = "NEARBY@beaconQueue:waitingTime{ANY,0}";
        tvWaitingTime = (TextView) findViewById(R.id.waitingTime);
        tvMaxWaitingTime = (TextView) findViewById(R.id.maxWaitingTime);

        try {
            ExpressionManager.registerValueExpression(this, String.valueOf(REQUEST_QUEUE_LOCAL),
                    (ValueExpression) ExpressionFactory.parse(queueLocalExpression),
                new ValueExpressionListener() {
                    /* Registering a listener to process new values from the registered sensor*/
                    @Override
                    public void onNewValues(String id, TimestampedValue[] arg1) {
                        if (arg1 != null && arg1.length > 0) {
                            long waitingTime = (long) arg1[0].getValue();
                            tvWaitingTime.setText("Waiting time: " + waitingTime);
                        } else {
                            Log.w(TAG, "value is null");
                        }

                    }
                });

            ExpressionManager.registerValueExpression(this, String.valueOf(REQUEST_QUEUE_REMOTE),
                    (ValueExpression) ExpressionFactory.parse(queueRemoteExpression),
                new ValueExpressionListener() {
                    /* Registering a listener to process new values from the registered sensor*/
                    @Override
                    public void onNewValues(String id, TimestampedValue[] arg1) {
                        if (arg1 != null && arg1.length > 0) {
                            Log.d(TAG, "received " + arg1[0].getValue());
                            String waitingTime = (String) arg1[0].getValue();
                            tvMaxWaitingTime.setText("Max waiting time: " + waitingTime);
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

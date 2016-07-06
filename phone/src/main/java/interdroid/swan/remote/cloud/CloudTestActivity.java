package interdroid.swan.remote.cloud;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import interdroid.swan.R;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SensorInfo;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.ValueExpression;

/**
 * Created by Roshan Bharath Das on 05/07/16.
 */
public class CloudTestActivity extends Activity {

    private static final String TAG = "CloudTestActivity";

    /* name of the sensor */
    final String MY_EXPRESSION = "cloud@test:value{ANY,1000} > 0";

    /* random id */
    public final String REQUEST_CODE = "cloud-test-light";

    TextView tv = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloudtest);

        initialize();

    }


    public void initialize(){

        tv = (TextView) findViewById(R.id.textView1);

        registerSWANSensor(MY_EXPRESSION);

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

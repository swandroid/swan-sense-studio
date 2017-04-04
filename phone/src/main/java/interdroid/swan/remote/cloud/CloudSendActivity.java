package interdroid.swan.remote.cloud;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import interdroid.swan.R;
import interdroid.swan.remote.ServerConnectionSocket;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swanmain.TriStateExpressionListener;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.TriState;
import interdroid.swancore.swansong.TriStateExpression;
import interdroid.swancore.swansong.ValueExpression;

/**
 * Created by Roshan Bharath Das on 05/07/16.
 */
public class CloudSendActivity extends Activity {

    private static final String TAG = "CloudSendActivity";



    final String URL = "http://pvsge050.labs.vu.nl:9000/swan/test/send/";
    //final String URL = "http://192.168.1.8:9000/swan/test/send/";

    final String DELAY = "3000";

    final String MY_EXPRESSION = "self@cloudtest:value?delay='"+DELAY+"'$" +
            "server_url="+URL+"~" +
            "server_use_location=false~" +
            "server_http_authorization=NoAuth~" +
            "server_storage=TRUE~" +
            "server_http_body_type=application/json~" +
            "server_http_header=null~" +
            "server_http_body=null~" +
            "server_http_method=POST" +
            "{ANY,0}";

    /* random id */
    public final String ID = "1236";

    TextView tv = null;

    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloudtest);

        addListenerOnButton();

        new CountDownTimer(5*60000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                unregisterSWANSensor();
            }

        }.start();

        initialize();




    }


    public void initialize(){

        tv = (TextView) findViewById(R.id.textView1);

        registerSWANSensor(MY_EXPRESSION);

    }


    public void addListenerOnButton() {

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unregisterSWANSensor();


            }
        });

    }




    /* Register expression to SWAN */
    private void registerSWANSensor(String myExpression){

        try {
            Expression checkExpression =  ExpressionFactory.parse(myExpression);

            if(checkExpression instanceof ValueExpression) {

                final long[] startValue = {System.currentTimeMillis()};
                ExpressionManager.registerValueExpression(this, String.valueOf(ID),
                        (ValueExpression) ExpressionFactory.parse(myExpression),
                        new ValueExpressionListener() {

                            /* Registering a listener to process new values from the registered sensor*/
                            @Override
                            public void onNewValues(String id,
                                                    TimestampedValue[] newValues) {
                                if (newValues != null && newValues.length > 0) {
                                    Log.d(TAG, "new value:"+newValues[0].toString());
                                }
                            }
                        });

            }


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

        ExpressionManager.unregisterExpression(this, String.valueOf(ID));

    }


    @Override
    protected void onPause() {
        super.onPause();
      //  unregisterSWANSensor();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   unregisterSWANSensor();

    }







}

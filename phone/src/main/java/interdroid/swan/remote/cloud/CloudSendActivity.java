package interdroid.swan.remote.cloud;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import interdroid.swan.R;
import interdroid.swan.remote.Constant;
import interdroid.swan.remote.ServerConnection;
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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Roshan Bharath Das on 05/07/16.
 */
public class CloudSendActivity extends Activity {

    private static final String TAG = "CloudSendActivity";



    final String URL = "http://pvsge050.labs.vu.nl:9000/swan/test/send/";
    //final String URL = "http://192.168.1.8:9000/swan/test/send/";

    final String DELAY = "1000";

  /*  final String MY_EXPRESSION = "self@cloudtest:value?delay='"+DELAY+"'$" +
            "server_url="+URL+"~" +
            "server_use_location=false~" +
            "server_http_authorization=NoAuth~" +
            "server_storage=TRUE~" +
            "server_http_body_type=application/json~" +
            "server_http_header=null~" +
            "server_http_body=null~" +
            "server_http_method=POST" +
            "{ANY,0}";
            */
    final String MY_EXPRESSION ="self@cloudtest:value?delay='1000'{MEAN,1000}";

    final String MY_EXPRESSION1 = "self@cloudtest:value";
    final String INIT_EXPRESSION = "cloud@profiler:value?case=1{ANY,0}";

    ServerConnection serverConnection = null;

    /* random id */
    public final String ID = "1236";
    public final String INIT_ID = "1235";

    TextView tv = null;

    Button button;

    protected Callback<Object> cb = new Callback<Object>() {
        @Override
        public void success(Object o, Response response) {

            Log.d(TAG,"Success:"+response.toString());
        }

        @Override
        public void failure(RetrofitError error) {

            Log.d(TAG,"Failure:"+error);

        }
    };

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

        //initialize();
        initializeSimple();



    }


    public void initializeSimple(){

        registerSWANSensorSimple(MY_EXPRESSION, ID);
    }

    public void initialize(){

        tv = (TextView) findViewById(R.id.textView1);

        Bundle httpConfiguration = new Bundle();
        httpConfiguration.putString(Constant.SERVER_HTTP_METHOD,"POST");
        httpConfiguration.putString(Constant.SERVER_HTTP_AUTHORIZATION,Constant.NULL);
        httpConfiguration.putSerializable(Constant.SERVER_HTTP_HEADER,Constant.NULL);
        httpConfiguration.putString(Constant.SERVER_HTTP_BODY,Constant.NULL);
        httpConfiguration.putString(Constant.SERVER_HTTP_BODY_TYPE,"application/json");
        httpConfiguration.putString(Constant.SERVER_URL, "http://pvsge050.labs.vu.nl:9000/swan/test/send/");

        serverConnection = new ServerConnection(httpConfiguration);

        registerSWANSensor(INIT_EXPRESSION, INIT_ID);

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
    private void registerSWANSensorSimple(final String myExpression, String id){

        try {
            Expression checkExpression =  ExpressionFactory.parse(myExpression);

            if(checkExpression instanceof ValueExpression) {

                //final long[] startValue = {System.currentTimeMillis()};
                ExpressionManager.registerValueExpression(this, String.valueOf(id),
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


    /* Register expression to SWAN */
    private void registerSWANSensor(final String myExpression, String id){

        try {
            Expression checkExpression =  ExpressionFactory.parse(myExpression);

            if(checkExpression instanceof ValueExpression) {

                final long[] startValue = {System.currentTimeMillis()};
                ExpressionManager.registerValueExpression(this, String.valueOf(id),
                        (ValueExpression) ExpressionFactory.parse(myExpression),
                        new ValueExpressionListener() {

                            /* Registering a listener to process new values from the registered sensor*/
                            @Override
                            public void onNewValues(String id,
                                                    TimestampedValue[] newValues) {
                                if (newValues != null && newValues.length > 0) {
                                    Log.d(TAG, "new value:"+newValues[0].toString());

                                    if(myExpression.equals(INIT_EXPRESSION)) {
                                        registerSWANSensor(MY_EXPRESSION1, ID);
                                    } else {
                                        long endValue = System.currentTimeMillis();
                                        long result = (endValue- startValue[0]);
                                        startValue[0] = endValue;
                                        Log.d(TAG, "swan took " + result + "ms");

                                        HashMap<String, Object> serverData = new HashMap<String, Object>();

                                        serverData.put("id", id);
                                        //serverData.put("channel",valuePath);
                                        serverData.put("field1", result + "");
                                        serverData.put("time", System.currentTimeMillis());
                                        serverConnection.useHttpMethod(serverData, cb);

                                        ExpressionManager.unregisterExpression(CloudSendActivity.this, String.valueOf(ID));
                                    }
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

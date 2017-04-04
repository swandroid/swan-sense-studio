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

  //  final String MY_EXPRESSION = "cloud@profiler:value?case=0{ANY,0} > 1";

   // final String MY_EXPRESSION = "wear@heartrate:heart_rate{ANY,1000}";
  //  final String MY_EXPRESSION = "self@wear_heartrate:heart_rate{ANY,0}";


  //  final String MY_EXPRESSION = "self@wear_movement:x{ANY,0}";
  //  final String MY_EXPRESSION = "wear@movement:x{ANY,0}";

   // final String MY_EXPRESSION = "cloud@profiler:value?case=1{ANY,0} > 1";

  //  final String MY_EXPRESSION = "self@profiler:value?case=0{ANY,0} > 1";

    final String MY_EXPRESSION = "self@light:lux{ANY,0}";

    final String url = "dsa-devel.labs.vu.nl";
    //final String url = "localhost";
    final int port = 7782;
    final String cloudSensorName = "tree";
    final String cloudSensorValuepath = "leaves";


    /* random id */
    public final String REQUEST_CODE = "cloud-send";

    TextView tv = null;

    Button button;

    ServerConnectionSocket serverConnectionSocket =null;

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

        serverConnectionSocket = new ServerConnectionSocket(url,port,REQUEST_CODE);
        new Thread(serverConnectionSocket).start();


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
                ExpressionManager.registerValueExpression(this, String.valueOf(REQUEST_CODE),
                        (ValueExpression) ExpressionFactory.parse(myExpression),
                        new ValueExpressionListener() {

                            /* Registering a listener to process new values from the registered sensor*/
                            @Override
                            public void onNewValues(String id,
                                                    TimestampedValue[] newValues) {
                                if (newValues != null && newValues.length > 0) {
                                    JSONObject jsonObject = new JSONObject();

                                    try {
                                        jsonObject.put("id", id);
                                        jsonObject.put("A", "V");
                                        jsonObject.put("data", newValues[0].getValue());
                                        jsonObject.put("time", newValues[0].getTimestamp());
                                        jsonObject.put("sensor", cloudSensorName);
                                        jsonObject.put("valuepath", cloudSensorValuepath);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //Log.e("Roshan","Before writing data:"+jsonObject.toString());
                                    //serverConnectionSocket.sendResult(jsonObject.toString());
                                    //serverConnectionSocket.execute(jsonObject.toString());
                                    try {
                                        if(serverConnectionSocket.getObjectOutputStream()!=null){
                                            //Log.e("Roshan","Writing data:"+jsonObject.toString());
                                            serverConnectionSocket.getObjectOutputStream().writeObject(""+jsonObject.toString());
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });

            }
            else if(checkExpression instanceof TriStateExpression){

                ExpressionManager.registerTriStateExpression(this, String.valueOf(REQUEST_CODE),
                        (TriStateExpression) ExpressionFactory.parse(myExpression), new TriStateExpressionListener() {
                            @Override
                            public void onNewState(String id, long timestamp, TriState newState) {
                                JSONObject jsonObject = new JSONObject();
                                //SendEmail.sendEmail();
                                //sendFacebookMessage.sendResult(senderid, newState, ws);
                                try {
                                    jsonObject.put("id", id);
                                    jsonObject.put("A", "T");
                                    jsonObject.put("data", newState);
                                    jsonObject.put("time", timestamp);
                                    jsonObject.put("sensor", cloudSensorName);
                                    jsonObject.put("valuepath", cloudSensorValuepath);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //serverConnectionSocket.sendResult(jsonObject.toString());
                                //serverConnectionSocket.execute(jsonObject.toString());

                                try {
                                    if(serverConnectionSocket.getObjectOutputStream()!=null){
                                        //Log.e("Roshan","Writing data:"+jsonObject.toString());
                                        serverConnectionSocket.getObjectOutputStream().writeObject(""+jsonObject.toString());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
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

        ExpressionManager.unregisterExpression(this, String.valueOf(REQUEST_CODE));

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

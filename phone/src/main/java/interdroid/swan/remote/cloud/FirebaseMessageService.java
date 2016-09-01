package interdroid.swan.remote.cloud;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swan.sensors.cuckoo.ProfilerSensor;
import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.TriState;

/**
 * Created by Roshan Bharath Das on 28/06/16.
 */
public class FirebaseMessageService extends FirebaseMessagingService{

    CloudManager cloudManager = CloudManager.getCreatedInstance();

    static int noOfTimes = 0;

    private static final String TAG = "FirebaseMessageService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Log.d(TAG, "Message body: "+ remoteMessage.getData().get("field"));


        //TODO: add proper implementation for handling messages coming from cuckoo server
        String message = remoteMessage.getData().get("field");
        if (message == null) {
            Log.d(TAG, "it's cuckoo");
            CloudManager.getCreatedInstance().sendCuckooValue(remoteMessage.getData().get("value"));
            return;
        }

        try {
            JSONObject jsonResult = new JSONObject(message);

            Result result = null;
            if(jsonResult.has("id") && jsonResult.has("action") && jsonResult.has("data") && jsonResult.has("timestamp")) {


                if(jsonResult.getString("action").contentEquals("register-value")) {

                    noOfTimes++;

                    TimestampedValue[]  timestampedValues = new TimestampedValue[1];

                    timestampedValues[0] = new TimestampedValue(jsonResult.get("data"),(long) jsonResult.get("timestamp"));

                    result = new Result(timestampedValues,(long) jsonResult.get("timestamp"));


                }
                else if(jsonResult.getString("action").contentEquals("register-tristate")){

                    noOfTimes++;
                    TriState triState;
                    if(jsonResult.getString("data").contentEquals("true")){
                        triState= TriState.TRUE;
                    }
                    else if(jsonResult.getString("data").contentEquals("false")){
                        triState=TriState.FALSE;
                    }
                    else{
                        triState=TriState.UNDEFINED;
                    }
                    result = new Result((long) jsonResult.get("timestamp"),triState);
                    result.setDeferUntilGuaranteed(false);
                }


                if(result!=null && cloudManager!=null) {
                    cloudManager.sendResult(jsonResult.getString("id"), jsonResult.getString("action"), Converter.objectToString(result));
                }
            }
            else{
                noOfTimes= noOfTimes+2;
                Log.e("Roshan","SWAN Cloud Communication "+noOfTimes);
            }
            //TODO: Handle unregister request
          //  else if(jsonResult.has("id") && jsonResult.has("action")){
           //     cloudManager.sendResult(jsonResult.getString("id"), jsonResult.getString("action"), Converter.objectToString(result));

          //  }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




}

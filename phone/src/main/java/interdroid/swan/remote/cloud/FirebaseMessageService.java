package interdroid.swan.remote.cloud;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


        String message = remoteMessage.getData().get("field");

        try {
            JSONObject jsonResult = new JSONObject(message);

            Result result = null;
            if(jsonResult.has("id") && jsonResult.has("A") && jsonResult.has("data") && jsonResult.has("time")) {


                if(jsonResult.getString("A").contentEquals("V")) {

                    noOfTimes++;

                    TimestampedValue[]  timestampedValues = new TimestampedValue[1];

                    timestampedValues[0] = new TimestampedValue(jsonResult.get("data"),(long) jsonResult.get("time"));

                    result = new Result(timestampedValues,(long) jsonResult.get("time"));


                }
                else if(jsonResult.getString("A").contentEquals("T")){

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
                    result = new Result((long) jsonResult.get("time"),triState);
                    result.setDeferUntilGuaranteed(false);
                }


                if(result!=null && cloudManager!=null) {
                    cloudManager.sendResult(jsonResult.getString("id"), jsonResult.getString("A"), Converter.objectToString(result));
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

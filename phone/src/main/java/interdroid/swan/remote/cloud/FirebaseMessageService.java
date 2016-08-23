package interdroid.swan.remote.cloud;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.TriState;

/**
 * Created by Roshan Bharath Das on 28/06/16.
 */
public class FirebaseMessageService extends FirebaseMessagingService{

    CloudManager cloudManager = CloudManager.getCreatedInstance();


    private static final String TAG = "FirebaseMessageService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Log.d(TAG, "Message body: "+ remoteMessage.getData().get("field"));


        String message = remoteMessage.getData().get("field");

        try {
            JSONObject jsonResult = new JSONObject(message);

            Result result = null;
            if(jsonResult.has("id") && jsonResult.has("action") && jsonResult.has("data") && jsonResult.has("timestamp")) {


                if(jsonResult.getString("action").contentEquals("register-value")) {

                    result = new Result((TimestampedValue[]) jsonResult.get("data"),(long) jsonResult.get("timestamp"));


                }
                else if(jsonResult.getString("action").contentEquals("register-tristate")){
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

                if(result!=null) {
                    cloudManager.sendResult(jsonResult.getString("id"), jsonResult.getString("action"), Converter.objectToString(result));
                }
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

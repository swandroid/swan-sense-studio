package interdroid.swan.remote.cloud;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

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


            if(jsonResult.has("id") && jsonResult.has("action") && jsonResult.has("data")) {
                cloudManager.sendResult(jsonResult.getString("id"), jsonResult.getString("action"), jsonResult.getString("data"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }




}

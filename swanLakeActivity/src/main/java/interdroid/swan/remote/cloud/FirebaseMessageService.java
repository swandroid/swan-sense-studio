package interdroid.swan.remote.cloud;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Roshan Bharath Das on 28/06/16.
 */
public class FirebaseMessageService extends FirebaseMessagingService{

    CloudManager cloudManager = new CloudManager();

    private static final String TAG = "FirebaseMessageService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Log.d(TAG, "Message body: "+remoteMessage.getData().toString());
        //Calling method to generate notification

        //TODO: Send result update to evaluation engine

        //cloudManager.sendResult(remoteMessage.getData());

    }




}

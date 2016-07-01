package interdroid.swan.remote.cloud;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Roshan Bharath Das on 28/06/16.
 */
public class FirebaseTokenService extends FirebaseInstanceIdService {


    private static final String TAG = "FirebaseTokenService";



    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //TODO: Send updated token to the server

    }


}

package interdroid.swan.crossdevice.swanplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vladimir on 11/25/15.
 */
public class TestReceiver extends BroadcastReceiver {

    private static final String TAG = "TestReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received " + intent);
    }
}

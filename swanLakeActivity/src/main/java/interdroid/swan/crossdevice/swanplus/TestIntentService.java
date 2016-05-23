package interdroid.swan.crossdevice.swanplus;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vladimir on 2/2/16.
 */
public class TestIntentService extends IntentService {

    public static final String TAG = "TestIntentService";

    public TestIntentService() {
        super("TestIntentService");
    }

    public TestIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String dataString = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.d(TAG, dataString);
    }
}

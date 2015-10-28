package interdroid.swan.rss_sensor;

import android.app.Application;

/**
 * Created by steven on 11/10/15.
 */
public class RssSensorApp extends Application {

    private static RssSensorApp sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
    }

    public static RssSensorApp getInstance() {
        return sInstance;
    }

}

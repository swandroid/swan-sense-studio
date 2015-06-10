package interdroid.swan.jsonsensor;

import android.app.Application;

/**
 * Created by steven on 29/08/14.
 */
public class JsonSensorApp extends Application {

    private static JsonSensorApp sInstance;


    @Override
    public void onCreate() {
        sInstance = this;
    }

    public static JsonSensorApp getInstance() {
        return sInstance;
    }

}

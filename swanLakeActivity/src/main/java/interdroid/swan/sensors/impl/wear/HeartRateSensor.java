package interdroid.swan.sensors.impl.wear;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swan.sensors.impl.wear.shared.RemoteSensorManager;

/**
 * Created by slavik on 2/22/16.
 */
public class HeartRateSensor extends AbstractSwanSensor {

    public static final String TAG = "HeartRateSensor";
    public static final String HEART_RATE = "heart_rate";
    RemoteSensorManager sensorMngr;


    ArrayList<Integer>  ids = new ArrayList<>();

    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.wear_heartrate_preferences;
        }

    }
    @Override
    public void initDefaultConfiguration(Bundle defaults) {
        Log.d("Heart Rate Sensor", "Init default configuration");
        sensorMngr = RemoteSensorManager.getInstance(this);
    }

    @Override
    public void register(String id, String valuePath, Bundle configuration) throws IOException {
        SENSOR_NAME = "Wear Heart Rate Sensor";
        Log.d("Heart RATE", "Register");
    }

    @Override
    public void unregister(String id) {
        Log.d("Heart RATE", "Register");
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{ "heart_rate" };
    };

    @Override
    public void onConnected() {

    }
}

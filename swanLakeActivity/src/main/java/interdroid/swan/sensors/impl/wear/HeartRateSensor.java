package interdroid.swan.sensors.impl.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swan.sensors.impl.wear.shared.AbstractWearSensor;
import interdroid.swan.sensors.impl.wear.shared.RemoteSensorManager;
import interdroid.swan.sensors.impl.wear.shared.data.WearSensor;
import interdroid.swan.sensors.impl.wear.shared.data.SensorDataPoint;

/**
 * Created by veaceslav on 2/22/16.
 */
public class HeartRateSensor extends AbstractWearSensor {

    public static final String TAG = "HeartRateSensor";
    public static final String HEART_RATE = "Heart Rate";
    public static final String VALUE_PATH = "heart_rate";



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
    }

    @Override
    public void register(String id, String valuePath, Bundle configuration) throws IOException {
        SENSOR_NAME = "Wear Heart Rate Sensor";
        sensor_name = "Heart Rate";
        valuePathMappings.put(VALUE_PATH, 0);
        Log.d("Heart RATE", "Register++++++++++++++++++++++++++++++++");

        super.register(id, valuePath, configuration);
    }


    @Override
    public String[] getValuePaths() {
        return new String[]{ "heart_rate" };
    };


}

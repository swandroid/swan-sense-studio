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
import interdroid.swan.sensors.impl.wear.shared.RemoteSensorManager;
import interdroid.swan.sensors.impl.wear.shared.data.WearSensor;
import interdroid.swan.sensors.impl.wear.shared.data.SensorDataPoint;

/**
 * Created by veaceslav on 2/22/16.
 */
public class HeartRateSensor extends AbstractSwanSensor {

    public static final String TAG = "HeartRateSensor";
    public static final String HEART_RATE = "Heart Rate";
    public static final String VALUE_PATH = "heart_rate";
    String id;

    private SensorUpdate   updateReceiver = new SensorUpdate();


    ArrayList<Integer>  ids = new ArrayList<>();

    private class SensorUpdate extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equalsIgnoreCase(RemoteSensorManager.UPDATE_MESSAGE)){
                Bundle extra = intent.getExtras();
                //String who = extra.getString("sensor");
                WearSensor s = (WearSensor)extra.getSerializable("sensor");
                SensorDataPoint d = (SensorDataPoint)extra.getSerializable("data");
                if(s.getName().equals(HEART_RATE)) {
                    Log.d(TAG, "Got update+++++++++++++++++++++" + s.getName());
                    float[] dataz = d.getValues();
                    if(dataz[0] != 0.0f)
                        putValueTrimSize(VALUE_PATH, id, System.currentTimeMillis(), dataz[0]);
                }

            }

        }
    }

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

        registerReceiver(updateReceiver, new IntentFilter(RemoteSensorManager.UPDATE_MESSAGE));
        SENSOR_NAME = "Wear Heart Rate Sensor";
        Log.d("Heart RATE", "Register++++++++++++++++++++++++++++++++");
    }

    @Override
    public void unregister(String id) {

        Log.d("Heart RATE", "Unregister++++++++++++++++++++++++++");
        this.id = id;
        //unregisterReceiver(messageReceiver);
        unregisterReceiver(updateReceiver);
        RemoteSensorManager.getInstance(this).stopMeasurement();
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{ "heart_rate" };
    };

    @Override
    public void onConnected() {

        RemoteSensorManager.getInstance(this).startMeasurement();
    }

    public void registerListener(){
        Log.d(TAG, "Registering Updater +++++++++++++++");
        registerReceiver(updateReceiver, new IntentFilter(RemoteSensorManager.UPDATE_MESSAGE));
    }

}

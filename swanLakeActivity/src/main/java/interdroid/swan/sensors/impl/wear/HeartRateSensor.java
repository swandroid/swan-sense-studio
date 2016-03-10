package interdroid.swan.sensors.impl.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swan.sensors.impl.wear.shared.RemoteSensorManager;
import interdroid.swan.sensors.impl.wear.shared.data.Sensor;
import interdroid.swan.sensors.impl.wear.shared.events.BusProvider;
import interdroid.swan.sensors.impl.wear.shared.events.NewSensorEvent;

/**
 * Created by slavik on 2/22/16.
 */
public class HeartRateSensor extends AbstractSwanSensor {

    public static final String TAG = "HeartRateSensor";
    public static final String HEART_RATE = "heart_rate";
    RemoteSensorManager sensorMngr;
    String id;

    private newMessage messageReceiver = new newMessage();


    ArrayList<Integer>  ids = new ArrayList<>();

    public class newMessage extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equalsIgnoreCase("MyMessage")){
                Bundle extra = intent.getExtras();
                //String who = extra.getString("sensor");
                Sensor s = (Sensor)extra.getSerializable("sensor");
                Log.d(TAG, "Got message+++++++++++++++++++++" + s.getName());
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
        sensorMngr = RemoteSensorManager.getInstance(getApplicationContext());


    }

    @Override
    public void register(String id, String valuePath, Bundle configuration) throws IOException {

        if(sensorMngr == null)
            sensorMngr = RemoteSensorManager.getInstance(getApplicationContext());

        registerReceiver(messageReceiver, new IntentFilter("MyMessage"));
        SENSOR_NAME = "Wear Heart Rate Sensor";
        Log.d("Heart RATE", "Register++++++++++++++++++++++++++++++++");

        //BusProvider.getInstance().register(this);

        List<Sensor> sensors = RemoteSensorManager.getInstance(getApplicationContext()).getSensors();

        sensorMngr.startMeasurement();
        for(Sensor s : sensors){
            Log.d("Heart rate", "Found Sensor" + s.getName());
        }


    }

    @Override
    public void unregister(String id) {

        Log.d("Heart RATE", "Unregister++++++++++++++++++++++++++");
        this.id = id;
        unregisterReceiver(messageReceiver);
        sensorMngr.stopMeasurement();
       // BusProvider.getInstance().unregister(this);
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{ "heart_rate" };
    };

    @Override
    public void onConnected() {

        sensorMngr.startMeasurement();
    }

    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
        Sensor s = event.getSensor();

        Log.d("Heart Rate", "Got new sensor " + s.getName() + "++++++++++++++++++++++");

        putValueTrimSize(HEART_RATE,id, System.currentTimeMillis(), 13);
    }
}

package interdroid.swan.sensors.impl.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swan.sensors.impl.wear.shared.RemoteSensorManager;
import interdroid.swan.sensors.impl.wear.shared.data.SensorDataPoint;
import interdroid.swan.sensors.impl.wear.shared.data.SensorNames;
import interdroid.swan.sensors.impl.wear.shared.data.WearSensor;

/**
 * Created by Veaceslav Munteanu on 14-March-16.
 * @email veaceslav.munteanu90@gmail.com
 */
public class WearMovementSensor extends AbstractSwanSensor {

    public static final String X_FIELD = "x";
    public static final String Y_FIELD = "y";
    public static final String Z_FIELD = "z";
    public static final String TOTAL_FIELD = "total";

    public static final String TAG = "MovementSensor";

    SensorUpdate updateReceiver = new SensorUpdate();

    ArrayList<String> ids = new ArrayList<>();

    /**
     * The configuration activity for this sensor.
     *
     * @author Veaceslav Munteanu
     *
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.movement_preferences;
        }

    }

    private class SensorUpdate extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action.equalsIgnoreCase(RemoteSensorManager.UPDATE_MESSAGE)){
                Bundle extra = intent.getExtras();
                WearSensor s = (WearSensor)extra.getSerializable("sensor");
                SensorDataPoint d = (SensorDataPoint)extra.getSerializable("data");
                if(s.getName().equals(SensorNames.getInstance().getName(android.hardware.Sensor.TYPE_ACCELEROMETER))) {
                    Log.d(TAG, "Got update+++++++++++++++++++++" + s.getName());
                    float[] dataz = d.getValues();

                    long time = System.currentTimeMillis();
                    for(String id : ids) {
                        putValueTrimSize(X_FIELD, id, time, dataz[0]);
                        putValueTrimSize(Y_FIELD, id, time, dataz[1]);
                        putValueTrimSize(Z_FIELD, id, time, dataz[2]);
                    }
                }
            }
        }
    }

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void register(String id, String valuePath, Bundle configuration) throws IOException {
        ids.add(id);
        registerReceiver(updateReceiver, new IntentFilter(RemoteSensorManager.UPDATE_MESSAGE));
        SENSOR_NAME = "Wear Movement Sensor";
    }

    @Override
    public void unregister(String id) {
        RemoteSensorManager.getInstance(this).stopMeasurement();
    }

    @Override
    public String[] getValuePaths() {
        return new String[] { X_FIELD, Y_FIELD, Z_FIELD, TOTAL_FIELD };
    }

    @Override
    public void onConnected() {
        RemoteSensorManager.getInstance(this).startMeasurement();

    }


}

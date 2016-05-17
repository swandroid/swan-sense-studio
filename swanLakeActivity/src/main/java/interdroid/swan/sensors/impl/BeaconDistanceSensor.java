package interdroid.swan.sensors.impl;

import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.crossdevice.beacon.AbstractBeaconSensor;
import interdroid.swan.crossdevice.beacon.BeaconUtils;
import interdroid.swan.sensors.AbstractConfigurationActivity;

/**
 * Created by Veaceslav Munteanu on 5/9/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class BeaconDistanceSensor  extends AbstractBeaconSensor{

    public  String TAG = "DistanceBeaconSensor";

    public final String DISTANCE_VALUEPATH = "distance";


    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.beacon_distance_preferences;
        }
    }


    @Override
    public void setData( Collection<Beacon> beacons, long time) {

        HashMap<String, Object> result = new HashMap<>();
        for(Beacon beacon : beacons){


            if(BeaconUtils.isEstimoteNearable(beacon)){
                Log.d(TAG, "Estimote nearable power " + beacon.getTxPower() + " " + getNearableTxPower(beacon));
                double distance = Beacon.getDistanceCalculator().calculateDistance(getNearableTxPower(beacon),
                                                                                    (double)beacon.getRssi());
                result.put(getBeaconId(beacon), distance);
            } else {
                result.put(getBeaconId(beacon), beacon.getDistance());
            }
        }
        if(!result.isEmpty()) {
            for (Map.Entry<String, String> id : ids.entrySet()) {
                putValueTrimSize(id.getValue(), id.getKey(),
                        time,
                        result);
            }
        }
    }

    @Override
    protected String getSensorName() {
        return TAG;
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{ DISTANCE_VALUEPATH };
    }

    private int getNearableTxPower(Beacon beacon){
        int[] powerLevels = {-30, -20, -16, 0, -8, -4, -12, 4};
        int powerIndex = (byte)beacon.getTxPower() & 0x0f;
        Log.d(TAG, "Power Index " + powerIndex);
        return powerLevels[powerIndex];
    }
}

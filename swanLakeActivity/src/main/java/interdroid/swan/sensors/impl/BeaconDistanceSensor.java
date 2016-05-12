package interdroid.swan.sensors.impl;

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
            result.put(getBeaconId(beacon), beacon.getDistance());
//            if(BeaconUtils.isEddystoneUID(beacon) || BeaconUtils.isEddystoneURL(beacon)) {
//            }
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
}

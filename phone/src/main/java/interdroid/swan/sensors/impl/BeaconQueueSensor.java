package interdroid.swan.sensors.impl;

import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.crossdevice.beacon.BeaconUtils;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

/**
 * Created by vladimir on 2/28/17.
 */

public class BeaconQueueSensor extends BeaconDiscoverySensor {

    private static final String TAG = "BeaconQueueSensor";
    private static final String WAITING_TIME = "waitingTime";
    private static final int QUEUE_BEACON_MAJOR_ID = 4023;

    private long discoveryTime = 0;

    @Override
    protected String getSensorName() {
        return TAG;
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{WAITING_TIME};
    }

    public static class ConfigurationActivity extends AbstractConfigurationActivity {
        @Override
        public int getPreferencesXML() {
            return R.xml.beacon_queue_preferences;
        }
    }

    @Override
    public void setData(HashMap<String, Beacon> beacons, long time) {
        for (Beacon beacon : beacons.values()) {
            // we check if the found beacon is a queue beacon by looking at its major id
            if(BeaconUtils.isAppleIBeacon(beacon) && beacon.getId2().toInt() == QUEUE_BEACON_MAJOR_ID) {
                long waitingTime = 0;

                if(discoveryTime == 0) {
                    discoveryTime = System.currentTimeMillis();
                } else {
                    waitingTime = System.currentTimeMillis() - discoveryTime;

                }

                // we set the new value for each registered expression
                for(Map.Entry<String, String> id : ids.entrySet()) {
                    putValueTrimSize(WAITING_TIME, id.getKey(), time, waitingTime);
                }
            }
        }
    }
}

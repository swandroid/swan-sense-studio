package interdroid.swan.sensors.impl;

import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.crossdevice.beacon.AbstractBeaconSensor;
import interdroid.swan.crossdevice.beacon.BeaconTypes;
import interdroid.swan.crossdevice.beacon.BeaconUtils;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

/**
 * Created by Veaceslav Munteanu on 5/9/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class BeaconDistanceSensor extends AbstractBeaconSensor {

    public String TAG = "DistanceBeaconSensor";

    public final String DISTANCE_VALUEPATH = "distance";

    double INVALID_DISTANCE = -9999;


    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.beacon_distance_preferences;
        }
    }


    @Override
    public void setData(HashMap<String, Beacon> beacons, long time) {

        Beacon beacon = getRequiredBeacon(locationString, beacons, BeaconTypes.ANY);

        if (beacon == null) {
            Log.e(TAG, "Error: Beacon is null");
            return;
        }

        double distance = INVALID_DISTANCE;

        if (BeaconUtils.isEstimoteNearable(beacon)) {
            Log.d(TAG, "Estimote nearable power " + beacon.getTxPower() + " " + getNearableTxPower(beacon));
            distance = Beacon.getDistanceCalculator().calculateDistance(getNearableTxPower(beacon),
                    (double) beacon.getRssi());
        } else {
            distance = beacon.getDistance();
        }

        for (Map.Entry<String, String> id : ids.entrySet()) {
            putValueTrimSize(id.getValue(), id.getKey(),
                    time,
                    distance);
        }
    }

    @Override
    protected String getSensorName() {
        return TAG;
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{DISTANCE_VALUEPATH};
    }

    private int getNearableTxPower(Beacon beacon) {
        int[] powerLevels = {-30, -20, -16, 0, -8, -4, -12, 4};
        int powerIndex = (byte) beacon.getTxPower() & 0x0f;
        Log.d(TAG, "Power Index " + powerIndex);
        return powerLevels[powerIndex];
    }
}

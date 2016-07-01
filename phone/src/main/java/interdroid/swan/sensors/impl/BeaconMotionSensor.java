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
 * Created by Veaceslav Munteanu on 5/13/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class BeaconMotionSensor extends AbstractBeaconSensor {

    public final String MOVING_VALUEPATH = "isMoving";
    public final String CURRENT_MOTION_TIME = "currentMotionTime";
    public final String PREVIOUS_MOTION_TIME = "previousMotionTime";

    public final String TAG = "BeaconMotionSensor";

    @Override
    public void setData(HashMap<String, Beacon> beacons, long time) {


        Beacon beacon = getRequiredBeacon(locationString, beacons, BeaconTypes.ESTIMOTE_NEARABLE);

        if (beacon == null) {
            Log.e(TAG, "Error: Beacon is null");
            return;
        }

        if (!BeaconUtils.isEstimoteNearable(beacon)) {
            return;
        }

        for (Map.Entry<String, String> id : ids.entrySet()) {
            if (id.getValue().equals(MOVING_VALUEPATH)) {
                putValueTrimSize(id.getValue(), id.getKey(),
                        time,
                        isMoving(beacon));
            }

            if (id.getValue().equals(CURRENT_MOTION_TIME)) {
                putValueTrimSize(id.getValue(), id.getKey(),
                        time,
                        currentMotionTime(beacon));
            }

            if (id.getValue().equals(PREVIOUS_MOTION_TIME)) {
                putValueTrimSize(id.getValue(), id.getKey(),
                        time,
                        currentMotionTime(beacon));
            }
        }

    }

    @Override
    protected String getSensorName() {
        return TAG;
    }

    private int isMoving(Beacon beacon) {
        byte val = beacon.getDataFields().get(3).byteValue();

        if ((val & 0x40) != 0) {
            return 1;
        } else {
            return 0;
        }
    }

    private int currentMotionTime(Beacon beacon) {
        return convertMotionStateDuration(beacon.getDataFields().get(7).byteValue());
    }

    private int previousMotionTime(Beacon beacon) {
        return convertMotionStateDuration(beacon.getDataFields().get(8).byteValue());
    }

    private int convertMotionStateDuration(byte raw) {
        byte unit = (byte) ((raw >> 6) & 0x03);
        int duration = (raw & 0x3f);

        if (unit == 1) {
            duration *= 60;
        } else if (unit == 2) {
            duration *= (60 * 60);
        }

        return duration;
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{MOVING_VALUEPATH, CURRENT_MOTION_TIME, PREVIOUS_MOTION_TIME};
    }

    /**
     * The configuration activity for this sensor.
     *
     * @author Veaceslav Munteanu
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.beacon_motion_preferences;
        }

    }
}

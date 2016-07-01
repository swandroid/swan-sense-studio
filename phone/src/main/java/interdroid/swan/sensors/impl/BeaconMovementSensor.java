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
public class BeaconMovementSensor extends AbstractBeaconSensor {

    public static final String TAG = "BeaconMovementSensor";

    public static final String X_FIELD = "x";
    public static final String Y_FIELD = "y";
    public static final String Z_FIELD = "z";
    public static final String TOTAL_FIELD = "total";

    @Override
    public void setData(HashMap<String, Beacon> beacons, long time) {

        Beacon beacon = getRequiredBeacon(locationString, beacons, BeaconTypes.ESTIMOTE_NEARABLE);

        if (beacon == null) {
            Log.e(TAG, "Error: Beacon is null");
            return;
        }

        for (Map.Entry<String, String> id : ids.entrySet()) {
            if (id.getValue().equals(X_FIELD)) {
                putValueTrimSize(id.getValue(), id.getKey(),
                        time,
                        getXValue(beacon));
            }

            if (id.getValue().equals(Y_FIELD)) {
                putValueTrimSize(id.getValue(), id.getKey(),
                        time,
                        getYValue(beacon));
            }

            if (id.getValue().equals(Z_FIELD)) {
                putValueTrimSize(id.getValue(), id.getKey(),
                        time,
                        getZValue(beacon));
            }
        }

    }

    @Override
    protected String getSensorName() {
        return TAG;
    }

    public double getXValue(Beacon beacon) {

        return beacon.getDataFields().get(4).byteValue() * 15.625;
    }

    public double getYValue(Beacon beacon) {
        return beacon.getDataFields().get(5).byteValue() * 15.625;
    }

    public double getZValue(Beacon beacon) {
        return beacon.getDataFields().get(6).byteValue() * 15.625;
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{X_FIELD, Y_FIELD, Z_FIELD, TOTAL_FIELD};
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
            return R.xml.movement_preferences;
        }

    }
}

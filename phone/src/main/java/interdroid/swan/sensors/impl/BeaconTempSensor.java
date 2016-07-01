package interdroid.swan.sensors.impl;

import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.crossdevice.beacon.AbstractBeaconSensor;
import interdroid.swan.crossdevice.beacon.BeaconTypes;
import interdroid.swan.crossdevice.beacon.BeaconUtils;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

/**
 * Created by Veaceslav Munteanu on 5/3/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class BeaconTempSensor extends AbstractBeaconSensor {

    public static final String TEMPERATURE_FIELD = "temperature";
    public static final String TAG = "BeaconTempSensor";

    float INVALID_TEMP = -99999;

    public static class ConfigurationActivity extends AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.temperature_preferences;
        }
    }

    @Override
    public void setData(HashMap<String, Beacon> beacons, long time) {

        Beacon beacon = getRequiredBeacon(locationString, beacons,
                BeaconTypes.ESTIMOTE_NEARABLE | BeaconTypes.EDDYSTOME_TML);

        if (beacon == null) {
            Log.e(TAG, "Error: Beacon is null");
            return;
        }

        float finalTemp = INVALID_TEMP;
        if (BeaconUtils.isEddystoneUID(beacon) || BeaconUtils.isEddystoneURL(beacon)) {
            if (beacon.getExtraDataFields().size() > 0) {
                long temp = beacon.getExtraDataFields().get(2);
                finalTemp = getTemp(temp);
            }
        }

        if (BeaconUtils.isEstimoteNearable(beacon)) {
            long temp = beacon.getDataFields().get(2);
            finalTemp = getNearableTemp(temp);
        }

        if (finalTemp != INVALID_TEMP) {
            for (Map.Entry<String, String> id : ids.entrySet()) {
                putValueTrimSize(id.getValue(), id.getKey(),
                        time,
                        finalTemp);
            }
        }
    }

    @Override
    protected String getSensorName() {
        return TAG;
    }

    @Override
    public String[] getValuePaths() {
        return new String[]{TEMPERATURE_FIELD};
    }

    public float getTemp(long rawTemp) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);
        buffer.putLong(rawTemp);
        byte tempData[] = buffer.array();
        byte tempIntegral = tempData[6];
        int tempFractional = tempData[7] & 0xff;

        float finalTemp = tempIntegral + (tempFractional / 256.0f);

        return finalTemp;
    }

    public float getNearableTemp(long rawTemp) {
        short tmp = (short) rawTemp;

        tmp = Short.reverseBytes(tmp);

        int raw = (tmp & 0x0fff) << 4;

        if ((raw & 0x8000) != 0) {
            return ((raw & 0x7fff) - 32768.0f) / 256.0f;
        } else {
            float data = (tmp & 0x0fff) << 4;
            return data / 256.0f;
        }
    }
}

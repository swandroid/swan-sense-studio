package interdroid.swan.sensors.impl;

import org.altbeacon.beacon.Beacon;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.crossdevice.beacon.AbstractBeaconSensor;
import interdroid.swan.crossdevice.beacon.BeaconUtils;
import interdroid.swan.sensors.AbstractConfigurationActivity;

/**
 * Created by Veaceslav Munteanu on 5/3/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class BeaconTempSensor extends AbstractBeaconSensor {

    public static final String TEMPERATURE_FIELD = "temperature";
    public static final String TAG = "BeaconTempSensor";

    public static class ConfigurationActivity extends AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.temperature_preferences;
        }
    }

    @Override
    public void setData(HashMap<String, String> ids, Collection<Beacon> beacons, long time) {

        HashMap<String, Object> result = new HashMap<>();
        for(Beacon beacon : beacons){
            if(BeaconUtils.isEddystoneUID(beacon) || BeaconUtils.isEddystoneURL(beacon)){
                if(beacon.getExtraDataFields().size() > 0){
                    long temp = beacon.getExtraDataFields().get(2);
                    float finalTemp = getTemp(temp);
                    result.put(getBeaconId(beacon), finalTemp);
                }
            }

            if(BeaconUtils.isEstimoteNearable(beacon)){
                long temp = beacon.getDataFields().get(2);
                float finalTemp = getNearableTemp(temp);
                result.put(getBeaconId(beacon), finalTemp);
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
        return new String[] { TEMPERATURE_FIELD };
    }

    public float getTemp(long rawTemp){
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE/8);
        buffer.putLong(rawTemp);
        byte tempData[] = buffer.array();
        byte tempIntegral = tempData[6];
        int tempFractional = tempData[7] & 0xff;

        float finalTemp = tempIntegral + (tempFractional / 256.0f);

        return finalTemp;
    }

    public float getNearableTemp(long rawTemp){
        short tmp = (short)rawTemp;

        tmp = Short.reverseBytes(tmp);

        int raw = (tmp & 0x0fff) << 4;

        if((raw & 0x8000) != 0){
            return ((raw & 0x7fff) - 32768.0f) / 256.0f;
        } else {
            float data = (tmp & 0x0fff) << 4;
            return data / 256.0f;
        }
    }
}

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

    public static class ConfigurationActivity extends AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.temperature_preferences;
        }
    }

    @Override
    public void setData(HashMap<String, String> ids, Collection<Beacon> beacons, long time) {
        for(Beacon beacon : beacons){
            if(BeaconUtils.isEddystoneUID(beacon) || BeaconUtils.isEddystoneURL(beacon)){
                if(beacon.getExtraDataFields().size() > 0){
                    for (Map.Entry<String, String> id : ids.entrySet()) {
                        long temp = beacon.getExtraDataFields().get(2);

                        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE/8);
                        buffer.putLong(temp);
                        byte tempData[] = buffer.array();
                        byte tempIntegral = tempData[6];
                        int tempFractional = tempData[7] & 0xff;

                        float finalTemp = tempIntegral + (tempFractional / 256.0f);
                        putValueTrimSize(id.getValue(), id.getKey(),
                                time,
                                finalTemp);
                    }
                }
            }
        }

    }

    @Override
    public String[] getValuePaths() {
        return new String[] { TEMPERATURE_FIELD };
    }
}

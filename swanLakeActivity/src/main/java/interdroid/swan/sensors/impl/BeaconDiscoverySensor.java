package interdroid.swan.sensors.impl;


import org.altbeacon.beacon.Beacon;

import org.altbeacon.beacon.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.crossdevice.beacon.AbstractBeaconSensor;
import interdroid.swan.crossdevice.beacon.BeaconUtils;
import interdroid.swan.sensors.AbstractConfigurationActivity;

/**
 * Created by slavik on 4/13/16.
 */
public class BeaconDiscoverySensor extends AbstractBeaconSensor{

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     *
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.beacon_preferences;
        }
    }



    @Override
    public String[] getValuePaths() {
        return new String[] { IBEACON , EDDYSTONE_UID , ALTBEACON , ESTIMOTE_NEARABLE};
    }


    @Override
    public void setData(HashMap<String, String> ids, Collection<Beacon> beacons, long time) {

        for(Beacon beacon : beacons) {
            StringBuilder allIndetifier = new StringBuilder();
            List<Identifier> identifierList = beacon.getIdentifiers();
            for (Identifier identifier : identifierList) {
                allIndetifier.append(identifier.toString() + "-");
            }
            allIndetifier.deleteCharAt(allIndetifier.length() - 1);

            for (Map.Entry<String, String> id : ids.entrySet()) {
                if (id.getValue().compareTo(IBEACON) == 0
                        && BeaconUtils.isAppleIBeacon(beacon)) {
                    putValueTrimSize(id.getValue(), id.getKey(), time, allIndetifier.toString());
                }

                if (id.getValue().compareTo(EDDYSTONE_UID) == 0
                        && BeaconUtils.isEddystoneUID(beacon)) {
                    putValueTrimSize(id.getValue(), id.getKey(), time, allIndetifier.toString());
                }

                if (id.getValue().compareTo(ALTBEACON) == 0
                        && BeaconUtils.isAltBeacon(beacon)) {
                    putValueTrimSize(id.getValue(), id.getKey(),
                            time,
                            allIndetifier.toString());

                }

                if (id.getValue().compareTo(ESTIMOTE_NEARABLE) == 0
                        && BeaconUtils.isEstimoteNearable(beacon)) {
                    putValueTrimSize(id.getValue(), id.getKey(),
                            time,
                            allIndetifier.toString());

                }
            }
        }
    }

}

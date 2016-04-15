package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.crossdevice.beacon.BeaconInitializer;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Created by slavik on 4/13/16.
 */
public class BeaconDiscoverySensor extends AbstractSwanSensor implements BeaconConsumer{

    BeaconManager beaconManager;
    HashMap<String, String>ids = new HashMap<>();

    public static final String TAG = "BatterySensor";

    public String value_path = "";

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
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, Bundle httpConfiguration) {

        super.register(id,valuePath, configuration, httpConfiguration);
        BeaconInitializer.getInstance(this); // needed to initialize the parser values
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
    }
    @Override
    public void unregister(String id) {
        beaconManager.unbind(this);
    }

    @Override
    public String[] getValuePaths() {
        return new String[] {"IBeaconUUID","EddystoneUUID","EddystoneTLM","EddystoneURL"};
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                Log.d("Beacon", "++ " + beacons.size());
                long time = System.currentTimeMillis();
                if (beacons.size() > 0) {
                    for(Beacon beacon : beacons) {
                        for (Map.Entry<String, String> id : ids.entrySet()) {
                            putValueTrimSize(id.getValue(), id.getKey(), time, beacon.getId1().toString());
                        }
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }
}

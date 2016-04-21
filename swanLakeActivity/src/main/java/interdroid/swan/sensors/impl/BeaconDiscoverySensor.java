package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import interdroid.swan.R;
import interdroid.swan.crossdevice.beacon.BeaconInitializer;
import interdroid.swan.crossdevice.beacon.BeaconUtils;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Created by slavik on 4/13/16.
 */
public class BeaconDiscoverySensor extends AbstractSwanSensor implements BeaconConsumer{

    BeaconManager beaconManager;
    HashMap<String, String> ids = new HashMap<>();

    ReentrantLock lock = new ReentrantLock();

    public static final String TAG = "BeaconDiscoverySensor";

    public static final String IBEACON ="ibeaconuuid";
    public static final String EDDYSTONE_UID = "eddystoneuid";
    public static final String EDDYSTONE_URL = "EddystoneURL";
    public static final String ALTBEACON = "altbeacon";
    public static final String ESTIMOTE_NEARABLE ="estimotenearable";

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

        lock.lock();
        ids.put(id, valuePath);

        if(ids.size() == 1)
            beaconManager.bind(this);

        lock.unlock();
    }
    @Override
    public void unregister(String id) {

        lock.lock();
        ids.remove(id);

        if(ids.isEmpty())
            beaconManager.unbind(this);
        lock.unlock();
    }

    @Override
    public String[] getValuePaths() {
        return new String[] { IBEACON , EDDYSTONE_UID , ALTBEACON , ESTIMOTE_NEARABLE};
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

                        StringBuilder allIndetifier =  new StringBuilder();
                        List<Identifier> identifierList = beacon.getIdentifiers();
                        for(Identifier identifier : identifierList){
                            allIndetifier.append(identifier.toString() + "-");
                        }
                        allIndetifier.deleteCharAt(allIndetifier.length()-1);

                        for (Map.Entry<String, String> id : ids.entrySet()) {
                            if(id.getValue().compareTo(IBEACON) == 0
                                    && BeaconUtils.isAppleIBeacon(beacon)) {
                                putValueTrimSize(id.getValue(), id.getKey(), time, allIndetifier.toString());
                            }

                            if(id.getValue().compareTo(EDDYSTONE_UID) == 0
                                    && BeaconUtils.isEddystoneUID(beacon)){
                                putValueTrimSize(id.getValue(), id.getKey(), time, allIndetifier.toString());
                            }

                            if(id.getValue().compareTo(ALTBEACON) == 0
                                    && BeaconUtils.isAltBeacon(beacon)){
                                putValueTrimSize(id.getValue(), id.getKey(),
                                        time,
                                        allIndetifier.toString());

                            }

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

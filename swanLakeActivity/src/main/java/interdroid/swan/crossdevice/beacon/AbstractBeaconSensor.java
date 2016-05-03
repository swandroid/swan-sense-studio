package interdroid.swan.crossdevice.beacon;

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
import java.util.concurrent.locks.ReentrantLock;

import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Created by Veaceslav Munteanu on 5/3/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public abstract class AbstractBeaconSensor extends AbstractSwanSensor implements BeaconConsumer {

    BeaconManager beaconManager;
    HashMap<String, String> ids = new HashMap<>();

    ReentrantLock lock = new ReentrantLock();

    public static final String TAG = "AbstractBeaconSensor";

    public static final String IBEACON ="ibeaconuuid";
    public static final String EDDYSTONE_UID = "eddystoneuid";
    public static final String ALTBEACON = "altbeacon";
    public static final String ESTIMOTE_NEARABLE ="estimotenearable";

    public String value_path = "";


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
                        setData(ids, beacon, time);
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    /**
     * Each beacon sensor will implement this abstract method
     * @return object to be put in putValueTrimSize
     */
    public abstract void setData(HashMap<String, String> ids, Beacon beacon, long time);
}

package interdroid.swan.crossdevice.beacon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Veaceslav Munteanu on 5/10/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class BeaconSingleton extends Service implements BeaconConsumer {

    BeaconManager beaconManager;

    boolean isBinded = false;

    private final String TAG = "BeaconSingleton";
    private static BeaconSingleton ourInstance;

    ReentrantLock lock = new ReentrantLock();

    volatile HashSet<AbstractBeaconSensor> sensors = new HashSet<>();

    public static BeaconSingleton getInstance() {
        return ourInstance;
    }

    Region myRegion = new Region(TAG, null, null, null);


    public BeaconSingleton() {
        Log.d(TAG, "++++ Service started+++");
    }

    public void addSensor(AbstractBeaconSensor sensor) {
        lock.lock();

        if (sensors.isEmpty() && !isBinded) {
            beaconManager.bind(this);
            Log.d(TAG, "Binding+++++++++++++++++++");
            isBinded = true;
        }

        sensors.add(sensor);
        lock.unlock();
    }

    public void removeSensor(AbstractBeaconSensor sensor) {
        lock.lock();

        sensors.remove(sensor);

        if (sensors.isEmpty() && isBinded) {
            try {
                beaconManager.stopRangingBeaconsInRegion(myRegion);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            beaconManager.unbind(this);
            isBinded = false;
        }
        lock.unlock();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        ourInstance = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "On beacon connect+++++++++++++++++");
        beaconManager.setBackgroundMode(false);
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                Log.d(TAG, "++ " + beacons.size());

                if (beacons.isEmpty())
                    return;
                HashMap<String, Beacon> beaconData = new HashMap<String, Beacon>();

                for (Beacon beacon : beacons) {
                    beaconData.put(getBeaconId(beacon), beacon);
                }

                long time = System.currentTimeMillis();
                try {
                    lock.lock();
                    for (AbstractBeaconSensor sensor : sensors) {
                        sensor.setData(beaconData, time);
                    }
                } finally {
                    lock.unlock();
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(myRegion);
        } catch (RemoteException e) {
            Log.d(TAG, "Got exception" + e.getMessage());
        }
    }

    public String getBeaconId(Beacon beacon) {
        StringBuilder allIndetifier = new StringBuilder();
        List<Identifier> identifierList = beacon.getIdentifiers();
        for (Identifier identifier : identifierList) {
            allIndetifier.append(identifier.toString() + "-");
        }
        allIndetifier.deleteCharAt(allIndetifier.length() - 1);

        return allIndetifier.toString();
    }
}

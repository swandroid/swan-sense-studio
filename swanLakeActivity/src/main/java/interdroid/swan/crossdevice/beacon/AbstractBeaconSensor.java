package interdroid.swan.crossdevice.beacon;

import android.os.Bundle;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Created by Veaceslav Munteanu on 5/3/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public abstract class AbstractBeaconSensor extends AbstractSwanSensor {

    protected HashMap<String, String> ids = new HashMap<>();

    public String locationString = new String();

    ReentrantLock lock = new ReentrantLock();

    public String TAG = "AbstractBeaconSensor";

    public static final String IBEACON ="ibeaconuuid";
    public static final String EDDYSTONE_UID = "eddystoneuid";
    public static final String ALTBEACON = "altbeacon";
    public static final String ESTIMOTE_NEARABLE ="estimotenearable";

    public String value_path = "";


    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    public void setTag(String tagName){
        TAG = tagName;
    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id,valuePath, configuration, httpConfiguration, extraConfiguration);
        locationString = extraConfiguration.getString("location");

        lock.lock();
        ids.put(id, valuePath);
        Log.d(TAG, "Register " + id + " " + valuePath + " " + ids.toString() + " " +ids.size());

        BeaconSingleton.getInstance().addSensor(this);
        lock.unlock();
    }
    @Override
    public void unregister(String id) {


        lock.lock();
        ids.remove(id);
        Log.d(TAG, "UNRegister " + id + " " + ids.toString());
        BeaconSingleton.getInstance().removeSensor(this);
        lock.unlock();
    }

    @Override
    public void onConnected() {

    }


    /**
     * Each beacon sensor will implement this abstract method
     * @return object to be put in putValueTrimSize
     */
    public abstract void setData(HashMap<String, Beacon> beacons, long time);

    public String getBeaconId(Beacon beacon){
        StringBuilder allIndetifier = new StringBuilder();
        List<Identifier> identifierList = beacon.getIdentifiers();
        for (Identifier identifier : identifierList) {
            allIndetifier.append(identifier.toString() + "-");
        }
        allIndetifier.deleteCharAt(allIndetifier.length() - 1);

        return allIndetifier.toString();
    }

    protected abstract String getSensorName();

    /**
     * getRequired beacon will parse, self, any or identifier
     * and then the sensor will put that data in the set
     * @return
     */
    public Beacon getRequiredBeacon(String location, HashMap<String,Beacon> beacons){


        if(location.equals("self") || location.equals("any")){
            // return random beacon
            int val = new Random().nextInt(beacons.size());
            for(Beacon t: beacons.values()) if (--val < 0) return t;
        } else if(beacons.containsKey(location)){
            return beacons.get(location);
        }

        return null;
    }
}

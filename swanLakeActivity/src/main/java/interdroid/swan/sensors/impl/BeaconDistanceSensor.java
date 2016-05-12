package interdroid.swan.sensors.impl;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.HashMap;

import interdroid.swan.crossdevice.beacon.AbstractBeaconSensor;

/**
 * Created by Veaceslav Munteanu on 5/9/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class BeaconDistanceSensor  extends AbstractBeaconSensor{

    public  String TAG = "DistanceBeaconSensor";

    BeaconDistanceSensor(){
        super();
        setTag("DistanceBeaconSensor");
    }

    @Override
    public void setData( Collection<Beacon> beacons, long time) {

    }

    @Override
    protected String getSensorName() {
        return TAG;
    }

    @Override
    public String[] getValuePaths() {
        return new String[0];
    }
}

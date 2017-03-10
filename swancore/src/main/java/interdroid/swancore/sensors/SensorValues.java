package interdroid.swancore.sensors;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import interdroid.swancore.swansong.TimestampedValue;

public class SensorValues {

    /**
     * The map of values for this sensor. The values are grouped per each unique pair (valuePath, configuration)
     */
    private volatile Map<Bundle, List<TimestampedValue>> mSensorValues = new HashMap<>();

    public synchronized final Map<Bundle, List<TimestampedValue>> getValues() {
        return mSensorValues;
    }

    public synchronized void put(Bundle configuration, ArrayList<TimestampedValue> timestampedValues) {
        mSensorValues.put(configuration, timestampedValues);
    }

    public synchronized List<TimestampedValue> get(Bundle configuration) {
        for (Bundle key : mSensorValues.keySet()) {  //containsKey does not seem to work
            if (equalBundles(key, configuration)) {
                return mSensorValues.get(key);
            }
        }
        return null;
    }

    public synchronized void addNewValue(Bundle configuration, TimestampedValue newValue) {
        if (!containsKey(configuration)) {
            //Log.d(getClass().getSimpleName(), "Add new config value " + configuration.get("latitude"));
            put(configuration, new ArrayList<TimestampedValue>());
        }
        get(configuration).add(newValue);
    }

    public synchronized boolean containsKey(Bundle keyConfig) {
        for (Bundle key: keys()) {  //containsKey does not seem to work
            if (equalBundles(key, keyConfig)) {
                return true;
            }
        }
        return false;
    }

    public synchronized Set<Bundle> keys() {
        return mSensorValues.keySet();
    }

    public synchronized Collection<List<TimestampedValue>> values() {
        return mSensorValues.values();
    }

    private boolean equalBundles(Bundle one, Bundle two) {
        if (one.size() != two.size())
            return false;

        Set<String> setOne = one.keySet();
        Object valueOne;
        Object valueTwo;

        for (String key : setOne) {
            valueOne = one.get(key);
            valueTwo = two.get(key);

            if (valueOne instanceof Bundle && valueTwo instanceof Bundle &&
                    !equalBundles((Bundle) valueOne, (Bundle) valueTwo)) {
                return false;
            } else if (valueOne == null) {
                if (valueTwo != null || !two.containsKey(key))
                    return false;
            } else if (!valueOne.equals(valueTwo))
                return false;
        }

        return true;
    }
}

package interdroid.swancore.sensors;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import interdroid.swancore.swansong.TimestampedValue;
import io.fabric.sdk.android.Fabric;

/**
 * This class is the abstract base for all Sensor services. Sensor implementors
 * are advised to use AbstractVdbSensor or AbstractMemorySensor as a basis for
 * their sensors instead of using this class directly.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public abstract class AbstractSensorBase extends Service implements
        SensorInterface {

    private static final String TAG = "AbstractSensorBase";
    public static final String VALUE_PATH = "value_path";

    /**
     * The sensor interface.
     */
    private final SensorInterface mSensorInterface = this;

    private long mStartTime;
    // Designed for direct use by subclasses.
    /**
     * The value paths we support.
     */
    protected final String[] VALUE_PATHS = getValuePaths();

    /**
     * The default configuration.
     */
    protected final Bundle mDefaultConfiguration = new Bundle();

    /**
     * The current configuration of the sensor.
     */
    protected final Bundle currentConfiguration = new Bundle();

    /**
     * The registered configurations for the sensor.
     */
    protected final Map<String, Bundle> registeredConfigurations = new ConcurrentHashMap<>();

    /**
     * The value paths registered as watched.
     */
    protected final Map<String, String> registeredValuePaths = new HashMap<String, String>();


    protected final Map<String, Bundle> registeredHttpConfigurations = new HashMap<String, Bundle>();


    /**
     * The expression ids for each value path.
     */
    protected final Map<Bundle, List<String>> expressionIdsPerConfig = new HashMap<>();

    /**
     * Initializes the default configuration for this sensor.
     *
     * @param defaults the bundle to add defaults to
     */
    public abstract void initDefaultConfiguration(Bundle defaults);

    /**
     * Called when the sensor is starting to allow subclasses to handle any
     * setup that needs to be done.
     */
    protected abstract void init();

    @Override
    public abstract String[] getValuePaths();

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onCreate()
     *
     * Creates the ContextManager and connects to the Swan service.
     */
    @Override
    public final void onCreate() {
        Fabric.with(this, new Crashlytics());
        mStartTime = System.currentTimeMillis();
        init();
        initDefaultConfiguration(mDefaultConfiguration);
        onConnected();
    }

    /**
     * The binder.
     */
    private final Sensor.Stub mBinder = new Sensor.Stub() {

        @Override
        public void register(final String id, final String valuePath, final Bundle configuration,
                             final Bundle httpConfiguration, final Bundle extraConfiguration) throws RemoteException {

            // value path exists and id is unique (enforced by evaluation engine)
            synchronized (mSensorInterface) {
                try {
                    configuration.putString(VALUE_PATH, valuePath);     // add value path as part of the configuration
                    Log.d(TAG, "Registering id: " + id + " value path: " + valuePath);
                    List<String> ids = expressionIdsPerConfig.get(configuration);

                    if (ids == null) {
                        ids = new ArrayList<>();
                        expressionIdsPerConfig.put(configuration, ids);
                    }

                    registeredConfigurations.put(id, configuration);
                    registeredValuePaths.put(id, valuePath);
                    registeredHttpConfigurations.put(id, httpConfiguration);

                    ids.add(id);
                    printState();
                    Log.d(TAG, "Registering with implementation.");

                    // if an expression was already registered with the same configuration
                    // then do not call register on the sensor
                    if (ids.size() == 1)
                        mSensorInterface.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

                } catch (Exception e) {
                    Log.e(TAG, "Caught exception while registering.", e);
                    throw new RemoteException();
                }
            }
        }

        @Override
        public void unregister(final String id) throws RemoteException {
            Bundle configuration = registeredConfigurations.remove(id);
            expressionIdsPerConfig.get(configuration).remove(id);
            registeredValuePaths.remove(id);
            printState();
            mSensorInterface.unregister(id);
        }

        @Override
        public List<TimestampedValue> getValues(final String id,
                                                final long now, final long timespan) throws RemoteException {
            try {
                return mSensorInterface.getValues(id, now, timespan);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        public long getStartUpTime(String id) throws RemoteException {
            return mSensorInterface.getStartUpTime(id);
        }

        @Override
        public Bundle getInfo() throws RemoteException {
            Bundle info = new Bundle();
            info.putString("name", AbstractSensorBase.this.getClass().getSimpleName());
            int num = 0;
            for (Map.Entry<Bundle, List<String>> entry : expressionIdsPerConfig
                    .entrySet()) {
                num += entry.getValue().size();
            }
            info.putInt("registeredids", num);
            info.putDouble("sensingRate", getAverageSensingRate());
            info.putLong("starttime", getStartTime());
            info.putFloat("currentMilliAmpere", getCurrentMilliAmpere());
            return info;
        }
    };

    /**
     * Debug helper which prints the state for this sensor.
     */
    private void printState() {
        for (String key : registeredConfigurations.keySet()) {
            Log.d(TAG,
                    "configs: " + key + ": "
                            + registeredConfigurations.get(key));
        }
        for (String key : registeredValuePaths.keySet()) {
            Log.d(TAG,
                    "valuepaths: " + key + ": " + registeredValuePaths.get(key));
        }
        for (Bundle key : expressionIdsPerConfig.keySet()) {
            Log.d(TAG, "expressionIds: " + key + ": "
                    + expressionIdsPerConfig.get(key));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onBind(android.content.Intent)
     *
     * returns the sensor interface
     */
    @Override
    public final IBinder onBind(final Intent arg0) {
        return mBinder;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     *
     * Stops the connection to Swan
     */
    @Override
    public final void onDestroy() {
        try {
            mSensorInterface.onDestroySensor();
        } catch (Exception e) {
            Log.e(TAG, "Got exception destroying sensor service", e);
        }
        super.onDestroy();
    }

    // =-=-=-=- Utility Functions -=-=-=-=

    /**
     * Send a notification that data changed for the given id.
     *
     * @param ids the id of the expression to notify for.
     */
    protected final void notifyDataChangedForId(final String... ids) {
        Intent notifyIntent = new Intent(ACTION_NOTIFY);
        notifyIntent.putExtra("expressionIds", ids);
        sendBroadcast(notifyIntent);
    }

    @Override
    public long getStartUpTime(String id) {
        return 0;
    }

    /**
     * Send a notification that data for the given value path changed.
     *
     * @param configuration the configuration of the expression to notify for.
     */
    protected final void notifyDataChanged(final Bundle configuration) {
        List<String> notify = new ArrayList<String>();

        synchronized (mSensorInterface) {
            // can be null if multiple valuepaths are updated together and not
            // for all of them, there's an id registered.
            for (Bundle conf: expressionIdsPerConfig.keySet()) {
                if (equalBundles(conf, configuration)) {
                    for (String id : expressionIdsPerConfig.get(conf)) {
                        notify.add(id);
                    }
                }
            }
//            if (expressionIdsPerConfig.get(configuration) != null) {
//                for (String id : expressionIdsPerConfig.get(configuration)) {
//                    notify.add(id);
//                }
//            }
        }

        if (notify.size() > 0) {
            notifyDataChangedForId(notify.toArray(new String[notify.size()]));
        }
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
    /**
     * Gets all readings from timespan seconds ago until now. Readings are returned in
     * reverse order (latest first). This is important for the expression
     * engine.
     *
     * @param now      the start
     * @param timespan the end
     * @param values   the values
     * @return All readings in the timespan between timespan seconds ago and now
     */
    protected static List<TimestampedValue> getValuesForTimeSpan(
            final List<TimestampedValue> values, final long now,
            final long timespan) {
        List<TimestampedValue> result = new ArrayList<>();
        if (timespan == 0) {
            if (values != null && values.size() > 0) {
                result.add(values.get(values.size() - 1));    //item in the last position has the latest timestamp
            }
        } else {
            if (values != null) {
                for (int i = values.size() - 1; i >= 0; i--) {
                    if ((now - timespan) <= values.get(i).getTimestamp()) {
                        if (now >= values.get(i).getTimestamp())    //it shouldn't be a future value
                            result.add(values.get(i));
                    } else {    //stop when it reaches too outdated values
                        break;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public double getAverageSensingRate() {
        return (double) getReadings()
                / ((System.currentTimeMillis() - mStartTime) / 1000.0);
    }

    public long getStartTime() {
        return mStartTime;
    }

    public abstract long getReadings();

    public float getCurrentMilliAmpere() {
        return -1;
    }

    public Class<?>[] getParameterTypes() {
        return new Class<?>[0];
    }

    public String getModelClassName() {
        return null;
    }

}

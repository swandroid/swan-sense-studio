package interdroid.swan.sensors;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interdroid.swan.remote.ServerConnection;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.sensors.AbstractSensorBase;
import nl.sense_os.platform.TrivialSensorRegistrator;
import nl.sense_os.service.R;
import nl.sense_os.service.commonsense.SensorRegistrator;
import nl.sense_os.service.constants.SenseDataTypes;
import nl.sense_os.service.constants.SensePrefs;
import nl.sense_os.service.constants.SensorData.DataPoint;
import nl.sense_os.service.storage.LocalStorage;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class AbstractSwanSensor extends AbstractSensorBase {
    public static String TAG = "Abstract Sensor";

    public static final String DELAY = "delay";

    public static final String ALL_VALUES = "all_values";

    /**
     * State for sensor update rate
     */
    private long lastUpdate = 0;
    private int currentDelay = 0;

    private LocationManager mLocationManager;

    /**
     * The map of values for this sensor. The values are grouped per each unique pair (valuePath, configuration)
     */
    private volatile Map<SensorDataKey, List<TimestampedValue>> mSensorValues = new HashMap<>();

    private class SensorDataKey {
        public String valuePath;
        public Bundle configuration;

        public SensorDataKey(String valuePath, Bundle configuration) {
            this.valuePath = valuePath;
            this.configuration = configuration;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SensorDataKey sensorDataKey = (SensorDataKey) o;

            if (!valuePath.equals(sensorDataKey.valuePath)) return false;
            return configuration != null ? configuration.equals(sensorDataKey.configuration) : sensorDataKey.configuration == null;

        }

        @Override
        public int hashCode() {
            int result = valuePath.hashCode();
            result = 31 * result + (configuration != null ? configuration.hashCode() : 0);
            return result;
        }
    }

    /**
     * Sensor specific name, as it will appear on Sense.
     * Each sensor implementation should set this field
     */
    protected static String SENSOR_NAME;
    protected ServerConnection serverConnection;
    protected String registeredValuepath;
    // protected HashMap<String,Object> serverData = new HashMap<String,Object>();
    protected Callback<Object> cb = new Callback<Object>() {
        @Override
        public void success(Object o, Response response) {

        }

        @Override
        public void failure(RetrofitError error) {

        }
    };
    /**
     * Timestamp indicating when the last flush occurred
     */
    private long mLastFlushed = 0;

    private long mReadings = 0;
    private long mLastReadingTimestamp = 0;

    private Bundle mHttpConfiguration;

    /**
     * @return the values
     */
    public final Map<SensorDataKey, List<TimestampedValue>> getValues() {
        return mSensorValues;
    }

    @Override
    public void init() {
        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        final Context context = this;
        for (final String valuePath : VALUE_PATHS) {
            expressionIdsPerValuePath.put(valuePath, new ArrayList<String>());
        }

        new Thread() {
            @Override
            public void run() {
                // register the sensor
                SensorRegistrator registrator = new TrivialSensorRegistrator(context);
                for (final String valuePath : VALUE_PATHS) {
                    registrator.checkSensor(SENSOR_NAME, SENSOR_NAME, SenseDataTypes.FLOAT, "valuePath= " + valuePath, "", null, null);
                }
            }
        }.start();
    }


    @Override
    public void register(String id, String valuePath, Bundle configuration, Bundle httpConfiguration, Bundle extraConfiguration) {
        this.mHttpConfiguration = httpConfiguration;

        for (String key : httpConfiguration.keySet()) {
            Object obj = httpConfiguration.get(key);   //later parse it as per your required type
            Log.d(TAG, "bundle data in register " + obj.toString());

        }

        registeredValuepath = valuePath;

        String serverStorage = httpConfiguration.getString("server_storage", "FALSE");

        Log.d(TAG, "Server Storage is " + serverStorage);

        if (serverStorage.equals("TRUE")) {
            serverConnection = new ServerConnection(httpConfiguration);
        }
    }

    /**
     * Normalize SENSOR_DELAY_ values into regular microseconds
     * for backward compatibility
     *
     * @param delay the delay, either SENSOR_DELAY_* (i.e., 0..4) or number of microseconds
     */
    protected int normalizeSensorDelay(int delay) {
        if (delay <= SensorManager.SENSOR_DELAY_NORMAL) {
            if (delay == SensorManager.SENSOR_DELAY_FASTEST) {
                return 0;
            } else if (delay == SensorManager.SENSOR_DELAY_GAME) {
                return 20000;
            } else if (delay == SensorManager.SENSOR_DELAY_UI) {
                return 60000;
            } else if (delay == SensorManager.SENSOR_DELAY_NORMAL) {
                return 200000;
            }
        }
        return delay;
    }

    protected int getSensorDelay() {
        // sensorManager.unregisterListener(sensorEventListener);

        Log.d(TAG, "confs: " + registeredConfigurations.size());

        if (registeredConfigurations.size() > 0) {
            // if multiple delays are set, use lowest one:
            int lowestDelay = Integer.MAX_VALUE;
            for (Bundle configuration : registeredConfigurations.values()) {
                if (configuration == null) {
                    continue;
                }
                if (configuration.containsKey(DELAY)) {
                    int delay = normalizeSensorDelay(configuration.getInt(DELAY));
                    lowestDelay = Math.min(lowestDelay, delay);
                    Log.d(TAG, "delay now " + lowestDelay);
                }
            }
            // if no value was set, use the default one
            if (lowestDelay == Integer.MAX_VALUE) {
                lowestDelay = normalizeSensorDelay(mDefaultConfiguration.getInt(DELAY));
                Log.d(TAG, "delay default " + lowestDelay);
            }
            // sensorManager.registerListener(sensorEventListener, lightSensor,
            //                lowestDelay);
            // Log.d(TAG, "delay set to " + lowestDelay);

            currentDelay = lowestDelay;

            return lowestDelay;
        } else {
            return -1;
        }
    }

    protected long acceptSensorReading() {
        long now = System.currentTimeMillis();

		/* Ignore sensor updates that are too early; use 1050 instead of
         * 1000 to avoid missing an update just at the boundary
		 */
        if ((now - lastUpdate) >= (currentDelay / 1050)) {
            Log.d(TAG, "acceptSensorReading: " + (now - lastUpdate) +
                    " usec since last reading; within " + currentDelay + " msec delay");
            lastUpdate = now;
            return now;
        } else {
            Log.d(TAG, "acceptSensorReading: skip; " +
                    " last reported " + (now - lastUpdate) +
                    " usec ago, delay is " + currentDelay + " usec");
            return -1;
        }
    }

    /**
     * Adds a value for the given value path to the history.
     *
     * @param valuePath the value path
     * @param now       the current time
     * @param value     the value
     */
    protected final void putValueTrimSize(final String valuePath,
                                          final String id, final long now, final Object value /*, final int historySize*/) {
        updateReadings(now);

        if (registeredValuepath.contains(valuePath)) {
            if (serverConnection != null) {
                HashMap<String, Object> serverData = new HashMap<>();

                serverData.clear();
                serverData.put("id", id);
                //serverData.put("channel",valuePath);
                serverData.put("field1", value);
                serverData.put("time", now);

                if (mHttpConfiguration != null &&
                        mHttpConfiguration.getString("server_use_location") != null &&
                        mHttpConfiguration.getString("server_use_location").equalsIgnoreCase("true")) {
                    Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    serverData.put("latitude", lastKnownLocation.getLatitude());
                    serverData.put("longitude", lastKnownLocation.getLongitude());
                }
                serverConnection.useHttpMethod(serverData, cb);
            }
        } else {
            Log.d(TAG, "No valuepath registered");
        }

        try {
            if (expressionIdsPerValuePath.containsKey(valuePath)) {
                List<String> ids = expressionIdsPerValuePath.get(valuePath);

                for (String exprId: ids) {
                    Bundle config = registeredConfigurations.get(exprId);
                    SensorDataKey sensorDataKey = new SensorDataKey(valuePath, config);
                    addNewValue(sensorDataKey, new TimestampedValue(value, now));
                }
            }

            if (expressionIdsPerValuePath.containsKey(ALL_VALUES)) {
                List<String> ids = expressionIdsPerValuePath.get(ALL_VALUES);

                for (String exprId: ids) {
                    Bundle config = registeredConfigurations.get(exprId);
                    SensorDataKey sensorDataKey = new SensorDataKey(valuePath, config);
                    addNewValue(sensorDataKey, new TimestampedValue(value, now));
                }
            }

           /* String registeredValuePath = registeredValuePaths.get(id);
            if (registeredValuePath.equalsIgnoreCase(valuePath) || registeredValuePath.equalsIgnoreCase(ALL_VALUES)) {
                SensorDataKey sensorDataKey = new SensorDataKey(valuePath, registeredConfigurations.get(id));
                addNewValue(sensorDataKey, new TimestampedValue(value, now));
            } else {
                Log.e(TAG, "Value path \"" + valuePath + "\" not registered");
                return;
            }*/
        } catch (OutOfMemoryError e) {
            Log.d(TAG, "OutOfMemoryError");
            onDestroySensor();
        }

        checkMemoryAtRuntime();

        if (id != null) {
            notifyDataChangedForId(id);
        } else {
            notifyDataChanged(valuePath);
        }
    }

    /**
     * Add new sensor value in the data source
     *
     * @param sensorDataKey
     * @param newValue
     */
    private void addNewValue(SensorDataKey sensorDataKey, TimestampedValue newValue) {
        if (!mSensorValues.containsKey(sensorDataKey)) {
            mSensorValues.put(sensorDataKey, new ArrayList<TimestampedValue>());
        }
        mSensorValues.get(sensorDataKey).add(newValue);
    }

    /**
     * Check memory used and flush when low on free memory
     */
    static boolean flush = true;

    public void checkMemoryAtRuntime() {
        long usedMemory = Runtime.getRuntime().totalMemory() -
                Runtime.getRuntime().freeMemory();
        if (usedMemory > Runtime.getRuntime().maxMemory() / 5) {
            if (flush == true) {
                Log.d(TAG, "Flush to the database");
                flush = false;

                SharedPreferences mainPrefs = getSharedPreferences(SensePrefs.MAIN_PREFS, Context.MODE_PRIVATE);
                String storageOption = mainPrefs.getString(SensePrefs.Main.Advanced.STORAGE, "Remote Storage");
                Log.d(TAG, "storage option: " + storageOption);
                if (0 == storageOption.compareTo("None"))
                    clearData();
                else
                    flushData();
            }
        } else {
            if (flush == false) {
                Log.d(TAG, "Memory freed");
                flush = true;
            }
        }
    }

    /**
     * Adds a value for the given value path to the history.
     *
     * @param valuePath     the value path
     * @param now           the current time
     * @param value         the value
     * @param historyLength the history length
     */
    protected final void putValueTrimTime(final String valuePath,
                                          final String id, final long now, final Object value,
                                          final long historyLength) {
        updateReadings(now);
        getValues().get(valuePath).add(new TimestampedValue(value, now));
        trimValueByTime(now - historyLength);
        if (id != null) {
            notifyDataChangedForId(id);
        } else {
            notifyDataChanged(valuePath);
        }
    }

    private void updateReadings(long now) {
        if (now != mLastReadingTimestamp) {
            mReadings++;
            mLastReadingTimestamp = now;
        }
    }

    /**
     * Trims values past the given expire time.
     *
     * @param expire the time to trim after
     */
    private final void trimValueByTime(final long expire) {
        for (String valuePath : VALUE_PATHS) {
            List<TimestampedValue> values = getValues().get(valuePath);
            while ((values.size() > 0 && values.get(0)
                    .getTimestamp() < expire)) {
                values.remove(0);
            }
        }
    }

    @Override
    public final List<TimestampedValue> getValues(final String id,
                                                  final long now, final long timespan) {
        /*
         * First check if we have all data in memory, otherwise fetch it from upper storage layer
		 */
        List<TimestampedValue> valuesForTimeSpan = null;
        if (mLastFlushed > (now - timespan))
            getLocalValues(now - timespan, mLastFlushed);

        try {
            String registeredValuePath = registeredValuePaths.get(id);
            if (registeredValuePath == null) {
                Log.e(TAG, "No Value path registered");
                return null;
            }

            Bundle registeredConfiguration = registeredConfigurations.get(id);

            // return all value path data if all is set
            if (registeredValuePath.equalsIgnoreCase(ALL_VALUES)) {
                return constructValues(now, timespan, registeredConfiguration);
            }

            SensorDataKey valKey = new SensorDataKey(registeredValuePath, registeredConfiguration);
            if (!getValues().containsKey(valKey)) {
                Log.e(TAG, "No Values for this expression id");
                return null;
            }

            valuesForTimeSpan = getValuesForTimeSpan(getValues().get(valKey), now, timespan);

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError");
            onDestroySensor();
        }
        return valuesForTimeSpan;
    }

    /**
     * Construct list of values for expressions registered to ALL_VALUES.
     *
     * @param now
     * @param timespan
     * @param configuration
     * @return
     */
    private List<TimestampedValue> constructValues(final long now, final long timespan, Bundle configuration) {
        List<TimestampedValue> valuesForTimeSpan = new ArrayList<>();
        Map<String, List<TimestampedValue>> allValues = new HashMap<>();

        for (String valuePath : getValuePaths()) {
            SensorDataKey key = new SensorDataKey(valuePath, configuration);
            if (mSensorValues.containsKey(key)) {
                allValues.put(key.valuePath, mSensorValues.get(key));
            }
        }

        int counterKeys = allValues.size();
        if (counterKeys > 1) {
            String[] keySet = new String[counterKeys];
            allValues.keySet().toArray(keySet);
            int counterValues = allValues.get(keySet[0]).size();
            for (int i = 0; i < counterValues; i++) {
                long ts = 0;
                Object[] parameters = new Object[counterKeys];
                for (int j = 0; j < counterKeys; j++) {
                    String key = keySet[j];
                    List<TimestampedValue> valuesList = allValues.get(key);
                    if (valuesList.size() > i) {
                        TimestampedValue tsValue = valuesList.get(i);
                        if (tsValue != null) {
                            parameters[j] = tsValue.getValue();
                            ts = tsValue.getTimestamp();
                        }
                    }
                }

                try {
                    Class<?> sensor = Class.forName(getModelClassName());
                    Constructor constructor = sensor.getConstructor(getParameterTypes());
                    Object newValue = constructor.newInstance(parameters);
                    valuesForTimeSpan.add(new TimestampedValue(newValue, ts));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            System.out.println(valuesForTimeSpan.toString());
            return valuesForTimeSpan;
        } else {
            if (counterKeys == 1) { // sensor has only one value_path, return corresponding list of values
                return getValuesForTimeSpan(allValues.get(allValues.keySet().iterator().next()), now, timespan);
            } else {
                return null;
            }
        }
    }

    /**
     * Store data from memory to local storage. Called when all configurations unregistered from this sensor,
     * causing the service to destroy.
     */
    public void flushData() {
        Log.d(TAG, "Flush data to db");
        for (final SensorDataKey sensorDataKey : getValues().keySet()) {
            if (getValues().get(sensorDataKey).size() == 0) {
                Log.d(TAG, "No values to send for value path" + sensorDataKey);
                continue;
            }
            insertDataInLocalStorage(sensorDataKey);
        }
    }

    /**
     * Inserts all sensor values from corresponding valuePath into the local database
     * All values are insert in a single batch
     * The values are removed from the hash map, clearing memory
     *
     * @param sensorDataKey
     */
    private void insertDataInLocalStorage(SensorDataKey sensorDataKey) {
        int size = getValues().get(sensorDataKey).size();
        ArrayList<ContentValues> vals = new ArrayList<>();
        for (int i = size - 1; i >= 0; i--) {
            TimestampedValue tsVal = getValues().get(sensorDataKey).get(i);
            if (i == size - 1) {
                mLastFlushed = tsVal.getTimestamp();    //the latest timestamp is of the last item in the list
            }

            ContentValues val = new ContentValues();
            val.put(DataPoint.SENSOR_NAME, SENSOR_NAME);
            val.put(DataPoint.DISPLAY_NAME, SENSOR_NAME);
            val.put(DataPoint.SENSOR_DESCRIPTION, "valuePath= " + sensorDataKey.valuePath);
            val.put(DataPoint.VALUE_PATH, sensorDataKey.valuePath);
            val.put(DataPoint.DATA_TYPE, SenseDataTypes.FLOAT);
//			val.put(DataPoint.DEVICE_UUID, null);
            val.put(DataPoint.TIMESTAMP, tsVal.getTimestamp());
            val.put(DataPoint.VALUE, tsVal.getValue().toString());

            SharedPreferences mainPrefs = getSharedPreferences(SensePrefs.MAIN_PREFS, Context.MODE_PRIVATE);
            String storageOption = mainPrefs.getString(SensePrefs.Main.Advanced.STORAGE, "Remote Storage");
            Log.d(TAG, "storage option: " + storageOption);
            if (0 == storageOption.compareToIgnoreCase("Remote storage"))
                val.put(DataPoint.TRANSMIT_STATE, 0);
            else
                val.put(DataPoint.TRANSMIT_STATE, 1);


            vals.add(val);
        }
        getValues().get(sensorDataKey).clear();
        bulkInsertToLocalStorage(vals);
    }

    /**
     * Insert data in the database in a separate thread
     *
     * @param cvalues -data to be inserted
     */
    public void bulkInsertToLocalStorage(final ArrayList<ContentValues> cvalues) {
        final int sizeToInsert = cvalues.size();
        new Thread() {
            @Override
            public void run() {

                int count = LocalStorage.getInstance(getApplicationContext()).bulkInsert(cvalues);
                if (count == sizeToInsert) {
                    Log.d(TAG, "data (count = " + count + ") flushed successfully");
                } else
                    Log.d(TAG, "inserted " + count + " elements in the db instead of " + sizeToInsert);
            }
        }.start();
    }

    /**
     * Get data from the db, collected in the period (start,end)  and put it in the hash map with values
     */
    protected void getLocalValues(final long start, final long end) {
        try {
            String[] projection = new String[]{DataPoint.VALUE_PATH, DataPoint.TIMESTAMP, DataPoint.VALUE};
            String where = "(" + DataPoint.TIMESTAMP + " >= " + String.valueOf(start) +
                    " AND " + DataPoint.TIMESTAMP + " <= " + String.valueOf(end) + ")";
            Uri uri = Uri.parse("content://" + getResources().getString(R.string.local_storage_authority) + DataPoint.CONTENT_URI_PATH);

            //sort order matter because latest values should go last
            String sortOrder = DataPoint.TIMESTAMP + " ASC";
            Cursor c = LocalStorage.getInstance(getApplicationContext()).query(uri, projection, where, null, sortOrder);
            if (null == c || !c.moveToFirst()) {
                Log.d(TAG, "Nothing in the db");    //fetch it from remote storage?
                return;
            }
            Log.d(TAG, c.getCount() + " items retrieved from db");

            while (!c.isAfterLast()) {
                getValues().get(c.getString(c.getColumnIndex(DataPoint.VALUE_PATH)))
                        .add(new TimestampedValue(c.getString(c.getColumnIndex(DataPoint.VALUE)), c.getLong(c.getColumnIndex(DataPoint.TIMESTAMP))));
                c.moveToNext();
            }
            c.close();
        } catch (IllegalStateException e) {
            Log.w(TAG, "Failed to query remote data", e);
        }
    }

    @Override
    public long getReadings() {
        return mReadings;
    }

    /**
     * Checks whether there are any readings in memory
     */
    public boolean isMemoryEmpty() {
        for (List<TimestampedValue> readingsList : getValues().values()) {
            if (!readingsList.isEmpty())
                return false;
        }
        return true;
    }

    /**
     * Clears the values from the lit
     */
    private void clearData() {
        for (List<TimestampedValue> readingsList : getValues().values())
            readingsList.clear();
    }

    @Override
    public void onDestroySensor() {
        if (registeredConfigurations.size() == 0)
            flushData();
    }
}

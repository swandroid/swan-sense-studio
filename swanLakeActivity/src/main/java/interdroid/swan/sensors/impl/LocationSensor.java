package interdroid.swan.sensors.impl;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Field;

import interdroid.swan.R;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * A sensor for location.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public class LocationSensor extends AbstractSwanSensor {
    private static final String TAG = "Location Sensor";

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.location_preferences;
        }

    }

    /**
     * Location field name.
     */
    public static final String LOCATION_FIELD = "location";

    /**
     * Accuracy field name.
     */
    public static final String ACCURACY_FIELD = "accuracy";

    /**
     * Bearing field name.
     */
    public static final String BEARING_FIELD = "bearing";

    /**
     * Latitude field name.
     */
    public static final String LATITUDE_FIELD = "latitude";
    /**
     * Longitude field name.
     */
    public static final String LONGITUDE_FIELD = "longitude";
    /**
     * Altitude field name.
     */
    public static final String ALTITUDE_FIELD = "altitude";
    /**
     * Speed field name.
     */
    public static final String SPEED_FIELD = "speed";

    /**
     * Minimum acceptable distance.
     */
    public static final String MIN_DISTANCE = "min_distance";
    /**
     * Minimum acceptable time.
     */
    public static final String MIN_TIME = "min_time";
    /**
     * The type of provider desired.
     */
    public static final String PROVIDER = "provider";

    /**
     * The current provider we are using.
     */
    private String currentProvider;

    /**
     * The location manager we use.
     */
    private LocationManager locationManager;

    /**
     * The location listener.
     */
    private LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(final Location location) {
            long now = System.currentTimeMillis();
            Log.d(TAG, "Location: " + location.getLatitude() + ", "
                    + location.getLongitude());

            putValueTrimSize(LATITUDE_FIELD, null, now, location.getLatitude());
            putValueTrimSize(LONGITUDE_FIELD, null, now, location.getLongitude());
            putValueTrimSize(ALTITUDE_FIELD, null, now, location.getAltitude());
            putValueTrimSize(SPEED_FIELD, null, now, location.getSpeed());
            putValueTrimSize(BEARING_FIELD, null, now, location.getBearing());
            putValueTrimSize(ACCURACY_FIELD, null, now, location.getAccuracy());
        }

        public void onProviderDisabled(final String provider) {
            Log.d(TAG, "provider disabled: " + provider + ". I am using: "
                    + currentProvider);
            if (provider.equals(currentProvider)) {
                Log.w(TAG, "location sensor disabled due to lack of provider");
            }
        }

        public void onProviderEnabled(final String provider) {
            Log.d(TAG, "provider enabled: " + provider);
        }

        public void onStatusChanged(final String provider, final int status,
                                    final Bundle extras) {
            if (provider.equals(currentProvider)
                    && status != LocationProvider.AVAILABLE) {
                Log.w(TAG,
                        "location sensor disabled because sensor unavailable");
            }
        }
    };

    @Override
    public final String[] getValuePaths() {
        return new String[]{LOCATION_FIELD, LATITUDE_FIELD, LONGITUDE_FIELD,
                ALTITUDE_FIELD, SPEED_FIELD, BEARING_FIELD, ACCURACY_FIELD};
    }

    @Override
    public final void initDefaultConfiguration(final Bundle defaults) {
        defaults.putLong(MIN_TIME, 0);
        defaults.putLong(MIN_DISTANCE, 0);
        defaults.putString(PROVIDER, LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public final void onConnected() {
        SENSOR_NAME = "Location Sensor";
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public final void register(final String id, final String valuePath,
                               final Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {

        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        if (registeredConfigurations.size() == 1) {
            updateListener();
        }
    }

    /**
     * Updates the listener.
     */
    private void updateListener() {
        long minTime = Long.MAX_VALUE;
        long minDistance = Long.MAX_VALUE;
        String mostAccurateProvider;
        String passiveProvider = null;
        // Reflect out PASSIVE_PROVIDER so we can still run on 7.
        try {
            Field passive = LocationManager.class.getField("PASSIVE_PROVIDER");
            passiveProvider = (String) passive.get(null);
            mostAccurateProvider = passiveProvider;
        } catch (Exception e) {
            Log.w(TAG, "Caught exception checking for PASSIVE_PROVIDER.", e);
            mostAccurateProvider = LocationManager.NETWORK_PROVIDER;
        }

        for (Bundle configuration : registeredConfigurations.values()) {
            if (configuration.containsKey(MIN_TIME)) {
                minTime = Math.min(minTime, configuration.getLong(MIN_TIME));
            }
            if (configuration.containsKey(MIN_DISTANCE)) {
                minDistance = Math.min(minDistance,
                        configuration.getLong(MIN_DISTANCE));
            }
            if (configuration.containsKey(PROVIDER)) {
                if (mostAccurateProvider.equals(passiveProvider)) {
                    // if current is passive, anything is better
                    mostAccurateProvider = configuration.getString(PROVIDER);
                } else if (LocationManager.NETWORK_PROVIDER
                        .equals(mostAccurateProvider)
                        && LocationManager.GPS_PROVIDER.equals(configuration
                        .getString(PROVIDER))) {
                    // if current is network, only gps is better
                    mostAccurateProvider = LocationManager.GPS_PROVIDER;
                }
                // if it isn't PASSIVE or NETWORK, we can't do any better, it
                // must be GPS
            }
        }
        if (minTime == Long.MAX_VALUE) {
            minTime = mDefaultConfiguration.getLong(MIN_TIME);
        }
        if (minDistance == Long.MAX_VALUE) {
            minDistance = mDefaultConfiguration.getLong(MIN_DISTANCE);
        }

        locationManager.removeUpdates(locationListener);
        locationManager.requestLocationUpdates(mostAccurateProvider, minTime,
                minDistance, locationListener, Looper.getMainLooper());
    }

    @Override
    public final void unregister(final String id) {
        if (registeredConfigurations.size() == 0) {
            locationManager.removeUpdates(locationListener);
        } else {
            updateListener();
        }

    }

    @Override
    public void onDestroySensor() {
        super.onDestroySensor();
    }

//	@Override
//	public List<TimestampedValue> getValues(String id, long now, long timespan) {
//		if (LOCATION_FIELD.equals(registeredValuePaths.get(id))) {
//			List<TimestampedValue> latitudes = getValuesForValuePath(
//					LATITUDE_FIELD, null, now, timespan);
//			List<TimestampedValue> longitudes = getValuesForValuePath(
//					LONGITUDE_FIELD, null, now, timespan);
//			List<TimestampedValue> altitudes = getValuesForValuePath(
//					ALTITUDE_FIELD, null, now, timespan);
//			List<TimestampedValue> speeds = getValuesForValuePath(SPEED_FIELD,
//					null, now, timespan);
//			List<TimestampedValue> bearings = getValuesForValuePath(
//					BEARING_FIELD, null, now, timespan);
//			List<TimestampedValue> accuracies = getValuesForValuePath(
//					ACCURACY_FIELD, null, now, timespan);
//			for (int i = 0; i < latitudes.size(); i++) {
//				Location location = new Location("SWAN Location Sensor");
//				location.setLatitude((Double) latitudes.get(i).getValue());
//				location.setLongitude((Double) longitudes.get(i).getValue());
//				location.setSpeed((Float) speeds.get(i).getValue());
//				location.setAltitude((Double) altitudes.get(i).getValue());
//				location.setBearing((Float) bearings.get(i).getValue());
//				location.setAccuracy((Float) accuracies.get(i).getValue());
//
//				latitudes.set(i, new TimestampedValue(location, latitudes
//						.get(i).getTimestamp()));
//			}
//			return latitudes;
//		}
//		return super.getValues(id, now, timespan);
//	}
}

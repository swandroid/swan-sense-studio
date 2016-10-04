package interdroid.swan.sensors.impl;

import android.os.Bundle;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swancore.models.GoogleDirections;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

public class GoogleDirectionsSensor extends AbstractSwanSensor {
    public static final String ORIGIN_LAT = "origin_latitude";
    public static final String ORIGIN_LNG = "origin_longitude";
    public static final String DESTINATION_LAT = "destination_latitude";
    public static final String DESTINATION_LNG = "destination_longitude";
    public static final String DIRECTIONS = "directions";
    private static final String TRAVEL_MODE = "travel_mode";

    private GeoApiContext geoApiContext;

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void register(final String id, String valuePath, final Bundle configuration, Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        double originLat = Double.valueOf((String)configuration.get(ORIGIN_LAT));
        double originLng = Double.valueOf((String)configuration.get(ORIGIN_LNG));
        double destLat = Double.valueOf((String)configuration.get(DESTINATION_LAT));
        double destLng = Double.valueOf((String)configuration.get(DESTINATION_LNG));
        String travelModeString = configuration.getString(TRAVEL_MODE);

        try {
//            for (final TravelMode travelMode: TravelMode.values()) {
//                if (travelMode == TravelMode.TRANSIT || travelMode == TravelMode.UNKNOWN)
//                    continue;
            TravelMode travelMode = TravelMode.BICYCLING;
            for (TravelMode mode: TravelMode.values()) {
                if (mode.toString().equals(travelModeString)) {
                    travelMode = mode;
                    break;
                }
            }

            final TravelMode finalTravelMode = travelMode;
            DirectionsApi.newRequest(geoApiContext)
                        .mode(travelMode)
                        .units(Unit.METRIC)
                        .origin(new com.google.maps.model.LatLng(originLat, originLng))
                        .destination(new com.google.maps.model.LatLng(destLat, destLng))
                        .alternatives(false)
                        .setCallback(new PendingResult.Callback<DirectionsResult>() {

                            @Override
                            public void onResult(DirectionsResult result) {
                                putValueTrimSize(configuration, id, System.currentTimeMillis(), new GoogleDirections(result, finalTravelMode));
                            }

                            @Override
                            public void onFailure(Throwable e) {

                            }
                        });
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregister(String id) {

    }

    @Override
    public String[] getValuePaths() {
        return new String[] {DIRECTIONS};
    }

    @Override
    public void onConnected() {
        geoApiContext = new GeoApiContext().setApiKey(getString(R.string.google_maps_key));
    }

    public static class ConfigurationActivity extends AbstractConfigurationActivity {
        @Override
        public final int getPreferencesXML() {
            return R.xml.google_directions_preferences;
        }
    }
}

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
import interdroid.swancore.sensors.AbstractConfigurationActivity;

public class GoogleDirectionsSensor extends AbstractSwanSensor {
    public static final String ORIGIN = "origin";
    public static final String DESTINATION = "destination";
    public static final String DIRECTIONS = "directions";

    private GeoApiContext geoApiContext;

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void register(final String id, String valuePath, final Bundle configuration, Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        try {
            for (TravelMode travelMode: TravelMode.values()) {
                DirectionsApi.newRequest(geoApiContext)
                        .mode(travelMode)
                        .units(Unit.METRIC)
                        .origin(configuration.getString(ORIGIN))
                        .destination(configuration.getString(DESTINATION))
                        .alternatives(false)
                        .setCallback(new PendingResult.Callback<DirectionsResult>() {

                            @Override
                            public void onResult(DirectionsResult result) {
                                putValueTrimSize(configuration, id, System.currentTimeMillis(), result);
                            }

                            @Override
                            public void onFailure(Throwable e) {

                            }
                        });
            }

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

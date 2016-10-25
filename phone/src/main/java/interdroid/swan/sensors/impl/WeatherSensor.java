package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;

import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swancore.models.WeatherForecast;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WeatherSensor extends AbstractSwanSensor {

    public static final String TAG = "Weather Sensor";

    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.weather_preferences;
        }

    }

    /*Value path */
    public static final String WEATHER = "weather";


    /*Configuration */
    public static final String SAMPLE_INTERVAL = "sample_interval";
    public static final long DEFAULT_SAMPLE_INTERVAL = 5 * 60 * 1000;   // default update frequency is 5 minutes
    public static final String LATITUDE = "latitude";
    public static final double DEFAULT_LATITUDE = 52.3;
    public static final String LONGITUDE = "longitude";
    public static final double DEFAULT_LONGITUDE = 4.886;

    protected static final int HISTORY_SIZE = 10;
    private Map<String, WeatherPoller> activeThreads = new HashMap<>();


    @Override
    public String[] getValuePaths() {
        return new String[]{WEATHER};
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putLong(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);
        DEFAULT_CONFIGURATION.putDouble(LATITUDE, DEFAULT_LATITUDE);
        DEFAULT_CONFIGURATION.putDouble(LONGITUDE, DEFAULT_LONGITUDE);
    }


    @Override
    public void onConnected() {
        ForecastApi.create("1ba93f8d745fee9e1bd0a570ddb4bddd");
    }

    @Override
    public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        WeatherPoller weatherPoller = new WeatherPoller(id, valuePath, configuration);
        activeThreads.put(id, weatherPoller);
        weatherPoller.start();
    }

    @Override
    public final void unregister(String id) {
        activeThreads.remove(id).interrupt();
    }

    class WeatherPoller extends Thread {

        private Bundle configuration;
        private String valuePath;
        private String id;

        WeatherPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;
        }

        public void run() {
            while (!isInterrupted()) {
                final long start = System.currentTimeMillis();

                RequestBuilder weather = new RequestBuilder();

                Request request = new Request();
                request.setLat(String.valueOf(configuration.get(LATITUDE)));
                request.setLng(String.valueOf(configuration.get(LONGITUDE)));
                request.setUnits(Request.Units.CA);
                request.setLanguage(Request.Language.ENGLISH);
                request.addExcludeBlock(Request.Block.CURRENTLY);

                weather.getWeather(request, new Callback<WeatherResponse>() {
                    @Override
                    public void success(WeatherResponse weatherResponse, Response response) {
                        putValueTrimSize(configuration, id, start, new WeatherForecast(weatherResponse));
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.d(TAG, "Error while calling: " + retrofitError.getUrl());
                    }
                });

                try {
                    Thread.sleep(Math.max(
                            0,
                            configuration.getLong(SAMPLE_INTERVAL,
                                    mDefaultConfiguration.getLong(SAMPLE_INTERVAL))
                                    + start - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    @Override
    public void onDestroySensor() {
        for (WeatherPoller rainPoller : activeThreads.values()) {
            rainPoller.interrupt();
        }
        super.onDestroySensor();
    }
}
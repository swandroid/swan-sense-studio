package interdroid.swan.actuator.impl;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

import interdroid.swan.actuator.Actuator;
import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swan.remote.Constant;
import interdroid.swan.remote.ServerConnection;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * {@link Actuator} the makes an HTTP request. It uses the existing {@link ServerConnection} class
 * to make HTTP requests. Therefore the configuration of the actuator is similar, except the method
 * is the value path of the {@link SensorValueExpression} and the config parameters are not in the
 * http config part of the expression.
 */
public class HttpActuator extends Actuator {

    private static final String TAG = HttpActuator.class.getSimpleName();

    // TODO: 2018-06-26 for some reason the SWAN song parser drops the h from http
    public static final String ENTITY = "ttp";

    private static final String[] KEYS = new String[]{
            Constant.SERVER_URL,
            Constant.SERVER_HTTP_AUTHORIZATION,
            Constant.SERVER_HTTP_BODY,
            Constant.SERVER_HTTP_BODY_TYPE,
            Constant.SERVER_HTTP_HEADER
    };

    private static final String[] PATHS = new String[]{"post", "put"};

    private final ServerConnection connection;

    /**
     * Create a {@link HttpActuator} object
     *
     * @param httpConfig the HTTP config used with the {@link ServerConnection} object
     */
    private HttpActuator(Bundle httpConfig) {
        connection = new ServerConnection(httpConfig);
    }

    @Override
    public void performAction(Context context, TimestampedValue[] newValues) {
        // TODO: 2018-06-26 Send only the latest value for now
        TimestampedValue latest = (newValues != null && newValues.length > 0) ? newValues[0] : null;

        HashMap<String, Object> bodyMap = new HashMap<>();

        if (latest != null) {
            bodyMap.put("value", latest.getValue());
            bodyMap.put("timestamp", latest.getTimestamp());
        }

        connection.useHttpMethod(bodyMap, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                Log.i(TAG, "HTTP actuation response: " + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                int code = error.getResponse() == null ? -1 : error.getResponse().getStatus();
                Log.e(TAG, "HTTP actuation failed: " + code, error.getCause());
            }
        });
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            String method = expression.getValuePath();

            Bundle config = expression.getConfiguration();

            config.putString(Constant.SERVER_HTTP_METHOD, method.toUpperCase());

            return new HttpActuator(config);
        }
    }

    public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[]{
                    "https://example.com",
                    "NoAuth",
                    "key:value",
                    "application/json",
                    "key:value"
            };
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            // return ENTITY;
            // TODO: 2018-07-06 see TODO on top of the class
            return "http";
        }
    }
}

package interdroid.swan.actuator.impl;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.ByteBuffer;

import interdroid.swan.actuator.Actuator;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * An {@link Actuator} that sends MQTT messages to a broker.
 */
public class MqttActuator extends Actuator {

    private static final String TAG = MqttActuator.class.getSimpleName();

    public static final String ENTITY = "mqtt";

    private static final String PARAM_URL = "url";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_TOPIC = "topic";
    private static final String PARAM_CLEAN_SESSION = "clean_session";
    private static final String PARAM_AUTO_RECONNECT = "auto_reconnect";
    private static final String PARAM_CONNECTION_TIMEOUT = "connection_timeout";
    private static final String PARAM_KEEP_ALIVE_INTERVAL = "keep_alive_interval";
    private static final String PARAM_MAX_INFLIGHT = "max_inflight";
    private static final String PARAM_MQTT_VERSION = "mqtt_version";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD= "password";

    private static final String[] KEYS = new String[]{
            PARAM_URL,
            PARAM_CLIENT_ID,
            PARAM_TOPIC,
            PARAM_CLEAN_SESSION,
            PARAM_AUTO_RECONNECT,
            PARAM_CONNECTION_TIMEOUT,
            PARAM_KEEP_ALIVE_INTERVAL,
            PARAM_MAX_INFLIGHT,
            PARAM_MQTT_VERSION,
            PARAM_USERNAME,
            PARAM_PASSWORD
    };

    private static final String[] PATHS = new String[]{"publish"};

    private final MqttClient client;

    private final String topic;

    private MqttActuator(String url, String clientId, String topic, MqttConnectOptions options) throws MqttException {
        this.topic = topic;

        MemoryPersistence memoryPersistence = new MemoryPersistence();
        client = new MqttClient(url, clientId, memoryPersistence);

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    Log.d(TAG, "reconnected to " + serverURI);
                } else {
                    Log.d(TAG, "connected to " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.w(TAG, "connection lost", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d(TAG, "message arrived: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "message sent");
            }
        });

        client.connect(options);
    }

    @Override
    public void performAction(Context context, String expressionId, TimestampedValue[] newValues) {
        // TODO: 2018-06-26 Send only the latest value for now
        TimestampedValue latest = (newValues != null && newValues.length > 0) ? newValues[0] : null;

        if (latest == null) {
            return;
        }

        try {
            byte[] value = latest.getValue().toString().getBytes();

            ByteBuffer buffer = ByteBuffer.allocate(8 + value.length);

            buffer.putLong(latest.getTimestamp());
            buffer.put(latest.getValue().toString().getBytes());

            MqttMessage message = new MqttMessage(buffer.array());
            Log.e(TAG, "Message:"+message.toString()+" publish for Topic:"+topic);
            client.publish(topic, message);
        } catch (MqttException e) {
            Log.e(TAG, "Exception while sending mqtt message", e);
        }
    }

    @Override
    public void onRemoved() throws MqttException {
        if (client.isConnected()) {
            client.disconnect();
        }
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) throws Exception {
            Bundle conf = expression.getConfiguration();
            String url = conf.getString(PARAM_URL);
            String clientId = conf.getString(PARAM_CLIENT_ID);
            String topic = conf.getString(PARAM_TOPIC);

            MqttConnectOptions options = new MqttConnectOptions();

            if (conf.containsKey(PARAM_CLEAN_SESSION)) {
                options.setCleanSession(Boolean.parseBoolean(conf.getString(PARAM_CLEAN_SESSION)));
            }

            if (conf.containsKey(PARAM_AUTO_RECONNECT)) {
                options.setAutomaticReconnect(Boolean.parseBoolean(conf.getString(PARAM_AUTO_RECONNECT)));
            }

            if (conf.containsKey(PARAM_CONNECTION_TIMEOUT)) {
                options.setConnectionTimeout(Integer.parseInt(conf.getString(PARAM_CONNECTION_TIMEOUT)));
            }

            if (conf.containsKey(PARAM_KEEP_ALIVE_INTERVAL)) {
                options.setKeepAliveInterval(Integer.parseInt(conf.getString(PARAM_KEEP_ALIVE_INTERVAL)));
            }

            if (conf.containsKey(PARAM_MAX_INFLIGHT)) {
                options.setMaxInflight(Integer.parseInt(conf.getString(PARAM_MAX_INFLIGHT)));
            }

            if (conf.containsKey(PARAM_MQTT_VERSION)) {
                options.setMqttVersion(Integer.parseInt(conf.getString(PARAM_MQTT_VERSION)));
            }

            String username = conf.getString(PARAM_USERNAME);
            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
            }

            String password = conf.getString(PARAM_PASSWORD);
            if (password != null && !password.isEmpty()) {
                options.setPassword(password.toCharArray());
            }

            return new MqttActuator(url, clientId, topic, options);
        }
    }

}

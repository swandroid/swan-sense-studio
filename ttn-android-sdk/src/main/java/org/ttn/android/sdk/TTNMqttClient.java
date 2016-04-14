package org.ttn.android.sdk;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONException;
import org.json.JSONObject;
import org.ttn.android.sdk.api.converter.MqttPacketConverter;
import org.ttn.android.sdk.api.listeners.MqttApiListener;
import org.ttn.android.sdk.domain.packet.Packet;

import java.net.URISyntaxException;

public class TTNMqttClient {
    // hardcoded endpoint
    private static final String MQTT_HOST = "tcp://croft.thethings.girovito.nl:1883";

    MQTT mMqtt = new MQTT();
    CallbackConnection mConnection;
    MqttPacketConverter mMqttPacketConverter = new MqttPacketConverter();

    public TTNMqttClient() {
        try {
            mMqtt.setHost(MQTT_HOST);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void packets(String nodeEui, MqttApiListener listener) {
        setupMqtt(nodeEui, listener);
    }

    public void packets(MqttApiListener listener) {
        setupMqtt("+", listener);
    }

    void setupMqtt(String nodeEui, final MqttApiListener listener) {

        // establish connection
        mConnection = mMqtt.callbackConnection();
        mConnection.listener(new Listener() {
            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected() {
            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {

                try {
                    JSONObject jsonObj = new JSONObject(body.ascii().toString());
                    Packet packet = (Packet) mMqttPacketConverter.fromJson(jsonObj);
                    listener.onPacket(packet);
                } catch (JSONException e) {
                    listener.onError(e);
                }

                ack.run();
            }

            @Override
            public void onFailure(Throwable value) {
                listener.onError(value);
            }
        });

        final Topic[] topics = {new Topic("nodes/" + nodeEui + "/packets", QoS.AT_LEAST_ONCE)};

        // connect
        mConnection.connect(new Callback<Void>() {
            @Override
            public void onFailure(Throwable value) {
                listener.onError(value);
            }

            @Override
            public void onSuccess(Void value) {

                // subscribe
                mConnection.subscribe(topics, new Callback<byte[]>() {
                    public void onSuccess(byte[] qoses) {
                        // The result of the subscribe request.
                    }

                    public void onFailure(Throwable value) {
                        listener.onError(value);
                    }
                });
            }

        });

    }

    public void disconnect() {
        if (mConnection != null) {
            mConnection.disconnect(new Callback<Void>() {
                @Override
                public void onSuccess(Void value) {

                }

                @Override
                public void onFailure(Throwable value) {

                }
            });
        }
    }
}

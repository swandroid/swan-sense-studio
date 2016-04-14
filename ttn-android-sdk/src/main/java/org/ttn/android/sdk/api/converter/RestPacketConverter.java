package org.ttn.android.sdk.api.converter;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.ttn.android.sdk.api.converter.base.JsonConverter;
import org.ttn.android.sdk.api.converter.base.JsonErrorMessage;
import org.ttn.android.sdk.domain.packet.Packet;

/*
 * Copyright 2015 The Things Network
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by fabiotiriticco on 25/09/15.
 */
public class RestPacketConverter extends JsonConverter {

    // example of a packet from the rest api
    /*
    {
        "data":"SGVsbG8h",
        "datarate":"SF7BW125",
        "snr":10,
        "time":"2016-01-23T16:01:26.818Z",
        "node_eui":"00409D23",
        "data_raw":"QCOdQAAANgAB7INkYOqjGKkIOQ==",
        "frequency":867.7,
        "gateway_eui":"FFFEB827EBA36424",
        "rssi":-43,
        "data_plain":"Hello!"
    }
    */

    public static final String JSON_KEY_DATA = "data";
    public static final String JSON_KEY_TIME = "time";
    public static final String JSON_KEY_RSSI = "rssi";
    public static final String JSON_KEY_DATA_RAW = "data_raw";
    public static final String JSON_KEY_NODE_EUI = "node_eui";
    public static final String JSON_KEY_DATA_RATE = "datarate";
    public static final String JSON_KEY_DATA_JSON = "data_json";
    public static final String JSON_KEY_FREQUENCY = "frequency";
    public static final String JSON_KEY_DATA_PLAIN = "data_plain";
    public static final String JSON_KEY_GATEWAY_EUI = "gateway_eui";

    @Override
    public Object fromJson(JSONObject jsonObj) throws JSONException {
        Packet.Builder builder = new Packet.Builder();

        if (jsonObj.has(JSON_KEY_DATA)) {
            builder.setData(jsonObj.getString(JSON_KEY_DATA));
        }

        if (jsonObj.has(JSON_KEY_DATA_RATE)) {
            builder.setDataRate(jsonObj.getString(JSON_KEY_DATA_RATE));
        }

        if (jsonObj.has(JSON_KEY_FREQUENCY)) {
            builder.setFrequency(jsonObj.getDouble(JSON_KEY_FREQUENCY));
        }

        if (jsonObj.has(JSON_KEY_RSSI)) {
            builder.setRSSI(jsonObj.getInt(JSON_KEY_RSSI));
        }

        if (jsonObj.has(JSON_KEY_DATA_JSON)) {
            builder.setDataJson(jsonObj.getString(JSON_KEY_DATA_JSON));
        }

        if (jsonObj.has(JSON_KEY_DATA_PLAIN)) {
            builder.setDataPlain(jsonObj.getString(JSON_KEY_DATA_PLAIN));
        }

        if (jsonObj.has(JSON_KEY_DATA_RAW)) {
            builder.setDataRaw(JSON_KEY_DATA_RAW);
        }

        if (jsonObj.has(JSON_KEY_GATEWAY_EUI)) {
            builder.setGatewayEui(jsonObj.getString(JSON_KEY_GATEWAY_EUI));
        }

        if (jsonObj.has(JSON_KEY_NODE_EUI)) {
            builder.setNodeEui(jsonObj.getString(JSON_KEY_NODE_EUI));
        }

        if (jsonObj.has(JSON_KEY_TIME)) {
            String dateStr = jsonObj.getString(JSON_KEY_TIME);
            DateTime time = null;
            try {
                time = ISODateTimeFormat.dateTime().parseDateTime(dateStr);
            } catch (IllegalArgumentException e1) {
                // maybe we need to add a Z at the end? Someone might be sending wrong dates
                try {
                    time = ISODateTimeFormat.dateTime().parseDateTime(dateStr + "Z");
                } catch (IllegalArgumentException e2) {
                    // pity, nothing to do
                }
            }
            if (time != null) {
                builder.setTime(time);
            }
        }

        return builder.build();
    }

    @Override
    public JSONObject toJson(Object object) throws JSONException {
        if (!(object instanceof Packet)) {
            throw new JSONException(JsonErrorMessage.unexpectedObject(Packet.class, object));
        }

        Packet packet = (Packet) object;
        final JSONObject jsonObj = new JSONObject();

        // TODO

        return jsonObj;
    }
}

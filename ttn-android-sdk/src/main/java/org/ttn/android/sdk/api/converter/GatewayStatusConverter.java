package org.ttn.android.sdk.api.converter;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.ttn.android.sdk.api.converter.base.JsonConverter;
import org.ttn.android.sdk.api.converter.base.JsonErrorMessage;
import org.ttn.android.sdk.domain.gateway.Gateway;
import org.ttn.android.sdk.domain.gateway.GatewayStatus;

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
 * Created by fabiotiriticco on 9/10/15.
 */
public class GatewayStatusConverter extends JsonConverter {

    public static final String JSON_KEY_LATITUDE = "latitude";
    public static final String JSON_KEY_ALTITUDE = "altitude";
    public static final String JSON_KEY_LONGITUDE = "longitude";
    public static final String JSON_KEY_TIME = "time";
    public static final String JSON_KEY_EUI = "eui";
    public static final String JSON_KEY_RX_OK = "rxok";
    public static final String JSON_KEY_RX_COUNT = "rxcount";
    public static final String JSON_KEY_DATAGRAMS_RECEIVED = "datagramsreceived";
    public static final String JSON_KEY_DATAGRAMS_SENT = "datagramssent";
    public static final String JSON_KEY_RX_FORWARDED = "rxforwarded";
    public static final String JSON_KEY_ACK_RATIO = "ackratio";

    @Override
    public Object fromJson(JSONObject jsonObj) throws JSONException {
        GatewayStatus.Builder builder = new GatewayStatus.Builder();

        if (jsonObj.has(JSON_KEY_ACK_RATIO)) {
            builder.setAckRatio(jsonObj.getDouble(JSON_KEY_ACK_RATIO));
        }

        if (jsonObj.has(JSON_KEY_RX_FORWARDED)) {
            builder.setRxForwarded(jsonObj.getInt(JSON_KEY_RX_FORWARDED));
        }

        if (jsonObj.has(JSON_KEY_DATAGRAMS_SENT)) {
            builder.setDatagramSent(jsonObj.getInt(JSON_KEY_DATAGRAMS_SENT));
        }

        if (jsonObj.has(JSON_KEY_DATAGRAMS_RECEIVED)) {
            builder.setDatagramReceived(jsonObj.getInt(JSON_KEY_DATAGRAMS_RECEIVED));
        }

        if (jsonObj.has(JSON_KEY_RX_COUNT)) {
            builder.setRxCount(jsonObj.getInt(JSON_KEY_RX_COUNT));
        }

        if (jsonObj.has(JSON_KEY_RX_OK)) {
            builder.setRxOk(jsonObj.getInt(JSON_KEY_RX_OK));
        }

        if (jsonObj.has(JSON_KEY_EUI)) {
            builder.setNodeEui(jsonObj.getString(JSON_KEY_EUI));
        }

        if (jsonObj.has(JSON_KEY_LATITUDE)) {
            builder.setLatitude(jsonObj.getDouble(JSON_KEY_LATITUDE));
        }

        if (jsonObj.has(JSON_KEY_LONGITUDE)) {
            builder.setLongitude(jsonObj.getDouble(JSON_KEY_LONGITUDE));
        }

        if (jsonObj.has(JSON_KEY_ALTITUDE)) {
            builder.setAltitude(jsonObj.getDouble(JSON_KEY_ALTITUDE));
        }

        if (jsonObj.has(JSON_KEY_TIME)) {
            String dateStr = jsonObj.getString(JSON_KEY_TIME);
            DateTime time = ISODateTimeFormat.dateTime().parseDateTime(dateStr);
            builder.setLastSeen(time);
        }

        return builder.build();
    }

    @Override
    public JSONObject toJson(Object object) throws JSONException {
        if (!(object instanceof Gateway)) {
            throw new JSONException(JsonErrorMessage.unexpectedObject(Gateway.class, object));
        }

        Gateway gateway = (Gateway) object;
        final JSONObject jsonObj = new JSONObject();

        // TODO

        return jsonObj;
    }
}

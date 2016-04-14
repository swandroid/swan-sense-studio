package org.ttn.android.sdk.api.converter.base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import retrofit.mime.TypedString;

/**
 * Copyright 2015 The Things Network
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Created by fabiotiriticco on 25/09/15.
 */
abstract public class JsonConverter implements Converter {

    public static final String JSON_KEY_COLLECTION = "collection";
    public static final String JSON_MIME_TYPE = "application/json";

    abstract public Object fromJson(JSONObject jsonObj) throws JSONException;

    abstract public JSONObject toJson(Object object) throws JSONException;

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        Object obj = null;
        try {
            String jsonStr = streamToString(body.in());
            Object json = new JSONTokener(jsonStr).nextValue();
            if (json instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) json;
                List<Object> coll = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                    Object item = fromJson(jsonItem);
                    coll.add(item);
                }

                obj = coll;

            } else if (json instanceof JSONObject) {
                // no collection.
                JSONObject jsonObj = (JSONObject) json;
                obj = fromJson(jsonObj);
            }

        } catch (IOException | JSONException e) {
            throw new ConversionException(e.getMessage());
        }

        return obj;
    }

    @Override
    public TypedOutput toBody(Object object) {
        String jsonStr = null;
        try {
            JSONObject jsonObj = toJson(object);
            if (jsonObj != null) {
                jsonStr = jsonObj.toString();
            }

        } catch (JSONException e) {
            // TODO: do something
            e.printStackTrace();
        }

        return new TypedJsonString(jsonStr);
    }

    /**
     * Converts an InputStream to a String.
     *
     * @param is The input stream to convert.
     * @return The string
     */
    protected String streamToString(final InputStream is) throws ConversionException {
        String response = null;

        try {
            if (is != null) {
                final BufferedReader br = new BufferedReader(new InputStreamReader(is));
                final StringBuilder sb = new StringBuilder();
                String read = br.readLine();
                while (read != null) {
                    sb.append(read);
                    read = br.readLine();
                }
                response = sb.toString();
            }

        } catch (final IllegalStateException | IOException e) {
            throw new ConversionException(e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (final IOException e) {
                throw new ConversionException(e.getMessage());
            }
        }

        return response;
    }

    private static class TypedJsonString extends TypedString {
        public TypedJsonString(String body) {
            super(body);
        }

        @Override
        public String mimeType() {
            return JSON_MIME_TYPE;
        }
    }
}

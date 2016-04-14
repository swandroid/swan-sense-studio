package org.ttn.android.sdk.api.retrofit;

import org.ttn.android.sdk.api.converter.base.JsonConverter;

import retrofit.RestAdapter;

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
public class ServiceGenerator {
    private static final String PROTOCOL = "http";
    private static final String API_ADDRESS = "thethingsnetwork.org:80";

    private static final String API_ENDPOINT = PROTOCOL + "://" + API_ADDRESS + "/api";

    /**
     * Generates the API service for a specific interface.
     *
     * @param serviceClass The API interface.
     * @param <S>          The interface type.
     * @return The API service.
     * @throws RuntimeException
     */
    public <S> S createService(Class<S> serviceClass,
                               JsonConverter converter) throws RuntimeException {

        RestAdapter adapter = new RestAdapter.Builder()

                // set endpoint for the APIs
                .setEndpoint(API_ENDPOINT)

                        // set the converter to translate the API responses
                .setConverter(converter)

                        // sets the log level. Disable before release!
                .setLogLevel(RestAdapter.LogLevel.FULL)

                        // build the adapter
                .build();

        return adapter.create(serviceClass);
    }
}

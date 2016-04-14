package org.ttn.android.sdk.api.client;/*
 * Copyright 2015 The Things Network
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by fabiotiriticco on 25/09/15.
 *
 */

import org.ttn.android.sdk.api.converter.GatewayConverter;
import org.ttn.android.sdk.api.converter.GatewayStatusConverter;
import org.ttn.android.sdk.api.converter.NodeConverter;
import org.ttn.android.sdk.api.converter.RestPacketConverter;
import org.ttn.android.sdk.api.retrofit.ServiceGenerator;
import org.ttn.android.sdk.api.retrofit.v0.GatewayApi;
import org.ttn.android.sdk.api.retrofit.v0.GatewayStatusApi;
import org.ttn.android.sdk.api.retrofit.v0.NodeApi;
import org.ttn.android.sdk.api.retrofit.v0.PacketApi;

public class ApiClientV0 {

    final NodeApi mNodeService;
    final PacketApi mPacketService;
    final GatewayApi mGatewayService;
    final GatewayStatusApi mGatewayStatusService;

    public ApiClientV0() {
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        mNodeService = serviceGenerator.createService(NodeApi.class, new NodeConverter());
        mPacketService = serviceGenerator.createService(PacketApi.class, new RestPacketConverter());
        mGatewayService = serviceGenerator.createService(GatewayApi.class, new GatewayConverter());
        mGatewayStatusService = serviceGenerator.createService(GatewayStatusApi.class, new GatewayStatusConverter());
    }

    public NodeApi getNodeService() {
        return mNodeService;
    }

    public PacketApi getPacketService() {
        return mPacketService;
    }

    public GatewayApi getGatewaysService() {
        return mGatewayService;
    }

    public GatewayStatusApi getGatewayStatusService() {
        return mGatewayStatusService;
    }
}

package org.ttn.android.sdk.domain.node;

import org.joda.time.DateTime;

/*
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
 * Created by fabiotiriticco on 20/11/15.
 *
 */
public class Node {

    final String mNodeEui;
    final DateTime mLastSeen;
    final Integer mPacketsCount;
    final String mLastGatewayEui;

    public String getNodeEui() {
        return mNodeEui;
    }

    public DateTime getLastSeen() {
        return mLastSeen;
    }

    public Integer getPacketsCount() {
        return mPacketsCount;
    }

    public String getLastGatewayEui() {
        return mLastGatewayEui;
    }

    public Node(Builder builder) {
        mNodeEui = builder.mNodeEui;
        mLastSeen = builder.mLastSeen;
        mPacketsCount = builder.mPacketsCount;
        mLastGatewayEui = builder.mLastGatewayEui;
    }

    public static class Builder {
        private String mNodeEui;
        private DateTime mLastSeen;
        private Integer mPacketsCount;
        private String mLastGatewayEui;

        public Builder setLastGatewayEui(String lastGatewayEui) {
            mLastGatewayEui = lastGatewayEui;
            return this;
        }

        public Builder setNodeEui(String nodeEui) {
            mNodeEui = nodeEui;
            return this;
        }

        public Builder setLastSeen(DateTime lastSeen) {
            mLastSeen = lastSeen;
            return this;
        }

        public Builder setPacketsCount(Integer packetsCount) {
            mPacketsCount = packetsCount;
            return this;
        }

        public Node build() {
            return new Node(this);
        }
    }
}

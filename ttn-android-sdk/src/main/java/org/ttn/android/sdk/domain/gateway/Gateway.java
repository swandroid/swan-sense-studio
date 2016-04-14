package org.ttn.android.sdk.domain.gateway;

import org.joda.time.DateTime;

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
public class Gateway {
    final String mNodeEui;
    final Double mLatitude;
    final Double mLongitude;
    final Double mAltitude;
    final DateTime mLastSeen;
    final Integer mStatusPacketCount;

    public Gateway(Builder builder) {
        mLatitude = builder.mLatitude;
        mLongitude = builder.mLongitude;
        mAltitude = builder.mAltitude;
        mNodeEui = builder.mNodeEui;
        mLastSeen = builder.mLastSeen;
        mStatusPacketCount = builder.mStatusPacketCount;
    }

    public String getNodeEui() {
        return mNodeEui;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public Double getAltitude() {
        return mAltitude;
    }

    public DateTime getTime() {
        return mLastSeen;
    }

    public Integer getStatusPacketCount() {
        return mStatusPacketCount;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }

        if (other == this) {
            return true;
        }

        if (other.getClass() == getClass()) {
            final Gateway otherGateway = (Gateway) other;
            return getLatitude().equals(otherGateway.getLatitude()) &&
                    getLongitude().equals(otherGateway.getLongitude()) &&
                    getAltitude().equals(otherGateway.getAltitude()) &&
                    getNodeEui().equals(otherGateway.getNodeEui()) &&
                    getTime().equals(otherGateway.getTime()) &&
                    getStatusPacketCount().equals(otherGateway.getStatusPacketCount());
        }

        return super.equals(other);
    }

    public static class Builder {
        private String mNodeEui;
        private Double mLatitude;
        private Double mLongitude;
        private Double mAltitude;
        private DateTime mLastSeen;
        private Integer mStatusPacketCount;

        public Builder setNodeEui(String nodeEui) {
            mNodeEui = nodeEui;
            return this;
        }

        public Builder setLatitude(Double latitude) {
            mLatitude = latitude;
            return this;
        }

        public Builder setLongitude(Double longitude) {
            mLongitude = longitude;
            return this;
        }

        public Builder setAltitude(Double altitude) {
            mAltitude = altitude;
            return this;
        }

        public Builder setLastSeen(DateTime time) {
            mLastSeen = time;
            return this;
        }

        public Builder setStatusPacketcount(Integer statusPacketcount) {
            mStatusPacketCount = statusPacketcount;
            return this;
        }

        public Gateway build() {
            return new Gateway(this);
        }
    }
}

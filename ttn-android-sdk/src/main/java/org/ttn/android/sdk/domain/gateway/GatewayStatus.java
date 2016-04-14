package org.ttn.android.sdk.domain.gateway;

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
public class GatewayStatus {
    final String mGatewayEui;
    final DateTime mTime;
    final Double mLatitude;
    final Double mLongitude;
    final Double mAltitude;
    final Integer mRxOk;
    final Integer mRxCount;
    final Double mAckRatio;
    final Integer mRxForwarded;
    final Integer mDatagramSent;

    public Integer getDatagramReceived() {
        return mDatagramReceived;
    }

    public Integer getRxOk() {
        return mRxOk;
    }

    public Integer getRxCount() {
        return mRxCount;
    }

    public Double getAckRatio() {
        return mAckRatio;
    }

    public Integer getRxForwarded() {
        return mRxForwarded;
    }

    public Integer getDatagramSent() {
        return mDatagramSent;
    }

    final Integer mDatagramReceived;

    public GatewayStatus(Builder builder) {
        mLatitude = builder.mLatitude;
        mLongitude = builder.mLongitude;
        mAltitude = builder.mAltitude;
        mGatewayEui = builder.mNodeEui;
        mTime = builder.mLastSeen;
        mAckRatio = builder.mAckRatio;
        mRxCount = builder.mRxCount;
        mRxOk = builder.mRxOk;
        mRxForwarded = builder.mRxForwarded;
        mDatagramReceived = builder.mDatagramReceived;
        mDatagramSent = builder.mDatagramSent;
    }

    public String getNodeEui() {
        return mGatewayEui;
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
        return mTime;
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
            final GatewayStatus otherGateway = (GatewayStatus) other;
            return getLatitude().equals(otherGateway.getLatitude()) &&
                    getLongitude().equals(otherGateway.getLongitude()) &&
                    getAltitude().equals(otherGateway.getAltitude()) &&
                    getNodeEui().equals(otherGateway.getNodeEui()) &&
                    getTime().equals(otherGateway.getTime()) &&
                    getAckRatio().equals(otherGateway.getAckRatio()) &&
                    getDatagramReceived().equals(otherGateway.getDatagramReceived()) &&
                    getDatagramSent().equals(otherGateway.getDatagramSent()) &&
                    getRxCount().equals(otherGateway.getRxCount()) &&
                    getRxForwarded().equals(otherGateway.getRxForwarded()) &&
                    getRxOk().equals(otherGateway.getRxOk());
        }

        return super.equals(other);
    }

    public static class Builder {
        private String mNodeEui;
        private Double mLatitude;
        private Double mLongitude;
        private Double mAltitude;
        private DateTime mLastSeen;
        private Integer mRxOk;
        private Integer mRxCount;
        private Double mAckRatio;
        private Integer mRxForwarded;
        private Integer mDatagramSent;
        private Integer mDatagramReceived;

        public Builder setRxOk(Integer rxOk) {
            mRxOk = rxOk;
            return this;
        }

        public Builder setRxCount(Integer rxCount) {
            mRxCount = rxCount;
            return this;
        }

        public Builder setAckRatio(Double ackRatio) {
            mAckRatio = ackRatio;
            return this;
        }

        public Builder setRxForwarded(Integer rxForwarded) {
            mRxForwarded = rxForwarded;
            return this;
        }

        public Builder setDatagramSent(Integer datagramSent) {
            mDatagramSent = datagramSent;
            return this;
        }

        public Builder setDatagramReceived(Integer datagramReceived) {
            mDatagramReceived = datagramReceived;
            return this;
        }

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

        public GatewayStatus build() {
            return new GatewayStatus(this);
        }
    }
}

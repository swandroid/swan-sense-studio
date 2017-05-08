package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class LocationCoordinates implements Parcelable, Serializable {
    public double latitude = 0.0;
    public double longitude = 0.0;
    public double altitude = 0.0f;
    public float speed = 0.0f;
    public float bearing = 0.0f;
    public float accuracy = 0.0f;

    public LocationCoordinates(double latitude, double longitude, double altitude, float speed, float bearing, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.bearing = bearing;
        this.accuracy = accuracy;
    }

    protected LocationCoordinates(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        speed = in.readFloat();
        bearing = in.readFloat();
        accuracy = in.readFloat();
    }

    public static final Creator<LocationCoordinates> CREATOR = new Creator<LocationCoordinates>() {
        @Override
        public LocationCoordinates createFromParcel(Parcel in) {
            return new LocationCoordinates(in);
        }

        @Override
        public LocationCoordinates[] newArray(int size) {
            return new LocationCoordinates[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeFloat(speed);
        dest.writeFloat(bearing);
        dest.writeFloat(accuracy);
    }
}

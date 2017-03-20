package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bojansimoski on 20/03/2017.
 */

public class MovementCoordinates implements Parcelable {

    public double x = 0.0;
    public double y = 0.0;
    public double z = 0.0f;
    public double total=0.0;

    public MovementCoordinates(double x, double y, double z,double total) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.total=total;
    }

    protected MovementCoordinates(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        total=in.readDouble();
    }


    public static final Creator<MovementCoordinates> CREATOR = new Creator<MovementCoordinates>() {
        @Override
        public MovementCoordinates createFromParcel(Parcel in) {
            return new MovementCoordinates(in);
        }

        @Override
        public MovementCoordinates[] newArray(int size) {
            return new MovementCoordinates[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeDouble(z);
        dest.writeDouble(total);
    }
}

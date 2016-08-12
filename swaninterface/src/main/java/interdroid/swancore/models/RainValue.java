package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RainValue implements Parcelable {
    public String time;
    public float value;

    public RainValue(String time, float value) {
        this.time = time;
        this.value = value;
    }

    public RainValue(Parcel in) {
        time = in.readString();
        value = in.readFloat();
    }

    public static final Creator<RainValue> CREATOR = new Creator<RainValue>() {
        @Override
        public RainValue createFromParcel(Parcel in) {
            return new RainValue(in);
        }

        @Override
        public RainValue[] newArray(int size) {
            return new RainValue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeFloat(value);
    }
}

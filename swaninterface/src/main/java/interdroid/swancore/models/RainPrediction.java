package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class RainPrediction implements Parcelable {
    private List<RainValue> rainValues;

    public RainPrediction() {
        rainValues = new ArrayList<>();
    }

    public RainPrediction(Parcel in) {
        this();
        in.readList(rainValues, getClass().getClassLoader());
    }

    public static final Creator<RainPrediction> CREATOR = new Creator<RainPrediction>() {
        @Override
        public RainPrediction createFromParcel(Parcel in) {
            return new RainPrediction(in);
        }

        @Override
        public RainPrediction[] newArray(int size) {
            return new RainPrediction[size];
        }
    };

    public void addRainValue(RainValue rainValue){
        rainValues.add(rainValue);
    }

    public void addRainValue(String time, float value) {
        rainValues.add(new RainValue(time, value));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(rainValues);
    }
}
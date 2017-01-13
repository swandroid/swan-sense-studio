package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class RainPrediction implements Parcelable {
    private List<RainValue> rainValues;
    private double latitude;
    private double longitude;

    public RainPrediction(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        rainValues = new ArrayList<>();
    }

    public RainPrediction(Parcel in) {
        rainValues = new ArrayList<>();
        latitude = in.readDouble();
        longitude = in.readDouble();
        in.readList(rainValues, getClass().getClassLoader());
    }

    public RainPrediction(List<RainValue> rainValues) {
        this.rainValues = rainValues;
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

    public void setRainValues(List<RainValue> rainValues) {
        this.rainValues = rainValues;
    }

    public List<RainValue> getRainValues() {
        return rainValues;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeList(rainValues);
    }
}
package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RainPrediction implements Parcelable {
    private List<RainValue> rainValues;
    private LatLng latLng;

    public RainPrediction(double latitude, double longitude) {
        rainValues = new ArrayList<>();
        latLng = new LatLng(latitude, longitude);
    }

    public RainPrediction(Parcel in) {
        rainValues = new ArrayList<>();
        latLng = in.readParcelable(getClass().getClassLoader());
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

    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(latLng, flags);
        dest.writeList(rainValues);
    }
}
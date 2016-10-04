package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.Chronology;


import interdroid.swancore.util.deserializers.ChronologyDeserializer;
import interdroid.swancore.util.deserializers.ChronologySerializer;

public class GoogleDirections implements Parcelable {
    public DirectionsResult directionsResult;
    public TravelMode travelMode;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Chronology.class, new ChronologySerializer())
            .registerTypeAdapter(Chronology.class, new ChronologyDeserializer())
            .create();

    public GoogleDirections(DirectionsResult directionsResult, TravelMode travelMode) {
        this.directionsResult = directionsResult;
        this.travelMode = travelMode;
    }

    protected GoogleDirections(Parcel in) {
        String json = in.readString();
        String travelModeString = in.readString();

        directionsResult = gson.fromJson(json, DirectionsResult.class);
        for (TravelMode mode: TravelMode.values()) {
            if (mode.toString().equals(travelModeString)) {
                travelMode = mode;
                break;
            }
        }
    }

    public static final Creator<GoogleDirections> CREATOR = new Creator<GoogleDirections>() {
        @Override
        public GoogleDirections createFromParcel(Parcel in) {
            return new GoogleDirections(in);
        }

        @Override
        public GoogleDirections[] newArray(int size) {
            return new GoogleDirections[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String jsonPlaces = gson.toJson(directionsResult);
        dest.writeString(jsonPlaces);
        dest.writeString(travelMode.toString());
    }
}

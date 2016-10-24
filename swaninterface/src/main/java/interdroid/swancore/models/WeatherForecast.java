package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;


public class WeatherForecast implements Parcelable {
    public WeatherResponse weatherResponse;
    Gson gson = new GsonBuilder().create();

    public WeatherForecast(WeatherResponse weatherResponse) {
        this.weatherResponse = weatherResponse;
    }

    protected WeatherForecast(Parcel in) {
        String json = in.readString();
        weatherResponse = gson.fromJson(json, WeatherResponse.class);
    }

    public static final Creator<WeatherForecast> CREATOR = new Creator<WeatherForecast>() {
        @Override
        public WeatherForecast createFromParcel(Parcel in) {
            return new WeatherForecast(in);
        }

        @Override
        public WeatherForecast[] newArray(int size) {
            return new WeatherForecast[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String jsonWeather = gson.toJson(weatherResponse);
        dest.writeString(jsonWeather);
    }

    public LatLng getLatLng() {
        double lat = weatherResponse.getLatitude();
        double lng = weatherResponse.getLongitude();
        return new LatLng(lat, lng);
    }
}

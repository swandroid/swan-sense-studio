package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GoogleEvent implements Parcelable{
    public String title;
    public String location;
    public String start;
    public String end;

    public GoogleEvent(String end, String start, String location, String title) {
        this.title = title;
        this.location = location;
        this.start = start;
        this.end = end;
    }

    protected GoogleEvent(Parcel in) {
        title = in.readString();
        location = in.readString();
        start = in.readString();
        end = in.readString();
    }

    public static final Creator<GoogleEvent> CREATOR = new Creator<GoogleEvent>() {
        @Override
        public GoogleEvent createFromParcel(Parcel in) {
            return new GoogleEvent(in);
        }

        @Override
        public GoogleEvent[] newArray(int size) {
            return new GoogleEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(location);
        dest.writeString(start);
        dest.writeString(end);
    }
}

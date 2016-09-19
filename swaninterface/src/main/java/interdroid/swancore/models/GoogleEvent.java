package interdroid.swancore.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GoogleEvent implements Parcelable{
    public String title;
    public String location;
    public String start;
    public String end;

    // the order of the parameters should be identical with the order in value paths array declaration
    public GoogleEvent(String title, String location, String start, String end) {
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

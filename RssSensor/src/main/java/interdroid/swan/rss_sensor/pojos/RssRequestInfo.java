package interdroid.swan.rss_sensor.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by steven on 25/10/15.
 */
public class RssRequestInfo implements Parcelable {

    public int id;
    public String name;
    public String url;

    public RssRequestInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected RssRequestInfo(Parcel in) {
        id = in.readInt();
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<RssRequestInfo> CREATOR = new Creator<RssRequestInfo>() {
        @Override
        public RssRequestInfo createFromParcel(Parcel in) {
            return new RssRequestInfo(in);
        }

        @Override
        public RssRequestInfo[] newArray(int size) {
            return new RssRequestInfo[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(url);
    }

    @Override
    public String toString() {
        return name;
    }
}

package interdroid.swan.jsonsensor.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by steven on 17/05/15.
 */
public class Parameter implements Parcelable {

    public String name;
    public String value;

    public Parameter(Parcel in) {
        readFromParcel(in);
    }

    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void readFromParcel(Parcel in) {
        this.name = in.readString();
        this.value = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(value);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Parameter createFromParcel(Parcel in) {
            return new Parameter(in);
        }

        public Parameter[] newArray(int size) {
            return new Parameter[size];
        }
    };

    @Override
    public Parameter clone() {
        return new Parameter(name, value);
    }
}

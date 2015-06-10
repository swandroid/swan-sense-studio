package interdroid.swan.jsonsensor.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by steven on 20/05/15.
 */
public class JsonPathType implements Parcelable{

    public static final int JSON_TYPE_OBJECT = 0;
    public static final int JSON_TYPE_ARRAY = 1;
    public static final int JSON_TYPE_STRING = 2;

    public String key;
    public int index;
    public int type;
    //TODO: for future
    public String chooseKey; //If previous index was -1, select an key/value of item instead of index
    public String chooseValue;

    public JsonPathType(String key) {
        this.key = key;
    }

    public JsonPathType(int index) {
        this.index = index;
    }

    public JsonPathType(Parcel in) {
        key = in.readString();
        index = in.readInt();
        type = in.readInt();
        chooseKey = in.readString();
        chooseValue = in.readString();
    }

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
        dest.writeString(key);
        dest.writeInt(index);
        dest.writeInt(type);
        dest.writeString(chooseKey);
        dest.writeString(chooseValue);
    }

    public static Creator<JsonPathType> CREATOR = new Creator<JsonPathType>() {

        @Override
        public JsonPathType createFromParcel(Parcel source) {
            return new JsonPathType(source);
        }

        @Override
        public JsonPathType[] newArray(int size) {
            return new JsonPathType[size];
        }

    };
}

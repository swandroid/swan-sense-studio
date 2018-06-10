package interdroid.swan.jsonsensor.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by steven on 14/04/15.
 */
public class JsonItem implements Parcelable {

    public static final int JSON_TYPE_OBJECT = 0;
    public static final int JSON_TYPE_ARRAY = 1;
    public static final int JSON_TYPE_STRING = 2;

    public int type;
    public String key;
    public JsonItem jsonItem;
    public ArrayList<JsonItem> jsonItems;
    public String stringItem;

    public JsonItem(String key) {
        this.key = key;
    }

    protected JsonItem(Parcel in) {
        type = in.readInt();
        key = in.readString();
        jsonItem = in.readParcelable(JsonItem.class.getClassLoader());
        jsonItems = in.createTypedArrayList(JsonItem.CREATOR);
        stringItem = in.readString();
    }

    public static final Creator<JsonItem> CREATOR = new Creator<JsonItem>() {
        @Override
        public JsonItem createFromParcel(Parcel in) {
            return new JsonItem(in);
        }

        @Override
        public JsonItem[] newArray(int size) {
            return new JsonItem[size];
        }
    };

    @Override
    public JsonItem clone() {
        JsonItem jsonItemCopy = new JsonItem(key);
        if (jsonItem != null) {
            jsonItemCopy.jsonItem = jsonItem.clone();
        }
        if (jsonItems != null) {
            jsonItemCopy.jsonItems = new ArrayList<>(jsonItems.size());
            for (int i = 0; i < jsonItems.size(); i++) {
                jsonItemCopy.jsonItems.add(jsonItems.get(i).clone());
            }
        }
        jsonItemCopy.stringItem = stringItem;
        return jsonItemCopy;
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
        dest.writeInt(type);
        dest.writeString(key);
        dest.writeParcelable(jsonItem, flags);
        dest.writeTypedList(jsonItems);
        dest.writeString(stringItem);
    }
}

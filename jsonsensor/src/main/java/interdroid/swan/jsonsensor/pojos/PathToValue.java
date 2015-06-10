package interdroid.swan.jsonsensor.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steven on 20/05/15.
 */
public class PathToValue implements Parcelable {

    public int id;
    public String name;
    public List<JsonPathType> jsonPathTypes;

    public PathToValue(String name) {
        this.name = name;
        jsonPathTypes = new ArrayList<>();
    }

    public PathToValue(Parcel in) {
        readFromParcel(in);
    }

    public PathToValue(int id, String name, List<JsonPathType> jsonPathTypes) {
        this.id = id;
        this.name = name;
        this.jsonPathTypes = jsonPathTypes;
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        jsonPathTypes = in.readArrayList(JsonPathType.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeList(jsonPathTypes);
    }

    public static Creator<PathToValue> CREATOR = new Creator<PathToValue>() {

        @Override
        public PathToValue createFromParcel(Parcel source) {
            return new PathToValue(source);
        }

        @Override
        public PathToValue[] newArray(int size) {
            return new PathToValue[size];
        }

    };
}

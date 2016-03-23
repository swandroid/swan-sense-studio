package interdroid.swan.jsonsensor.pojos;

import java.util.ArrayList;

/**
 * Created by steven on 14/04/15.
 */
public class JsonItem {

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

}

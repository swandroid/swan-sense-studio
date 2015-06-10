package interdroid.swan.jsonsensor.pojos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steven on 20/05/15.
 */
public class JsonRequestList {

    public List<JsonRequestInfo> jsonRequestInfoList;
    public int maxId;

    public JsonRequestList() {
        jsonRequestInfoList = new ArrayList<>();
    }

}

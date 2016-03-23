package interdroid.swan.jsonsensor.pojos;

/**
 * Created by steven on 18/02/16.
 */
public class JsonResponse {

    public JsonRequestInfo jsonRequestInfo;
    public long responseTime;
    public JsonItem jsonItem;
    public long lastRequestUpdate;

    public JsonResponse() {

    }

    public JsonResponse(JsonRequestInfo jsonRequestInfo, long responseTime, JsonItem jsonItem) {
        this.jsonRequestInfo = jsonRequestInfo;
        this.responseTime = responseTime;
        this.jsonItem = jsonItem;
        this.lastRequestUpdate = jsonRequestInfo.lastUpdate;
    }

}

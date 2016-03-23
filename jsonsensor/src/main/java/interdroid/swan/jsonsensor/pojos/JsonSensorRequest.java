package interdroid.swan.jsonsensor.pojos;

/**
 * Created by steven on 18/02/16.
 */
public class JsonSensorRequest {

    public String sensorId;
    public JsonRequestInfo jsonRequestInfo;
    public long sampleRate;
    public JsonRequestListener listener;

    public JsonSensorRequest() {

    }

    public JsonSensorRequest(String sensorId, JsonRequestInfo jsonRequestInfo, long sampleRate,
            JsonRequestListener jsonRequestListener) {
        this.sensorId = sensorId;
        this.jsonRequestInfo = jsonRequestInfo.cloneForCache(); //TODO: move to cache
        this.sampleRate = sampleRate;
        this.listener = jsonRequestListener;
    }

    public interface JsonRequestListener {
        void onResult(JsonItem jsonItem);
    }

}

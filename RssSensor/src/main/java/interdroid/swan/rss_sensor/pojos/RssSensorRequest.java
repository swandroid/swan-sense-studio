package interdroid.swan.rss_sensor.pojos;

import java.util.List;

/**
 * Created by steven on 30/01/16.
 */
public class RssSensorRequest {

    public String sensorId;
    public int sensorUrlId;
    public String sensorUrl;
    public long sampleRate;
    public RssSensorRequestListener listener;

    public interface RssSensorRequestListener {
        void onResult(List<RssItem> rssItemList);
    }

}

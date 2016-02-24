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
    public long lastUpdate;

    public RssSensorRequest() {

    }

    public RssSensorRequest(RssRequestComplete rssRequestComplete, String sensorId, long sampleRate,
            RssSensorRequestListener listener) {
        this.sensorId = sensorId;
        this.sensorUrlId = rssRequestComplete.id;
        this.sensorUrl = rssRequestComplete.url;
        this.sampleRate = sampleRate;
        this.listener = listener;
        this.lastUpdate = rssRequestComplete.lastUpdate;
    }

    public interface RssSensorRequestListener {
        void onResult(List<RssItem> rssItemList);
    }

}

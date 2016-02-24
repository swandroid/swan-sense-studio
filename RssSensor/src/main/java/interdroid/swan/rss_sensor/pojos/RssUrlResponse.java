package interdroid.swan.rss_sensor.pojos;

import java.util.List;

/**
 * Created by steven on 30/01/16.
 */
public class RssUrlResponse {

    public int urlId;
    public String urlString;
    public long responseTime;
    public List<RssItem> rssItemList;
    public long lastUpdate;

    public RssUrlResponse() {

    }

    public RssUrlResponse(int urlId, String urlString, long responseTime, List<RssItem> rssItemList, long lastUpdate) {
        this.urlId = urlId;
        this.urlString = urlString;
        this.responseTime = responseTime;
        this.rssItemList = rssItemList;
        this.lastUpdate = lastUpdate;
    }

}

package interdroid.swan.rss_sensor.pojos;

/**
 * Created by steven on 01/11/15.
 */
public class RssRequestComplete {

    public int id;
    public String name;
    public String url;
    public String word;

    public RssRequestComplete(RssRequestInfo rssRequestInfo, String word) {
        this.id = rssRequestInfo.id;
        this.name = rssRequestInfo.name;
        this.url = rssRequestInfo.url;
        this.word = word;
    }
}

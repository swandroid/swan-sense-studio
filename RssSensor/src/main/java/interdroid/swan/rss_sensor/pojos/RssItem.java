package interdroid.swan.rss_sensor.pojos;

/**
 * Created by steven on 05/11/15.
 */
public class RssItem {

    public String title;
    public String description;

    public RssItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof RssItem)) {
            return false;
        }
        RssItem rssItem = (RssItem) object;
        return title.equals(rssItem.title) && description.equals(rssItem.description);
    }
}

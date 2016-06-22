package interdroid.swan.crossdevice.bluetooth;

/**
 * Created by vladimir on 6/21/16.
 */
public class BTPendingItem {

    private Object item;

    private int timeout;
    public BTPendingItem(Object item, int timeout) {
        this.item = item;
        this.timeout = timeout;
    }

    public Object getItem() {
        return item;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "{item=" + item + "; timeout=" + timeout + "}";
    }
}

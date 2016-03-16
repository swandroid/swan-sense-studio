package interdroid.swan.crossdevice.swanplus;

/**
 * Created by vladimir on 3/10/16.
 */
public interface ProximityManagerI {

    public int getPeerCount();
    public SwanUser getPeerAt(int position);
    public void init();
    public void clean();
    public void registerService();
    public void disconnect();
    public void discoverPeers();
}

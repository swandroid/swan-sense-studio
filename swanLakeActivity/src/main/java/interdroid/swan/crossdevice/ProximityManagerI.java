package interdroid.swan.crossdevice;

import interdroid.swan.crossdevice.wifidirect.WDSwanDevice;

/**
 * Created by vladimir on 3/10/16.
 */
public interface ProximityManagerI {

    public int getPeerCount();

    public WDSwanDevice getPeerAt(int position);

    public boolean hasPeer(String username);

    public void init();

    public void clean();

    public void registerService();

    public void disconnect();

    public void discoverPeers();

    public void registerExpression(String id, String expression, String resolvedLocation);

    public void unregisterExpression(String id, String expression, String resolvedLocation);

    public void send(String toPeerName, String expressionId, String action, String data);
}

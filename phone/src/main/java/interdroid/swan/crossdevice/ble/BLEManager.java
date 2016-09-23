package interdroid.swan.crossdevice.ble;

import interdroid.swan.crossdevice.ProximityManagerI;
import interdroid.swan.crossdevice.wifidirect.WDSwanDevice;

/**
 * Created by vladimir on 9/23/16.
 */

public class BLEManager implements ProximityManagerI {

    public BLEManager() {

    }

    @Override
    public int getPeerCount() {
        return 0;
    }

    @Override
    public WDSwanDevice getPeerAt(int position) {
        return null;
    }

    @Override
    public boolean hasPeer(String username) {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {

    }

    @Override
    public void registerService() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void discoverPeers() {

    }

    @Override
    public void registerExpression(String id, String expression, String resolvedLocation) {

    }

    @Override
    public void unregisterExpression(String id, String expression, String resolvedLocation) {

    }

    @Override
    public void send(String toPeerName, String expressionId, String action, String data) {

    }
}

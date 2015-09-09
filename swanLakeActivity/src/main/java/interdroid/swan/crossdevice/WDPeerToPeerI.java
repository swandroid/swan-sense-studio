package interdroid.swan.crossdevice;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.Collection;

/**
 * Created by vladimir on 9/8/15.
 */
public interface WDPeerToPeerI {

    public void updatePeers(Collection<WifiP2pDevice> deviceList);

    public void processWDUpdate(String update);
}

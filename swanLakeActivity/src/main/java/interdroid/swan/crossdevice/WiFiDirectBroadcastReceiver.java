package interdroid.swan.crossdevice;

import java.net.InetAddress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
	
	private final String TAG = getClass().getName();

    private WifiP2pManager p2pManager;
    private Channel p2pChannel;
    private WDPeerToPeerI wdP2pActivity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, WDPeerToPeerI activity) {
        this.p2pManager = manager;
        this.p2pChannel = channel;
        this.wdP2pActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
        	log("wifi direct changed state", false);
        	// Determine if Wifi P2P mode is enabled or not, alert the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
           
            if (state != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                log("p2p not enabled", true);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	log("peer list changed", false);

        	if(p2pManager != null) {
            	p2pManager.requestPeers(p2pChannel, new PeerListListener() {
					@Override
					public void onPeersAvailable(WifiP2pDeviceList peers) {
						log("peer list changed", false);
                        wdP2pActivity.updatePeers(peers.getDeviceList());
					}
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        	log("conncection received", false);
            // Respond to new connection or disconnections
        	if (p2pManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
            	log("connected", false);

                // We are connected with the other device, request connection info to find group owner IP
                p2pManager.requestConnectionInfo(p2pChannel, new ConnectionInfoListener() {
					@Override
					public void onConnectionInfoAvailable(WifiP2pInfo info) {
				        InetAddress groupOwnerAddress = info.groupOwnerAddress;

				        if (info.groupFormed && info.isGroupOwner) {
				        	log("[connected] I'm group owner with ip = " + groupOwnerAddress.getHostAddress(), true);
				        } else if (info.groupFormed) {
				        	log("[connected] I'm not group owner; group owner ip = " + groupOwnerAddress.getHostAddress(), true);
				        }
					}
				});
            } else {
            	// this is called also when the other peer refuses connection
            	log("connectivity lost", true);
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // This is called when the connectivity is lost
        	log("Device details have changed", true);
        }
    }
    
    private void log(String message, boolean logInAppConsole) {
    	Log.d(TAG, message);
    	
    	if(logInAppConsole) {
            wdP2pActivity.processWDUpdate(message);
    	}
    }
}
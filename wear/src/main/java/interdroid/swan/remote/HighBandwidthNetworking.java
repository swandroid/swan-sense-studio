package interdroid.swan.remote;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

/**
 * Created by Roshan Bharath Das on 29/10/2018.
 */
public class HighBandwidthNetworking{

    private static final String TAG = HighBandwidthNetworking.class.getSimpleName();

    public static boolean NETWORK_AVAILABLE=false;
    private static HighBandwidthNetworking highBandwidthNetworking = null;

    private ConnectivityManager mConnectivityManager;// = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private static final int MIN_NETWORK_BANDWIDTH_KBPS = 10000;

    public HighBandwidthNetworking(ConnectivityManager mConnectivityManager){
        this.mConnectivityManager = mConnectivityManager;
    }

    public static HighBandwidthNetworking getInstance(ConnectivityManager mConnectivityManager)
    {
        if (highBandwidthNetworking == null)
            highBandwidthNetworking = new HighBandwidthNetworking(mConnectivityManager);

        return highBandwidthNetworking;
    }



    // Determine if there is a high-bandwidth network exists. Checks both the active
    // and bound networks. Returns false if no network is available (low or high-bandwidth).
    private boolean isNetworkHighBandwidth() {
        Network network = mConnectivityManager.getBoundNetworkForProcess();
        network = network == null ? mConnectivityManager.getActiveNetwork() : network;
        if (network == null) {
            return false;
        }

        // requires android.permission.ACCESS_NETWORK_STATE
        int bandwidth = mConnectivityManager
                .getNetworkCapabilities(network).getLinkDownstreamBandwidthKbps();

        return bandwidth >= MIN_NETWORK_BANDWIDTH_KBPS;

    }


    public void requestHighBandwidthNetwork() {
        // Before requesting a high-bandwidth network, ensure prior requests are invalidated.

        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        unregisterNetworkCallback();

        if (isNetworkHighBandwidth()) {
            Log.d(TAG, "Active network is available");
        }

        Log.d(TAG, "Requesting high-bandwidth network");

        // Requesting an unmetered network may prevent you from connecting to the cellular
        // network on the user's watch or phone; however, unless you explicitly ask for permission
        // to a access the user's cellular network, you should request an unmetered network.
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        mNetworkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(final Network network) {
                //mTimeOutHandler.removeMessages(MESSAGE_CONNECTIVITY_TIMEOUT);
                Log.d(TAG,"on available called:" +network);
                if (mConnectivityManager.bindProcessToNetwork(network)) {
                    // socket connections will now use this network
                    NETWORK_AVAILABLE =true;
                    Log.d(TAG,"high bandwidth network available");
                } else {
                    // app doesn't have android.permission.INTERNET permission
                    NETWORK_AVAILABLE =false;
                }
            }

            @Override
            public void onLosing(Network network, int maxMsToLive) {

            }

            @Override
            public void onCapabilitiesChanged(Network network,
                                              NetworkCapabilities networkCapabilities) {
                Log.d(TAG, "Network capabilities changed");
                //NETWORK_AVAILABLE =false;

            }

            @Override
            public void onLost(Network network) {
                Log.d(TAG, "Network lost");
                NETWORK_AVAILABLE =false;
            }
        };

        // requires android.permission.CHANGE_NETWORK_STATE
        mConnectivityManager.requestNetwork(request, mNetworkCallback);

        //mTimeOutHandler.sendMessageDelayed(
        //        mTimeOutHandler.obtainMessage(MESSAGE_CONNECTIVITY_TIMEOUT),
         //       NETWORK_CONNECTIVITY_TIMEOUT_MS);
    }




    private void unregisterNetworkCallback() {
        if (mNetworkCallback != null) {
            Log.d(TAG, "Unregistering network callback");
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            mNetworkCallback = null;
        }
    }

    public void releaseHighBandwidthNetwork() {
        mConnectivityManager.bindProcessToNetwork(null);
        unregisterNetworkCallback();
    }
}

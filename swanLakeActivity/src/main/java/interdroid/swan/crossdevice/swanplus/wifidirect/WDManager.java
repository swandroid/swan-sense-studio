package interdroid.swan.crossdevice.swanplus.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interdroid.swan.crossdevice.Registry;
import interdroid.swan.crossdevice.swanplus.ProximityManagerI;
import interdroid.swan.crossdevice.swanplus.SwanLakePlusActivity;
import interdroid.swan.crossdevice.swanplus.SwanUser;
import interdroid.swan.swansong.Expression;

/**
 * Created by vladimir on 12/3/15.
 */
public class WDManager implements ProximityManagerI {

    private static final String TAG = "WDManager";
    private final int PEER_DISCOVERY_INTERVAL = 10000;
    private final int PORT = 2222;

    /** IMPORTANT the order of items in this list matters! */
    private List<SwanUser> nearbyPeers = new ArrayList<SwanUser>();
//    private WDManager instance;
    private SwanLakePlusActivity slpActivity;

    public static WifiP2pManager p2pManager;
    private static WifiP2pManager.Channel p2pChannel;

    private BroadcastReceiver p2pReceiver;
    private IntentFilter p2pIntentFilter;
    private Handler handler;
    private WifiP2pDnsSdServiceInfo serviceInfo;
    private WifiDirectAutoAccept wdAutoAccept;

    /** TODO temporary solution */
    private Thread waitingThread;
    private SwanUser waitingUser;

    private boolean connected = false;

    /* we schedule peer discovery to take place at regular intervals */
    Runnable nearbyPeersChecker = new Runnable() {
        public void run() {
            discoverPeers();
            handler.postDelayed(nearbyPeersChecker, PEER_DISCOVERY_INTERVAL);
        }
    };

    public WDManager(SwanLakePlusActivity activity) {
        this.slpActivity = activity;

        /* initialize WifiDirect service */
        p2pManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        p2pChannel = p2pManager.initialize(activity, activity.getMainLooper(), null);
        p2pReceiver = new WDBroadcastReceiver(p2pManager, p2pChannel, this);

        p2pIntentFilter = new IntentFilter();
        p2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        p2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        p2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        p2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        wdAutoAccept = new WifiDirectAutoAccept(activity);
        handler = new Handler();
    }

    public void init() {
        initPeersDiscovery();
        registerService();
        slpActivity.registerReceiver(p2pReceiver, p2pIntentFilter);
        wdAutoAccept.intercept(true);
        new WDReceiver(this, slpActivity).execute();
        Log.d(TAG, "WDManager initilized successfully");
    }

    public void clean() {
        disconnect();
        slpActivity.unregisterReceiver(p2pReceiver);
        wdAutoAccept.intercept(false);
    }

    public void discoverPeers() {
        p2pManager.discoverServices(p2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                /*Log.d(TAG, "peer discovery was successful");*/
            }

            @Override
            public void onFailure(int code) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                if (code == WifiP2pManager.P2P_UNSUPPORTED) {
                    Log.d(TAG, "P2P isn't supported on this device.");
                } else {
                    Log.d(TAG, "some other error");
                }
            }
        });
    }

    private void initPeersDiscovery() {
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance("run2gether", "presence");

        p2pManager.addServiceRequest(p2pChannel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "success");
                    }

                    @Override
                    public void onFailure(int code) {
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                });

        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomain, Map<String, String> userAttribMap, WifiP2pDevice device) {
                final SwanUser nearbyUser = new SwanUser(userAttribMap.get("name"), userAttribMap.get("regId"), device);

                if(!hasPeer(nearbyUser.getUsername())) {
                    addPeer(nearbyUser);
                    slpActivity.getNearbyPeersAdapter().notifyDataSetChanged();
                    Log.d(TAG, "Found new nearby user " + nearbyUser);
                } else {
                    if(updatePeer(nearbyUser)) {
                        log("Updated nearby user " + nearbyUser, true);
                    }
                }
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice resourceType) {
                // nothing to do here
                Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
            }
        };

        p2pManager.setDnsSdResponseListeners(p2pChannel, servListener, txtListener);
        nearbyPeersChecker.run();
    }

    public void registerService() {
        //  Create a string map containing information about your service.
        Map<String, String> userAttribMap = new HashMap<String, String>();
        String regId = Registry.get(slpActivity, Expression.LOCATION_SELF);

        if (regId == null) {
            slpActivity.log("Not registered with Google Cloud Messaging, cannot share", true);
            return;
        }

        String userFriendlyName = PreferenceManager.getDefaultSharedPreferences(slpActivity).getString("name", null);
        if (userFriendlyName == null) {
            slpActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    slpActivity.log("Please set a name for your device", true);
                    slpActivity.showDialog(slpActivity.DIALOG_SET_NAME);
                }
            });
            return;
        }

        userAttribMap.put("name", userFriendlyName);
        userAttribMap.put("regId", regId);

        if(serviceInfo != null) {
            unregisterService();
        }

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("run2gether", "presence", userAttribMap);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        p2pManager.addLocalService(p2pChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "service registered");
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
            }

            @Override
            public void onFailure(int arg0) {
                Log.e(TAG, "cannot register service");
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });
    }

    private void unregisterService() {
        p2pManager.removeLocalService(p2pChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "service unregistered");
            }

            @Override
            public void onFailure(int arg0) {
                Log.e(TAG, "cannot unregister service");
            }
        });
    }

    /**
     * returns true if the sending thread from Pusher has to wait for getting the IP of user
     *
     */
    public boolean connect(SwanUser user, Thread thread) {
        if(user.getDevice() == null) {
            return false;
        }
        if(user.getIp() != null) {
            return false;
        }

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = user.getDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        waitingThread = thread;
        waitingUser = user;

        Log.d(TAG, "connecting to peer");
        p2pManager.connect(p2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "connection succesful");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Connect failed. Retry.");

                switch (reason) {
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        Log.e(TAG, "p2p unsupported");
                        break;
                    case WifiP2pManager.ERROR:
                        Log.e(TAG, "internal error");
                        break;
                    case WifiP2pManager.BUSY:
                        Log.e(TAG, "busy");
                        break;
                    default:
                        break;
                }
            }
        });

        return true;
    }

    public void connected(String ip, boolean myIp) {
        try {
            if(waitingThread != null && waitingUser != null) {
                if(!myIp) {
                    waitingUser.setIp(InetAddress.getByName(ip));
                    synchronized (waitingThread) {
                        waitingThread.notify();
                    }
                    waitingThread = null;
                    waitingUser = null;
                    Log.d(TAG, "notify sending thread");
                }
            } else {
                if(!myIp) {
                    initConn(InetAddress.getByName(ip));
                    Log.d(TAG, "send initConn to GO");
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void initConn(final InetAddress ip) {
        new Thread() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> dataMap = new HashMap<String, String>();
                    dataMap.put("action", "initConnect");

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
                    os.writeObject(dataMap);
                    byte[] sendData = outputStream.toByteArray();

                    DatagramSocket clientSocket = new DatagramSocket();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, PORT);
                    clientSocket.send(sendPacket);
                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void disconnect() {
        p2pManager.removeGroup(p2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                setConnected(false);
                Log.d(TAG, "disconnected successfully");
            }

            @Override
            public void onFailure(int code) {
                Log.e(TAG, "couldn't disconnect");
            }
        });
    }

    public int getPeerCount() {
        return nearbyPeers.size();
    }

    public void addPeer(SwanUser peer) {
        nearbyPeers.add(peer);
    }

    public SwanUser getPeerAt(int position) {
        return nearbyPeers.get(position);
    }

    public SwanUser getPeer(String username) {
        for(SwanUser peer : nearbyPeers) {
            if(peer.getUsername().equals(username)) {
                return peer;
            }
        }
        return null;
    }

    public SwanUser getPeerByRegId(String regId) {
        for(SwanUser peer : nearbyPeers) {
            if(peer.getRegId().equals(regId)) {
                return peer;
            }
        }
        return null;
    }

    public boolean hasPeer(String username) {
        for(SwanUser peer : nearbyPeers) {
            if(peer.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean updatePeer(SwanUser newPeer) {
        SwanUser peer = getPeer(newPeer.getUsername());
        boolean updated = false;

        if(peer != null) {
            if(!peer.getRegId().equals(newPeer.getRegId())) {
                peer.setRegId(newPeer.getRegId());
                updated = true;
            }
            if(!peer.getDevice().equals(newPeer.getDevice())) {
                peer.setDevice(newPeer.getDevice());
                updated = true;
            }
        }

        return updated;
    }

    public void resetPeers() {
        for(SwanUser peer : nearbyPeers) {
            peer.setIp(null);
        }
    }

    public BroadcastReceiver getP2pReceiver() {
        return p2pReceiver;
    }

    public void setP2pReceiver(BroadcastReceiver p2pReceiver) {
        this.p2pReceiver = p2pReceiver;
    }

    public void log(String message, boolean display) {
        Log.d(TAG, message);

        if(display) {
            Toast.makeText(slpActivity, message, Toast.LENGTH_LONG).show();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}

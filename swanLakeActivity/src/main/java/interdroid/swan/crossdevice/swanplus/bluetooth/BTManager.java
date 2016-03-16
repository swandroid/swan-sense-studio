package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import interdroid.swan.crossdevice.swanplus.ProximityManagerI;
import interdroid.swan.crossdevice.swanplus.SwanLakePlusActivity;
import interdroid.swan.crossdevice.swanplus.SwanUser;
import interdroid.swan.engine.EvaluationEngineService;

/**
 * Created by vladimir on 3/9/16.
 *
 * TODO remove users from the list when they go out of range
 * TODO run peer discovery periodically
 * TODO do something with the device name
 * TODO handle properly the cases when BT is switched on/off during usage
 */
public class BTManager implements ProximityManagerI {

    private static final String TAG = "BTManager";
    private static final int REQUEST_ENABLE_BT = 12321;
    protected final static UUID SERVICE_UUID = UUID.fromString("e2035693-b335-403f-b921-537e5ce2d27d");
    protected final static String SERVICE_NAME = "swanlake";
    public static final String ACTION_NEARBY_DEVICE_FOUND = "interdroid.swan.crossdevice.swanplus.bluetooth.ACTION_NEARBY_DEVICE_FOUND";

    /** IMPORTANT the order of items in this list matters! */
    private List<SwanUser> nearbyPeers = new ArrayList<SwanUser>();
    private Context context;
    private BTReceiver btReceiver;
    private BluetoothAdapter btAdapter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getName() != null) {
                    SwanUser nearbyUser = new SwanUser(device.getName(), device.getName(), device);

                    if (!hasPeer(nearbyUser.getUsername())) {
                        addPeer(nearbyUser);
                        Intent deviceFoundintent = new Intent();
                        deviceFoundintent.setAction(ACTION_NEARBY_DEVICE_FOUND);
                        context.sendBroadcast(deviceFoundintent);
                        Log.d(TAG, "Found new nearby user " + nearbyUser);
                    }

                    Log.d(TAG, "added new device: " + device.getName());
                }
            } else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                if(connState == BluetoothAdapter.STATE_ON && btReceiver.getStatus() == AsyncTask.Status.PENDING) {
                    Log.d(TAG, "bluetooth connected, starting receiver thread...");
                    btReceiver.execute();
                }
            }
        }
    };

    public BTManager(Context context) {
        this.context = context;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Log.w(TAG, "Bluetooth not supported");
            return;
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    public void registerService() {
        String userFriendlyName = PreferenceManager.getDefaultSharedPreferences(context).getString("name", null);

        if (userFriendlyName == null) {
            Log.e(TAG, "Name not set for device");
            return;
        }

        btAdapter.setName(userFriendlyName);
    }

    public void init() {
        registerService();
        initDiscovery();
        btAdapter.startDiscovery();
        // TODO don't start it until BT is enabled!!!
        btReceiver = new BTReceiver(this, context);

        if(btAdapter.isEnabled()) {
            btReceiver.execute();
        }
    }

    public void clean() {
        disconnect();
        context.unregisterReceiver(mReceiver);
    }

    public void discoverPeers() {
        btAdapter.startDiscovery();
    }

    public void initDiscovery() {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d(TAG, "device already paired: " + device.getName());
            }
        }

        if(btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(discoverableIntent);
        }
    }

    public void disconnect() {
        //TODO implement me
    }

    // blocking call; use only in a separate thread
    public BluetoothSocket connect(SwanUser user) {
        BluetoothSocket btSocket = null;
        btAdapter.cancelDiscovery();

        try {
            btSocket = user.getBtSocket();

            if(btSocket == null) {
                btSocket = user.getBtDevice().createInsecureRfcommSocketToServiceRecord(SERVICE_UUID);
                btSocket.connect();
                user.setBtSocket(btSocket);
                manageBtSocket(btSocket);
            }
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        return btSocket;
    }

    protected void manageBtSocket(final BluetoothSocket socket) {
        new Thread() {
            public void run() {
                try {
                    InputStream is = socket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);

                    while(true) {
                        HashMap<String, String> dataMap = (HashMap<String, String>) ois.readObject();
                        String action = dataMap.get("action");

                        if (action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)
                                || action.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                            String source = dataMap.get("source");

                            if (source != null) {
                                SwanUser user = getPeerByUsername(source);
                                if (user != null) {
                                    user.setBtSocket(socket);
                                }
                            } else {
                                Log.w(TAG, "source field is empty");
                            }

                            Intent intent = new Intent(action);
                            intent.setClass(context, EvaluationEngineService.class);
                            intent.putExtra("source", source);
                            intent.putExtra("id", dataMap.get("id"));
                            intent.putExtra("data", dataMap.get("data"));
                            context.startService(intent);

                            Log.d(TAG, "received " + action + " from " + source);
                        } else {
                            Intent intent = new Intent(action);
                            intent.setClass(context, EvaluationEngineService.class);
                            intent.putExtra("id", dataMap.get("id"));
                            intent.putExtra("data", dataMap.get("data"));
                            context.startService(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public boolean hasPeer(String username) {
        for(SwanUser peer : nearbyPeers) {
            if(peer.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void addPeer(SwanUser peer) {
        nearbyPeers.add(peer);
    }

    public SwanUser getPeerAt(int position) {
        return nearbyPeers.get(position);
    }

    public SwanUser getPeerByUsername(String username) {
        for(SwanUser peer : nearbyPeers) {
            if(peer.getUsername().equals(username)) {
                return peer;
            }
        }
        return null;
    }

    public int getPeerCount() {
        return nearbyPeers.size();
    }

    public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }
}

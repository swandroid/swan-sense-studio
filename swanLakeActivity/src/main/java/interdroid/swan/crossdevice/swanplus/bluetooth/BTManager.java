package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import interdroid.swan.crossdevice.Converter;
import interdroid.swan.crossdevice.swanplus.ProximityManagerI;
import interdroid.swan.crossdevice.swanplus.SwanUser;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swan.swansong.Expression;
import interdroid.swan.swansong.Result;

/**
 * Created by vladimir on 3/9/16.
 *
 * TODO remove users from the list when they go out of range
 * TODO run peer discovery periodically
 * TODO do something with the device name
 * TODO handle properly the cases when BT is switched on/off during usage
 * TODO handle Broken pipe exceptions
 * TODO create method for sending intents to the Evaluation Engine
 * TODO connect might hang forever
 */
public class BTManager implements ProximityManagerI {

    private static final String TAG = "BTManager";
    protected final static UUID SERVICE_UUID = UUID.fromString("e2035693-b335-403f-b921-537e5ce2d27d");
    protected final static String SERVICE_NAME = "swanlake";
    public static final String ACTION_NEARBY_DEVICE_FOUND = "interdroid.swan.crossdevice.swanplus.bluetooth.ACTION_NEARBY_DEVICE_FOUND";
    private final int PEER_DISCOVERY_INTERVAL = 40000;

    /** IMPORTANT the order of items in this list matters! */
    private List<SwanUser> nearbyPeers = new ArrayList<SwanUser>();
    private Map<String, String> registeredExpressions = new HashMap<String, String>();
    private Context context;
    private BTReceiver btReceiver;
    private BluetoothAdapter btAdapter;
    private ConcurrentLinkedQueue<BTRemoteExpression> evalQueue;
    private CopyOnWriteArrayList<BTRemoteExpression> processingList = new CopyOnWriteArrayList<>();
    private boolean busy = false;
    private Handler handler;

    /* we schedule peer discovery to take place at regular intervals */
    Runnable nearbyPeersChecker = new Runnable() {
        public void run() {
            discoverPeers();
            handler.postDelayed(nearbyPeersChecker, PEER_DISCOVERY_INTERVAL);
        }
    };

    private final Thread evalThread = new Thread() {
        @Override
        public void run() {
            try {
                while(true) {
                    while(evalQueue.isEmpty()) {
                        synchronized (this) {
                            wait();
                        }
                    }

                    BTRemoteExpression expression = removeFromQueue();
                    processExpression(expression);

                    while(isBusy()) {
                        synchronized (this) {
                            wait();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getName() != null && device.getName().toLowerCase().contains("swan")) {
                    SwanUser nearbyUser = new SwanUser(device.getName(), device.getName(), device);
                    Log.d(TAG, "Found new nearby user " + nearbyUser);

                    if (!hasPeer(nearbyUser.getUsername())) {
                        Log.d(TAG, "adding new device: " + device.getName());
                        addPeer(nearbyUser);
                        Intent deviceFoundintent = new Intent();
                        deviceFoundintent.setAction(ACTION_NEARBY_DEVICE_FOUND);
                        context.sendBroadcast(deviceFoundintent);
                    }
                }
            } else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                if(connState == BluetoothAdapter.STATE_ON && btReceiver == null) {
                    Log.d(TAG, "bluetooth connected, starting receiver thread...");
                    btReceiver = new BTReceiver(BTManager.this, context);
                    btReceiver.execute();
                }
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "discovery finished");

                synchronized (evalThread) {
                    evalThread.notify();
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

        evalQueue = new ConcurrentLinkedQueue<BTRemoteExpression>();
        handler = new Handler();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
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
        evalThread.start();

        if(btAdapter.isEnabled()) {
            btReceiver = new BTReceiver(this, context);
            btReceiver.execute();
        }

        nearbyPeersChecker.run();
    }

    public void clean() {
        disconnect();
        context.unregisterReceiver(mReceiver);
    }

    public void discoverPeers() {
        if(!btAdapter.isDiscovering()) {
            Log.d(TAG, "Discovering...");
            btAdapter.startDiscovery();
        } else {
            Log.d(TAG, "Discovery already started");
        }
    }

    public void initDiscovery() {
        if(!btAdapter.getName().toLowerCase().contains("swan")) {
            btAdapter.setName(btAdapter.getName() + "swan");
        }

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

    public void registerExpression(String id, String expression, String resolvedLocation, String action) {
        boolean addedExpr = false;

        if(resolvedLocation.equals(Expression.LOCATION_NEARBY)) {
            if (action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                registeredExpressions.put(id, expression);
            } else if (action.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                registeredExpressions.remove(id);
            } else {
                Log.e(TAG, "not a valid action");
            }

            // TODO filter by name
            for (SwanUser user : nearbyPeers) {
                if (user.isConnectable()) {
                    BTRemoteExpression remoteExpr = new BTRemoteExpression(id, user, expression, action);
                    addToQueue(remoteExpr);
                    addedExpr = true;
                    Log.d(TAG, "added new expression to queue: " + expression);
                }
            }
        } else {
            SwanUser user = getPeerByUsername(resolvedLocation);

            if(user.isConnectable()) {
                BTRemoteExpression remoteExpr = new BTRemoteExpression(id, user, expression, action);
                addToQueue(remoteExpr);
                addedExpr = true;
                Log.d(TAG, "added new expression to queue: " + expression);
            }
        }

        if(addedExpr) {
            synchronized (evalThread) {
                evalThread.notify();
            }
        }
    }

    /** remove all queued expressions that are assigned to a remote user */
    public void removeRemoteExpressions(SwanUser user) {
        for(Iterator<BTRemoteExpression> it = evalQueue.iterator(); it.hasNext();) {
            BTRemoteExpression remoteExpr = it.next();

            if(remoteExpr.getUser().equals(user)) {
                it.remove();
            }
        }
    }

    private void processExpression(BTRemoteExpression expression) {
        Log.d(TAG, "processing " + expression);

        // TODO get rid of this
        if(!expression.getUser().isConnectable()) {
            Log.e(TAG, "user not connectable");
            return;
        }

        processingList.add(expression);
        setBusy(true);

        send(expression.getUser().getUsername(), expression.getId(),
                expression.getAction(), expression.getExpression());
    }

    private boolean isProcessing(String id, String user) {
        for(BTRemoteExpression expr : processingList) {
            if(expr.getId().equals(id) && expr.getUser().getUsername().equals(user)) {
                return true;
            }
        }
        return false;
    }

    private void stopProcessing(String id, String user) {
        BTRemoteExpression toRemove = null;

        for(BTRemoteExpression expr : processingList) {
            if(expr.getId().equals(id) && expr.getUser().getUsername().equals(user)) {
                toRemove = expr;
            }
        }

        if(toRemove != null) {
            processingList.remove(toRemove);
        }
    }

    // blocking call; use only in a separate thread
    public BluetoothSocket connect(SwanUser user, int tag) {
        BluetoothSocket btSocket = null;
        btAdapter.cancelDiscovery();

        try {
            btSocket = user.getBtSocket();

            if(btSocket == null) {
                btSocket = user.getBtDevice().createInsecureRfcommSocketToServiceRecord(SERVICE_UUID);
                btSocket.connect();
                Log.i(TAG, "connected to " + user);
                user.setBtSocket(btSocket);
                manageConnection(btSocket, tag);
            }
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

//            removeRemoteExpressions(user);
            Log.e(TAG, "can't connect to " + user.getUsername() + ": " + e.getMessage());
            user.setConnectable(false);

            setBusy(false);
            synchronized (evalThread) {
                evalThread.notify();
            }

            btAdapter.startDiscovery();

            return null;
        }

        return btSocket;
    }

    public void disconnect(SwanUser user) {
        BluetoothSocket btSocket;

        try {
            btSocket = user.getBtSocket();

            if(btSocket != null) {
                btSocket.close();
                user.setBtSocket(null);
                user.setOos(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean send(final String toUsername, final String expressionId,
                      final String action, final String data) {
        new Thread() {
            @Override
            public void run() {
                SwanUser user = getPeerByUsername(toUsername);
                int tag = new Random().nextInt(100);
                Log.w(TAG, "[" + tag + "] sending " + action + " message to " + toUsername + ": " + data + " (id: " + expressionId + ")");

                try {
                    if (user != null) {
                        BluetoothSocket btSocket = connect(user, tag);

                        if(btSocket != null) {
                            ObjectOutputStream oos = user.getOos();
                            HashMap<String, String> dataMap = new HashMap<String, String>();

                            if(oos == null) {
                                OutputStream os = btSocket.getOutputStream();
                                oos = new ObjectOutputStream(os);
                                user.setOos(oos);
                            }

                            // "from" is not allowed and results in InvalidDataKey, see:
                            // http://developer.android.com/google/gcm/gcm.html
                            dataMap.put("source", getBtAdapter().getName());
                            dataMap.put("action", action);
                            dataMap.put("data", data);
                            dataMap.put("id", expressionId);

                            synchronized (oos) {
                                oos.writeObject(dataMap);
                            }

                            String _data = data;
                            if(action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE) && data != null) {
                                _data = Converter.stringToObject(data).toString();
                            }
                            Log.w(TAG, "[" + tag + "]successfully sent " + action + " message to " + toUsername + ": " + _data + " (id: " + expressionId + ")");

                            return;
                        }
                    } else {
                        Log.e(TAG, "user not found");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "couldn't send " + action + " to " + user + ": " + e.getMessage());
                }

                // if we are here then the send failed for some reason
                if(action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                    Log.e(TAG, "couldn't send remote result, unregistering expression " + expressionId);

                    // unregister the expression
                    Intent intent = new Intent(EvaluationEngineService.ACTION_UNREGISTER_REMOTE);
                    intent.setClass(context, EvaluationEngineService.class);
                    intent.putExtra("source", user.getUsername());
                    intent.putExtra("id", expressionId);
                    intent.putExtra("data", (String) null);
                    context.startService(intent);
                } else if(action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                    Log.e(TAG, "cancel remote registration for expression " + expressionId);
                    stopProcessing(expressionId, toUsername);
                    setBusy(false);

                    synchronized (evalThread) {
                        evalThread.notify();
                    }
                }

            }
        }.start();

        return false;
    }

    protected void manageConnection(final BluetoothSocket socket, final int tag) {
        new Thread() {
            public void run() {
                try {
                    InputStream is = socket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);

                    while (true) {
                        HashMap<String, String> dataMap = (HashMap<String, String>) ois.readObject();
                        String action = dataMap.get("action");
                        String source = dataMap.get("source");
                        String id = dataMap.get("id");

                        if (action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)
                                || action.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {

                            Log.w(TAG, "received " + action + " from " + source + ": " + dataMap.get("data") + " (id: " + dataMap.get("id") + ")");

                            if (source != null) {
                                SwanUser user = getPeerByUsername(source);
                                if (user != null) {
                                    if(action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                                        user.setBtSocket(socket);
                                        // e.g. user1 register expr1 for user2; user2 is not connectable; user2 register expr2
                                        // for user1, so it becomes connectable, so we have to register expr1 again for user2
                                        if (!user.isConnectable()) {
                                            user.setConnectable(true);
                                            registerPeer(user);
                                        }
                                    } else if(action.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                                        disconnect(user);
                                    }
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
                        } else if (action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                            String data = dataMap.get("data");
                            Result result = null;

                            if (data != null) {
                                result = (Result) Converter.stringToObject(data);
                            }

                            Log.w(TAG, "[" + tag + "] received " + action + " from " + source + ": " + result + " (id: " + dataMap.get("id") + ")");

                            if(isProcessing(id, source)) {
                                if (result != null && result.getValues().length > 0) {
                                    // if result is not null, send it to the evaluation engine
                                    Intent intent = new Intent(action);
                                    intent.setClass(context, EvaluationEngineService.class);
                                    intent.putExtra("id", dataMap.get("id"));
                                    intent.putExtra("data", data);
                                    context.startService(intent);

                                    // unregister the expression remotely after we get the first result, then register it again
                                    send(source, id, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
                                    addToQueue(new BTRemoteExpression(id, getPeerByUsername(source),
                                            registeredExpressions.get(id), EvaluationEngineService.ACTION_REGISTER_REMOTE));

                                    stopProcessing(id, source);
                                    setBusy(false);

                                    synchronized (evalThread) {
                                        evalThread.notify();
                                    }
                                }
                            } else {
                                Log.e(TAG, "already processed expression; ignoring result");
                            }
                        }
                    }
                } catch(Exception e) {
                    SwanUser user = getPeer(socket);

                    if(user != null) {
                        user.setBtSocket(null);
                        user.setOos(null);
                        Log.e(TAG, "[" + tag + "] disconnected from " + user + ": " + e.getMessage());
                    } else {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void addToQueue(BTRemoteExpression remoteExpr) {
        evalQueue.add(remoteExpr);
        Log.d(TAG, "remote expr added to queue: " + remoteExpr);
        Log.d(TAG, "[Queue] " + evalQueue);
    }

    private BTRemoteExpression removeFromQueue() {
        BTRemoteExpression remoteExpr = evalQueue.remove();
        Log.d(TAG, "remote expr removed from queue: " + remoteExpr);
        Log.d(TAG, "[Queue] " + evalQueue);
        return remoteExpr;
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
        registerPeer(peer);
    }

    private void registerPeer(SwanUser peer) {
        for(Map.Entry<String, String> entry : registeredExpressions.entrySet()) {
            BTRemoteExpression remoteExpr = new BTRemoteExpression(entry.getKey(), peer,
                    entry.getValue(), EvaluationEngineService.ACTION_REGISTER_REMOTE);
            addToQueue(remoteExpr);
            Log.d(TAG, "added new expression to queue: " + entry.getValue());
        }
    }

    private void unregisterPeer(SwanUser peer) {
        for(Map.Entry<String, String> entry : registeredExpressions.entrySet()) {

        }
    }

    public void removePeer(SwanUser peer) {
        nearbyPeers.remove(peer);
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

    public SwanUser getPeer(BluetoothSocket btSocket) {
        for(SwanUser peer : nearbyPeers) {
            if(peer.getBtSocket() != null && peer.getBtSocket().equals(btSocket)) {
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

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;

        if(busy) {
            Log.e(TAG, "IS BUSY");
        } else {
            Log.e(TAG, "IS NOT BUSY");
        }
    }
}

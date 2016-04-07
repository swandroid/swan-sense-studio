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
    private int exprCounter = 0;

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
                    sleep(1000);

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

                    addOrUpdatePeer(nearbyUser);
                    // code below is used by SwanLakePlus
                    Intent deviceFoundintent = new Intent();
                    deviceFoundintent.setAction(ACTION_NEARBY_DEVICE_FOUND);
                    context.sendBroadcast(deviceFoundintent);
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
                BTRemoteExpression remoteExpr = new BTRemoteExpression(getNewId(id), user, expression, action);
                addToQueue(remoteExpr);
                addedExpr = true;
                Log.d(TAG, "added new expression to queue: " + expression);
            }
        } else {
            SwanUser user = getPeerByUsername(resolvedLocation);

            BTRemoteExpression remoteExpr = new BTRemoteExpression(getNewId(id), user, expression, action);
            addToQueue(remoteExpr);
            addedExpr = true;
            Log.d(TAG, "added new expression to queue: " + expression);
        }

        if(addedExpr) {
            synchronized (evalThread) {
                evalThread.notify();
            }
        }
    }

    public void reRegisterExpression(BTRemoteExpression remoteExpression) {
        String exprId = getNewId(getOriginalId(remoteExpression.getId()));
        SwanUser exprUser = remoteExpression.getUser();
        String exprData = registeredExpressions.get(getOriginalId(remoteExpression.getId()));

        addToQueue(new BTRemoteExpression(exprId, exprUser, exprData, EvaluationEngineService.ACTION_REGISTER_REMOTE));
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

        processingList.add(expression);
        setBusy(true);

        try {
            if(expression.getAction().equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                BluetoothSocket btSocket = connect(expression.getUser());

                if(btSocket != null) {
                    OutputStream os = btSocket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(os);
                    InputStream is = btSocket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);

                    manageConnection(btSocket, ois, oos);
                    send(oos, expression.getUser().getUsername(), expression.getId(),
                            expression.getAction(), expression.getExpression());
                } else {
                    finishProcessing(expression.getId());
                    setBusy(false);

                    addToQueue(expression);

                    synchronized (evalThread) {
                        evalThread.notify();
                    }
                }
            } else {
                send(expression.getUser().getUsername(), expression.getId(),
                        expression.getAction(), expression.getExpression());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isProcessing(String id, String user) {
        for(BTRemoteExpression expr : processingList) {
            if(expr.getId().equals(id) && expr.getUser().getUsername().equals(user)) {
                return true;
            }
        }
        return false;
    }

    private boolean isProcessing(String user) {
        for(BTRemoteExpression expr : processingList) {
            if(expr.getUser().getUsername().equals(user)) {
                return true;
            }
        }
        return false;
    }

    // TODO use the list of worker threads instead
    protected void finishProcessing(String id) {
        BTRemoteExpression toRemove = null;

        for(BTRemoteExpression expr : processingList) {
            if(expr.getId().equals(id)) {
                toRemove = expr;
            }
        }

        if(toRemove != null) {
            Log.d(TAG, "stopped processing expression: " + toRemove);
            processingList.remove(toRemove);
        }
    }

    /** send expression to the evaluation engine
     * TODO consider moving this to Worker base class
     * */
    protected void sendExprForEvaluation(String exprId, String exprAction, String exprSource, String exprData) {
        Intent intent = new Intent(exprAction);
        intent.setClass(context, EvaluationEngineService.class);
        intent.putExtra("id", exprId);
        intent.putExtra("data", exprData);
        context.startService(intent);
    }

    // blocking call; use only in a separate thread
    public BluetoothSocket connect(SwanUser user) {
        Log.i(TAG, "connecting to " + user + "...");
        BluetoothSocket btSocket = null;
        btAdapter.cancelDiscovery();

        try {
            btSocket = user.getBtDevice().createInsecureRfcommSocketToServiceRecord(SERVICE_UUID);
            btSocket.connect();
            Log.i(TAG, "connected to " + user);
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

//            removeRemoteExpressions(user);
            Log.e(TAG, "can't connect to " + user.getUsername() + ": " + e.getMessage());

            return null;
        }

        return btSocket;
    }

    public void disconnect(SwanUser user) {
        try {
            BluetoothSocket btSocket = user.getBtSocket();

            if(btSocket != null) {
                btSocket.close();
                user.setBtSocket(null);
                user.setOos(null);

                Log.e(TAG, "disconnecting from " + user);

                synchronized (evalThread) {
                    evalThread.notify();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean send(final String toUsername, final String expressionId,
                        final String action, final String data) {
        return send(null, toUsername, expressionId, action, data);
    }

    public boolean send(final ObjectOutputStream _oos, final String toUsername, final String expressionId,
                      final String action, final String data) {
        new Thread() {
            @Override
            public void run() {
                SwanUser user = getPeerByUsername(toUsername);
                Log.w(TAG, "sending " + action + " message to " + toUsername + ": " + data + " (id: " + expressionId + ")");

                try {
                    if (user != null) {
                        HashMap<String, String> dataMap = new HashMap<String, String>();

                        // "from" is not allowed and results in InvalidDataKey, see:
                        // http://developer.android.com/google/gcm/gcm.html
                        dataMap.put("source", getBtAdapter().getName());
                        dataMap.put("action", action);
                        dataMap.put("data", data);
                        dataMap.put("id", expressionId);

                        ObjectOutputStream oos = _oos;

                        if(oos == null) {
                            oos = user.getOos();
                        }
                        synchronized (oos) {
                            oos.writeObject(dataMap);
                        }

                        String _data = data;
                        if(action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE) && data != null) {
                            _data = Converter.stringToObject(data).toString();
                        }

                        Log.w(TAG, "successfully sent " + action + " message to " + toUsername + ": " + _data + " (id: " + expressionId + ")");

                        return;
                    } else {
                        Log.e(TAG, "user not found");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "couldn't send " + action + " to " + user + ": " + e.getMessage(), e);
                }

                // if we are here then the send failed for some reason
                if(action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                    Log.e(TAG, "couldn't send remote result, unregistering expression " + expressionId);
                    disconnect(user);

                    // unregister the expression
                    Intent intent = new Intent(EvaluationEngineService.ACTION_UNREGISTER_REMOTE);
                    intent.setClass(context, EvaluationEngineService.class);
                    intent.putExtra("source", user.getUsername());
                    intent.putExtra("id", expressionId);
                    intent.putExtra("data", (String) null);
                    context.startService(intent);
                } else if(action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                    Log.e(TAG, "cancel remote registration for expression " + expressionId);
                    finishProcessing(expressionId);
                    setBusy(false);

                    synchronized (evalThread) {
                        evalThread.notify();
                    }
                }

            }
        }.start();

        return false;
    }

    protected void manageConnection(final BluetoothSocket socket, final ObjectInputStream ois, final ObjectOutputStream oos) {
        new Thread() {
            public void run() {
                String connectedUser = null;

                try {
                    while (true) {
                        HashMap<String, String> dataMap = (HashMap<String, String>) ois.readObject();
                        String action = dataMap.get("action");
                        String source = dataMap.get("source");
                        String id = dataMap.get("id");
                        String data = dataMap.get("data");

                        if(connectedUser == null) {
                            connectedUser = source;
                        }

                        if (action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)
                                || action.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {

                            Log.w(TAG, "received " + action + " from " + source + ": " + dataMap.get("data") + " (id: " + dataMap.get("id") + ")");

                            if (source != null) {
                                SwanUser user = getPeerByUsername(source);

                                // we've got a connection from a user that is not discovered yet
                                if(user == null) {
                                    user = new SwanUser(source, source);
                                    addOrUpdatePeer(user);
                                }

                                if(action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                                    user.setBtSocket(socket);
                                    user.setOos(oos);
                                } else if(action.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                                    disconnect(user);
                                }
                            } else {
                                Log.w(TAG, "source field is empty");
                            }

                            Intent intent = new Intent(action);
                            intent.setClass(context, EvaluationEngineService.class);
                            intent.putExtra("source", source);
                            intent.putExtra("id", id);
                            intent.putExtra("data", data);
                            context.startService(intent);
                        } else if (action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                            Result result = null;

                            if (data != null) {
                                result = (Result) Converter.stringToObject(data);
                            }

                            Log.w(TAG, "received " + action + " from " + source + ": " + result + " (id: " + id + ")");

                            if(isProcessing(id, source)) {
                                if (result != null && result.getValues().length > 0) {
                                    // if result is not null, send it to the evaluation engine
                                    Intent intent = new Intent(action);
                                    intent.setClass(context, EvaluationEngineService.class);
                                    intent.putExtra("id", getOriginalId(id));
                                    intent.putExtra("data", data);
                                    context.startService(intent);

                                    // unregister the expression remotely after we get the first result, then register it again
                                    send(oos, source, id, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
                                    addToQueue(new BTRemoteExpression(getNewId(getOriginalId(id)), getPeerByUsername(source),
                                            registeredExpressions.get(getOriginalId(id)), EvaluationEngineService.ACTION_REGISTER_REMOTE));

                                    finishProcessing(id);

                                }
                            } else {
                                Log.e(TAG, "already processed expression; ignoring result");
                                send(oos, source, id, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
                            }
                        }
                    }
                } catch(Exception e) {
                    Log.e(TAG, "disconnected from " + connectedUser + ": " + e.getMessage());

                    try {
                        setBusy(false);
                        socket.close();

                        synchronized (evalThread) {
                            evalThread.notify();
                        }
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

    public void addOrUpdatePeer(SwanUser peer) {
        if(peer.getBtDevice() != null) {
            if(hasPeer(peer.getUsername())) {
                Log.d(TAG, "updating peer " + peer.getUsername());
                getPeerByUsername(peer.getUsername()).setBtDevice(peer.getBtDevice());
            } else {
                Log.d(TAG, "adding peer " + peer.getUsername());
                nearbyPeers.add(peer);
            }
            registerPeer(peer);
        } else {
            if(!hasPeer(peer.getUsername())) {
                Log.d(TAG, "adding peer " + peer.getUsername());
                nearbyPeers.add(peer);
            }
        }
    }

    private void registerPeer(SwanUser peer) {
        for(Map.Entry<String, String> entry : registeredExpressions.entrySet()) {
            BTRemoteExpression remoteExpr = new BTRemoteExpression(getNewId(entry.getKey()), peer,
                    entry.getValue(), EvaluationEngineService.ACTION_REGISTER_REMOTE);
            addToQueue(remoteExpr);
            Log.d(TAG, "added new expression to queue: " + entry.getValue());
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

    protected void workerDone(Thread worker) {
        if(worker instanceof BTClientThread) {
            BTClientThread clientWorker = (BTClientThread) worker;
            BTRemoteExpression remoteExpression = clientWorker.getRemoteExpression();

            if(remoteExpression.getAction().equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                Log.d(TAG, "worker finished processing expression " + remoteExpression.getId());

                addToQueue(remoteExpression);
                setBusy(false);

                synchronized (evalThread) {
                    evalThread.notify();
                }
            }
        }
    }

    /** increment expression counter */
    private synchronized int incCounter() {
        return exprCounter++;
    }

    private String getNewId(String id) {
        return id + "/" + incCounter();
    }

    // TODO cosider moving this to Worker base class
    private String getOriginalId(String id) {
        return id.replaceAll("/.*", "");
    }

    public Context getContext() {
        return context;
    }
}

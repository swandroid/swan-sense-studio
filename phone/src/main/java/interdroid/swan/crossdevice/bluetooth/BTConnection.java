package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import interdroid.swan.crossdevice.CrossdeviceConnectionI;

/**
 * Created by vladimir on 7/1/16.
 * TODO set connected to false in catch blocks
 * TODO make sure that send and disconnect are not called at the same time
 * TODO fix log messages
 */
public class BTConnection extends Thread implements CrossdeviceConnectionI {

    private static final String TAG = "BTConnection";

    protected BTManager btManager;
    protected BTConnectionHandler connectionHandler;
    private boolean connected = false;

    protected BluetoothSocket btSocket;
    protected ObjectOutputStream outStream;
    protected ObjectInputStream inStream;

    public BTConnection(BTManager btManager, BluetoothSocket btSocket) {
        this.btSocket = btSocket;
        this.btManager = btManager;

        try {
            initConnection();
            connected = true;
        } catch (Exception e) {
            btManager.log(TAG, "can't connect to " + btSocket.getRemoteDevice().getName() + ": " + e.getMessage(), Log.ERROR, true);

            try {
                btSocket.close();
            } catch (Exception e1) {
                btManager.log(TAG, "couldn't close socket", Log.ERROR, e1);
            }
        }
    }

    public BTConnection(BTManager btManager, BTConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.btManager = btManager;
    }

    protected void initConnection() throws IOException {
        OutputStream os = btSocket.getOutputStream();
        outStream = new ObjectOutputStream(os);
        InputStream is = btSocket.getInputStream();
        inStream = new ObjectInputStream(is);
    }

    // blocking call; use only in a separate thread
    protected void connect(BluetoothDevice device) {
        int uuidIdx = new Random().nextInt(BTManager.SERVICE_UUIDS.length);
        UUID uuid = BTManager.SERVICE_UUIDS[uuidIdx];
        btManager.log(TAG, "connecting to " + device.getName() + " on port " + uuidIdx + "...", Log.INFO, true);
        btManager.bcastLogMessage("connecting to " + device.getName() + " on port " + uuidIdx + "...");

        try {
            btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
            initConnection();
            connected = true;
            btManager.log(TAG, "connected to " + device.getName(), Log.INFO, true);
            btManager.bcastLogMessage("connected to " + device.getName());
            return;
        } catch (Exception e) {
            btManager.log(TAG, "can't connect to " + device.getName() + ": " + e.getMessage(), Log.ERROR, true);

            try {
                btSocket.close();
            } catch (Exception e1) {
                btManager.log(TAG, "couldn't close socket", Log.ERROR, e1);
            }
        }

        btSocket = null;
    }

    // we synchronize this to make sure that disconnect() is not called at the same time
    public synchronized void send(HashMap<String, String> dataMap) throws Exception {
        synchronized (outStream) {
            outStream.writeObject(dataMap);
        }
    }

    @Override
    public void run() {
        btManager.log(TAG, connectionHandler + " connection ready", Log.DEBUG);

        try {
            while (true) {
                HashMap<String, String> dataMap = (HashMap<String, String>) inStream.readObject();
                connectionHandler.onReceive(dataMap);
            }
        } catch (Exception e) {
            btManager.log(TAG, connectionHandler + " disconnected: " + e.getMessage(), Log.ERROR);
            connected = false;
            connectionHandler.onDisconnected(e);

            try {
                btSocket.close();
            } catch (IOException e1) {
                btManager.log(TAG, connectionHandler + " couldn't close socket", Log.ERROR, e1);
            }
        }
    }

    // we synchronize this to make sure that BTWorker.send() is not called at the same time
    public synchronized void disconnect() {
        try {
            btManager.log(TAG, connectionHandler + " disconnecting", Log.ERROR, true);
            btSocket.close();
        } catch (IOException e) {
            btManager.log(TAG, connectionHandler + " couldn't close socket", Log.ERROR, e);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnectionHandler(BTConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }
}

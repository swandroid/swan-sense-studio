package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import interdroid.swan.crossdevice.CrossdeviceConnectionI;

/**
 * Created by vladimir on 7/14/16.
 */
public class WifiConnection extends Thread implements CrossdeviceConnectionI {

    private static final String TAG = "BTConnection";
    protected static final int PORT = 2162;

    protected BTManager btManager;
    protected BTSwanDevice swanDevice;
    protected BTConnectionHandler connectionHandler;
    private boolean connected = false;

    private Socket socket;
    protected ObjectOutputStream outStream;
    protected ObjectInputStream inStream;

    public WifiConnection(BTManager btManager, BTSwanDevice swanDevice) {
        this.swanDevice = swanDevice;
        this.connectionHandler = swanDevice;
        this.btManager = btManager;
    }

    public WifiConnection(BTManager btManager, BTSwanDevice swanDevice, Socket socket) {
        this(btManager, swanDevice);
        this.socket = socket;

        try {
            initConnection();
            connected = true;
        } catch (Exception e) {
            btManager.log(TAG, "can't connect to " + swanDevice.getName() + ": " + e.getMessage(), Log.ERROR, true);
        }
    }

    protected void initConnection() throws IOException {
        OutputStream os = socket.getOutputStream();
        outStream = new ObjectOutputStream(os);
        InputStream is = socket.getInputStream();
        inStream = new ObjectInputStream(is);
    }

    protected void connect(String ipAddress) {
        btManager.log(TAG, "connecting to " + swanDevice.getName() + " on port " + PORT + " via wifi...", Log.INFO, true);
        btManager.bcastLogMessage("connecting to " + swanDevice.getName() + " on port " + PORT + " via wifi...");

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            socket = new Socket(inetAddress, PORT);
            initConnection();
            connected = true;
            btManager.log(TAG, "connected to " + swanDevice.getName() + " via wifi", Log.INFO, true);
            btManager.bcastLogMessage("connected to " + swanDevice.getName() + " via wifi");
            return;
        } catch (Exception e) {
            btManager.log(TAG, "can't connect to " + swanDevice.getName() + ": " + e.getMessage(), Log.ERROR, true);

            try {
                socket.close();
            } catch (Exception e1) {
                btManager.log(TAG, "couldn't close socket", Log.ERROR, e1);
            }
        }

        socket = null;
    }

    // we synchronize this to make sure that BTWorker.send() is not called at the same time
    public synchronized void disconnect() {
        try {
            btManager.log(TAG, connectionHandler + " disconnecting", Log.ERROR, true);
            socket.close();
        } catch (IOException e) {
            btManager.log(TAG, connectionHandler + " couldn't close socket", Log.ERROR, e);
        }
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
                socket.close();
            } catch (IOException e1) {
                btManager.log(TAG, connectionHandler + " couldn't close socket", Log.ERROR, e1);
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }
}

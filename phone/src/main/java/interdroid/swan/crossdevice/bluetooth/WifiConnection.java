package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by vladimir on 7/14/16.
 */
public class WifiConnection {

    private static final String TAG = "BTConnection";

    protected BTManager btManager;
    protected BTConnectionHandler connectionHandler;
    private boolean connected = false;

    private Socket socket;
    protected ObjectOutputStream outStream;
    protected ObjectInputStream inStream;

    public WifiConnection(BTManager btManager, Socket socket) {
        this.socket = socket;
        this.btManager = btManager;

        try {
            initConnection();
            connected = true;
        } catch (Exception e) {
            //btManager.log(TAG, "can't connect to " + btSocket.getRemoteDevice().getName() + ": " + e.getMessage(), Log.ERROR, true);
        }
    }

    protected void initConnection() throws IOException {
        OutputStream os = socket.getOutputStream();
        outStream = new ObjectOutputStream(os);
        InputStream is = socket.getInputStream();
        inStream = new ObjectInputStream(is);
    }
}

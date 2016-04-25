package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

/**
 * Created by vladimir on 3/11/16.
 */
public class BTReceiver extends Thread  {
    private static final String TAG = "BTReceiver";

    private BTManager btManager;
    private Context context;
    private UUID uuid;
    private BluetoothServerSocket serverSocket;

    public BTReceiver(BTManager btManager, Context context, UUID uuid) {
        this.btManager = btManager;
        this.context = context;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        Log.d(TAG, "BT receiver started for uuid " + uuid);
        serverSocket = getServerSocket();

        try {
            while (true) {
                BluetoothSocket socket = serverSocket.accept();

                if (socket != null) {
                    BTServerWorker serverWorker = new BTServerWorker(btManager, socket);
                    // we add the device in the nearby devices list in case it wasn't discovered already
                    btManager.addNearbyDevice(socket.getRemoteDevice());
                    btManager.addServerWorker(serverWorker);
                    serverWorker.start();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "receiver was stopped: " + e.getMessage());
        }
    }

    private BluetoothServerSocket getServerSocket() {
        BluetoothServerSocket serverSocket = null;
        try {
            serverSocket = btManager.getBtAdapter().listenUsingInsecureRfcommWithServiceRecord(BTManager.SERVICE_NAME, uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverSocket;
    }

    public void abort() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.e(TAG, this + " couldn't close socket", e);
        }
    }
}

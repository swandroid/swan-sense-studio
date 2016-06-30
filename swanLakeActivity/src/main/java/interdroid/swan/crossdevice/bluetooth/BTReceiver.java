package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by vladimir on 3/11/16.
 */
public class BTReceiver extends Thread {
    private static final String TAG = "BTReceiver";

    private BTManager btManager;
    private Context context;
    private UUID uuid;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;

    public BTReceiver(BTManager btManager, Context context, UUID uuid) {
        this.btManager = btManager;
        this.context = context;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        Log.d(TAG, "BT receiver started for uuid " + uuid);

        // comment this and uncomment the rest to allow only one serverWorker to be active at a time
        serverSocket = openServerSocket();

        try {
            while (true) {
//                Log.d(TAG, "receiver resumed for uuid " + uuid);
//                serverSocket = openServerSocket();
                socket = serverSocket.accept();

                if (socket != null) {
//                    Log.d(TAG, "receiver paused for uuid " + uuid);
//                    serverSocket.close();

                    // we add the device in the nearby devices list in case it wasn't discovered already
                    BTSwanDevice swanDevice = btManager.addNearbyDevice(new BTSwanDevice(socket.getRemoteDevice()));

                    BTServerWorker serverWorker = new BTServerWorker(btManager, socket, swanDevice);
                    btManager.addServerWorker(serverWorker);
                    serverWorker.start();

//                    synchronized (this) {
//                        wait();
//                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "receiver was stopped: " + e.getMessage());
        }
    }

    private BluetoothServerSocket openServerSocket() {
        BluetoothServerSocket serverSocket = null;
        try {
            serverSocket = btManager.getBtAdapter().listenUsingInsecureRfcommWithServiceRecord(BTManager.SERVICE_NAME, uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverSocket;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }
}

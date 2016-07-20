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
    private UUID uuid;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;

    public BTReceiver(BTManager btManager, UUID uuid) {
        this.btManager = btManager;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        Log.d(TAG, "BT receiver started for uuid " + uuid);

        if(BTManager.SYNC_RECEIVERS) {
            runWithSynchronousWorkers();
        } else {
            runWithAsynchronousWorkers();
        }
    }

    private void runWithAsynchronousWorkers() {
        serverSocket = openServerSocket();

        try {
            while (true) {
                socket = serverSocket.accept();

                if (socket != null) {
                    manageConnection();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "receiver was stopped: " + e.getMessage());
        }
    }

    private void runWithSynchronousWorkers() {
        try {
            while (true) {
                Log.d(TAG, "receiver resumed for uuid " + uuid);
                serverSocket = openServerSocket();
                socket = serverSocket.accept();

                if (socket != null) {
                    Log.d(TAG, "receiver paused for uuid " + uuid);
                    serverSocket.close();

                    if(manageConnection()) {
                        synchronized (this) {
                            wait();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "receiver was stopped: " + e.getMessage());
        }
    }

    private boolean manageConnection() {
        Log.i(TAG, "new connection from " + socket.getRemoteDevice().getName());
        BTConnection btConnection = new BTConnection(btManager, socket);
        // we add the device in the nearby devices list in case it wasn't discovered already
        BTSwanDevice swanDevice = btManager.addNearbyDevice(socket.getRemoteDevice(), btConnection);

        if(btConnection.isConnected()) {
            BTServerWorker serverWorker = new BTServerWorker(btManager, swanDevice, btConnection);
            btManager.addServerWorker(serverWorker);

            if(BTManager.SHARED_CONNECTIONS) {
                btConnection.setConnectionHandler(swanDevice);
                swanDevice.setServerWorker(serverWorker);
            } else {
                btConnection.setConnectionHandler(serverWorker);
            }

            btConnection.start();
            return true;
        } else {
            return false;
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

    public void abort() {
        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

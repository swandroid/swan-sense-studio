package interdroid.swan.crossdevice.bluetooth;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by vladimir on 7/15/16.
 */
public class WifiReceiver extends Thread {
    private static final String TAG = "WifiReceiver";

    private BTManager btManager;
    private ServerSocket serverSocket;
    private Socket socket;

    public WifiReceiver(BTManager btManager) {
        this.btManager = btManager;
    }

    @Override
    public void run() {
        Log.d(TAG, "Wifi receiver started on port " + WifiConnection.PORT);

        try {
            serverSocket = new ServerSocket(WifiConnection.PORT);

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

    private void manageConnection() {
        BTSwanDevice swanDevice = btManager.getNearbyDeviceByIp(socket.getInetAddress().getHostAddress());
        Log.i(TAG, "new connection from " + swanDevice.getName() + " via wifi");
        WifiConnection wifiConnection = new WifiConnection(btManager, swanDevice, socket);

        if(wifiConnection.isConnected()) {
            BTServerWorker serverWorker = new BTServerWorker(btManager, swanDevice, wifiConnection);
            btManager.addServerWorker(serverWorker);
            swanDevice.setServerWorker(serverWorker);
            swanDevice.setConnection(wifiConnection);
            wifiConnection.start();
        }
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

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

/**
 * Created by vladimir on 3/11/16.
 */
public class BTReceiver extends AsyncTask<Void, String, Void>  {
    private static final String TAG = "BTReceiver";

    private BTManager btManager;
    private Context context;

    public BTReceiver(BTManager btManager, Context context) {
        this.btManager = btManager;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "BT receiver started");

        try {
            while (true) {
                BluetoothServerSocket serverSocket = getServerSocket();
                BluetoothSocket socket = serverSocket.accept();

                if (socket != null) {
                    BTServerWorker serverWorker = new BTServerWorker(btManager, socket);
                    // we add the device in the nearby devices list in case it wasn't discovered already
                    btManager.addNearbyDevice(socket.getRemoteDevice());
                    btManager.addServerWorker(serverWorker);
                    serverWorker.start();
                }

                synchronized (this) {
                    wait();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    private BluetoothServerSocket getServerSocket() {
        BluetoothServerSocket tmp = null;
        try {
            tmp = btManager.getBtAdapter().listenUsingInsecureRfcommWithServiceRecord(BTManager.SERVICE_NAME, BTManager.SERVICE_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;
    }
}

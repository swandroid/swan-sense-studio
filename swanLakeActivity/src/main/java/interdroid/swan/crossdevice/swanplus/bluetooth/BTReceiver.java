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
    private final BluetoothServerSocket mmServerSocket;

    public BTReceiver(BTManager btManager, Context context) {
        this.btManager = btManager;
        this.context = context;

        BluetoothServerSocket tmp = null;
        try {
            tmp = btManager.getBtAdapter().listenUsingInsecureRfcommWithServiceRecord(BTManager.SERVICE_NAME, BTManager.SERVICE_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mmServerSocket = tmp;
    }

    @Override
    protected Void doInBackground(Void... params) {
        BluetoothSocket socket;
        Log.d(TAG, "BT receiver started");

        try {
            while (true) {
                socket = mmServerSocket.accept();

                if (socket != null) {
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(os);
                    InputStream is = socket.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);

                    // Do work to manage the connection (in a separate thread)
                    btManager.manageConnection(socket, ois, oos);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }

}

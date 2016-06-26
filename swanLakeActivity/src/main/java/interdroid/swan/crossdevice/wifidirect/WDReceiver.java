package interdroid.swan.crossdevice.wifidirect;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import interdroid.swan.engine.EvaluationEngineService;

/**
 * Created by vladimir on 12/1/15.
 */
public class WDReceiver extends AsyncTask<Void, String, Void> {

    private static final String TAG = "WDReceiver";
    private WDManager wdManager;
    private Context context;
    private final static int PORT = 2222;

    public WDReceiver(WDManager wdManager, Context context) {
        this.wdManager = wdManager;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            while (true) {
                receive();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public void receive() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            byte[] data = receivePacket.getData();
            InetAddress remoteIp = receivePacket.getAddress();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);

            HashMap<String, String> dataMap = (HashMap<String, String>) is.readObject();
            String action = dataMap.get("action");
            Log.d(TAG, "received " + action + " from " + remoteIp);

            if (action.equals("initConnect")) {
                wdManager.connected(remoteIp.getHostAddress(), false);
            } else if (action.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)) {
                String source = dataMap.get("source");

                if (source != null) {
                    WDSwanDevice user = wdManager.getPeerByRegId(source);
                    if (user != null) {
                        user.setIp(remoteIp);
                    }
                } else {
                    Log.w(TAG, "source field is empty");
                }

                Intent intent = new Intent(action);
                intent.setClass(context, EvaluationEngineService.class);
                intent.putExtra("source", source);
                intent.putExtra("id", dataMap.get("id"));
                intent.putExtra("data", dataMap.get("data"));
                context.startService(intent);
            } else if (action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                Intent intent = new Intent(action);
                intent.setClass(context, EvaluationEngineService.class);
                intent.putExtra("id", dataMap.get("id"));
                intent.putExtra("data", dataMap.get("data"));
                context.startService(intent);
            } else {
                Log.d(TAG, "got something else: " + action);
            }

            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

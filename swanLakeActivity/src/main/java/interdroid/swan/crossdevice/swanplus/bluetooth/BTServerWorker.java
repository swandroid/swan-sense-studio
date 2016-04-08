package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import interdroid.swan.engine.EvaluationEngineService;

/**
 * Created by vladimir on 4/8/16.
 */
public class BTServerWorker extends BTWorker {

    private static final String TAG = "BTServerWorker";

    public BTServerWorker(BTManager btManager, BluetoothSocket btSocket) {
        this.btManager = btManager;
        this.btSocket = btSocket;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "server worker started for device " + getRemoteDeviceName());
            initConnection();
            manageServerConnection();
        } catch(Exception e) {
            Log.e(TAG, "ERROR in server worker", e);
        }
    }

    protected void manageServerConnection() {
        try {
            while (true) {
                HashMap<String, String> dataMap = (HashMap<String, String>) inStream.readObject();

                String exprAction = dataMap.get("action");
                String exprSource = dataMap.get("source");
                String exprId = dataMap.get("id");
                String exprData = dataMap.get("data");

                if (exprAction.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)
                        || exprAction.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                    Log.w(TAG, "received " + exprAction + " from " + exprSource + ": " + exprData + " (id: " + exprId + ")");

                    btManager.sendExprForEvaluation(exprId, exprAction, exprSource, exprData);

                    if(exprAction.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                        disconnect();
                    }
                } else {
                    Log.e(TAG, "ERROR, shouldn't receive " + exprAction);
                }
            }
        } catch(Exception e) {
            Log.e(TAG, "disconnected from " + getRemoteDeviceName() + ": " + e.getMessage());

            try {
                btManager.workerDone(this);
                btSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, "couldn't close socket: " + e1.getMessage(), e1);
            }
        }
    }

    public void disconnect() {
        try {
            Log.e(TAG, "disconnecting from " + getRemoteDeviceName());
            btSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "couldn't close socket: " + e.getMessage(), e);
        }
    }

    protected String getTag() {
        return TAG;
    }

    @Override
    public String toString() {
        return "BTServerWorker(device = " + getRemoteDeviceName() + ")";
    }
}

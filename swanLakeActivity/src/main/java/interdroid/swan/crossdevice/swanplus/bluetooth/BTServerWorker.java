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
            Log.d(TAG, this + " started");
            initConnection();
            manageServerConnection();
        } catch(Exception e) {
            Log.e(TAG, this + " crashed", e);
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
                    Log.w(TAG, this + " received " + exprAction + ": " + exprData);

                    btManager.sendExprForEvaluation(exprId, exprAction, exprSource, exprData);

                    if(exprAction.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                        disconnect();
                    }
                } else {
                    Log.e(TAG, this + " didn't expect " + exprAction);
                }
            }
        } catch(Exception e) {
            Log.e(TAG, this + " disconnected: " + e.getMessage());
            //TODO unregister expression

            try {
                btManager.workerDone(this);
                btSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, this + " couldn't close socket", e1);
            }
        }
    }

    public void disconnect() {
        try {
            Log.e(TAG, this + " disconnecting");
            btSocket.close();
        } catch (IOException e) {
            Log.e(TAG, this + " couldn't close socket", e);
        }
    }

    protected String getTag() {
        return TAG;
    }

    @Override
    public String toString() {
        return "SW[" + getRemoteDeviceName() + "]";
    }
}

package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.swansong.Result;

/**
 * Created by vladimir on 4/8/16.
 */
public class BTServerWorker extends BTWorker {

    private static final String TAG = "BTServerWorker";

    private Set<String> expressionIds = new TreeSet<String>();

    public BTServerWorker(BTManager btManager, BluetoothSocket btSocket, BTSwanDevice swanDevice) {
        this.btManager = btManager;
        this.btSocket = btSocket;
        this.swanDevice = swanDevice;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, this + " started");
            initConnection();
            manageServerConnection();
        } catch (Exception e) {
            Log.e(TAG, this + " crashed", e);
            btManager.serverWorkerDone(this, true);
        }
    }

    @Override
    protected synchronized void send(String expressionId, String expressionAction, String expressionData) throws Exception {
        // we don't check action type, because it can only be ACTION_NEW_RESULT_REMOTE
        Result result = expressionData != null ? (Result) Converter.stringToObject(expressionData) : null;

        if(isValidResult(result)) {
            btManager.sendExprForEvaluation(expressionId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, swanDevice.getName(), null);
            super.send(expressionId, expressionAction, expressionData);
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

                // add the expression id to the list of registered expressions
                expressionIds.add(exprId);

                if (exprAction.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)
                        || exprAction.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                    Log.w(TAG, this + " received " + exprAction + ": " + exprData);

                    btManager.sendExprForEvaluation(exprId, exprAction, exprSource, exprData);

                    if (exprAction.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                        expressionIds.remove(exprId);

                        if(expressionIds.isEmpty()) {
                            disconnect();
                        }
                    }
                } else {
                    Log.e(TAG, this + " didn't expect " + exprAction);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, this + " disconnected: " + e.getMessage());

            // unregister expressions
            for(String exprId : expressionIds) {
                btManager.sendExprForEvaluation(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, swanDevice.getName(), null);
            }

            try {
                btManager.serverWorkerDone(this, false);
                btSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, this + " couldn't close socket", e1);
            }
        }
    }

    // we synchronize this to make sure that BTWorker.send() is not called at the same time
    public synchronized void disconnect() {
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
        return "SW[" + getRemoteDeviceName() + ":" + expressionIds + "]";
    }
}

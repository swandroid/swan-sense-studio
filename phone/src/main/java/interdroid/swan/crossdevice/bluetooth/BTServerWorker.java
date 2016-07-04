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
public class BTServerWorker extends BTWorker implements BTConnectionHandler {

    private static final String TAG = "BTServerWorker";

    private Set<String> expressionIds = new TreeSet<String>();

    public BTServerWorker(BTManager btManager, BTSwanDevice swanDevice, BTConnection btConnection) {
        this.btManager = btManager;
        this.swanDevice = swanDevice;
        this.btConnection = btConnection;
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

    @Override
    public void onReceive(HashMap<String, String> dataMap) {
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
                    done();
                }
            }
        } else {
            Log.e(TAG, this + " didn't expect " + exprAction);
        }
    }

    @Override
    public void onDisconnected(Exception e) {
        Log.e(TAG, this + " disconnected: " + e.getMessage());

        // unregister expressions
        for(String exprId : expressionIds) {
            btManager.sendExprForEvaluation(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, swanDevice.getName(), null);
        }

        swanDevice.setServerWorker(null);
        btManager.serverWorkerDone(this);
    }

    private void done() {
        if(BTManager.THREADED_WORKERS) {
            btConnection.disconnect();
        } else {
            swanDevice.setServerWorker(null);
            btManager.serverWorkerDone(this);
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

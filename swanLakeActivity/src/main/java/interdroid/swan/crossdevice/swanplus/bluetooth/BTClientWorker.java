package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import interdroid.swan.crossdevice.Converter;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swan.swansong.Result;

/**
 * Created by vladimir on 4/7/16.
 *
 * TODO handle exceptions in send()
 */
public class BTClientWorker extends BTWorker {

    private static final String TAG = "BTClientWorker";

    private BTRemoteExpression remoteExpression;

    public BTClientWorker(BTManager btManager, BTRemoteExpression remoteExpression) {
        this.btManager = btManager;
        this.remoteExpression = remoteExpression;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "client worker started processing expression " + remoteExpression.getId());
            btSocket = btManager.connect(remoteExpression.getRemoteDevice());

            if (btSocket != null) {
                initConnection();
                send(remoteExpression.getId(), remoteExpression.getAction(), remoteExpression.getExpression());
                manageClientConnection();
            } else {
                btManager.workerDone(this);
            }
        } catch(Exception e) {
            Log.e(TAG, "ERROR in client worker", e);
            btManager.workerDone(this);
        }
    }

    protected void manageClientConnection() {
        try {
            while (true) {
                HashMap<String, String> dataMap = (HashMap<String, String>) inStream.readObject();

                String exprAction = dataMap.get("action");
                String exprSource = dataMap.get("source");
                String exprId = dataMap.get("id");
                String exprData = dataMap.get("data");

                if(exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
                    Result result = exprData != null ? (Result) Converter.stringToObject(exprData) : null;
                    Log.w(TAG, "received " + exprAction + " from " + exprSource + ": " + result + " (id: " + exprId + ")");

                    if(exprId.equals(remoteExpression.getId())) {
                        if (result != null && result.getValues().length > 0) {
                            btManager.sendExprForEvaluation(getOriginalId(exprId), exprAction, exprSource, exprData);
                            send(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
                        }
                    } else {
                        Log.e(TAG, "ERROR, received result for wrong expression, expecting " + remoteExpression.getId());
                        send(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
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

    private String getOriginalId(String id) {
        return id.replaceAll("/.*", "");
    }

    public BTRemoteExpression getRemoteExpression() {
        return remoteExpression;
    }

    protected String getTag() {
        return TAG;
    }

}

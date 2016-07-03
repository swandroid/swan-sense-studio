package interdroid.swan.crossdevice.bluetooth;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import interdroid.swancore.crossdevice.Converter;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swancore.swansong.Result;

/**
 * Created by vladimir on 4/7/16.
 */
public class BTClientWorker extends BTWorker implements BTConnectionHandler {

    private static final String TAG = "BTClientWorker";

    private BTRemoteEvaluationTask remoteEvaluationTask;
    int remoteTimeToNextRequest = 0;

    public BTClientWorker(BTManager btManager, BTRemoteEvaluationTask remoteEvaluationTask) {
        this.btManager = btManager;
        this.remoteEvaluationTask = remoteEvaluationTask;
        this.swanDevice = remoteEvaluationTask.getSwanDevice();
    }

    public void doWork() {
        try {
            Log.d(TAG, this + " started processing");
            connectToRemote();

            if(isConnectedToRemote()) {
                for(BTRemoteExpression remoteExpression : remoteEvaluationTask.getExpressions()) {
                    send(remoteExpression.getId(), EvaluationEngineService.ACTION_REGISTER_REMOTE, remoteExpression.getExpression());
                }
            } else {
                btManager.clientWorkerDone(this);
            }
        } catch (Exception e) {
            Log.e(TAG, this + " crashed", e);
            btManager.clientWorkerDone(this);
        }
    }

    protected void connectToRemote() {
        if(BTManager.THREADED_WORKERS) {
            btConnection = new BTConnection(this);
            btConnection.connect(swanDevice.getBtDevice());

            if(btConnection.isConnected()) {
                btConnection.start();
            }
        } else {
            if(!swanDevice.isConnectedToRemote()) {
                btConnection = new BTConnection(swanDevice);
                btConnection.connect(swanDevice.getBtDevice());

                if(btConnection.isConnected()) {
                    btConnection.start();
                }
            }
        }
    }

    public boolean isConnectedToRemote() {
        if(BTManager.THREADED_WORKERS) {
            return btConnection != null && btConnection.isConnected();
        } else {
            return swanDevice.isConnectedToRemote();
        }
    }

    @Override
    public void onReceive(HashMap<String, String> dataMap) throws Exception {
        String exprAction = dataMap.get("action");
        String exprSource = dataMap.get("source");
        String exprId = dataMap.get("id");
        String exprData = dataMap.get("data");
        String timeToNextReq = dataMap.get("timeToNextReq");

        if (exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
            BTRemoteExpression remoteExpression = remoteEvaluationTask.getRemoteExpression(exprId);
            // TODO for "undefined" result, resend request
            Result result = exprData != null ? (Result) Converter.stringToObject(exprData) : null;
            Log.w(TAG, this + " received " + exprAction + ": " + result);

            if (remoteExpression != null) {
                if (isValidResult(result)) {
                    if(timeToNextReq != null) {
                        remoteTimeToNextRequest = Integer.parseInt(timeToNextReq);
                    }
                    btManager.sendExprForEvaluation(remoteExpression.getBaseId(), exprAction, exprSource, exprData);
                    send(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
                }
            } else {
                Log.e(TAG, this + " received result for wrong expression: " + exprId);
                send(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
            }
        } else {
            Log.e(TAG, this + " didn't expect " + exprAction);
        }
    }

    @Override
    public void onDisconnected(Exception e, boolean crashed) {
        Log.e(TAG, this + " disconnected: " + e.getMessage());
        btManager.clientWorkerDone(this, remoteTimeToNextRequest);
    }

    public BTRemoteEvaluationTask getRemoteEvaluationTask() {
        return remoteEvaluationTask;
    }

    protected String getTag() {
        return TAG;
    }

    @Override
    public String toString() {
        return "CW[" + getRemoteDeviceName() + ":" + remoteEvaluationTask.getExpressionIds() + "]";
    }
}

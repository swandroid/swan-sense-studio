package interdroid.swan.crossdevice.bluetooth;

import android.util.Log;

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
        logRecord = new BTLogRecord(btManager.getStartTime(), true);
    }

    public void doWork() {
        try {
            connectToRemote();
            logRecord.connDuration = System.currentTimeMillis() - logRecord.startTime;

            if(isConnectedToRemote()) {
                for(BTRemoteExpression remoteExpression : remoteEvaluationTask.getExpressions()) {
                    send(remoteExpression.getId(), EvaluationEngineService.ACTION_REGISTER_REMOTE, remoteExpression.getExpression());
                }
            } else {
                done();
            }
        } catch (Exception e) {
            Log.e(TAG, this + " crashed", e);
        }
    }

    protected void connectToRemote() {
        if(BTManager.SHARED_CONNECTIONS) {
            swanDevice.setClientWorker(this);

            if(!swanDevice.isConnectedToRemote()) {
                BTConnection btConnection = new BTConnection(btManager, swanDevice);
                btConnection.connect(swanDevice.getBtDevice());

                if(btConnection.isConnected()) {
                    swanDevice.setBtConnection(btConnection);
                    btConnection.start();
                }
            }
        } else {
            btConnection = new BTConnection(btManager, this);
            btConnection.connect(swanDevice.getBtDevice());

            if(btConnection.isConnected()) {
                btConnection.start();
            }
        }
    }

    public boolean isConnectedToRemote() {
        if(BTManager.SHARED_CONNECTIONS) {
            return swanDevice.isConnectedToRemote();
        } else {
            return btConnection != null && btConnection.isConnected();
        }
    }

    @Override
    public void onReceive(HashMap<String, String> dataMap) throws Exception {
        String exprAction = dataMap.get("action");
        String exprSource = dataMap.get("source");
        String exprId = dataMap.get("id");
        String exprData = dataMap.get("data");
        String timeToNextReq = dataMap.get("timeToNextReq");
        String swanDuration = dataMap.get("swanDuration");

        if (exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
            BTRemoteExpression remoteExpression = remoteEvaluationTask.getRemoteExpression(exprId);
            // TODO for "undefined" result, resend request
            Result result = exprData != null ? (Result) Converter.stringToObject(exprData) : null;
            Log.w(TAG, this + " received " + exprAction + ": " + result);

            if (remoteExpression != null) {
                if (isValidResult(result)) {
                    btManager.bcastLogMessage("got new result from " + swanDevice);

                    if(timeToNextReq != null) {
                        remoteTimeToNextRequest = Integer.parseInt(timeToNextReq);
                    }
                    if(swanDuration != null) {
                        logRecord.swanDuration = Integer.parseInt(swanDuration);
                    }

                    btManager.sendExprForEvaluation(remoteExpression.getBaseId(), exprAction, exprSource, exprData);
                    send(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
                    remoteEvaluationTask.removeExpression(remoteExpression);

                    if(!remoteEvaluationTask.hasExpressions()) {
                        done();
                    }
                }
            } else {
                Log.e(TAG, this + " received result for wrong or outdated expression: " + exprId);
                send(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
            }
        } else {
            Log.e(TAG, this + " didn't expect " + exprAction);
        }
    }

    @Override
    public void onDisconnected(Exception e) {
        Log.e(TAG, this + " disconnected: " + e.getMessage());
        done();
    }

    @Override
    protected void done() {
        super.done();

        // if we have unprocessed expressions left, it means it crashed
        if(remoteEvaluationTask.hasExpressions()) {
            logRecord.failed = true;
        }
        swanDevice.setClientWorker(null);
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

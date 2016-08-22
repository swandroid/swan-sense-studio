package interdroid.swan.crossdevice.bluetooth;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        logRecord.sensors = remoteEvaluationTask.getExpressions().size();
    }

    @Override
    public void run() {
        try {
            connectToRemote();
            logRecord.connDuration = System.currentTimeMillis() - logRecord.startTime;
            // we make a copy of the original expressions list to avoid ConcurrentModificationException that occurs
            // when we iterate over the expressions while at the same time expressions are removed in onReceive()
            List<BTRemoteExpression> remoteExpressions = new ArrayList<>(remoteEvaluationTask.getExpressions());

            if(isConnectedToRemote()) {
                for(BTRemoteExpression remoteExpression : remoteExpressions) {
                    send(remoteExpression.getId(), EvaluationEngineService.ACTION_REGISTER_REMOTE, remoteExpression.getExpression());
                }
            } else {
                done();
            }
        } catch (Exception e) {
            btManager.log(TAG, this + " crashed", Log.ERROR, true, e);
        }
    }

    protected void connectToRemote() {
        if(BTManager.SHARED_CONNECTIONS) {
            swanDevice.setClientWorker(this);

            if(!swanDevice.isConnectedToRemote()) {
                if(!BTManager.USE_WIFI || swanDevice.getIpAddress() == null) {
                    BTConnection btConnection = new BTConnection(btManager, swanDevice);
                    btConnection.connect(swanDevice.getBtDevice());

                    if (btConnection.isConnected()) {
                        swanDevice.setConnection(btConnection);
                        btConnection.start();
                    }
                } else {
                    WifiConnection wifiConnection = new WifiConnection(btManager, swanDevice);
                    wifiConnection.connect(swanDevice.getIpAddress());

                    if(wifiConnection.isConnected()) {
                        swanDevice.setConnection(wifiConnection);
                        wifiConnection.start();
                    }
                }
            }
        } else {
            BTConnection btConnection = new BTConnection(btManager, this);
            btConnection.connect(swanDevice.getBtDevice());

            if(btConnection.isConnected()) {
                connection = btConnection;
                btConnection.start();
            }
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
        String ipAddress = dataMap.get("ip");

        if(ipAddress != null) {
            swanDevice.setIpAddress(ipAddress);
        }

        if (exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
            BTRemoteExpression remoteExpression = remoteEvaluationTask.getRemoteExpression(exprId);
            // TODO for "undefined" result, resend request
            Result result = exprData != null ? (Result) Converter.stringToObject(exprData) : null;
            btManager.log(TAG, this + " received " + exprAction + ": " + result, Log.WARN);

            if (remoteExpression != null) {
                if (isValidResult(result)) {
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
                        if(BTManager.SHARED_CONNECTIONS) {
                            done();
                        }
                    }
                }
            } else {
                btManager.log(TAG, this + " received result for wrong or outdated expression: " + exprId, Log.ERROR);
                send(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
            }
        } else {
            btManager.log(TAG, this + " didn't expect " + exprAction, Log.ERROR);
        }
    }

    @Override
    public void onDisconnected(Exception e) {
        btManager.log(TAG, this + " disconnected: " + e.getMessage(), Log.ERROR);
        done();
    }

    @Override
    protected void done() {
        super.done();

        // if we have unprocessed expressions left, it means it crashed
        if(remoteEvaluationTask.hasExpressions()) {
            btManager.bcastLogMessage("[FAIL] worker failed for " + swanDevice);
            btManager.log(TAG, "[FAIL] worker failed for " + swanDevice, Log.ERROR, true);
            logRecord.failed = true;
        } else {
            btManager.bcastLogMessage("[SUCCESS] worker done for " + swanDevice);
            btManager.log(TAG, "[SUCCESS] worker done for " + swanDevice, Log.INFO, true);
        }
        swanDevice.setClientWorker(null);
        btManager.clientWorkerDone(this, remoteTimeToNextRequest);
    }

    protected String getTag() {
        return TAG;
    }

    @Override
    public String toString() {
        return "CW[" + getRemoteDeviceName() + ":" + remoteEvaluationTask.getExpressionIds() + "]";
    }
}

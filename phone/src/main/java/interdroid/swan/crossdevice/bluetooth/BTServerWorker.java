package interdroid.swan.crossdevice.bluetooth;

import android.util.Log;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import interdroid.swan.crossdevice.CrossdeviceConnectionI;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.swansong.Result;

/**
 * Created by vladimir on 4/8/16.
 */
public class BTServerWorker extends BTWorker implements BTConnectionHandler {

    private static final String TAG = "BTServerWorker";

    private Set<String> expressionIds = new TreeSet<String>();

    public BTServerWorker(BTManager btManager, BTSwanDevice swanDevice, CrossdeviceConnectionI btConnection) {
        this.btManager = btManager;
        this.swanDevice = swanDevice;
        this.connection = btConnection;
        logRecord = new BTLogRecord(btManager.getStartTime(), false);
    }

    @Override
    protected synchronized void send(String expressionId, String expressionAction, String expressionData) throws Exception {
        // we don't check action type, because it can only be ACTION_NEW_RESULT_REMOTE
        Result result = expressionData != null ? (Result) Converter.stringToObject(expressionData) : null;

        if(isValidResult(result)) {
            logRecord.swanDuration = System.currentTimeMillis() - logRecord.startSwanTime;
            HashMap<String, String> extra = new HashMap<String, String>();
            extra.put("swanDuration", logRecord.swanDuration + "");

            btManager.sendExprForEvaluation(expressionId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, swanDevice.getName(), null);
            super.send(expressionId, expressionAction, expressionData, extra);
        }
    }

    @Override
    public void onReceive(HashMap<String, String> dataMap) {
        String exprAction = dataMap.get("action");
        String exprSource = dataMap.get("source");
        String exprId = dataMap.get("id");
        String exprData = dataMap.get("data");
        String ipAddress = dataMap.get("ip");

        if(ipAddress != null) {
            swanDevice.setIpAddress(ipAddress);
        }

        // add the expression id to the list of registered expressions
        expressionIds.add(exprId);
        logRecord.sensors = expressionIds.size();

        if (exprAction.equals(EvaluationEngineService.ACTION_REGISTER_REMOTE)
                || exprAction.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
            btManager.log(TAG, this + " received " + exprAction + ": " + exprData, Log.WARN);
            logRecord.startSwanTime = System.currentTimeMillis();
            btManager.sendExprForEvaluation(exprId, exprAction, exprSource, exprData);

            if (exprAction.equals(EvaluationEngineService.ACTION_UNREGISTER_REMOTE)) {
                expressionIds.remove(exprId);

                if(expressionIds.isEmpty()) {
                    if(BTManager.SHARED_CONNECTIONS) {
                        done();
                    } else {
                        connection.disconnect();
                    }
                }
            }
        } else {
            btManager.log(TAG, this + " didn't expect " + exprAction, Log.ERROR);
        }
    }

    @Override
    public void onDisconnected(Exception e) {
        btManager.log(TAG, this + " disconnected: " + e.getMessage(), Log.ERROR);

        // unregister expressions
        for(String exprId : expressionIds) {
            btManager.sendExprForEvaluation(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, swanDevice.getName(), null);
        }

        done();
    }

    @Override
    protected void done() {
        super.done();

        // if we have unprocessed expressions left, it means it crashed
        if(!expressionIds.isEmpty()) {
            logRecord.failed = true;
        }
        swanDevice.setServerWorker(null);
        btManager.serverWorkerDone(this);
    }

    protected String getTag() {
        return TAG;
    }

    @Override
    public String toString() {
        return "SW[" + getRemoteDeviceName() + ":" + expressionIds + "]";
    }
}

package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import interdroid.swancore.crossdevice.Converter;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.TriState;

/**
 * Created by vladimir on 4/7/16.
 */
public class BTClientWorker extends BTWorker {

    private static final String TAG = "BTClientWorker";

    private BTRemoteEvaluationTask remoteEvaluationTask;
    private boolean connected = false;

    public BTClientWorker(BTManager btManager, BTRemoteEvaluationTask remoteEvaluationTask) {
        this.btManager = btManager;
        this.remoteEvaluationTask = remoteEvaluationTask;
        this.swanDevice = remoteEvaluationTask.getSwanDevice();
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, this + " started processing");
            connect(swanDevice.getBtDevice());

            if (btSocket != null) {
                connected = true;
                initConnection();

                for(BTRemoteExpression remoteExpression : remoteEvaluationTask.getExpressions()) {
                    send(remoteExpression.getId(), EvaluationEngineService.ACTION_REGISTER_REMOTE, remoteExpression.getExpression());
                }

                manageClientConnection();
            } else {
                btManager.clientWorkerDone(this);
            }
        } catch (Exception e) {
            Log.e(TAG, this + " crashed", e);
            btManager.clientWorkerDone(this);
        }
    }

    // blocking call; use only in a separate thread
    protected void connect(BluetoothDevice device) {
        int uuidIdx = new Random().nextInt(BTManager.SERVICE_UUIDS.length);
        UUID uuid = BTManager.SERVICE_UUIDS[uuidIdx];
        Log.i(TAG, this + " connecting to " + device.getName() + " on port " + uuidIdx + "...");
        btManager.bcastLogMessage("connecting to " + device.getName() + " on port " + uuidIdx + "...");

        try {
            btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
            Log.i(TAG, this + " connected to " + device.getName());
            btManager.bcastLogMessage("connected to " + device.getName());
            return;
        } catch (Exception e) {
            Log.e(TAG, this + " can't connect to " + device.getName() + ": " + e.getMessage());
            btManager.bcastLogMessage("can't connect to " + device.getName() + ": " + e.getMessage());

            try {
                btSocket.close();
            } catch (Exception e1) {
                Log.e(TAG, "couldn't close socket", e1);
            }
        }

        btSocket = null;
    }

    protected void manageClientConnection() {
        int remoteTimeToNextRequest = 0;

        try {
            while (true) {
                HashMap<String, String> dataMap = (HashMap<String, String>) inStream.readObject();

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
        } catch (Exception e) {
            Log.e(TAG, this + " disconnected: " + e.getMessage());

            try {
                btManager.clientWorkerDone(this, remoteTimeToNextRequest);
                btSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, this + " couldn't close socket", e1);
            }
        }
    }

    private boolean isValidResult(Result result) {
        if(result == null) { return false; }
        if(result.getValues() != null && result.getValues().length > 0) { return true; }
        if(result.getTriState() != null && result.getTriState() != TriState.UNDEFINED) { return true; }
        return false;
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

    public boolean isConnected() {
        return connected;
    }
}

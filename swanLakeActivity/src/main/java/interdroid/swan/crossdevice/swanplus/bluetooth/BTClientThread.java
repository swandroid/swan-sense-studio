package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import interdroid.swan.crossdevice.Converter;
import interdroid.swan.crossdevice.swanplus.SwanUser;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swan.swansong.Result;

/**
 * Created by vladimir on 4/7/16.
 *
 * TODO handle exceptions in send()
 */
public class BTClientThread extends Thread {

    private static final String TAG = "BTClientThread";

    private BTManager btManager;
    private BTRemoteExpression remoteExpression;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private BluetoothSocket btSocket;

    public BTClientThread(BTManager btManager, BTRemoteExpression remoteExpression) {
        this.btManager = btManager;
        this.remoteExpression = remoteExpression;
    }

    @Override
    public void run() {
        try {
            btSocket = btManager.connect(remoteExpression.getUser());

            if (btSocket != null) {
                OutputStream os = btSocket.getOutputStream();
                outStream = new ObjectOutputStream(os);
                InputStream is = btSocket.getInputStream();
                inStream = new ObjectInputStream(is);

                send(remoteExpression.getId(), remoteExpression.getAction(), remoteExpression.getExpression());
                manageClientConnection();
            } else {
                btManager.finishProcessing(remoteExpression.getId());
                btManager.workerDone(this);
            }
        } catch(Exception e) {
            Log.e(TAG, "ERROR in client worker", e);

            btManager.finishProcessing(remoteExpression.getId());
            btManager.workerDone(this);
        }
    }

    protected void send(String expressionId, String expressionAction, String expressionData) throws IOException {
        Log.w(TAG, "sending " + expressionAction + " to " + remoteExpression.getUser() + ": "
                + toPrintableData(expressionData, expressionAction) + " (id: " + expressionId + ")");

        HashMap<String, String> dataMap = new HashMap<>();

        dataMap.put("source", btManager.getBtAdapter().getName());
        dataMap.put("id", expressionId);
        dataMap.put("action", expressionAction);
        dataMap.put("data", expressionData);

        synchronized (outStream) {
            outStream.writeObject(dataMap);
        }

        Log.w(TAG, "successfully sent " + expressionAction + " to " + remoteExpression.getUser() + ": "
                + toPrintableData(expressionData, expressionAction) + " (id: " + expressionId + ")");
    }

    private String toPrintableData(String data, String action) {
        if(data == null) {
            return "(no data)";
        }

        if(action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
            try {
                return Converter.stringToObject(data).toString();
            } catch (Exception e) {
                Log.e(TAG, "can't get printable data", e);
            }
        }

        return data;
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
//                            btManager.reRegisterExpression(remoteExpression);
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
            Log.e(TAG, "disconnected from " + remoteExpression.getUser() + ": " + e.getMessage());

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
}

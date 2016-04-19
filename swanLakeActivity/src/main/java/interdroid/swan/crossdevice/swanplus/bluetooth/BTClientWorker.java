package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import interdroid.swan.crossdevice.Converter;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swan.swansong.Result;

/**
 * Created by vladimir on 4/7/16.
 *
 */
public class BTClientWorker extends BTWorker {

    private static final String TAG = "BTClientWorker";

    private BTRemoteExpression remoteExpression;
    private boolean connected = false;

    public BTClientWorker(BTManager btManager, BTRemoteExpression remoteExpression) {
        this.btManager = btManager;
        this.remoteExpression = remoteExpression;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, this + " started processing");
            btSocket = connect(remoteExpression.getRemoteDevice());

            // if the worker was interrupted, we no longer need
            // to clean it from manager by calling workerDone()
            if(isInterrupted()) {
                Log.e(TAG, this + " interrupted, closing connection");

                if(btSocket != null) {
                    try {
                        btSocket.close();
                    } catch (IOException e) {
                        Log.e(TAG, this + " couldn't close socket", e);
                    }
                }

                return;
            }

            if (btSocket != null) {
                connected = true;
                initConnection();
                send(remoteExpression.getId(), remoteExpression.getAction(), remoteExpression.getExpression());
                manageClientConnection();
            } else {
                btManager.workerDone(this);
            }
        } catch(Exception e) {
            Log.e(TAG, this + " crashed", e);
            btManager.workerDone(this);
        }
    }

    // blocking call; use only in a separate thread
    protected BluetoothSocket connect(BluetoothDevice device) {
        Log.i(TAG, this + " connecting to " + device.getName() + "...");
        BluetoothSocket btSocket = null;
//        btAdapter.cancelDiscovery();

        try {
            btSocket = device.createInsecureRfcommSocketToServiceRecord(btManager.SERVICE_UUID);
            btSocket.connect();
            Log.i(TAG, this + " connected to " + device.getName());
        } catch (IOException e) {
            Log.e(TAG, this + " can't connect to " + device.getName() + ": " + e.getMessage());

            try {
                btSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        }

        return btSocket;
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
                    Log.w(TAG, this + " received " + exprAction + ": " + result);

                    if(exprId.equals(remoteExpression.getId())) {
                        if (result != null && result.getValues().length > 0) {
                            btManager.sendExprForEvaluation(remoteExpression.getBaseId(), exprAction, exprSource, exprData);
                            send(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
                        }
                    } else {
                        Log.e(TAG, this + " received result for wrong expression");
                        send(exprId, EvaluationEngineService.ACTION_UNREGISTER_REMOTE, null);
                    }
                } else {
                    Log.e(TAG, this + " didn't expect " + exprAction);
                }
            }
        } catch(Exception e) {
            Log.e(TAG, this + " disconnected: " + e.getMessage());

            try {
                btManager.workerDone(this);
                btSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, this + " couldn't close socket", e1);
            }
        }
    }

    public BTRemoteExpression getRemoteExpression() {
        return remoteExpression;
    }

    protected String getTag() {
        return TAG;
    }

    @Override
    public String toString() {
        return "CW[" + getRemoteDeviceName() + ":" + remoteExpression.getId() + "]";
    }

    public boolean isConnected() {
        return connected;
    }
}

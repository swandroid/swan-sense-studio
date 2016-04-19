package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.bluetooth.BluetoothDevice;
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
import interdroid.swan.engine.EvaluationEngineService;

/**
 * Created by vladimir on 4/8/16.
 */
public class BTWorker extends Thread {

    private static final String TAG = "BTWorker";

    protected BTManager btManager;
    protected BluetoothSocket btSocket;
    protected ObjectOutputStream outStream;
    protected ObjectInputStream inStream;

    protected void initConnection() throws IOException {
        try {
            OutputStream os = btSocket.getOutputStream();
            outStream = new ObjectOutputStream(os);
            InputStream is = btSocket.getInputStream();
            inStream = new ObjectInputStream(is);
        } catch (IOException e) {
            // we ususally get this exception when the worker on the other side was interrupted
            // due to an expired timeout
            Log.e(getTag(), this + " connection was closed: " + e.getMessage());
        }
    }

    protected void send(String expressionId, String expressionAction, String expressionData) throws IOException {
        Log.w(getTag(), this + " sending " + expressionAction + ": "
                + toPrintableData(expressionData, expressionAction));

        HashMap<String, String> dataMap = new HashMap<>();

        dataMap.put("source", btManager.getBtAdapter().getName());
        dataMap.put("id", expressionId);
        dataMap.put("action", expressionAction);
        dataMap.put("data", expressionData);

        synchronized (outStream) {
            outStream.writeObject(dataMap);
        }

        Log.w(getTag(), this + " successfully sent " + expressionAction + ": "
                + toPrintableData(expressionData, expressionAction));
    }

    private String toPrintableData(String data, String action) {
        if(data == null) {
            return "(no data)";
        }

        if(action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
            try {
                return Converter.stringToObject(data).toString();
            } catch (Exception e) {
                Log.e(getTag(), this + " can't get printable data", e);
            }
        }

        return data;
    }

    public String getRemoteDeviceName() {
        if(btSocket != null) {
            return btSocket.getRemoteDevice().getName();
        }
        return null;
    }

    protected String getTag() {
        return TAG;
    }
}

package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import interdroid.swancore.crossdevice.Converter;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.TriState;

/**
 * Created by vladimir on 4/8/16.
 */
public class BTWorker {

    private static final String TAG = "BTWorker";

    protected BTManager btManager;
    protected BTSwanDevice swanDevice;
    protected BTConnection btConnection;

    // we synchronize this to make sure that BTServerWorker.disconnect() is not called at the same time
    protected synchronized void send(String expressionId, String expressionAction, String expressionData) throws Exception {
        Log.w(getTag(), this + " sending " + expressionAction + ": " + toPrintableData(expressionData, expressionAction));

        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("source", btManager.getBtAdapter().getName());
        dataMap.put("id", expressionId);
        dataMap.put("action", expressionAction);
        dataMap.put("data", expressionData);
        //TODO check here if device has any expression registered remotely
        dataMap.put("timeToNextReq", getTimeToNextRequest() + "");

        if(BTManager.THREADED_WORKERS) {
            btConnection.send(dataMap);
        } else {
            swanDevice.getBtConnection().send(dataMap);
        }

        Log.w(getTag(), this + " successfully sent " + expressionAction + ": "
                + toPrintableData(expressionData, expressionAction));
    }

    protected int getTimeToNextRequest() {
        if(swanDevice.getPendingItem() != null) {
            return swanDevice.getPendingItem().getTimeout();
        } else {
            return BTManager.TIME_BETWEEN_REQUESTS;
        }
    }

    protected boolean isValidResult(Result result) {
        if(result == null) { return false; }
        if(result.getValues() != null && result.getValues().length > 0) { return true; }
        if(result.getTriState() != null && result.getTriState() != TriState.UNDEFINED) { return true; }
        return false;
    }

    private String toPrintableData(String data, String action) {
        if (data == null) {
            return "(no data)";
        }

        if (action.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
            try {
                return Converter.stringToObject(data).toString();
            } catch (Exception e) {
                Log.e(getTag(), this + " can't get printable data", e);
            }
        }

        return data;
    }

    public String getRemoteDeviceName() {
        return swanDevice.getName();
    }

    protected String getTag() {
        return TAG;
    }

    public void abort() {
        //TODO
    }

    public BTSwanDevice getSwanDevice() {
        return swanDevice;
    }
}

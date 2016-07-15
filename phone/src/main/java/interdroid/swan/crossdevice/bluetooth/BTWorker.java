package interdroid.swan.crossdevice.bluetooth;

import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interdroid.swan.crossdevice.CrossdeviceConnectionI;
import interdroid.swancore.crossdevice.Converter;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.TriState;

/**
 * Created by vladimir on 4/8/16.
 */
public class BTWorker extends Thread {

    private static final String TAG = "BTWorker";

    protected BTManager btManager;
    protected BTSwanDevice swanDevice;
    protected CrossdeviceConnectionI connection;

    protected BTLogRecord logRecord;

    protected void send(String expressionId, String expressionAction, String expressionData) throws Exception {
        send(expressionId, expressionAction, expressionData, null);
    }

    protected void send(String expressionId, String expressionAction, String expressionData, Map<String, String> extra) throws Exception {
        btManager.log(getTag(), this + " sending " + expressionAction + ": " + toPrintableData(expressionData, expressionAction), Log.WARN);

        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("source", btManager.getBtAdapter().getName());
        dataMap.put("id", expressionId);
        dataMap.put("action", expressionAction);
        dataMap.put("data", expressionData);
        //TODO check here if device has any expression registered remotely
        dataMap.put("timeToNextReq", getTimeToNextRequest() + "");
        dataMap.put("ip", getIPAddress(true));

        if(extra != null) {
            for(Map.Entry<String, String> entry : extra.entrySet()) {
                dataMap.put(entry.getKey(), entry.getValue());
            }
        }

        if(BTManager.SHARED_CONNECTIONS) {
            swanDevice.getConnection().send(dataMap);
        } else {
            connection.send(dataMap);
        }

        btManager.log(getTag(), this + " successfully sent " + expressionAction + ": "
                + toPrintableData(expressionData, expressionAction), Log.WARN);
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
                btManager.log(getTag(), this + " can't get printable data", Log.ERROR, e);
            }
        }

        return data;
    }

    protected void disconnectFromRemote() {
        if(BTManager.SHARED_CONNECTIONS) {
            if(swanDevice.getConnection() != null) {
                swanDevice.getConnection().disconnect();
            }
        } else {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    protected boolean isConnectedToRemote() {
        if(BTManager.SHARED_CONNECTIONS) {
            return swanDevice.isConnectedToRemote();
        } else {
            return connection != null && connection.isConnected();
        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    public String getRemoteDeviceName() {
        return swanDevice.getName();
    }

    protected String getTag() {
        return TAG;
    }

    protected void done() {
        logRecord.totalDuration = System.currentTimeMillis() - logRecord.startTime;
    }

    public BTSwanDevice getSwanDevice() {
        return swanDevice;
    }

    public CrossdeviceConnectionI getConnection() {
        return connection;
    }

    public BTLogRecord getLogRecord() {
        return logRecord;
    }
}

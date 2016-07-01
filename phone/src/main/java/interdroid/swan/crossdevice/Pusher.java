package interdroid.swan.crossdevice;

import android.util.Log;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

import interdroid.swan.crossdevice.wifidirect.WDSwanDevice;
import interdroid.swan.crossdevice.wifidirect.WDManager;

public class Pusher {

    public static final String TAG = "SWAN Pusher";
    final static int PORT = 2222;

    public static void push(String toRegistrationId, String expressionId,
                            String action, String data) {
        push(null, toRegistrationId, expressionId, action, data);
    }

    public static void push(final String fromRegistrationId,
                            final String toRegistrationId, final String expressionId,
                            final String action, final String data) {
        new Thread() {
            public void run() {
//				sendOverGCM(fromRegistrationId, toRegistrationId, expressionId, action, data);
//				sendOverWD(fromRegistrationId, toRegistrationId, expressionId, action, data);
            }
        }.start();
    }

    private static void sendOverWD(final String fromRegistrationId,
                                   final String toRegistrationId, final String expressionId,
                                   final String action, final String data) {
        new Thread() {
            public void run() {
                try {
                    WDManager wdManager = (WDManager) SwanLakePlusActivity.getProximityManager();

                    WDSwanDevice user = wdManager.getPeerByRegId(toRegistrationId);
                    if (user != null) {
                        if (wdManager.connect(user, this)) {
                            synchronized (this) {
                                wait();
                            }
                        }
                        if (user.getIp() != null) {
                            HashMap<String, String> dataMap = new HashMap<String, String>();

                            if (fromRegistrationId != null) {
                                // from is not allowed and results in InvalidDataKey, see:
                                // http://developer.android.com/google/gcm/gcm.html
                                dataMap.put("source", fromRegistrationId);
                            }
                            dataMap.put("action", action);
                            dataMap.put("data", data);
                            dataMap.put("id", expressionId);

                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            ObjectOutputStream os = new ObjectOutputStream(outputStream);
                            os.writeObject(dataMap);
                            byte[] sendData = outputStream.toByteArray();

                            DatagramSocket clientSocket = new DatagramSocket();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, user.getIp(), PORT);
                            clientSocket.send(sendPacket);
                            clientSocket.close();

                            Log.d(TAG, "successfully sent push message for id: "
                                    + expressionId + ", type: " + action + ", data: " + data);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void sendOverGCM(final String fromRegistrationId,
                                    final String toRegistrationId, final String expressionId,
                                    final String action, final String data) {
        Sender sender = new Sender(SwanGCMConstants.API_KEY);
        Message.Builder builder = new Message.Builder();
        builder.timeToLive(60 * 60).collapseKey("MAGIC_STRING")
                .delayWhileIdle(true);
        if (fromRegistrationId != null) {
            // from is not allowed and results in InvalidDataKey, see:
            // http://developer.android.com/google/gcm/gcm.html
            builder.addData("source", fromRegistrationId);
        }
        builder.addData("action", action);
        builder.addData("data", data);
        builder.addData("id", expressionId);
        Message message = builder.build();
        try {
            Result result = sender.send(message, toRegistrationId, 5);
            if (result.getMessageId() != null) {
                String canonicalRegId = result
                        .getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    Log.d(TAG,
                            "same device has more than on registration ID: update database");
                } else {
                    Log.d(TAG,
                            "successfully sent push message for id: "
                                    + expressionId + ", type: "
                                    + action + ", data: " + data);
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    Log.d(TAG,
                            "application has been removed from device - unregister database");
                } else {
                    Log.d(TAG, "no message id, error: " + error);
                }
            }
        } catch (IOException e) {
            Log.d(TAG,
                    "failed to deliver push message: " + e.toString());
        }
    }
}

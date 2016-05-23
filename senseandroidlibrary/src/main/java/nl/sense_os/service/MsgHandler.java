/**************************************************************************************************
 * Copyright (C) 2010 Sense Observation Systems, Rotterdam, the Netherlands. All rights reserved. *
 *************************************************************************************************/
package nl.sense_os.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import org.json.JSONObject;

import nl.sense_os.service.commonsense.senddata.BufferTransmitHandler;
import nl.sense_os.service.commonsense.senddata.DataTransmitHandler;
import nl.sense_os.service.commonsense.senddata.FileTransmitHandler;
import nl.sense_os.service.constants.SenseDataTypes;
import nl.sense_os.service.constants.SensePrefs;
import nl.sense_os.service.constants.SensePrefs.Auth;
import nl.sense_os.service.constants.SensePrefs.Main;
import nl.sense_os.service.storage.LocalStorage;

/**
 * This class is responsible for handling the sensor data that has been collected by the different
 * sensors. It has two main tasks:
 * <ul>
 * <li>Collect incoming sensor data and add it to the buffer.</li>
 * <li>Periodically transmit all sensor data in the buffer to CommonSense.</li>
 * </ul>
 * Sensors that have sampled a new data point should send it to the MsgHandler by sending an Intent
 * with {@link R.string#action_sense_new_data} that contains the details of the datapoint.<br/>
 * <br/>
 * For example:
 * <p/>
 * <pre>
 * Intent sensorData = new Intent(getString(R.string.action_sense_new_data));
 * sensorData.putExtra(DataPoint.SENSOR_NAME, &quot;sensor name&quot;);
 * sensorData.putExtra(DataPoint.VALUE, &quot;foo&quot;);
 * sensorData.putExtra(DataPoint.DATA_TYPE, SenseDataTypes.FLOAT);
 * sensorData.putExtra(DataPoint.TIMESTAMP, System.currentTimeMillis());
 * startService(sensorData);
 * </pre>
 *
 * @author Steven Mulder <steven@sense-os.nl>
 */
public class MsgHandler extends Service {

    private static final String TAG = "Sense MsgHandler";
    /**
     * Key for Intent extra that defines the buffer type to send data from. The value should be
     * either {@link #BUFFER_TYPE_FLASH} or {@link #BUFFER_TYPE_MEMORY}.
     */
    public static final String EXTRA_BUFFER_TYPE = "buffer-type";
    public static final int BUFFER_TYPE_FLASH = 1;
    public static final int BUFFER_TYPE_MEMORY = 0;

    private static FileTransmitHandler fileHandler;
    private static DataTransmitHandler dataTransmitHandler;
    private static BufferTransmitHandler bufferHandler;

    /**
     * Messenger for communicating with the service.
     */
    Messenger mSenseService = null;

    /**
     * Sends data points for one sensor to CommonSense.
     *
     * @param name        Sensor name, used to determine the sensor ID at CommonSense
     * @param description Sensor description (previously 'device_type'), used to determine the sensor ID at
     *                    CommonSense
     * @param dataType    Sensor data type, used to determine the sensor ID at CommonSense
     * @param deviceUuid  (Optional) UUID of the sensor's device. Set null to use this phone as the default
     *                    device.
     * @param sensorData  JSON Object with the sensor data.
     */
    public static void sendSensorData(Context context, String name, String description,
                                      String dataType, String deviceUuid, JSONObject sensorData) {

        try {
            // get cookie for transmission
            SharedPreferences authPrefs = context.getSharedPreferences(SensePrefs.AUTH_PREFS,
                    MODE_PRIVATE);
            String cookie = authPrefs.getString(Auth.LOGIN_COOKIE, null);

            if (cookie.length() > 0) {

                // prepare message to let handler run task
                Bundle args = new Bundle();
                args.putString("name", name);
                args.putString("description", description);
                args.putString("dataType", dataType);
                args.putString("deviceUuid", deviceUuid);
                args.putString("cookie", cookie);

                Message msg = Message.obtain();
                msg.setData(args);
                msg.obj = sensorData;

                // check for sending a file
                if (dataType.equals(SenseDataTypes.FILE)) {
                    fileHandler.sendMessage(msg);
                } else {
                    dataTransmitHandler.sendMessage(msg);
                }
            } else {
                Log.w(TAG, "Cannot send data point! no cookie");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in sending sensor data:", e);
        }
    }

    private void handleSendIntent(Intent intent) {
        Log.d(TAG, "handleSendIntent");
        if (isOnline()) {
            Log.d(TAG, "is online");
            // verify the sensor IDs
            //startService(new Intent(this, DefaultSensorRegistrationService.class));

            // get the cookie
            SharedPreferences authPrefs = getSharedPreferences(SensePrefs.AUTH_PREFS, MODE_PRIVATE);
            String cookie = authPrefs.getString(Auth.LOGIN_COOKIE, null);

            // send the message to the handler
            Message msg = Message.obtain();
            Bundle args = new Bundle();
            args.putString("cookie", cookie);
            msg.setData(args);
            bufferHandler.sendMessage(msg);
        }
    }


    /**
     * @return <code>true</code> if the phone has network connectivity.
     */
    private boolean isOnline() {
        SharedPreferences mainPrefs;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mainPrefs = getSharedPreferences(SensePrefs.MAIN_PREFS, MODE_MULTI_PROCESS);
        } else {
            mainPrefs = getSharedPreferences(SensePrefs.MAIN_PREFS, MODE_PRIVATE);
        }
        boolean isCommonSenseEnabled = mainPrefs.getBoolean(Main.Advanced.USE_COMMONSENSE, true);

        SharedPreferences authPrefs;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            authPrefs = getSharedPreferences(SensePrefs.AUTH_PREFS, MODE_MULTI_PROCESS);
        } else {
            authPrefs = getSharedPreferences(SensePrefs.AUTH_PREFS, MODE_PRIVATE);
        }
        boolean isLoggedIn = authPrefs.getString(Auth.LOGIN_COOKIE, null) != null;

        final ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        return (null != info) && info.isConnected() && isCommonSenseEnabled && isLoggedIn;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // you cannot bind to this service
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, " on create ");
        super.onCreate();
        {
            HandlerThread handlerThread = new HandlerThread("TransmitRecentDataThread");
            handlerThread.start();
            bufferHandler = new BufferTransmitHandler(this, LocalStorage.getInstance(this), handlerThread.getLooper());
        }

        {
            HandlerThread handlerThread = new HandlerThread("TransmitFileThread");
            handlerThread.start();
            fileHandler = new FileTransmitHandler(this, LocalStorage.getInstance(this), handlerThread.getLooper());
        }

        {
            HandlerThread handlerThread = new HandlerThread("TransmitDataPointThread");
            handlerThread.start();
            dataTransmitHandler = new DataTransmitHandler(this, LocalStorage.getInstance(this), handlerThread.getLooper());
        }
    }

    @Override
    public void onDestroy() {
        // stop buffered data transmission threads
        bufferHandler.getLooper().quit();
        fileHandler.getLooper().quit();
        dataTransmitHandler.getLooper().quit();

        super.onDestroy();
    }

    /**
     * Handles an incoming Intent that started the service by checking if it wants to store a new
     * message or if it wants to send data to CommonSense.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //if (getString(R.string.action_sense_send_data).equals(intent.getAction())) {
        handleSendIntent(intent);
//		} else {
//			Log.e(TAG, "Unexpected intent action: " + intent.getAction());
//		}

        // this service is not sticky, it will get an intent to restart it if necessary
        return START_NOT_STICKY;
    }
}

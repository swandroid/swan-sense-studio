/**************************************************************************************************
 * Copyright (C) 2010 Sense Observation Systems, Rotterdam, the Netherlands. All rights reserved. *
 *************************************************************************************************/
package nl.sense_os.service;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import nl.sense_os.service.constants.SensePrefs;
import nl.sense_os.service.constants.SensePrefs.Main;
import nl.sense_os.service.scheduler.Scheduler;

/**
 * This class is responsible for initiating the transmission of buffered data. It works by
 * registering the transmission task to the scheduler. The Sense service calls
 * {@link #scheduleTransmissions(Context)} when it starts sensing. <br/>
 * <br/>
 * When the transmission task is executed, an Intent is sent to the {@link MsgHandler} to empty its
 * buffer.<br/>
 * <br/>
 * The transmission frequency is based on the {@link Main#SYNC_RATE} preference. When the sync rate
 * is set to the real-time setting, we look at the and {@link Main#SAMPLE_RATE} to determine
 * periodic "just in case" transmissions. In case of transmission over 3G we transmit every one hour
 * for energy conservation.
 *
 * @author Steven Mulder <steven@sense-os.nl>
 */
public class DataTransmitter implements Runnable {

    private static final long ADAPTIVE_TX_INTERVAL = AlarmManager.INTERVAL_HALF_HOUR;
    private static final String TAG = "DataTransmitter";
    private static DataTransmitter sInstance;

    /**
     * Factory method to get the singleton instance.
     *
     * @param context
     * @return instance
     */
    public static DataTransmitter getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataTransmitter(context);
        }
        return sInstance;
    }

    private Context mContext;
    private long mLastTxBytes = 0;
    private long mLastTxTime = 0;
    private long mTxInterval;
    private long mTxBytes;

    /**
     * Constructor.
     *
     * @param context
     * @see #getInstance(Context)
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    protected DataTransmitter(Context context) {
        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mTxBytes = TrafficStats.getMobileTxBytes();
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public void run() {
        // check if the service is (supposed to be) alive before scheduling next alarm
        if (true == ServiceStateHelper.getInstance(mContext).isLoggedIn()) {
            // check if transmission should be started
            ConnectivityManager connManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            //Check battery state
            SharedPreferences mainPrefs = mContext.getSharedPreferences(SensePrefs.MAIN_PREFS, Context.MODE_PRIVATE);
            //boolean isChargingOnly = mainPrefs.getBoolean(Advanced.IS_CHARGING_ONLY, true);
            String batteryState = mainPrefs.getString(SensePrefs.Main.Advanced.BATTERY, "When battery level above threshold");
            if (0 == batteryState.compareTo("Only when charging")) {
                if (!isCharging()) {        //if upload should be done only when the device is charging, return
                    Log.d(TAG, "device is not charging, transfer delayed");
                    return;
                }
            } else if (0 == batteryState.compareTo("When battery level above threshold")) {
                String batteryTshold = mainPrefs.getString(SensePrefs.Main.Advanced.BATTERY_THRESHOLD, "60");
                if (Integer.parseInt(batteryTshold) >= getBatteryLevel()) {
                    Log.d(TAG, "device is low on battery, transfer delayed");
                    return;
                }
            } else if (0 == batteryState.compareTo("Always")) {
                Log.d(TAG, "Battery state: Always, continue the transfer");
            }

            //Check mobile network
            NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            String mobileNetwork = mainPrefs.getString(SensePrefs.Main.Advanced.MOBILE_NETWORK_UPLOAD, "WiFi");
            if ((0 == mobileNetwork.compareTo("Any") && (wifi.isConnected() || mobile.isConnected())) ||
                    (0 == mobileNetwork.compareTo("WiFi") && wifi.isConnected()) ||
                    ((0 == mobileNetwork.compareTo("3G") || 0 == mobileNetwork.compareTo("4G") || 0 == mobileNetwork.compareTo("2G")) && mobile.isConnected())) {
                Log.d(TAG, "Starting transmission");

                // start the transmission if we have WiFi connection
                if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) || wifi.isConnected()) {
                    if ((SystemClock.elapsedRealtime() - mLastTxTime >= mTxInterval)) {
                        transmissionService();
                    }
                } else {
                    // if there is no WiFi connection, postpone the transmission
                    mLastTxBytes = TrafficStats.getMobileTxBytes() - mTxBytes;
                    mTxBytes = TrafficStats.getMobileTxBytes();
                    if ((SystemClock.elapsedRealtime() - mLastTxTime >= ADAPTIVE_TX_INTERVAL)) {
                        transmissionService();
                    }
                    // if any transmission took place recently, use the tail to transmit the sensor data
                    else if ((mLastTxBytes >= 500)
                            && (SystemClock.elapsedRealtime() - mLastTxTime >= ADAPTIVE_TX_INTERVAL
                            - (long) (ADAPTIVE_TX_INTERVAL * 0.2))) {
                        transmissionService();
                    } else {
                        // do nothing
                    }
                }
            } else {
                Log.d(TAG, "Mobile network invalid, transfer delayed");
                return;
            }
        } else {
            Log.d(TAG, "skip transmission: Sense service is not logged in");
            // skip transmission: Sense service is not logged in
        }
    }

    public boolean isCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        return isCharging;
    }

    public float getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//    	int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level /*/ (float)scale*/;
        Log.d(TAG, "battery level: " + batteryPct);
        return batteryPct;
    }

    public int prefetchCacheSize() {        ///????????change sync rate better
        int DEFAULT_BATCH_SIZE = 10;
        int MAX_BATCH_SIZE = 100;

        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        TelephonyManager tm =
                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        int PrefetchCacheSize = DEFAULT_BATCH_SIZE;

        switch (activeNetwork.getType()) {
            case (ConnectivityManager.TYPE_WIFI):
                PrefetchCacheSize = MAX_BATCH_SIZE;
                break;
            case (ConnectivityManager.TYPE_MOBILE): {
                switch (tm.getNetworkType()) {
                    case (TelephonyManager.NETWORK_TYPE_LTE):        //4g
                        return PrefetchCacheSize * 2;

                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:    //3g
                        return PrefetchCacheSize;

                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:    //2g
                        return PrefetchCacheSize / 2;

                    default:
                        break;
                }
                break;
            }
            default:
                break;
        }

        return PrefetchCacheSize;
    }

    /**
     * Starts the periodic transmission of sensor data.
     *
     * @param mContext Context to access Scheduler
     */
    public void startTransmissions(long transmissionInterval, long taskTransmitterInterval) {

        // schedule transmissions
        mTxInterval = transmissionInterval;
        Scheduler.getInstance(mContext).register(this, taskTransmitterInterval,
                (long) (taskTransmitterInterval * 0.2));
    }

    /**
     * Stops the periodic transmission of sensor data.
     */
    public void stopTransmissions() {
        // stop transmissions
        Scheduler.getInstance(mContext).unregister(this);
    }

    /**
     * Initiates the data transmission.
     */
    public void transmissionService() {
        Log.v(TAG, "Start transmission");
        Intent task = new Intent(mContext, MsgHandler.class);
        mLastTxTime = SystemClock.elapsedRealtime();
        ComponentName service = mContext.startService(task);
        if (null == service) {
            Log.w(TAG, "Failed to start data sync service");
        }
    }
}

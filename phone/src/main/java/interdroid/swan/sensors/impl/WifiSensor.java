package interdroid.swan.sensors.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import interdroid.swan.R;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * A sensor for available Wifi networks.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public class WifiSensor extends AbstractSwanSensor {
    // wifi:ssid{ANY,1000} == test && wifi:level?ssid=test > 10
    // wifi:level?bssid=A:B:C:D > 10
    // wifi:level{MAX,1000} > 10

    private static final String TAG = "WiFi Sensor";

    /**
     * Configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.wifi_preferences;
        }

    }

    /**
     * The network identifier field.
     */
    public static final String SSID_FIELD = "ssid";
    /**
     * The base station identifier field.
     */
    public static final String BSSID_FIELD = "bssid";
    /**
     * The level seen.
     */
    public static final String LEVEL_FIELD = "level";

    /**
     * The discovery interval.
     */
    public static final String DISCOVERY_INTERVAL = "discovery_interval";

    /**
     * The interval at which to run discovery.
     */
    public static final long DEFAULT_DISCOVERY_INTERVAL = 60 * 1000;

    /**
     * true if we should stop polling and shutdown.
     */
    private boolean stopPolling = false;

    /**
     * The wifi manager we access wifi info with.
     */
    private WifiManager wifiManager;

    /**
     * The receiver we use to get wifi notifications.
     */
    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            long now = System.currentTimeMillis();

            List<ScanResult> results = wifiManager.getScanResults();
            for (ScanResult scanResult : results) {
                Log.d(TAG, "Got WiFi: " + scanResult.level + ", "
                        + scanResult.SSID + ", " + scanResult.BSSID);
                if (expressionIdsPerValuePath.containsKey(SSID_FIELD)) {
                    putValueTrimSize(SSID_FIELD, null, now, scanResult.SSID);
                }
                if (expressionIdsPerValuePath.containsKey(BSSID_FIELD)) {
                    putValueTrimSize(BSSID_FIELD, null, now, scanResult.BSSID);
                }
                if (expressionIdsPerValuePath.containsKey(LEVEL_FIELD)) {
                    for (String id : registeredConfigurations.keySet()) {
                        boolean matching = true;
                        if (registeredConfigurations.get(id).containsKey(
                                SSID_FIELD)) {
                            matching = matching
                                    && (registeredConfigurations.get(id)
                                    .getString(SSID_FIELD)
                                    .equals(scanResult.SSID));
                        }
                        if (registeredConfigurations.get(id).containsKey(
                                BSSID_FIELD)) {
                            matching = matching
                                    && (registeredConfigurations.get(id)
                                    .getString(BSSID_FIELD)
                                    .equals(scanResult.BSSID));
                        }
                        if (matching) {
                            Log.w(TAG, "matching result found!");
                            putValueTrimSize(LEVEL_FIELD, null, now, scanResult.level);
                        } else {
                            Log.d(TAG, "No matching result found!");
                        }
                    }
                }
            }
        }

    };

    /**
     * The thread we use to poll wifi.
     */
    private Thread wifiPoller = new Thread() {
        public void run() {
            while (!stopPolling) {
                long start = System.currentTimeMillis();
                if (registeredConfigurations.size() > 0) {
                    Log.d(TAG, "Starting WiFi scan.");
                    wifiManager.startScan();
                }
                try {
                    long waitTime = Math.max(
                            1,
                            start
                                    + currentConfiguration
                                    .getLong(DISCOVERY_INTERVAL)
                                    - System.currentTimeMillis());
                    Log.d(TAG, "Waiting for " + waitTime + " ms.");

                    synchronized (wifiPoller) {
                        wifiPoller.wait(waitTime);
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while waiting.", e);
                }
            }
        }
    };

    @Override
    public final String[] getValuePaths() {
        return new String[]{SSID_FIELD, BSSID_FIELD, LEVEL_FIELD};
    }

    @Override
    public final void initDefaultConfiguration(final Bundle defaults) {
        defaults.putLong(DISCOVERY_INTERVAL, DEFAULT_DISCOVERY_INTERVAL);
    }

    @Override
    public final void onConnected() {
        SENSOR_NAME = "WiFi Sensor";
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public final void register(final String id, final String valuePath,
                               final Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        if (registeredConfigurations.size() == 1) {
            registerReceiver(wifiReceiver, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            if (!wifiPoller.isAlive()) {
                wifiPoller.start();
            } else {
                synchronized (wifiPoller) {
                    wifiPoller.notifyAll();
                }
            }
        }
        updatePollRate();
    }

    /**
     * Updates the polling rate when we get a new registration.
     */
    private void updatePollRate() {
        boolean keepDefault = true;
        long updatedPollRate = Long.MAX_VALUE;
        for (Bundle configuration : registeredConfigurations.values()) {
            if (configuration.containsKey(DISCOVERY_INTERVAL)) {
                keepDefault = false;
                updatedPollRate = Math.min(updatedPollRate,
                        configuration.getLong(DISCOVERY_INTERVAL));
            }
        }
        if (keepDefault) {
            currentConfiguration.putLong(DISCOVERY_INTERVAL,
                    DEFAULT_DISCOVERY_INTERVAL);
        } else {
            currentConfiguration.putLong(DISCOVERY_INTERVAL, updatedPollRate);
        }
    }

    @Override
    public final void unregister(final String id) {
        if (registeredConfigurations.size() == 0) {
            unregisterReceiver(wifiReceiver);
        }
        updatePollRate();
    }

    @Override
    public void onDestroySensor() {
        try {
            unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error unregistering", e);
        }

        stopPolling = true;
        wifiPoller.interrupt();
        super.onDestroySensor();
    }

}

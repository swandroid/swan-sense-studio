package interdroid.swan.crossdevice.ble;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;

import interdroid.swan.crossdevice.bluetooth.BTManager;

/**
 * Created by vladimir on 9/23/16.
 */

public class BLEManager extends BTManager {

    private static final String TAG = "BLEManager";
    private static final int SCAN_PERIOD = 60000;

    /* we schedule peer discovery to take place at regular intervals */
    private Runnable nearbyPeersChecker = new Runnable() {
        public void run() {
            log(TAG, "discovery started", Log.INFO, true);
            bcastLogMessage("discovery started");

            discoverPeers();
            setDiscovering(true);
        }

        @Override
        public String toString() {
            return "BLEDiscoveryEvent";
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "found nearby device " + result.getDevice().getName());
        }
    };

    public BLEManager(Context context) {
        super(context);
    }

    @Override
    public void init() {
        if (btAdapter == null) {
            log(TAG, "Bluetooth not supported", Log.ERROR, true);
            return;
        }
        if(!btAdapter.isMultipleAdvertisementSupported()) {
            log(TAG, "BLE advertising not supported", Log.ERROR, true);
            return;
        }

        advertise();
        evalThread.start();
        addToQueue(nearbyPeersChecker);

        synchronized (evalThread) {
            evalThread.notify();
        }
    }

    private void advertise() {
        BluetoothLeAdvertiser bleAdvertiser = btAdapter.getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
                .build();
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();
        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e("BLE", "Advertising onStartFailure: " + errorCode);
                super.onStartFailure(errorCode);
            }
        };

        bleAdvertiser.startAdvertising(settings, data, advertisingCallback);
    }

    @Override
    public void discoverPeers() {
        // Stops scanning after a pre-defined scan period.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btAdapter.getBluetoothLeScanner().stopScan(scanCallback);

                log(TAG, "discovery finished", Log.INFO, true);
                bcastLogMessage("discovery finished");

                setDiscovering(false);
                synchronized (evalThread) {
                    evalThread.notify();
                }
            }
        }, SCAN_PERIOD);

        btAdapter.getBluetoothLeScanner().startScan(scanCallback);
    }
}

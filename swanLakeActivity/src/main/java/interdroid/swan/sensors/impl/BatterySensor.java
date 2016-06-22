package interdroid.swan.sensors.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interdroid.swan.R;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * A sensor for battery temperature, level and voltage.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public class BatterySensor extends AbstractSwanSensor {

    public static final String TAG = "BatterySensor";

    ExecutorService executorService = Executors.newCachedThreadPool();

    int numberOfPowerSensors = 0;

    HashMap<String, String> idToValuePath = new HashMap<>();

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.battery_preferences;
        }
    }

    /**
     * The level field.
     */
    public static final String LEVEL_FIELD = "level";
    /**
     * The voltage field.
     */
    public static final String VOLTAGE_FIELD = "voltage";
    /**
     * The temperature field.
     */
    public static final String TEMPERATURE_FIELD = "temperature";
    /**
     * The plugged field
     */
    public static final String PLUGGED_FIELD = "plugged";

    /**
     * The pluggedText field
     */
    public static final String STATUS_TEXT_FIELD = "status_text";

    /**
     * The discharge current level in miliamps
     */
    public static final String DISCHARGE_CURRENT_FIELD = "discharge_current";

    /**
     * The discharge power level in miliwats
     */

    public static final String REMAINING_POWER_FIELD = "remaining_power";

    /**
     * The receiver for battery events.
     */
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                long now = System.currentTimeMillis();
                Log.d(TAG, "New level: " + intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));

                putValueTrimSize(LEVEL_FIELD, null, now, intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));
                putValueTrimSize(TEMPERATURE_FIELD, null, now, intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0));

                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                putValueTrimSize(VOLTAGE_FIELD, null, now, voltage);
                putValueTrimSize(PLUGGED_FIELD, null, now, intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));
                putValueTrimSize(STATUS_TEXT_FIELD, null, now, intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));


            }
        }
    };


    @Override
    public final String[] getValuePaths() {
        return new String[]{TEMPERATURE_FIELD, LEVEL_FIELD, VOLTAGE_FIELD,
                PLUGGED_FIELD, STATUS_TEXT_FIELD, DISCHARGE_CURRENT_FIELD, REMAINING_POWER_FIELD};
    }

    @Override
    public void initDefaultConfiguration(final Bundle defaults) {
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Battery Sensor";
    }


    public void updateAccuracy(){
        if(numberOfPowerSensors ==1){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            BatteryManager mBatteryManager =
                                    (BatteryManager) getApplicationContext().getSystemService(Context.BATTERY_SERVICE);
                            Integer energy =
                                    mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                            putValueTrimSize(DISCHARGE_CURRENT_FIELD, null, System.currentTimeMillis(), energy/1000);

                            Long energyinWatts =
                                    mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                            Log.i(TAG, "Remaining energy = " + energy + "nWh");
                            putValueTrimSize(REMAINING_POWER_FIELD, null, System.currentTimeMillis(), energyinWatts);
                        }
                        try {
                            Thread.sleep(getSensorDelay());
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            });
        } else if(numberOfPowerSensors == 0){
            executorService.shutdownNow();
        }

    }

    @Override
    public final void register(final String id, final String valuePath,
                               final Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);


        if (registeredConfigurations.size() == 1) {
            registerReceiver(batteryReceiver, new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED));
        }

        if(valuePath.equals(REMAINING_POWER_FIELD) || valuePath.equals(DISCHARGE_CURRENT_FIELD)){
            numberOfPowerSensors++;
            idToValuePath.put(id,valuePath);
            updateAccuracy();
        }
    }

    @Override
    public final void unregister(final String id) {
        if (registeredConfigurations.size() == 0) {
            unregisterReceiver(batteryReceiver);
        }

        if(idToValuePath.containsKey(id)) {
            String valuePath = idToValuePath.get(id);
            if (valuePath.equals(REMAINING_POWER_FIELD) || valuePath.equals(DISCHARGE_CURRENT_FIELD)) {
                numberOfPowerSensors++;
                updateAccuracy();
            }

            idToValuePath.remove(id);
        }
    }

    @Override
    public final void onDestroySensor() {
        if (registeredConfigurations.size() > 0) {
            unregisterReceiver(batteryReceiver);
        }
        super.onDestroySensor();
    }
}

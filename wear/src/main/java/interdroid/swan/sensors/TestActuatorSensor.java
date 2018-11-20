package interdroid.swan.sensors;

/**
 * Created by slavik on 7/7/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swancore.sensors.AbstractSwanSensorBase;
import interdroid.swancore.shared.DataMapKeys;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.TimestampedValue;

import static interdroid.swancore.swanmain.ActuatorManager.SENSOR_ACTUATOR_INTERCEPTOR;


/**
 * A sensor for battery temperature, level and voltage.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public class TestActuatorSensor extends AbstractSwanSensorBase {

    public static final String TAG = "TestActuatorSensor";

    ExecutorService executorService = Executors.newCachedThreadPool();

    public static boolean TEST_ACTUATOR_SENSOR = false;

    int numberOfPowerSensors = 0;

    PowerManager.WakeLock wakeLock;

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
            return 1;
        }
    }

    /**
     * The level field.
     */
    public static final String ZERO_FIELD = "zero";
    /**
     * The voltage field.
     */
    public static final String ONE_FIELD = "one";

    public static final String ALTERNATE_FIELD = "alternate_test";

    private SensorUpdate updateReceiver = new SensorUpdate();
    int val=0;

    private class SensorUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(SENSOR_ACTUATOR_INTERCEPTOR)) {
                /*Bundle extra = intent.getExtras();
                try {
                    final Result result = (Result) Converter.stringToObject(extra.getString(DataMapKeys.VALUES));
                    Log.d(TAG, "Good till now !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    if(result.getValues().length>0){
                        Log.d(TAG, "received remote sensor value:"+result.getValues()[0]);
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                // putValueTrimSize(ALTERNATE_FIELD, null, System.currentTimeMillis(), d.getValues());

                                putValueTrimSize(ALTERNATE_FIELD, null, System.currentTimeMillis(), result.getValues()[0]);
                            }

                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                */
                Log.d(TAG, "received remote sensor value:"+val);
                putValueTrimSize(ALTERNATE_FIELD, null, System.currentTimeMillis(), val);
                val = 1-val;
            }

        }
    }


    @Override
    public final String[] getValuePaths() {
        return new String[]{ALTERNATE_FIELD };//ZERO_FIELD, ONE_FIELD, ALTERNATE_FIELD };
    }

    @Override
    public void initDefaultConfiguration(final Bundle defaults) {
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Test Actuator Sensor";
    }


    @Override
    public final void register(final String id, final String valuePath,
                               final Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                TAG);
        wakeLock.acquire();

        registerReceiver(updateReceiver, new IntentFilter(SENSOR_ACTUATOR_INTERCEPTOR));
        TEST_ACTUATOR_SENSOR= true;

        idToValuePath.put(id,valuePath);

    }

    @Override
    public final void unregister(final String id) {

        executorService.shutdownNow();
        unregisterReceiver(updateReceiver);
        TEST_ACTUATOR_SENSOR = false;
        idToValuePath.remove(id);

        if(wakeLock!=null) {
            wakeLock.release();
        }


    }

    @Override
    public final void onDestroySensor() {
        executorService.shutdownNow();
        super.onDestroySensor();
    }
}


package interdroid.swan.sensors.impl;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interdroid.swan.R;
import interdroid.swan.actuator.ActuatorInterceptor;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swan.sensors.impl.wear.shared.AbstractWearSensor;
import interdroid.swan.sensors.impl.wear.shared.RemoteSensorManager;
import interdroid.swan.sensors.impl.wear.shared.data.SensorDataPoint;
import interdroid.swan.sensors.impl.wear.shared.data.WearSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swancore.shared.SensorConstants;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swansong.TimestampedValue;


import static interdroid.swancore.swanmain.ActuatorManager.SENSOR_ACTUATOR_INTERCEPTOR;


/**
 * A sensor for battery temperature, level and voltage.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public class TestActuatorSensor extends AbstractSwanSensor {

    public static final String TAG = "TestActuatorSensor";

    ExecutorService executorService = Executors.newCachedThreadPool();

    public static boolean TEST_ACTUATOR_SENSOR = false;

    int numberOfPowerSensors = 0;
    //public static long testCounter = 0;

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
            return R.xml.wear_test_preferences;
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

    int val;

    private SensorUpdate updateReceiver = new SensorUpdate();

    private class SensorUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            val =val+1;
            if (action.equalsIgnoreCase(SENSOR_ACTUATOR_INTERCEPTOR)) {
                Bundle extra = intent.getExtras();
                //String who = extra.getString("sensor");
                //WearSensor s = (WearSensor) extra.getSerializable(SensorConstants.SENSOR_OBJECT);
                Parcelable[] parcelables = (Parcelable[]) intent
                        .getParcelableArrayExtra(ExpressionManager.EXTRA_NEW_VALUES);
                final TimestampedValue[] timestampedValues = new TimestampedValue[parcelables.length];
                System.arraycopy(parcelables, 0, timestampedValues, 0, parcelables.length);

                Log.d(TAG, "received remote sensor value:"+timestampedValues[0].getValue());
                if(timestampedValues.length>0){

                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                           // putValueTrimSize(ALTERNATE_FIELD, null, System.currentTimeMillis(), d.getValues());

                            putValueTrimSize(ALTERNATE_FIELD, null, System.currentTimeMillis(), timestampedValues[0].getValue());
                        }

                    });

                }
            }

        }
    }


    @Override
    public final String[] getValuePaths() {
        return new String[]{ALTERNATE_FIELD};//ZERO_FIELD, ONE_FIELD, ALTERNATE_FIELD };
    }

    @Override
    public void initDefaultConfiguration(final Bundle defaults) {
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Test Sensor";
    }


    @Override
    public final void register(final String id, final String valuePath,
                               final Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                TAG);
        wakeLock.acquire();

        //This timer runs for 30 seconds and the testCounter resets every second. NOTE: delete after test
        /*new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "Throughput Phone = "+testCounter);
                testCounter=0;

            }

            public void onFinish() {
                testCounter = 0;
                Log.d(TAG, "Count down finished");
            }
        }.start();*/

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


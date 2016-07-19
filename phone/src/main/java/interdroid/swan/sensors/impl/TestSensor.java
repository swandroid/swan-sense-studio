package interdroid.swan.sensors.impl;

/**
 * Created by slavik on 7/7/16.
 */

import android.os.Build;
import android.os.Bundle;

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
public class TestSensor extends AbstractSwanSensor {

    public static final String TAG = "TestSensor";

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



    @Override
    public final String[] getValuePaths() {
        return new String[]{ ZERO_FIELD, ONE_FIELD, ALTERNATE_FIELD };
    }

    @Override
    public void initDefaultConfiguration(final Bundle defaults) {
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Test Sensor";
    }


    public void updateAccuracy(){
        if(numberOfPowerSensors ==1){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    int val = 0;
                    while(true) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                            putValueTrimSize(ZERO_FIELD, null, System.currentTimeMillis(),0);
                            putValueTrimSize(ONE_FIELD, null, System.currentTimeMillis(), 1);
                            putValueTrimSize(ALTERNATE_FIELD, null, System.currentTimeMillis(), val);

                            val = 1-val;

                        }
                        try {
                            Thread.sleep(getSensorDelay()/1000);
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


        numberOfPowerSensors++;
        idToValuePath.put(id,valuePath);
        updateAccuracy();
    }

    @Override
    public final void unregister(final String id) {

        numberOfPowerSensors--;
        updateAccuracy();
        idToValuePath.remove(id);

    }

    @Override
    public final void onDestroySensor() {
        executorService.shutdownNow();
        super.onDestroySensor();
    }
}


package interdroid;

/**
 * Created by slavik on 7/8/16.
 */

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interdroid.swan.DeviceClient;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swancore.sensors.AbstractSwanSensorBase;

/**
 * Created by slavik on 7/7/16.
 */

import android.os.Build;
import android.os.Bundle;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swancore.sensors.AbstractSwanSensorBase;
import interdroid.swancore.shared.SensorConstants;


/**
 * A sensor for battery temperature, level and voltage.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public class FixedSensor{

    public static final String TAG = "TestSensor";

    ExecutorService executorService = Executors.newCachedThreadPool();

    int numberOfPowerSensors = 0;

    int sensorDelay = 0;

    Context mContext;


    public FixedSensor(Context context){
        this.mContext = context;
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


    public final String[] getValuePaths() {
        return new String[]{ ZERO_FIELD, ONE_FIELD, ALTERNATE_FIELD };
    }




    public void updateAccuracy(){
        if(numberOfPowerSensors ==1){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    int val = 0;
                    float[] values = new float[3];
                    while(true) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            values[0] = 0;
                            values[1] = 1;
                            values[2] = val;

                            DeviceClient.getInstance(mContext).sendSensorData(SensorConstants.TEST_SENSOR_ID,
                                    sensorDelay, System.currentTimeMillis(), values);

                            val = 1-val;

                        }
                        try {
                            Thread.sleep(sensorDelay);
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

    public final void register(int accuracy) {

        this.sensorDelay = (accuracy/1000);
        numberOfPowerSensors++;
        updateAccuracy();
    }

    public final void unregister() {

        numberOfPowerSensors--;
        updateAccuracy();
    }

    public final void onDestroySensor() {
        executorService.shutdownNow();
    }
}



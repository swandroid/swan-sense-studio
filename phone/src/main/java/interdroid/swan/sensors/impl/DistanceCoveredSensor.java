package interdroid.swan.sensors.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import uk.ac.ox.eng.stepcounter.StepCounter;

/**
 * Created by bojansimoski on 20/03/2017.
 */

public class DistanceCoveredSensor  extends AbstractSwanSensor {
    public static final String TAG = "DistanceCoveredSensor";

    /**
     * The configuration activity for this sensor.
     *
     * @author Bojan Simoski
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {
        //todo define something in the distance covered preferences
        @Override
        public final int getPreferencesXML() {
            return R.xml.distance_covered_preferences;
        }

    }

    /**
     * Value of ACCURACY must be one of SensorManager.SENSOR_STATUS_ACCURACY_*
     */
    public static final String ACCURACY = "accuracy";

    public static final String METERS = "meters";
    public static final String STEPS = "steps";

    public static final String STEP_COEFFICIENT = "step_coefficient";
    public static final double DEFAULT_STEP_COEFFICIENT = 180*0.415;
    private Sensor accelerometer;
    private SensorManager sensorManager;
    double step_coeficient=0.0;
    double sensitivity=0.0;

    private final int SAMPLING_FREQUENCY = 100;
    private StepCounter stepCounterManager;

    private int currentSteps = 0;
    private int currentMeters = 0;

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                currentConfiguration.putInt(ACCURACY, accuracy);
            }
        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                stepCounterManager.processSample(event.timestamp, event.values);
            }
        }
    };

    @Override
    public String[] getValuePaths() {
        return new String[]{METERS,STEPS};
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putInt(DELAY, normalizeSensorDelay(SensorManager.SENSOR_DELAY_NORMAL));
        DEFAULT_CONFIGURATION.putInt(ACCURACY, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        DEFAULT_CONFIGURATION.putDouble(STEP_COEFFICIENT, DEFAULT_STEP_COEFFICIENT);
    }


    @Override
    public void onConnected() {
        SENSOR_NAME = "Distance Covered Sensor";
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        stepCounterManager= new StepCounter(SAMPLING_FREQUENCY);

            stepCounterManager.start();
            stepCounterManager.addOnStepUpdateListener(new StepCounter.OnStepUpdateListener() {
                @Override
                public void onStepUpdate(final int steps) {
                    long now = acceptSensorReading();
                    currentSteps = steps;
                    currentMeters = getDistance(currentSteps);
                    putValueTrimSize(METERS, null, now, currentMeters);
                }
            });



        if (sensorList.size() > 0) {
            accelerometer = sensorList.get(0);
        } else {
            Toast.makeText(getApplicationContext(), "No accelerometer found on device!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No accelerometer found on device!");
        }
    }

    @Override
    public final void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        //todo modify the way you get these values so no overwrites will happen
        step_coeficient= Double.valueOf((String)configuration.get("step_coeficient"));
        sensitivity=Double.valueOf((String)configuration.get("sensitivity"));

        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        updateDelay();
    }


    private void updateDelay() {
        sensorManager.unregisterListener(sensorEventListener);
        int delay = getSensorDelay();
        if (delay >= 0) {
            sensorManager.registerListener(sensorEventListener, accelerometer, delay);
            Log.d(TAG, "delay set to " + delay);
        }

    }

    public int getDistance(long steps){
        return (int)(steps*step_coeficient)/100;
    }

    @Override
    public final void unregister(String id) {
        updateDelay();
    }

    @Override
    public final void onDestroySensor() {
        stepCounterManager.stop();
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroySensor();
    }

    @Override
    public float getCurrentMilliAmpere() {
        return accelerometer.getPower();
    }
}

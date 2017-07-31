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
import interdroid.swancore.models.MovementCoordinates;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

/**
 * Created by bojansimoski on 20/03/2017.
 */

public class DistanceCoveredSensor  extends AbstractSwanSensor {
    public static final String TAG = "DistanceCoveredSensor";

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
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

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                currentConfiguration.putInt(ACCURACY, accuracy);
            }
        }





        final double   mLastValues[] = new double[3*2];
        final double   mScale[] = new double[2];
        int h = 480; // TODO: remove this constant
        final double   mYOffset= h * 0.5f;

        final double   mLastDirections[] = new double[3*2];
        final double   mLastExtremes[][] = { new double[3*2], new double[3*2] };
        final double   mLastDiff[] = new double[3*2];

        int     mLastMatch = -1;
        int     stepCounter=0;

        int meters=0;

        double t = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));


        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mScale[1]=t;
                double vSum = 0;
                long now = acceptSensorReading();

                if (now >= 0) {
                    Log.d(TAG, "onSensorChanged: " + now + " val " +
                            event.values[0] + " " + event.values[1] + " " +
                            event.values[2]);
                    //the event.values contains the x,y,z movement values
                    vSum = mYOffset*3 + event.values[0] * mScale[1]+event.values[1]* mScale[1]+event.values[2]*mScale[1];

                    int k = 0;
                    double v = vSum / 3;

                    float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                    if (direction == - mLastDirections[k]) {
                        // Direction changed
                        int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                        mLastExtremes[extType][k] = mLastValues[k];
                        double diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                        Log.d(TAG, " sensitivity: " + sensitivity + " step_coeficient: " + step_coeficient);

                        if (diff > sensitivity) {

                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                            boolean isNotContra = (mLastMatch != 1 - extType);

                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                Log.i(TAG, "step");
                                stepCounter++;
                                meters= getDistanceRun(stepCounter);
                                //insert new values only if new step is detected
                                putValueTrimSize(METERS, null, now, meters);
                                putValueTrimSize(STEPS, null, now, stepCounter);
                                mLastMatch = extType;
                            }
                            else {
                                mLastMatch = -1;
                            }
                        }
                        mLastDiff[k] = diff;
                    }
                    mLastDirections[k] = direction;
                    mLastValues[k] = v;



                }
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

    public int getDistanceRun(long steps){
        // 0.415 for men and 0.413 for women * height in cm
     //   configuration.get(STEP_COEFFICIENT)''
        return (int)(steps*step_coeficient)/100;
    }

//TODO check if you need this
//    @Override
//    public String getModelClassName() {
//        return MovementCoordinates.class.getName();
//    }
//
//    @Override
//    public Class<?>[] getParameterTypes() {
//        return MovementCoordinates.class.getConstructors()[0].getParameterTypes();
//    }
    @Override
    public final void unregister(String id) {
        updateDelay();
    }

    @Override
    public final void onDestroySensor() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroySensor();
    }

    @Override
    public float getCurrentMilliAmpere() {
        return accelerometer.getPower();
    }
}

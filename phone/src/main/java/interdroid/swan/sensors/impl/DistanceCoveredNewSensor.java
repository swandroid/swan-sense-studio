
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

    /**
     * Created by bojansimoski on 25/07/2017.
     */

    public class DistanceCoveredNewSensor  extends AbstractSwanSensor {
        public static final String TAG = "DistanceCoveredNewSensor";

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


        private static final int ACCEL_RING_SIZE = 50;
        private static final int VEL_RING_SIZE = 10;
        // change this threshold according to your sensitivity preferences
        double STEP_THRESHOLD = 50f;
        private static final int STEP_DELAY_NS = 250000000;
        private int accelRingCounter = 0;
        private float[] accelRingX = new float[ACCEL_RING_SIZE];
        private float[] accelRingY = new float[ACCEL_RING_SIZE];
        private float[] accelRingZ = new float[ACCEL_RING_SIZE];
        private int velRingCounter = 0;
        private float[] velRing = new float[VEL_RING_SIZE];
        private long lastStepTimeNs = 0;
        private float oldVelocityEstimate = 0;

        int numSteps=0;
        int meters=0;


        private SensorEventListener sensorEventListener = new SensorEventListener() {

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    currentConfiguration.putInt(ACCURACY, accuracy);
                }
            }


            public void onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                    long now = acceptSensorReading();
                    STEP_THRESHOLD=sensitivity;
                    //  if (now >= 0) {
                    Log.d(TAG, "onSensorChanged: " + now + " val " + event.values[0] + " " + event.values[1] + " " + event.values[2]);
                    //this should be called on sensor event
                    float[] currentAccel = new float[3];
                    currentAccel[0] = event.values[0];
                    currentAccel[1] = event.values[1];
                    currentAccel[2] = event.values[2];

                    // First step is to update our guess of where the global z vector is.
                    accelRingCounter++;
                    accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
                    accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
                    accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

                    float[] worldZ = new float[3];
                    worldZ[0] = sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
                    worldZ[1] = sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
                    worldZ[2] = sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

                    float normalization_factor = norm(worldZ);

                    worldZ[0] = worldZ[0] / normalization_factor;
                    worldZ[1] = worldZ[1] / normalization_factor;
                    worldZ[2] = worldZ[2] / normalization_factor;

                    float currentZ = dot(worldZ, currentAccel) - normalization_factor;
                    velRingCounter++;
                    velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

                    float velocityEstimate = sum(velRing);

                    if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
                            && (event.timestamp - lastStepTimeNs > STEP_DELAY_NS)) {

                        numSteps++;
                        meters= getDistanceRun(numSteps);
                        putValueTrimSize(METERS, null, now, meters);
                        putValueTrimSize(STEPS, null, now, numSteps);

                        lastStepTimeNs = event.timestamp;

                    }
                    oldVelocityEstimate = velocityEstimate;

                    //this should be called on sensor event
                }
                // }
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
                Log.d("TAG",Integer.toString(delay));
            }

        }

        public int getDistanceRun(long steps){
            // 0.415 for men and 0.413 for women * height in cm
            //   configuration.get(STEP_COEFFICIENT)''
            Log.d(TAG,"The step coef final = " +step_coeficient);
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


        //methods needed for the step discovery
        public static float sum(float[] array) {
            float retval = 0;
            for (int i = 0; i < array.length; i++) {
                retval += array[i];
            }
            return retval;
        }

        public static float[] cross(float[] arrayA, float[] arrayB) {
            float[] retArray = new float[3];
            retArray[0] = arrayA[1] * arrayB[2] - arrayA[2] * arrayB[1];
            retArray[1] = arrayA[2] * arrayB[0] - arrayA[0] * arrayB[2];
            retArray[2] = arrayA[0] * arrayB[1] - arrayA[1] * arrayB[0];
            return retArray;
        }

        public static float norm(float[] array) {
            float retval = 0;
            for (int i = 0; i < array.length; i++) {
                retval += array[i] * array[i];
            }
            return (float) Math.sqrt(retval);
        }


        public static float dot(float[] a, float[] b) {
            float retval = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
            return retval;
        }

        public static float[] normalize(float[] a) {
            float[] retval = new float[a.length];
            float norm = norm(a);
            for (int i = 0; i < a.length; i++) {
                retval[i] = a[i] / norm;
            }
            return retval;
        }

    }



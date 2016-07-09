package interdroid.swan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

import interdroid.FixedSensor;
import interdroid.swancore.shared.DataMapKeys;
import interdroid.swan.expression.ManageExpressions;
import interdroid.swancore.shared.SensorConstants;


class SensorContainer {
    public Sensor sensor;
    public int accuracy;
    public int count;


    public SensorContainer(Sensor sensor, int accuracy, int count) {
        this.sensor = sensor;
        this.accuracy = accuracy;
        this.count = count;
    }
}

public class SensorService extends Service implements SensorEventListener {


    enum Measurement {
        EXPRESSION,
        SENSOR
    }
    private static final String TAG = "Wear/SensorService";

    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_MAGNETIC_FIELD = Sensor.TYPE_MAGNETIC_FIELD;
    // 3 = @Deprecated Orientation
    private final static int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    private final static int SENS_LIGHT = Sensor.TYPE_LIGHT;
    private final static int SENS_PRESSURE = Sensor.TYPE_PRESSURE;
    // 7 = @Deprecated Temperature
    private final static int SENS_PROXIMITY = Sensor.TYPE_PROXIMITY;
    private final static int SENS_GRAVITY = Sensor.TYPE_GRAVITY;
    private final static int SENS_LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;
    private final static int SENS_ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;
    private final static int SENS_HUMIDITY = Sensor.TYPE_RELATIVE_HUMIDITY;
    // TODO: there's no Android Wear devices yet with a body temperature monitor
    private final static int SENS_AMBIENT_TEMPERATURE = Sensor.TYPE_AMBIENT_TEMPERATURE;
    private final static int SENS_MAGNETIC_FIELD_UNCALIBRATED = Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;
    private final static int SENS_GAME_ROTATION_VECTOR = Sensor.TYPE_GAME_ROTATION_VECTOR;
    private final static int SENS_GYROSCOPE_UNCALIBRATED = Sensor.TYPE_GYROSCOPE_UNCALIBRATED;
    private final static int SENS_SIGNIFICANT_MOTION = Sensor.TYPE_SIGNIFICANT_MOTION;
    private final static int SENS_STEP_DETECTOR = Sensor.TYPE_STEP_DETECTOR;
    private final static int SENS_STEP_COUNTER = Sensor.TYPE_STEP_COUNTER;
    private final static int SENS_GEOMAGNETIC = Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
    private final static int SENS_HEARTRATE = Sensor.TYPE_HEART_RATE;

    SensorManager mSensorManager = null;

    private Sensor mHeartrateSensor;

    private DeviceClient client;
    private ScheduledExecutorService mScheduler;

    ReentrantLock lock = new ReentrantLock();

    private HashMap<Integer, SensorContainer> activeSensors = new HashMap<>();

    private HashMap<String, String> expressionContainer = new HashMap<>();

    Notification.Builder notificationBuilder;

    SensorCommand sensorCommand = new SensorCommand();

    ManageExpressions exp;

    FixedSensor fixedSensor;

    private class SensorCommand extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(WearConstants.BROADCAST_ADD_SENSOR)) {
                Bundle extra = intent.getExtras();
                int sensorId = extra.getInt(SensorConstants.SENSOR_ID);
                int accuracy = extra.getInt(SensorConstants.ACCURACY);
                Log.d("TAG", "starting sensor+++++++" + sensorId + " " + accuracy);
                startSingleMeasurement(sensorId, accuracy, Measurement.SENSOR, null , null);
            }

            if (action.equalsIgnoreCase(WearConstants.BROADCAST_REMOVE_SENSOR)) {

                int sensorId = intent.getExtras().getInt(SensorConstants.SENSOR_ID);
                Log.d("TAG", "stopping sensors+++++" + sensorId);
                stopSingleMeasurement(sensorId, Measurement.SENSOR, null);
            }

            if(action.equalsIgnoreCase(WearConstants.BROADCAST_REGISTER_EXPR)){

                String id = intent.getExtras().getString(DataMapKeys.EXPRESSION_ID);
                String expr = intent.getExtras().getString(DataMapKeys.EXPRESSION);
                Log.d("TAG", "starting expression+++++" + id + " Expr:" + expr);
                startSingleMeasurement(0, 0, Measurement.EXPRESSION, expr, id);
            }

            if(action.equalsIgnoreCase(WearConstants.BROADCAST_UNREGISTER_EXPR)){
                String id = intent.getExtras().getString(DataMapKeys.EXPRESSION_ID);
                Log.d("TAG", "stopping expression+++++" + id);
                stopSingleMeasurement(0,Measurement.EXPRESSION, id);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        client = DeviceClient.getInstance(this);
        client.initExecutor();

        fixedSensor = new FixedSensor(this);

        exp = new ManageExpressions(getApplicationContext());


        notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setContentTitle("Swan");
        notificationBuilder.setContentText("Collecting sensor data..");
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);

        registerReceiver(sensorCommand, new IntentFilter(WearConstants.BROADCAST_ADD_SENSOR));
        registerReceiver(sensorCommand, new IntentFilter(WearConstants.BROADCAST_REMOVE_SENSOR));
        registerReceiver(sensorCommand, new IntentFilter(WearConstants.BROADCAST_REGISTER_EXPR));
        registerReceiver(sensorCommand, new IntentFilter(WearConstants.BROADCAST_UNREGISTER_EXPR));


       // testSwanExpression();

        startForeground(1, notificationBuilder.build());

    }

    private void registerSwanExpression(String id, String expression){
        expressionContainer.put(id, expression);
        exp.registerValueExpression( id, expression);
    }

    private void unregisterSwanExpression(String id) {
        exp.unregisterSWANExpression(id);
        expressionContainer.remove(id);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(sensorCommand);
        client.shutDown();
        stopMeasurement();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    protected void startSingleMeasurement(int sensorId, int accuracy, Measurement type, String expression, String expressionID) {

        lock.lock();
        try {
            if (type == Measurement.EXPRESSION) {
                registerSwanExpression(expressionID, expression);
                return;
            }

            if(sensorId == SensorConstants.TEST_SENSOR_ID){
                activeSensors.put(sensorId, new SensorContainer(null, accuracy, 1));
                fixedSensor.register(accuracy);
                return;
            }
            if (sensorId <= 0) {
                Log.w(TAG, "Bad sensor ID");
                return;
            }

            if (mSensorManager == null) {
                mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
            }

            Sensor sensor = mSensorManager.getDefaultSensor(sensorId);

            if (sensor != null) {
                if (activeSensors.containsKey(sensorId)) {
                    update_accuracy(sensorId, accuracy);
                } else {
                    activeSensors.put(sensorId, new SensorContainer(sensor, accuracy, 1));
                    Log.d(TAG,"Registering sensor with accuracy" + accuracy);
                    mSensorManager.registerListener(this, sensor, accuracy);
                }
                update_notification();
            } else {
                Log.w(TAG, "Sensor with id " + sensorId + " not found");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * If the accuracy changes, set the highest accuracy
     *
     * @param sensorId - sensor id
     * @param accuracy - accuracy of the sensor
     */
    private void update_accuracy(int sensorId, int accuracy) {
        SensorContainer container = activeSensors.get(sensorId);

        if (accuracy < container.accuracy) {
            container.accuracy = accuracy;
            mSensorManager.registerListener(this, container.sensor, accuracy);
        }

        container.count++;
        activeSensors.put(sensorId, container);
    }

    protected void stopSingleMeasurement(int sensorID, Measurement type, String exprID) {

        lock.lock();
        try {

            if(type == Measurement.EXPRESSION){
                unregisterSwanExpression(exprID);
            } else {

                if(sensorID == SensorConstants.TEST_SENSOR_ID){
                    activeSensors.remove(sensorID);
                    fixedSensor.unregister();
                    return;
                }
                if (!activeSensors.containsKey(sensorID)) {
                    Log.w(TAG, "Trying to stop an non-stated sensor");
                    return;
                }

                SensorContainer container = activeSensors.get(sensorID);

                if (container.count == 1) {
                    activeSensors.remove(sensorID);

                    mSensorManager.unregisterListener(this, container.sensor);
                } else {
                    container.count--;
                    activeSensors.put(sensorID, container);
                }

                update_notification();
            }


        } finally {
            if (activeSensors.isEmpty() && expressionContainer.isEmpty()) {
                Log.d(TAG, "Stopping service");
                stopSelf();
            }

            lock.unlock();
        }

    }

    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        if (mScheduler != null && !mScheduler.isTerminated()) {
            mScheduler.shutdown();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        client.sendSensorData(event.sensor.getType(), event.accuracy,
                event.timestamp, event.values);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Update active sensor count when add or remove sensor is called
     */
    public void update_notification() {
        notificationBuilder.setContentText("Active Sensors: " + activeSensors.size());

        NotificationManager nf = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));

        nf.notify(1, notificationBuilder.build());
    }

}

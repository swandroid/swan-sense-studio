package interdroid.swan.sensors.impl.wear.shared;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import interdroid.swan.actuator.ActuationManager;
import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.engine.QueuedExpression;
import interdroid.swancore.shared.ClientPaths;
import interdroid.swancore.shared.DataMapKeys;
import interdroid.swancore.shared.SensorConstants;
import interdroid.swan.sensors.impl.wear.shared.data.SensorDataPoint;
import interdroid.swan.sensors.impl.wear.shared.data.SensorNames;
import interdroid.swan.sensors.impl.wear.shared.data.WearSensor;
import interdroid.swancore.swanmain.ActuatorManager;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.ValueExpression;

import static interdroid.swan.sensors.impl.TestActuatorSensor.TEST_ACTUATOR_SENSOR;
import static interdroid.swancore.swanmain.ActuatorManager.ACTUATOR_INTERCEPTOR;
import static interdroid.swancore.swanmain.ActuatorManager.SENSOR_ACTUATOR_INTERCEPTOR;

public class RemoteSensorManager {
    private static final String TAG = "RemoteSensorManager";
    private static final int CLIENT_CONNECTION_TIMEOUT = 15000;

    private static RemoteSensorManager instance;

    private Context context;
    private ExecutorService executorService;
    private SparseArray<WearSensor> sensorMapping;
    private ArrayList<WearSensor> sensors;
    private SensorNames sensorNames;
    private GoogleApiClient googleApiClient;

    public static final String ACTUATOR_UPDATE = "values";

    public static final String REGISTER_MESSAGE = "RegisterMessage";
    public static final String UPDATE_MESSAGE = "SensorUpdateMessage";

    public static synchronized RemoteSensorManager getInstance(Context context) {
        if (instance == null) {
            instance = new RemoteSensorManager(context.getApplicationContext());
        }

        return instance;
    }

    private RemoteSensorManager(Context context) {
        this.context = context;
        this.sensorMapping = new SparseArray<WearSensor>();
        this.sensors = new ArrayList<WearSensor>();
        this.sensorNames = SensorNames.getInstance();

        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        this.executorService = Executors.newCachedThreadPool();
    }

    public List<WearSensor> getSensors() {
        return (List<WearSensor>) sensors.clone();
    }

    public WearSensor getSensor(long id) {
        return sensorMapping.get((int) id);
    }

    private WearSensor createSensor(int id) {
        WearSensor sensor = new WearSensor(id, sensorNames.getName(id));

        sensors.add(sensor);
        sensorMapping.append(id, sensor);

        Intent i = new Intent(REGISTER_MESSAGE);
        i.putExtra("sensor", sensor);
        context.sendBroadcast(i);

        return sensor;
    }

    private WearSensor getOrCreateSensor(int id) {
        WearSensor sensor = sensorMapping.get(id);

        if (sensor == null) {
            sensor = createSensor(id);
        }

        return sensor;
    }

    public synchronized void addSensorData(int sensorType, int accuracy, long timestamp, float[] values) {
        WearSensor sensor = getOrCreateSensor(sensorType);

        // TODO: We probably want to pull sensor data point objects from a pool here
        SensorDataPoint dataPoint = new SensorDataPoint(timestamp, accuracy, values);

        sensor.addDataPoint(dataPoint);

        Log.d(TAG, "Sensor Update Event!!!++++++++++++++++++");
        Intent i = new Intent(UPDATE_MESSAGE);
        i.putExtra(SensorConstants.SENSOR_OBJECT, sensor);
        i.putExtra(SensorConstants.SENSOR_DATA, dataPoint);
        context.sendBroadcast(i);
    }

    public synchronized void addExpressionData(String id, String data){

        /*Log.d(TAG, "Got data for id: " + id);
        Intent i = new Intent(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE);
        i.setClass(context, EvaluationEngineService.class);
        i.putExtra("id", id);
        i.putExtra("data", data);
        context.startService(i);*/
        //Implementation to call actuator directly instead of passing through evaluation engine
        try {
            Result result = (Result) Converter.stringToObject(data);
            Intent intent;
            //TODO: for now this runs for one test actuator sensor. in the future include support for multiple actuator sensors
            if(TEST_ACTUATOR_SENSOR) {
                intent = new Intent(SENSOR_ACTUATOR_INTERCEPTOR);
            }else{
                intent = new Intent(ACTUATOR_INTERCEPTOR);
            }
            intent.putExtra(ExpressionManager.EXTRA_NEW_VALUES,
                    result.getValues());
            intent.putExtra(ActuatorManager.EXTRA_EXPRESSION_ID, id);
            context.sendBroadcast(intent);
            Log.d(TAG, "broadcast sent with intent"+intent);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean validateConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }

    public void filterBySensorId(final int sensorId) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                filterBySensorIdInBackground(sensorId);
            }
        });
    }

    ;

    private void filterBySensorIdInBackground(final int sensorId) {
        Log.d(TAG, "filterBySensorId(" + sensorId + ")");

        if (validateConnection()) {
            PutDataMapRequest dataMap = PutDataMapRequest.create("/filter");

            dataMap.getDataMap().putInt(DataMapKeys.FILTER, sensorId);
            dataMap.getDataMap().putLong(DataMapKeys.TIMESTAMP, System.currentTimeMillis());

            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Filter by sensor " + sensorId + ": " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }

    public void startMeasurement(final Bundle config) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(12);

        byteBuffer.position(0);
        byteBuffer.putInt(config.getInt(SensorConstants.SENSOR_ID));
        byteBuffer.putInt(config.getInt(SensorConstants.ACCURACY));

        final byte[] data = byteBuffer.array();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                handleSensors(ClientPaths.START_MEASUREMENT,
                        config.getInt(SensorConstants.SENSOR_ID),
                        config.getInt(SensorConstants.ACCURACY));
            }
        });
    }

    public void stopMeasurement(final Bundle config) {


        executorService.submit(new Runnable() {
            @Override
            public void run() {
                handleSensors(ClientPaths.STOP_MEASUREMENT,config.getInt(SensorConstants.SENSOR_ID),0);
            }
        });
    }

    public void registerExpression(final String expression, final String id){

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                //handleExpressions(ClientPaths.REGISTER_EXPRESSION, expression, id);
                handleRegisteringSensorActuatorExpressions(ClientPaths.REGISTER_EXPRESSION, expression, id);
            }
        });
    }

    public void unregisterExpression( final String expression, final String id){


        executorService.submit(new Runnable() {
            @Override
            public void run() {

                handleExpressions(ClientPaths.UNREGISTER_EXPRESSION, expression, id);
            }
        });
    }

    public void registerActuationExpression(final String expression, final String id){

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                handleExpressions(ClientPaths.REGISTER_ACTUATION_EXPRESSION, expression, id);
            }
        });
    }

    public void unregisterActuationExpression(final String id){

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                handleExpressions(ClientPaths.UNREGISTER_ACTUATION_EXPRESSION, null, id);
            }
        });
    }

    public void Actuate(final String id, final Result result){

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                handleActuation(ClientPaths.ACTUATE, id, result);

            }
        });
    }

    public void ActuateAsFloat(final String id, final float value){

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                handleActuationWithFloat(ClientPaths.ACTUATE, id, value);

            }
        });
    }


    private void handleExpressions(final String path, final String expression, final String id) {
        if(validateConnection()) {
            PutDataMapRequest dataMap = PutDataMapRequest.create(path);

            dataMap.getDataMap().putString(DataMapKeys.EXPRESSION_ID, id);
            dataMap.getDataMap().putString(DataMapKeys.EXPRESSION, expression);

            dataMap.getDataMap().putLong("Time",System.currentTimeMillis());
            Log.d(TAG, "Expression to send remote:"+expression+" id:"+id);
            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            putDataRequest = putDataRequest.setUrgent();
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Sending new expession ++++++++" + path +" " + id + ": " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }


    private void handleActuation(final String path, final String id, final Result result) {
        if(validateConnection()) {
            PutDataMapRequest dataMap = PutDataMapRequest.create(path);

            dataMap.getDataMap().putString(DataMapKeys.EXPRESSION_ID, id);
            //dataMap.getDataMap().putString(DataMapKeys.EXPRESSION, expression);

            //dataMap.getDataMap().putLong("Time",System.currentTimeMillis());
            if(result!=null) {
                try {
                    dataMap.getDataMap().putString(DataMapKeys.VALUES, Converter.objectToString(result));
                } catch (IOException e) {
                    Log.e(TAG, "Error, unable to put expression Object as a string");
                }
            }
            else{
                dataMap.getDataMap().putString(DataMapKeys.VALUES, null);
            }

            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            putDataRequest = putDataRequest.setUrgent();
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Sending new expession ++++++++" + path +" " + id + ": " + dataItemResult.getStatus().isSuccess());
                    Log.d(TAG, "Sent expression");
                }
            });
        }
    }

    private void handleActuationWithFloat(final String path, final String id, final float value) {
        if(validateConnection()) {
            PutDataMapRequest dataMap = PutDataMapRequest.create(path);

            dataMap.getDataMap().putString(DataMapKeys.EXPRESSION_ID, id);
            //dataMap.getDataMap().putString(DataMapKeys.EXPRESSION, expression);

            //dataMap.getDataMap().putLong("Time",System.currentTimeMillis());

            dataMap.getDataMap().putFloat(DataMapKeys.VALUES, value);


            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            putDataRequest = putDataRequest.setUrgent();
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Sending new expession ++++++++" + path +" " + id + ": " + dataItemResult.getStatus().isSuccess());
                    Log.d(TAG, "Sent expression");
                }
            });
        }
    }




    private void handleRegisteringSensorActuatorExpressions(final String path, final String expression, final String id) {
        if(validateConnection()) {
            PutDataMapRequest dataMap = PutDataMapRequest.create(path);
            boolean phoneActuation = false;
            dataMap.getDataMap().putString(DataMapKeys.EXPRESSION_ID, id);
            dataMap.getDataMap().putString(DataMapKeys.EXPRESSION, expression);

            if(ActuationManager.ACTUATORS.containsKey(id) && ActuationManager.ACTUATORS.get(id).size() > 0){
                phoneActuation = true;
            }else{
                phoneActuation = false;
            }


            dataMap.getDataMap().putLong("Time",System.currentTimeMillis());
            if(ActuationManager.REMOTE_ACTUATORS.containsKey(id) && ActuationManager.REMOTE_ACTUATORS.get(id).equals(Expression.LOCATION_WEAR)){
                dataMap.getDataMap().putBoolean (DataMapKeys.WEAR_ACTUATION, true);
            }
            else{
                dataMap.getDataMap().putBoolean (DataMapKeys.WEAR_ACTUATION, false);
            }
            if(ActuationManager.REMOTE_ACTUATORS.containsKey(id) && ActuationManager.REMOTE_ACTUATORS.get(id).equals(Expression.LOCATION_CLOUD)){
                dataMap.getDataMap().putBoolean (DataMapKeys.CLOUD_ACTUATION, true);
            }
            else{
                dataMap.getDataMap().putBoolean (DataMapKeys.CLOUD_ACTUATION, false);
            }
            //dataMap.getDataMap().putBoolean (DataMapKeys.WEAR_ACTUATION, ActuationManager.REMOTE_ACTUATORS.containsKey(id));
            dataMap.getDataMap().putBoolean (DataMapKeys.PHONE_ACTUATION, phoneActuation);
            Log.d(TAG, "Expression to send remote:"+expression+" id:"+id+" phone actuation:"+phoneActuation);
            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            putDataRequest = putDataRequest.setUrgent();
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Sending new expession ++++++++" + path +" " + id + ": " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }

    private void handleSensors(final String path, final int sensorID, final int acuracy) {
        if(validateConnection()) {
            PutDataMapRequest dataMap = PutDataMapRequest.create(path);

            dataMap.getDataMap().putInt(SensorConstants.SENSOR_ID, sensorID);
            dataMap.getDataMap().putInt(SensorConstants.ACCURACY, acuracy);
            dataMap.getDataMap().putLong("Time",System.currentTimeMillis());

            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            putDataRequest = putDataRequest.setUrgent();
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Sending new sensor ++++++++" + path +" " + sensorID + ": " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }

    public void getNodes(ResultCallback<NodeApi.GetConnectedNodesResult> pCallback) {
        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(pCallback);
    }

    private void controlMeasurementInBackground(final String path, byte[] extra) {
        if (validateConnection()) {
            List<Node> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();

            Log.d(TAG, "Sending to nodes: " + nodes.size());


            for (Node node : nodes) {
                Log.i(TAG, "add node " + node.getDisplayName());
                Wearable.MessageApi.sendMessage(
                        googleApiClient, node.getId(), path, extra
                ).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        Log.d(TAG, "controlMeasurementInBackground(" + path + "): " + sendMessageResult.getStatus().isSuccess());
                    }
                });
            }
        } else {
            Log.w(TAG, "No connection possible");
        }
    }

}

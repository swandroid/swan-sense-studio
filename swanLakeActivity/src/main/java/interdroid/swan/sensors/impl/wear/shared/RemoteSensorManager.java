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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import interdroid.swan.sensordashboard.shared.ClientPaths;
import interdroid.swan.sensordashboard.shared.DataMapKeys;
import interdroid.swan.sensordashboard.shared.SensorConstants;
import interdroid.swan.sensors.impl.wear.shared.data.SensorDataPoint;
import interdroid.swan.sensors.impl.wear.shared.data.SensorNames;
import interdroid.swan.sensors.impl.wear.shared.data.WearSensor;

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

    public void startMeasurement(Bundle config) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(12);

        byteBuffer.position(0);
        byteBuffer.putInt(config.getInt(SensorConstants.SENSOR_ID));
        byteBuffer.putInt(config.getInt(SensorConstants.ACCURACY));

        final byte[] data = byteBuffer.array();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(ClientPaths.START_MEASUREMENT, data);
            }
        });
    }

    public void stopMeasurement(Bundle config) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(12);
        byteBuffer.putInt(config.getInt(SensorConstants.SENSOR_ID));
        final byte[] data = byteBuffer.array();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(ClientPaths.STOP_MEASUREMENT, data);
            }
        });
    }

    public void registerExpression(final String expression){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(ClientPaths.REGISTER_EXPRESSION, expression.getBytes());
            }
        });
    }

    public void unregisterExpression(final String expression){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                controlMeasurementInBackground(ClientPaths.UNREGISTER_EXPRESSION, expression.getBytes());
            }
        });
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

package interdroid.swan;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import interdroid.swancore.shared.DataMapKeys;
import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.swansong.Result;

public class DeviceClient {
    private static final String TAG = "Wear/DeviceClient";
    private static final int CLIENT_CONNECTION_TIMEOUT = 15000;

    public static DeviceClient instance;


    public static DeviceClient getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceClient(context.getApplicationContext());
        }

        return instance;
    }

    private Context context;
    private GoogleApiClient googleApiClient;
    private ExecutorService executorService;
    private int filterId;


    private DeviceClient(Context context) {
        this.context = context;

        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        //lastSensorData = new SparseLongArray();
    }

    public void setSensorFilter(int filterId) {
        Log.d(TAG, "Now filtering by sensor: " + filterId);

        this.filterId = filterId;
    }

    public void initExecutor(){
        executorService = Executors.newCachedThreadPool();
    }
    public void shutDown(){
        executorService.shutdownNow();
    }

    public void sendSensorData(final int sensorType, final int accuracy, final long timestamp, final float[] values) {

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                sendSensorDataInBackground(sensorType, accuracy, timestamp, values);
            }
        });
    }

    private void sendSensorDataInBackground(int sensorType, int accuracy, long timestamp, float[] values) {

        PutDataMapRequest dataMap = PutDataMapRequest.create("/sensors/" + sensorType);

       // dataMap.getDataMap().putInt(DataMapKeys.ACCURACY, accuracy);
        dataMap.getDataMap().putLong(DataMapKeys.TIMESTAMP, timestamp);
        dataMap.getDataMap().putFloatArray(DataMapKeys.VALUES, values);


        PutDataRequest putDataRequest = dataMap.asPutDataRequest();

        send(putDataRequest);
    }

    public void sendExpressionData(final String exprId, final Result value){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                sendExpressionDataInBackground(exprId, value);
            }
        });
    }

    private void sendExpressionDataInBackground(String exprId, Result value){
        PutDataMapRequest dataMap = PutDataMapRequest.create("/expressionData/");

        dataMap.getDataMap().putString(DataMapKeys.EXPRESSION_ID, exprId);
        try {
            dataMap.getDataMap().putString(DataMapKeys.VALUES, Converter.objectToString(value));
        } catch (IOException e) {
            Log.e(TAG, "Error, unable to put expression Object as a string");
        }

        send(dataMap.asPutDataRequest());
    }

    private boolean validateConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }

    private void send(PutDataRequest putDataRequest) {
        if (validateConnection()) {
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                   // Log.v(TAG, "Sending sensor data: " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }
}

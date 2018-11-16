package interdroid.swan.sensors.impl.wear.shared;

import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.IOException;
import java.util.Arrays;

import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.shared.DataMapKeys;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.TimestampedValue;

//import interdroid.swan.sensordashboard.database.DataEntry;

//import io.realm.Realm;

public class SensorReceiverService extends WearableListenerService {
    private static final String TAG = "SensorReceiverService";

    private RemoteSensorManager sensorManager;
    static long testCounter = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = RemoteSensorManager.getInstance(this);

        //This timer runs for 30 seconds and the testCounter resets every second. NOTE: delete after test
        /*new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "Throughput Receive Phone = "+testCounter);
                testCounter=0;

            }

            public void onFinish() {
                testCounter = 0;
                Log.d(TAG, "Count down finished");
            }
        }.start();*/

    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        Log.d(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);

        Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged()");

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();

                if (path.startsWith("/sensors/")) {
                    unpackSensorData(
                            Integer.parseInt(uri.getLastPathSegment()),
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }

                if(path.startsWith("/expressionData/")){
                    //++testCounter;
                    unpackExpressionData(DataMapItem.fromDataItem(dataItem).getDataMap());
                    //TODO: if needed send as float
                    //unpackExpressionDataAsFloat(DataMapItem.fromDataItem(dataItem).getDataMap());
                }
            }
        }
    }

    private void unpackSensorData(int sensorType, DataMap dataMap) {
        int accuracy = dataMap.getInt(DataMapKeys.ACCURACY);
        long timestamp = dataMap.getLong(DataMapKeys.TIMESTAMP);
        float[] values = dataMap.getFloatArray(DataMapKeys.VALUES);

        Log.d(TAG, "Received sensor data " + sensorType + " = " + Arrays.toString(values));

        sensorManager.addSensorData(sensorType, accuracy, timestamp, values);
    }

    private void unpackExpressionData(DataMap dataMap){
        String id = dataMap.getString(DataMapKeys.EXPRESSION_ID);
        String data = dataMap.getString(DataMapKeys.VALUES);
        try {
            Result result = (Result) Converter.stringToObject(data);
            Log.d(TAG, "Received data length = " + result.getValues().length + " String length="+data.length());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        sensorManager.addExpressionData(id,data);
    }

    private void unpackExpressionDataAsFloat(DataMap dataMap){
        String id = dataMap.getString(DataMapKeys.EXPRESSION_ID);
        float data = dataMap.getFloat(DataMapKeys.VALUES);

        long currentTime = System.currentTimeMillis();
        TimestampedValue[] timestampedValues = new TimestampedValue[1];
        timestampedValues[0] = new TimestampedValue(data, currentTime);

        Result result = new Result(timestampedValues, currentTime);

        try {
            sensorManager.addExpressionData(id,Converter.objectToString(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

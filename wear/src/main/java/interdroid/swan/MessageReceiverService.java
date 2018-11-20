package interdroid.swan;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;
import java.util.List;

import interdroid.swancore.shared.ClientPaths;
import interdroid.swancore.shared.DataMapKeys;
import interdroid.swancore.shared.SensorConstants;

import static interdroid.swan.sensors.TestActuatorSensor.TEST_ACTUATOR_SENSOR;
import static interdroid.swancore.swanmain.ActuatorManager.SENSOR_ACTUATOR_INTERCEPTOR;

public class MessageReceiverService extends WearableListenerService {
    private static final String TAG = "Wear/MessageReceiver";

    private DeviceClient deviceClient;
    PowerManager.WakeLock wakeLock;
    //public static long testCounter =0;

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                TAG);
        wakeLock.acquire();

        //This timer runs for 30 seconds and the testCounter resets every second. NOTE: delete after test
        /*new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "Throughput Send Wear = "+testCounter);
                testCounter=0;

            }

            public void onFinish() {
                testCounter = 0;
                Log.d(TAG, "Count down finished");
            }
        }.start();*/


        deviceClient = DeviceClient.getInstance(this);
    }

    @Override
    public void onDestroy(){
        if(wakeLock!=null) {
            wakeLock.release();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        //super.onDataChanged(dataEvents);


        final List<DataEvent> events = FreezableUtils
                .freezeIterable(dataEvents);


        for (DataEvent dataEvent : events) {

            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();

                if(path.startsWith(ClientPaths.REGISTER_EXPRESSION)
                        || path.startsWith(ClientPaths.UNREGISTER_EXPRESSION) ){
                    Intent intent = new Intent(this, SensorService.class);
                    startService(intent);

                    do {
                        SystemClock.sleep(200);
                    } while (!isMyServiceRunning(SensorService.class));

                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    String id = dataMap.getString(DataMapKeys.EXPRESSION_ID);
                    String expression = dataMap.getString(DataMapKeys.EXPRESSION);
                    boolean wearActuation = dataMap.getBoolean(DataMapKeys.WEAR_ACTUATION);
                    boolean cloudActuation = dataMap.getBoolean(DataMapKeys.CLOUD_ACTUATION);
                    boolean phoneActuation = dataMap.getBoolean(DataMapKeys.PHONE_ACTUATION);
                    handleExpressions(id, expression,path, wearActuation, cloudActuation, phoneActuation);
                }

                if(path.startsWith(ClientPaths.REGISTER_ACTUATION_EXPRESSION)
                        || path.startsWith(ClientPaths.UNREGISTER_ACTUATION_EXPRESSION)
                        ){
                    Intent intent = new Intent(this, SensorService.class);
                    startService(intent);

                    do {
                        SystemClock.sleep(200);
                    } while (!isMyServiceRunning(SensorService.class));
                    Log.d(TAG, "onDataChanged called "+ path);
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    String id = dataMap.getString(DataMapKeys.EXPRESSION_ID);
                    String expression = dataMap.getString(DataMapKeys.EXPRESSION);
                    handleExpressions(id, expression,path, true,false, false);
                }

                if(path.startsWith(ClientPaths.ACTUATE) ){
                    //TODO: remove starting of sensor service for only actuation
                    Intent intent = new Intent(this, SensorService.class);
                    startService(intent);

                    do {
                        SystemClock.sleep(200);
                    } while (!isMyServiceRunning(SensorService.class));
                    Log.d(TAG, "onDataChanged called "+ path);
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    String id = dataMap.getString(DataMapKeys.EXPRESSION_ID);
                    String data = dataMap.getString(DataMapKeys.VALUES);
                    //TODO: if needed send as float
                    //float data = dataMap.getFloat(DataMapKeys.VALUES);
                    //handleActuationAsFloat(id, data,path);
                    handleActuation(id, data,path);
                }

                if(path.startsWith(ClientPaths.START_MEASUREMENT)){
                    Intent intent = new Intent(this, SensorService.class);
                    startService(intent);

                    do {
                        SystemClock.sleep(200);
                    } while (!isMyServiceRunning(SensorService.class));

                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();

                    int sensorId = dataMap.getInt(SensorConstants.SENSOR_ID);
                    int accuracy =  dataMap.getInt(SensorConstants.ACCURACY);

                    addSensor(sensorId,accuracy);
                }

                if(path.startsWith(ClientPaths.STOP_MEASUREMENT)){
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();

                    int sensorId = dataMap.getInt(SensorConstants.SENSOR_ID);

                    removeSensor(sensorId);
                }


            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Received message: " + messageEvent.getPath());

        if(messageEvent.getPath().equals(ClientPaths.START_MEASUREMENT)
                || messageEvent.getPath().equals(ClientPaths.STOP_MEASUREMENT)) {
            byte[] data = messageEvent.getData();
            ByteBuffer bb = ByteBuffer.wrap(data);

            int sensorId = bb.getInt();
            int accuracy = bb.getInt();

            if (sensorId == 0)
                Log.w(TAG, "Request to start an unknown sensor");

            if (messageEvent.getPath().compareTo(ClientPaths.START_MEASUREMENT) == 0) {

                Intent intent = new Intent(this, SensorService.class);
                intent.putExtra(SensorConstants.SENSOR_ID, sensorId);
                intent.putExtra(SensorConstants.ACCURACY, accuracy);
                startService(intent);

                do {
                    SystemClock.sleep(200);
                } while (!isMyServiceRunning(SensorService.class));

                addSensor(sensorId, accuracy);

            }

            if (messageEvent.getPath().compareTo(ClientPaths.STOP_MEASUREMENT) == 0) {
                removeSensor(sensorId);
            }
        } else {
            //String expression = new String(messageEvent.getData());

            Intent intent = new Intent(this, SensorService.class);
            startService(intent);

            do {
                SystemClock.sleep(200);
            } while (!isMyServiceRunning(SensorService.class));

            ByteBuffer bb = ByteBuffer.wrap(messageEvent.getData());
            int exprSize = bb.getInt();
            int idSize = bb.getInt();

            byte expressionBytes[] = new byte[exprSize];
            byte idBytes[] = new byte[idSize];

            bb.get(expressionBytes);
            bb.get(idBytes);

            handleExpressions(new String(idBytes), new String(expressionBytes), messageEvent.getPath(),  false, false, false);

            Log.d(TAG, "Got expression ++++++++++" + new String(expressionBytes) + " " +new String(idBytes));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void addSensor(int sensorId, int accuracy) {
        Intent i = new Intent(WearConstants.BROADCAST_ADD_SENSOR);
        i.putExtra(SensorConstants.SENSOR_ID, sensorId);
        i.putExtra(SensorConstants.ACCURACY, accuracy);
        getApplicationContext().sendBroadcast(i);
    }

    private void removeSensor(int sensorId) {
        Intent i = new Intent(WearConstants.BROADCAST_REMOVE_SENSOR);
        i.putExtra(SensorConstants.SENSOR_ID, sensorId);
        getApplicationContext().sendBroadcast(i);
    }

    private void handleExpressions(String id, String expression, String broadcastType, boolean wearActuation, boolean cloudActuation, boolean phoneActuation){
        Intent i =null;

        if(broadcastType.equals(ClientPaths.REGISTER_EXPRESSION))
            i = new Intent(WearConstants.BROADCAST_REGISTER_EXPR);
        else if (broadcastType.equals(ClientPaths.UNREGISTER_EXPRESSION))
            i = new Intent(WearConstants.BROADCAST_UNREGISTER_EXPR);
        else if (broadcastType.equals(ClientPaths.REGISTER_ACTUATION_EXPRESSION))
            i = new Intent(WearConstants.BROADCAST_REGISTER_ACTUATION_EXPR);
        else if (broadcastType.equals(ClientPaths.UNREGISTER_ACTUATION_EXPRESSION))
            i = new Intent(WearConstants.BROADCAST_UNREGISTER_ACTUATION_EXPR);
        //else if (broadcastType.equals(ClientPaths.ACTUATE))
         //   i = new Intent(WearConstants.BROADCAST_ACTUATE);
        i.putExtra(DataMapKeys.EXPRESSION_ID, id);
        i.putExtra(DataMapKeys.EXPRESSION, expression);
        i.putExtra(DataMapKeys.WEAR_ACTUATION, wearActuation);
        i.putExtra(DataMapKeys.CLOUD_ACTUATION, cloudActuation);
        i.putExtra(DataMapKeys.PHONE_ACTUATION, phoneActuation);
        getApplicationContext().sendBroadcast(i);
    }

    private void handleActuation(String id, String values, String broadcastType){
        Intent i =null;

       if (broadcastType.equals(ClientPaths.ACTUATE)){
           if(TEST_ACTUATOR_SENSOR){
               i = new Intent(SENSOR_ACTUATOR_INTERCEPTOR);
           }else {
               i = new Intent(WearConstants.BROADCAST_ACTUATE);
           }
        i.putExtra(DataMapKeys.EXPRESSION_ID, id);
        i.putExtra(DataMapKeys.VALUES, values);

        getApplicationContext().sendBroadcast(i);
       }
    }

    private void handleActuationAsFloat(String id, float values, String broadcastType){
        Intent i =null;

        if (broadcastType.equals(ClientPaths.ACTUATE))
            i = new Intent(WearConstants.BROADCAST_ACTUATE);
        i.putExtra(DataMapKeys.EXPRESSION_ID, id);
        i.putExtra(DataMapKeys.VALUES, values);

        getApplicationContext().sendBroadcast(i);
    }



}

package wear.interdroid.swan;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;

import wear.interdroid.swan.SensorService;
import interdroid.swan.sensordashboard.shared.ClientPaths;
import interdroid.swan.sensordashboard.shared.DataMapKeys;
import interdroid.swan.sensordashboard.shared.SensorConstants;

public class MessageReceiverService extends WearableListenerService {
    private static final String TAG = "Wear/MessageReceiver";

    private DeviceClient deviceClient;


    @Override
    public void onCreate() {
        super.onCreate();
        deviceClient = DeviceClient.getInstance(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();

                if (path.startsWith("/filter")) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    int filterById = dataMap.getInt(DataMapKeys.FILTER);
                    deviceClient.setSensorFilter(filterById);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Received message: " + messageEvent.getPath());

        if(messageEvent.getPath().equals(ClientPaths.START_MEASUREMENT)
                || messageEvent.getPath().equals(ClientPaths.START_MEASUREMENT)) {
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

                while (!isMyServiceRunning(SensorService.class)) {
                    SystemClock.sleep(100);
                }

                addSensor(sensorId, accuracy);

            }

            if (messageEvent.getPath().compareTo(ClientPaths.STOP_MEASUREMENT) == 0) {
                removeSensor(sensorId);
            }
        } else {
            String expression = new String(messageEvent.getData());
            Log.d(TAG, "Got expression ++++++++++" + expression);
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
}

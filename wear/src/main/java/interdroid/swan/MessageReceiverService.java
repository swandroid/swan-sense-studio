package interdroid.swan;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import interdroid.swan.sensordashboard.shared.ClientPaths;
import interdroid.swan.sensordashboard.shared.DataMapKeys;
import interdroid.swan.sensordashboard.shared.SensorConstants;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

public class MessageReceiverService extends WearableListenerService {
    private static final String TAG = "Wear/MessageReceiverService";

    private DeviceClient deviceClient;

    private static int activeSensors = 0;

    ReentrantLock lock = new ReentrantLock();


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

        byte[] data = messageEvent.getData();
        ByteBuffer bb = ByteBuffer.wrap(data);

        int sensorId = bb.getInt();
        int accuracy = bb.getInt();

        if(sensorId == 0)
            Log.w(TAG, "Request to start an unknown sensor");

        if (messageEvent.getPath().equals(ClientPaths.START_MEASUREMENT)) {

            lock.lock();
            if(activeSensors == 0) {
                Intent intent = new Intent(this, SensorService.class);
                intent.putExtra(SensorConstants.SENSOR_ID, sensorId);
                intent.putExtra(SensorConstants.ACCURACY, accuracy);
                startService(intent);
            } else {
                addSensor(sensorId, accuracy);
            }

            lock.unlock();
        }

        if (messageEvent.getPath().equals(ClientPaths.STOP_MEASUREMENT)) {
            stopService(new Intent(this, SensorService.class));
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

    private void addSensor(int sensorId, int accuracy){
        Intent i = new Intent(WearConstants.BROADCAST_ADD_SENSOR);
        i.putExtra(SensorConstants.SENSOR_ID, sensorId);
        i.putExtra(SensorConstants.ACCURACY, accuracy);
        getApplicationContext().sendBroadcast(i);
    }

    private void remo
}

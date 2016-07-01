package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vladimir on 6/21/16.
 */
public class BTSwanDevice {

    private BluetoothDevice btDevice;
    private BTPendingItem pendingItem;
    private Map<String, String> registeredExpressions = new HashMap<String, String>();
    private BTConnection connection;
    private BTClientWorker clientWorker;
    private BTServerWorker serverWorker;

    public void sendData(HashMap<String, String> dataMap) {}

    public void receiveData(HashMap<String, String> dataMap) {}

    public BTSwanDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    public String getName() {
        return btDevice.getName();
    }

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public BTPendingItem getPendingItem() {
        return pendingItem;
    }

    public void setPendingItem(BTPendingItem pendingItem) {
        this.pendingItem = pendingItem;
    }

    public void registerExpression(String id, String expression) {
        registeredExpressions.put(id, expression);
    }

    public void unregisterExpression(String id) {
        registeredExpressions.remove(id);
    }

    public Map<String, String> getRegisteredExpressions() {
        return registeredExpressions;
    }

    @Override
    public String toString() {
        return btDevice.getName();
    }
}

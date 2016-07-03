package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vladimir on 6/21/16.
 */
public class BTSwanDevice implements BTConnectionHandler {

    private BluetoothDevice btDevice;
    private BTPendingItem pendingItem;
    private Map<String, String> registeredExpressions = new HashMap<String, String>();

    private BTConnection btConnection;
    private BTClientWorker clientWorker;
    private BTServerWorker serverWorker;

    public BTSwanDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    public void onReceive(HashMap<String, String> dataMap) {
        //TODO
    }

    @Override
    public void onDisconnected(Exception e, boolean crashed) {
        //TODO
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

    public void setBtConnection(BTConnection btConnection) {
        this.btConnection = btConnection;
    }

    public BTConnection getBtConnection() {
        return btConnection;
    }

    public boolean isConnectedToRemote() {
        return btConnection != null && btConnection.isConnected();
    }

    @Override
    public String toString() {
        return btDevice.getName();
    }
}

package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.Map;

import interdroid.swan.engine.EvaluationEngineService;

/**
 * Created by vladimir on 6/21/16.
 */
public class BTSwanDevice implements BTConnectionHandler {

    private BluetoothDevice btDevice;
    private BTManager btManager;
    private BTPendingItem pendingItem;
    private Map<String, String> registeredExpressions = new HashMap<String, String>();

    private BTConnection btConnection;

    private BTClientWorker clientWorker;
    private BTServerWorker serverWorker;

    public BTSwanDevice(BluetoothDevice btDevice, BTManager btManager) {
        this.btDevice = btDevice;
        this.btManager = btManager;
    }

    @Override
    public void onReceive(HashMap<String, String> dataMap) throws Exception {
        String exprAction = dataMap.get("action");

        // we forward the data to the corresponding worker
        if (exprAction.equals(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE)) {
            clientWorker.onReceive(dataMap);
        } else {
            // if there is no server worker to handle the message, we create one
            if(serverWorker == null) {
                serverWorker = new BTServerWorker(btManager, this, btConnection);
                btManager.addServerWorker(serverWorker);
            }
            serverWorker.onReceive(dataMap);
        }
    }

    @Override
    public void onDisconnected(Exception e) {
        if(clientWorker != null) {
            clientWorker.onDisconnected(e);
        }
        if(serverWorker != null) {
            serverWorker.onDisconnected(e);
        }
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

    public void setServerWorker(BTServerWorker serverWorker) {
        this.serverWorker = serverWorker;
    }

    public void setClientWorker(BTClientWorker clientWorker) {
        this.clientWorker = clientWorker;
    }

    @Override
    public String toString() {
        return btDevice.getName();
    }
}

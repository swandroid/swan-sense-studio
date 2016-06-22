package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by vladimir on 6/21/16.
 */
public class BTSwanDevice {

    private BluetoothDevice btDevice;
    private BTPendingItem pendingItem;

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

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

    @Override
    public String toString() {
        return btDevice.getName();
    }
}

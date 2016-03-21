package interdroid.swan.crossdevice.swanplus;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.net.wifi.p2p.WifiP2pDevice;

import java.io.ObjectOutputStream;
import java.net.InetAddress;

/**
 * Created by vladimir on 11/3/15.
 */
public class SwanUser {

    String username;
    String regId;
    WifiP2pDevice device;
    BluetoothDevice btDevice;
    BluetoothSocket btSocket;
    InetAddress ip;
    boolean connectable = true;

    ObjectOutputStream oos;

    public SwanUser(String username, String regId, WifiP2pDevice device) {
        this.username = username;
        this.regId = regId;
        this.device = device;
    }

    public SwanUser(String username, String regId, BluetoothDevice btDevice) {
        this.username = username;
        this.regId = regId;
        this.btDevice = btDevice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;

        if(!isConnectable()) {
            setConnectable(true);
        }
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwanUser swanUser = (SwanUser) o;

        return username.equals(swanUser.username);

    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }

    public boolean isConnectable() {
        return connectable;
    }

    public void setConnectable(boolean connectable) {
        this.connectable = connectable;
    }
}

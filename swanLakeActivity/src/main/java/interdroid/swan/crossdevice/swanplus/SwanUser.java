package interdroid.swan.crossdevice.swanplus;

import android.net.wifi.p2p.WifiP2pDevice;

import java.net.InetAddress;

/**
 * Created by vladimir on 11/3/15.
 */
public class SwanUser {

    String username;
    String regId;
    WifiP2pDevice device;
    InetAddress ip;

    public SwanUser(String username, String regId, WifiP2pDevice device) {
        this.username = username;
        this.regId = regId;
        this.device = device;
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

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
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
}

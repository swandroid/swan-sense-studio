package interdroid.swan.crossdevice.bluetooth;

import java.util.HashMap;

/**
 * Created by vladzy on 7/2/2016.
 */
public interface BTConnectionHandler {

    public void onReceive(HashMap<String, String> dataMap) throws Exception;

    //TODO not sure if we need crashed
    public void onDisconnected(Exception e);
}

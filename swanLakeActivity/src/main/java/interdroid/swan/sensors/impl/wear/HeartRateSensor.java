package interdroid.swan.sensors.impl.wear;

import android.os.Bundle;

import java.io.IOException;

import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Created by slavik on 2/22/16.
 */
public class HeartRateSensor extends AbstractSwanSensor {

    public static final String TAG = "HeartRateSensor";
    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void register(String id, String valuePath, Bundle configuration) throws IOException {

    }

    @Override
    public void unregister(String id) {

    }

    @Override
    public String[] getValuePaths() {
        return new String[0];
    }

    @Override
    public void onConnected() {

    }
}

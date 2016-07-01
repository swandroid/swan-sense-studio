package interdroid.swan.sensors.impl.wear.shared.data;

import java.io.Serializable;

public class SensorDataPoint implements Serializable {
    private long timestamp;
    private float[] values;
    private int accuracy;

    public SensorDataPoint(long timestamp, int accuracy, float[] values) {
        this.timestamp = timestamp;
        this.accuracy = accuracy;
        this.values = values.clone();
    }

    public float[] getValues() {
        return values;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getAccuracy() {
        return accuracy;
    }
}

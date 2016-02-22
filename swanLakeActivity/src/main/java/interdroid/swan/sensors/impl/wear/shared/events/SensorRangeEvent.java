package interdroid.swan.sensors.impl.wear.shared.events;

import interdroid.swan.sensors.impl.wear.shared.data.Sensor;

public class SensorRangeEvent {
    private Sensor sensor;

    public SensorRangeEvent(Sensor sensor) {
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }
}

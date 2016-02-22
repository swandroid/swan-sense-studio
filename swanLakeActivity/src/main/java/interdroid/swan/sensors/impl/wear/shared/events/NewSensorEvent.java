package interdroid.swan.sensors.impl.wear.shared.events;

import interdroid.swan.sensors.impl.wear.shared.data.Sensor;

public class NewSensorEvent {
    private Sensor sensor;

    public NewSensorEvent(Sensor sensor) {
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }
}

package interdroid.swan.actuator;

import android.content.Context;

/**
 * Abstract class for actuators.
 */
public abstract class Actuator {

    protected Context context;

    public Actuator(Context context) {
        this.context = context;
    }

    /**
     * Perform the actuation.
     */
    public abstract void performAction();
}

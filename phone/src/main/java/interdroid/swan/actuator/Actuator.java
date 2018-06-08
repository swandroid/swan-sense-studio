package interdroid.swan.actuator;

import android.content.Context;

public abstract class Actuator {

    protected Context context;

    public Actuator(Context context) {
        this.context = context;
    }

    public abstract void performAction();
}

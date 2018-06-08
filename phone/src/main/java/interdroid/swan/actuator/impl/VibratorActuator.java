package interdroid.swan.actuator.impl;

import android.content.Context;
import android.os.Vibrator;

import interdroid.swan.actuator.Actuator;

public class VibratorActuator extends Actuator {

    public static final String ENTITY = "vibrator";

    private Vibrator vibrator;

    private long duration;

    public VibratorActuator(Context context, long duration) {
        super(context);
        this.duration = duration;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void performAction() {
        vibrator.vibrate(duration);
    }
}

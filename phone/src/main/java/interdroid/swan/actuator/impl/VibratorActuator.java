package interdroid.swan.actuator.impl;

import android.content.Context;
import android.os.Vibrator;

import interdroid.swan.actuator.Actuator;

/**
 * THe actuator that vibrates the phone.
 */
public class VibratorActuator extends Actuator {

    public static final String ENTITY = "vibrator";

    /**
     * The vibrator object to perform the vibration on.
     */
    private Vibrator vibrator;

    /**
     * How long the vibration should last in milliseconds.
     */
    private long duration;

    /**
     * Create a {@link VibratorActuator} object
     *
     * @param context  the context
     * @param duration how long the vibration should last in milliseconds
     */
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

package interdroid.swan.actuator.impl;

import android.content.Context;
import android.os.Vibrator;

import interdroid.swan.actuator.Actuator;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.SensorValueExpression;

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
     * @param vibrator the system vibrator service
     * @param duration how long the vibration should last in milliseconds
     */
    public VibratorActuator(Vibrator vibrator, long duration) {
        this.duration = duration;
        this.vibrator = vibrator;
    }

    @Override
    public void performAction() {
        vibrator.vibrate(duration);
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, Expression expression) {
            SensorValueExpression sve = (SensorValueExpression) expression;
            long duration = Long.parseLong(sve.getConfiguration().getString("duration"));
            return new VibratorActuator((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE), duration);
        }
    }
}

package interdroid.swan.actuator.impl;

import android.content.Context;
import android.os.Vibrator;

import interdroid.swan.actuator.Actuator;
//import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * The actuator that vibrates the phone.
 */
public class VibratorActuator extends Actuator {

    public static final String ENTITY = "vibrator";

    private static final String[] KEYS = new String[]{"duration"};

    private static final String[] PATHS = new String[]{"vibrate"};

    /**
     * The vibrator object to perform the vibration on.
     */
    private final Vibrator vibrator;

    /**
     * How long the vibration should last in milliseconds.
     */
    private final long duration;

    /**
     * Create a {@link VibratorActuator} object
     *
     * @param vibrator the system vibrator service
     * @param duration how long the vibration should last in milliseconds
     */
    private VibratorActuator(Vibrator vibrator, long duration) {
        this.duration = duration;
        this.vibrator = vibrator;
    }

    @Override
    public void performAction(Context context, TimestampedValue[] newValues) {
        vibrator.vibrate(duration);
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            long duration = Long.parseLong(expression.getConfiguration().getString("duration"));
            return new VibratorActuator((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE), duration);
        }
    }

  /*  public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[]{"500"};
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            return ENTITY;
        }
    } */
}

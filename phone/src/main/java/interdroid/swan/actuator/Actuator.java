package interdroid.swan.actuator;

import android.content.Context;

import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * Abstract class for actuators.
 */
public abstract class Actuator {

    /**
     * Perform the actuation.
     *
     * @param context   the context
     * @param newValues the values of the sensor, null if the actuator is not associated to a
     *                  {@link SensorValueExpression}
     */
    public abstract void performAction(Context context, TimestampedValue[] newValues) throws Exception;

    /**
     * Interface for actuator factories.
     */
    public static interface Factory {

        /**
         * Create an actuator from an expression.
         *
         * @param context    the context for system service dependencies
         * @param expression the expression to convert into an actuator
         * @return the actuator
         */
        Actuator create(Context context, SensorValueExpression expression);
    }
}

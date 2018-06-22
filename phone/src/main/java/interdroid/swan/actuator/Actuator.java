package interdroid.swan.actuator;

import android.content.Context;

import interdroid.swancore.swansong.Expression;

/**
 * Abstract class for actuators.
 */
public abstract class Actuator {

    /**
     * Perform the actuation.
     */
    public abstract void performAction();

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
        Actuator create(Context context, Expression expression);
    }
}

package interdroid.swan.actuator.impl;

import android.content.Context;
import android.util.Log;

import interdroid.swan.actuator.Actuator;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.SensorValueExpression;

/**
 * Simple {@link Actuator} that prints out a log message.
 */
public class LogActuator extends Actuator {

    private static final String TAG = LogActuator.class.getSimpleName();

    public static final String ENTITY = "logger";

    private int priority;

    private String tag;

    private String message;

    /**
     * Create a {@link LogActuator} object.
     *
     * @param priority the priority if the log message
     * @param tag      the log tag to use
     * @param message  the log message
     */
    public LogActuator(int priority, String tag, String message) {
        this.priority = priority;
        this.tag = tag;
        this.message = message;
    }

    @Override
    public void performAction() {
        if (tag != null) {
            Log.println(priority, tag, message);
        } else {
            Log.println(priority, TAG, message);
        }
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, Expression expression) {
            SensorValueExpression sve = (SensorValueExpression) expression;
            int priority = Integer.parseInt(sve.getConfiguration().getString("priority"));
            String tag = sve.getConfiguration().getString("tag");
            String message = sve.getConfiguration().getString("message");
            return new LogActuator(priority, tag, message);
        }
    }
}

package interdroid.swan.actuator.impl;

import android.content.Context;
import android.util.Log;

import interdroid.swan.actuator.Actuator;
//import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * Simple {@link Actuator} that prints out a log message.
 */
public class LogActuator extends Actuator {

    private static final String TAG = LogActuator.class.getSimpleName();

    public static final String ENTITY = "logger";

    private static final String[] KEYS = new String[]{
            "tag", "message", "priority"
    };

    private static final String[] PATHS = new String[]{"log"};

    private final int priority;

    private final String tag;

    private final String message;

    /**
     * Create a {@link LogActuator} object.
     *
     * @param priority the priority if the log message
     * @param tag      the log tag to use
     * @param message  the log message
     */
    private LogActuator(int priority, String tag, String message) {
        this.priority = priority;
        this.tag = tag;
        this.message = message;
    }

    @Override
    public void performAction(Context context, TimestampedValue[] newValues) {
        if (tag != null) {
            Log.println(priority, tag, message);
        } else {
            Log.println(priority, TAG, message);
        }
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            int priority = Integer.parseInt(expression.getConfiguration().getString("priority"));
            String tag = expression.getConfiguration().getString("tag");
            String message = expression.getConfiguration().getString("message");
            return new LogActuator(priority, tag, message);
        }
    }

 /*   public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[]{
                    "MainActivity",
                    "log message",
                    "3"
            };
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            return ENTITY;
        }
    }*/
}
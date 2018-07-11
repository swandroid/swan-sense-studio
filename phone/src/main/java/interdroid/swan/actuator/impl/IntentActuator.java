package interdroid.swan.actuator.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import interdroid.swan.actuator.Actuator;
import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * {@link Actuator} that starts an activity/service or sends a broadcast based on the specified
 * intent. If the expression is a {@link SensorValueExpression}, the new values are attached
 * to the intent with the {@link ExpressionManager#EXTRA_NEW_VALUES} key.
 */
public class IntentActuator extends Actuator {

    public static final String ENTITY = "intent";

    private static final String PATH_ACTIVITY = "activity";
    private static final String PATH_SERVICE = "service";
    private static final String PATH_BROADCAST = "broadcast";

    private static final String PARAM_PACKAGE = "package";
    private static final String PARAM_CLASS = "class";
    private static final String PARAM_DATA = "data";
    private static final String PARAM_EXTRAS = "extras";
    private static final String PARAM_ACTION = "action";

    private static final String[] KEYS = new String[]{
            PARAM_PACKAGE,
            PARAM_CLASS,
            PARAM_DATA,
            PARAM_EXTRAS,
            PARAM_ACTION
    };

    private static final String[] PATHS = new String[]{PATH_ACTIVITY, PATH_SERVICE, PATH_BROADCAST};

    private final Intent baseIntent;

    private final String path;

    /**
     * Create an {@link IntentActuator} object.
     *
     * @param path     the value path of the actuator expression
     * @param _package the package name of the component
     * @param _class   the fully qualified class name of the component
     * @param action   the intent action
     * @param data     the intent data
     * @param extras   extras bundled to the intent
     */
    private IntentActuator(String path, String _package, String _class, @Nullable String action,
                           @Nullable Uri data, @Nullable Bundle extras) {
        this.path = path;

        baseIntent = new Intent();

        ComponentName componentName = new ComponentName(_package, _class);
        baseIntent.setComponent(componentName);

        if (action != null) {
            baseIntent.setAction(action);
        }

        if (data != null) {
            baseIntent.setData(data);
        }

        if (extras != null) {
            baseIntent.putExtras(extras);
        }
    }

    @Override
    public void performAction(Context context, TimestampedValue[] newValues) {
        Intent intent = new Intent(baseIntent);
        if (newValues != null) {
            intent.putExtra(ExpressionManager.EXTRA_NEW_VALUES, newValues);
        }
        switch (path) {
            case PATH_ACTIVITY:
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case PATH_SERVICE:
                context.startService(intent);
                break;
            case PATH_BROADCAST:
                context.sendBroadcast(intent);
                break;
        }
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            String path = expression.getValuePath();

            Bundle config = expression.getConfiguration();

            String _package = config.getString(PARAM_PACKAGE);
            String _class = config.getString(PARAM_CLASS);

            String action = config.getString(PARAM_ACTION);

            Uri data = null;
            if (config.containsKey(PARAM_DATA)) {
                data = Uri.parse(config.getString(PARAM_DATA));
            }

            String extraStr = config.getString(PARAM_EXTRAS);

            Bundle extras = null;
            if (extraStr != null) {
                extras = new Bundle();
                for (String s : extraStr.split(",")) {
                    String[] param = s.split(":", 2);

                    if (param.length == 2) {
                        extras.putString(param[0], param[1]);
                    }
                }
            }

            return new IntentActuator(path, _package, _class, action, data, extras);
        }
    }

    public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[KEYS.length];
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            return ENTITY;
        }
    }
}

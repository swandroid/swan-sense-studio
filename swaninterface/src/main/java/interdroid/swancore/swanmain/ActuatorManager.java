package interdroid.swancore.swanmain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TriStateExpression;

public class ActuatorManager {

    private static final String ACTUATOR_SEPARATOR = "THEN";

    public static final String ACTION_REGISTER = "interdroid.swan.actuator.REGISTER";
    public static final String ACTION_UNREGISTER = "interdroid.swan.actuator.UNREGISTER";

    private static final String ACTION_NEW_TRISTATE = "interdroid.swan.actuator.TRISTATE_INTERCEPTOR";

    public static final String EXTRA_EXPRESSION_ID = "expressionId";
    public static final String EXTRA_EXPRESSION = "expression";
    public static final String EXTRA_FORWARD = "forward";

    public static void registerActuator(Context context, String id, TriStateExpression expression, Expression action, ExpressionListener listener) throws SwanException {
        Intent newTriState = new Intent(ExpressionManager.ACTION_NEW_TRISTATE);
        newTriState.setData(Uri.parse("swan://" + context.getPackageName() + "#" + id));

        Intent intercept = new Intent(ACTION_NEW_TRISTATE);
        intercept.putExtra(EXTRA_FORWARD, newTriState);
        intercept.putExtra(EXTRA_EXPRESSION_ID, id);

        ExpressionManager.registerTriStateExpression(context, id, expression, intercept, intercept, intercept);

        sendRegister(context, id, action);
    }

    public static void registerActuator(Context context, String id, String actuatorExpression, ExpressionListener listener) throws SwanException {
        String[] split = actuatorExpression.split(ACTUATOR_SEPARATOR);

        if (split.length != 2) {
            throw new SwanException("Actuator expression must contain only one " +
                    ACTUATOR_SEPARATOR + " separator");
        }

        Expression expression;
        try {
            expression = ExpressionFactory.parse(split[0]);
        } catch (ExpressionParseException e) {
            throw new SwanException(e);
        }

        if (expression == null) {
            throw new SwanException("null expression");
        }

        if (!(expression instanceof TriStateExpression)) {
            // TODO: 2018-06-05 check if this is true
            throw new IllegalArgumentException("Only tristate expressions are supported");
        }

        Expression action;
        try {
            action = ExpressionFactory.parse(split[1]);
        } catch (ExpressionParseException e) {
            throw new SwanException(e);
        }

        registerActuator(context, id, (TriStateExpression) expression, action, listener);
    }

    public static void unregisterActuator(Context context, String id) {
        ExpressionManager.unregisterExpression(context, id);
        sendUnregister(context, id);
    }

    private static void sendRegister(Context context, String id, Expression expression) {
        Intent intent = new Intent(ACTION_REGISTER);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(EXTRA_EXPRESSION_ID, id);
        intent.putExtra(EXTRA_EXPRESSION, expression.toParseString());
        context.sendBroadcast(intent);
    }

    private static void sendUnregister(Context context, String id) {
        Intent intent = new Intent(ACTION_UNREGISTER);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(EXTRA_EXPRESSION_ID, id);
        context.sendBroadcast(intent);
    }
}

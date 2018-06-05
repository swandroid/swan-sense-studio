package interdroid.swancore.swanmain;

import android.content.Context;
import android.content.Intent;

import interdroid.swancore.swansong.ComparisonExpression;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;

public class ActuatorManager {

    private static final String ACTUATOR_SEPARATOR = "THEN";

    private static final String ACTION_REGISTER = "interdroid.swan.actuator.REGISTER";
    private static final String ACTION_UNREGISTER = "interdroid.swan.actuator.UNREGISTER";

    public static void registerActuator(Context context, String id, ComparisonExpression expression, Expression action, ExpressionListener listener) throws SwanException {
        ExpressionManager.registerExpression(context, id, expression, listener);
        sendRegister(context, id, expression);
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

        if (!(expression instanceof ComparisonExpression)) {
            // TODO: 2018-06-05 check if this is true
            throw new IllegalArgumentException("Only comparison expressions are supported");
        }

        Expression action;
        try {
            action = ExpressionFactory.parse(split[1]);
        } catch (ExpressionParseException e) {
            throw new SwanException(e);
        }

        registerActuator(context, id, (ComparisonExpression) expression, action, listener);
    }

    public static void unregisterActuator(Context context, String id) {
        ExpressionManager.unregisterExpression(context, id);
        sendUnregister(context, id);
    }

    private static void sendRegister(Context context, String id, Expression expression) {
        Intent intent = new Intent(ACTION_REGISTER);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("expressionId", id);
        intent.putExtra("expression", expression.toParseString());
        context.sendBroadcast(intent);
    }

    private static void sendUnregister(Context context, String id) {
        Intent intent = new Intent(ACTION_UNREGISTER);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("expressionId", id);
        context.sendBroadcast(intent);
    }
}

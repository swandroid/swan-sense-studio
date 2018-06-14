package interdroid.swancore.swanmain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TriStateExpression;
import interdroid.swancore.swansong.ValueExpression;

public class ActuatorManager {

    private static final String TAG = ActuatorManager.class.getSimpleName();

    private static final String ACTUATOR_SEPARATOR = "THEN";

    public static final String ACTION_REGISTER = "interdroid.swan.actuator.REGISTER";
    public static final String ACTION_UNREGISTER = "interdroid.swan.actuator.UNREGISTER";

    private static final String ACTUATOR_INTERCEPTOR = "interdroid.swan.actuator.ACTUATOR_INTERCEPTOR";

    public static final String EXTRA_EXPRESSION_ID = "expressionId";
    public static final String EXTRA_EXPRESSION = "expression";
    public static final String EXTRA_FORWARD_TRUE = "forward";
    public static final String EXTRA_FORWARD_FALSE = "forward";
    public static final String EXTRA_FORWARD_UNDEFINED = "forward";
    public static final String EXTRA_FORWARD_NEW_VALUES = "new_values";

    public static void registerActuator(Context context, String id, Expression expression,
                                        Expression action, ExpressionListener listener) throws SwanException {
        ExpressionManager.addExpressionListener(context, id, listener);

        Intent newTriState = new Intent(ExpressionManager.ACTION_NEW_TRISTATE);
        newTriState.setData(Uri.parse("swan://" + context.getPackageName() + "#" + id));
        Intent newValues = new Intent(ExpressionManager.ACTION_NEW_VALUES);
        newValues.setData(Uri.parse("swan://" + context.getPackageName() + "#" + id));

        registerActuator(context, id, expression, action, newTriState, newTriState, newTriState, newValues);
    }

    private static void registerActuator(Context context, String id, Expression expression,
                                         Expression action, Intent onTrue, Intent onFalse,
                                         Intent onUndefined, Intent onNewValues) {
        Intent intercept = new Intent(ACTUATOR_INTERCEPTOR);
        intercept.putExtra(EXTRA_FORWARD_TRUE, onTrue);
        intercept.putExtra(EXTRA_FORWARD_FALSE, onFalse);
        intercept.putExtra(EXTRA_FORWARD_UNDEFINED, onUndefined);
        intercept.putExtra(EXTRA_FORWARD_NEW_VALUES, onNewValues);
        intercept.putExtra(EXTRA_EXPRESSION_ID, id);

        ExpressionManager.registerExpression(context, id, expression, intercept, intercept, intercept, intercept);

        sendRegister(context, id, action);
    }

    public void registerTriStateActuator(Context context, String id, TriStateExpression expression,
                                         Expression action, Intent onTrue, Intent onFalse, Intent onUndefined) {
        registerActuator(context, id, expression, action, onTrue, onFalse, onUndefined, null);
    }

    public void registerValueActuator(Context context, String id, Expression action,
                                      ValueExpression expression, Intent onNewValues) {
        registerActuator(context, id, expression, action, null, null, null, onNewValues);
    }

    public static void registerActuator(Context context, String id, String actuatorExpression,
                                        ExpressionListener listener) throws SwanException {
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

        Expression action;
        try {
            action = ExpressionFactory.parse(split[1]);
        } catch (ExpressionParseException e) {
            throw new SwanException(e);
        }

        registerActuator(context, id, expression, action, listener);
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

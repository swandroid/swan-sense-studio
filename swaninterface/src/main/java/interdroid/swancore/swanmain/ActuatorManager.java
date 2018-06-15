package interdroid.swancore.swanmain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TriState;
import interdroid.swancore.swansong.TriStateExpression;
import interdroid.swancore.swansong.ValueExpression;

public class ActuatorManager {

    private static final String TAG = ActuatorManager.class.getSimpleName();

    /**
     * The string that separates and {@link Expression} from the actuator expression.
     */
    private static final String ACTUATOR_SEPARATOR = "THEN";

    /**
     * The action to register an actuator.
     */
    public static final String ACTION_REGISTER = "interdroid.swan.actuator.REGISTER";

    /**
     * The action to unregister an actuator.
     */
    public static final String ACTION_UNREGISTER = "interdroid.swan.actuator.UNREGISTER";

    /**
     * The action for intercepting new sensor state and value broadcasts.
     */
    private static final String ACTUATOR_INTERCEPTOR = "interdroid.swan.actuator.ACTUATOR_INTERCEPTOR";

    /**
     * The extra key for the id of the expression.
     */
    public static final String EXTRA_EXPRESSION_ID = "expressionId";

    /**
     * The extra key for the expression itself.
     */
    public static final String EXTRA_EXPRESSION = "expression";

    /**
     * The key for the {@link Intent} that should be fired on a {@link TriState#TRUE} value.
     */
    public static final String EXTRA_FORWARD_TRUE = "forward_true";

    /**
     * The key for the {@link Intent} that should be fired on a {@link TriState#FALSE} value.
     */
    public static final String EXTRA_FORWARD_FALSE = "forward_false";

    /**
     * The key for the {@link Intent} that should be fired on a {@link TriState#UNDEFINED} value.
     */
    public static final String EXTRA_FORWARD_UNDEFINED = "forward_undefined";

    /**
     * The key for the {@link Intent} that should be fired on new sensor values.
     */
    public static final String EXTRA_FORWARD_NEW_VALUES = "forward_new_values";

    /**
     * Registers an {@link Expression} for evaluation and an actuator to execute.
     *
     * @param context    the context, should be application context to avoid memory leaks
     * @param id         the user provided unique id of the expression. Should not
     *                   contain {@link Expression#SEPARATOR} or end with any of the
     *                   {@link Expression#RESERVED_SUFFIXES}.
     * @param expression the {@link Expression} that should be evaluated
     * @param action     the actuator expression
     * @param listener   a {@link ExpressionListener} that receives the evaluation
     *                   results. If this parameter is null, it is also possible to
     *                   listen for the results using a {@link BroadcastReceiver}.
     *                   Filter on datascheme "swan://<your.package.name>#<your.expression.id>" and
     *                   action {@link ExpressionManager#ACTION_NEW_VALUES} or
     *                   {@link ExpressionManager#ACTION_NEW_TRISTATE}.
     * @throws SwanException if id is null or invalid
     */
    public static void registerActuator(Context context, String id, Expression expression,
                                        Expression action, ExpressionListener listener) throws SwanException {
        ExpressionManager.addExpressionListener(context, id, listener);

        Intent newTriState = new Intent(ExpressionManager.ACTION_NEW_TRISTATE);
        newTriState.setData(Uri.parse("swan://" + context.getPackageName() + "#" + id));
        Intent newValues = new Intent(ExpressionManager.ACTION_NEW_VALUES);
        newValues.setData(Uri.parse("swan://" + context.getPackageName() + "#" + id));

        registerActuator(context, id, expression, action, newTriState, newTriState, newTriState, newValues);
    }

    /**
     * @param context     the context, should be application context to avoid memory leaks
     * @param id          the user provided unique id of the expression. Should not
     *                    contain {@link Expression#SEPARATOR} or end with any of the
     *                    {@link Expression#RESERVED_SUFFIXES}.
     * @param expression  the {@link Expression} that should be evaluated
     * @param action      the actuator expression
     * @param onTrue      Intent that should be fired when state changes to true. By
     *                    default the Intent is used to send a broadcast. Add
     *                    {@link ExpressionManager#EXTRA_INTENT_TYPE} with any of the values
     *                    {@link ExpressionManager#INTENT_TYPE_ACTIVITY}, {@link ExpressionManager#INTENT_TYPE_SERVICE} to
     *                    have Swan launch an activity or service.
     * @param onFalse     Intent that should be fired when state changes to false. By
     *                    default the Intent is used to send a broadcast. Add
     *                    {@link ExpressionManager#EXTRA_INTENT_TYPE} with any of the values
     *                    {@link ExpressionManager#INTENT_TYPE_ACTIVITY}, {@link ExpressionManager#INTENT_TYPE_SERVICE} to
     *                    have Swan launch an activity or service.
     * @param onUndefined Intent that should be fired when state changes to undefined.
     *                    By default the Intent is used to send a broadcast. Add
     *                    {@link ExpressionManager#EXTRA_INTENT_TYPE} with any of the values
     *                    {@link ExpressionManager#INTENT_TYPE_ACTIVITY}, {@link ExpressionManager#INTENT_TYPE_SERVICE} to
     *                    have Swan launch an activity or service.
     * @param onNewValues Intent that should be fired when new values are available. Add
     *                    {@link ExpressionManager#EXTRA_INTENT_TYPE} with any of the values
     *                    {@link ExpressionManager#INTENT_TYPE_ACTIVITY}, {@link ExpressionManager#INTENT_TYPE_SERVICE} to
     *                    have Swan launch an activity or service.
     */
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

    /**
     * @param context     the context, should be application context to avoid memory leaks
     * @param id          the user provided unique id of the expression. Should not
     *                    contain {@link Expression#SEPARATOR} or end with any of the
     *                    {@link Expression#RESERVED_SUFFIXES}.
     * @param expression  the {@link TriStateExpression} that should be evaluated
     * @param action      the actuator expression
     * @param onTrue      Intent that should be fired when state changes to true. By
     *                    default the Intent is used to send a broadcast. Add
     *                    {@link ExpressionManager#EXTRA_INTENT_TYPE} with any of the values
     *                    {@link ExpressionManager#INTENT_TYPE_ACTIVITY}, {@link ExpressionManager#INTENT_TYPE_SERVICE} to
     *                    have Swan launch an activity or service.
     * @param onFalse     Intent that should be fired when state changes to false. By
     *                    default the Intent is used to send a broadcast. Add
     *                    {@link ExpressionManager#EXTRA_INTENT_TYPE} with any of the values
     *                    {@link ExpressionManager#INTENT_TYPE_ACTIVITY}, {@link ExpressionManager#INTENT_TYPE_SERVICE} to
     *                    have Swan launch an activity or service.
     * @param onUndefined Intent that should be fired when state changes to undefined.
     *                    By default the Intent is used to send a broadcast. Add
     *                    {@link ExpressionManager#EXTRA_INTENT_TYPE} with any of the values
     *                    {@link ExpressionManager#INTENT_TYPE_ACTIVITY}, {@link ExpressionManager#INTENT_TYPE_SERVICE} to
     *                    have Swan launch an activity or service.
     */
    public void registerTriStateActuator(Context context, String id, TriStateExpression expression,
                                         Expression action, Intent onTrue, Intent onFalse, Intent onUndefined) {
        registerActuator(context, id, expression, action, onTrue, onFalse, onUndefined, null);
    }

    /**
     * @param context     the context, should be application context to avoid memory leaks
     * @param id          the user provided unique id of the expression. Should not
     *                    contain {@link Expression#SEPARATOR} or end with any of the
     *                    {@link Expression#RESERVED_SUFFIXES}.
     * @param expression  the {@link TriStateExpression} that should be evaluated
     * @param action      the actuator expression
     * @param onNewValues Intent that should be fired when new values are available. Add
     *                    {@link ExpressionManager#EXTRA_INTENT_TYPE} with any of the values
     *                    {@link ExpressionManager#INTENT_TYPE_ACTIVITY}, {@link ExpressionManager#INTENT_TYPE_SERVICE} to
     *                    have Swan launch an activity or service.
     */
    public void registerValueActuator(Context context, String id, Expression action,
                                      ValueExpression expression, Intent onNewValues) {
        registerActuator(context, id, expression, action, null, null, null, onNewValues);
    }

    /**
     * @param context            the context, should be application context to avoid memory leaks
     * @param id                 the user provided unique id of the expression. Should not
     *                           contain {@link Expression#SEPARATOR} or end with any of the
     *                           {@link Expression#RESERVED_SUFFIXES}.
     * @param actuatorExpression a full actuator expression that includes two {@link Expression Expressions}
     *                           separated the {@link #ACTUATOR_SEPARATOR}
     * @param listener           a {@link ExpressionListener} that receives the evaluation
     *                           results. If this parameter is null, it is also possible to
     *                           listen for the results using a {@link BroadcastReceiver}.
     *                           Filter on datascheme "swan://<your.package.name>#<your.expression.id>"
     *                           and action {@link ExpressionManager#ACTION_NEW_VALUES} or
     *                           {@link ExpressionManager#ACTION_NEW_TRISTATE}.
     * @throws SwanException if id is null or invalid
     */
    public static void registerActuator(Context context, String id, String actuatorExpression,
                                        ExpressionListener listener) throws SwanException {
        String[] split = actuatorExpression.split(ACTUATOR_SEPARATOR);

        if (split.length != 2) {
            throw new SwanException("Actuator expression must contain only one " +
                    ACTUATOR_SEPARATOR + " separator");
        }

        Expression expression;
        Expression action;
        try {
            // Parse the swan song expressions
            expression = ExpressionFactory.parse(split[0]);

            if (expression == null) {
                throw new SwanException("null expression");
            }

            action = ExpressionFactory.parse(split[1]);
        } catch (ExpressionParseException e) {
            throw new SwanException(e);
        }

        registerActuator(context, id, expression, action, listener);
    }

    /**
     * Unregisters a previously registered {@link Expression} for evaluation and the actuator
     * associated with it.
     *
     * @param context the context
     * @param id      the user provided unique id of the expression. Should not
     *                contain {@link Expression#SEPARATOR} or end with any of the
     *                {@link Expression#RESERVED_SUFFIXES}.
     * @param actuatorOnly if true the expression will not be unregistered, only the associated
     *                     actuators
     */
    public static void unregisterActuator(Context context, String id, boolean actuatorOnly) {
        if (!actuatorOnly) {
            ExpressionManager.unregisterExpression(context, id);
        }
        sendUnregister(context, id);
    }

    /**
     * Sends a broadcast to SWAN to register an actuator.
     *
     * @param context    the context
     * @param id         the user provided unique id of the expression. Should not
     *                   contain {@link Expression#SEPARATOR} or end with any of the
     *                   {@link Expression#RESERVED_SUFFIXES}.
     * @param expression the {@link Expression} that should be evaluated
     */
    private static void sendRegister(Context context, String id, Expression expression) {
        Intent intent = new Intent(ACTION_REGISTER);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(EXTRA_EXPRESSION_ID, id);
        intent.putExtra(EXTRA_EXPRESSION, expression.toParseString());
        context.sendBroadcast(intent);
    }

    /**
     * Sends a broadcast to SWAN to unregister an actuator.
     *
     * @param context the context
     * @param id      the user provided unique id of the expression.
     */
    private static void sendUnregister(Context context, String id) {
        Intent intent = new Intent(ACTION_UNREGISTER);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(EXTRA_EXPRESSION_ID, id);
        context.sendBroadcast(intent);
    }
}

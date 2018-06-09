package interdroid.swancore.swanmain;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TriState;
import interdroid.swancore.swansong.TriStateExpression;

public class ActuatorManager {

    private static final String TAG = ActuatorManager.class.getSimpleName();

    private static final String ACTUATOR_SEPARATOR = "THEN";

    public static final String ACTION_REGISTER = "interdroid.swan.actuator.REGISTER";
    public static final String ACTION_UNREGISTER = "interdroid.swan.actuator.UNREGISTER";

    private static final String ACTION_NEW_TRISTATE = "interdroid.swan.actuator.NEW_TRISTATE";
    private static final String TRISTATE_INTERCEPTOR = "interdroid.swan.actuator.TRISTATE_INTERCEPTOR";

    public static final String EXTRA_EXPRESSION_ID = "expressionId";
    public static final String EXTRA_EXPRESSION = "expression";
    public static final String EXTRA_FORWARD_TRUE = "forward";
    public static final String EXTRA_FORWARD_FALSE = "forward";
    public static final String EXTRA_FORWARD_UNDEFINED = "forward";

    private static final Map<String, ExpressionListener> LISTENERS = new HashMap<>();

    private static boolean sReceiverRegistered = false;

    private static Application sContext;

    private static BroadcastReceiver sReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getData().getFragment();
            Log.d(TAG, "on receive");

            ExpressionListener listener = LISTENERS.get(id);

            if (listener != null) {
                listener.onNewState(id,
                        intent.getLongExtra(ExpressionManager.EXTRA_NEW_TRISTATE_TIMESTAMP, 0),
                        TriState.valueOf(intent.getStringExtra(ExpressionManager.EXTRA_NEW_TRISTATE)));
            } else {
                Log.d(TAG, "got spurious broadcast: " + intent.getDataString());
            }
        }
    };

    public static void registerActuator(Context context, String id, TriStateExpression expression,
                                        Expression action, ExpressionListener listener) throws SwanException {
        checkContext(context);

        if (LISTENERS.containsKey(id)) {
            throw new SwanException("Listener already registered for id '" + id
                    + "'");
        } else {
            if (listener != null) {
                if (LISTENERS.size() == 0) {
                    sReceiverRegistered = true;
                    registerReceiver(context);
                    /* we store the context, as we need it later in unregisterExpression */
                    sContext = (Application) context;
                }
                LISTENERS.put(id, listener);
            }
        }

        Intent newTriState = new Intent(ACTION_NEW_TRISTATE);
        newTriState.setData(Uri.parse("swan://" + context.getPackageName() + "#" + id));

        registerActuator(context, id, expression, action, newTriState, newTriState, newTriState);
    }

    public static void registerActuator(Context context, String id, TriStateExpression expression,
                                        Expression action, Intent onTrue, Intent onFalse, Intent onUndefined) {
        checkContext(context);

        Intent intercept = new Intent(TRISTATE_INTERCEPTOR);
        intercept.putExtra(EXTRA_FORWARD_TRUE, onTrue);
        intercept.putExtra(EXTRA_FORWARD_FALSE, onFalse);
        intercept.putExtra(EXTRA_FORWARD_UNDEFINED, onUndefined);
        intercept.putExtra(EXTRA_EXPRESSION_ID, id);

        ExpressionManager.registerTriStateExpression(context, id, expression, intercept, intercept, intercept);

        sendRegister(context, id, action);
    }

    public static void registerActuator(Context context, String id, String actuatorExpression, ExpressionListener listener) throws SwanException {
        checkContext(context);

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
        LISTENERS.remove(id);
        if (LISTENERS.size() == 0 && sReceiverRegistered) {
            sReceiverRegistered = false;
            /* if we unregister context instead of sContext, then we have a problem for the following scenario:
             * expression1 is registered in context A (so the receiver is registered for context A), then expression2 is registered
             * in context B, then expression 1 is unregistered (but the receiver remains registered for context A),
             * then expression 2 is unregistered, so the code below tries to unregister the receiver for context B,
             * which is erroneous, as the receiver is registered for context A */
            unregisterReceiver(sContext);
        }
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

    /**
     * registers the broadcast receiver to receive values on behalve of
     * listeners and forward them subsequently.
     *
     * @param context
     */
    private static void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NEW_TRISTATE);
        intentFilter.addDataScheme("swan");
        intentFilter.addDataAuthority(context.getPackageName(), null);
        context.registerReceiver(sReceiver, intentFilter);
    }

    /**
     * unregisters the broadcast receiver. This is executed if no listeners are
     * present anymore.
     *
     * @param context
     */
    private static void unregisterReceiver(Context context) {
        context.unregisterReceiver(sReceiver);
    }

    private static void checkContext(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("context must be application context to prevent leaks");
        }
    }
}

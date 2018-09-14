package interdroid.swan.actuator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

import interdroid.swan.engine.EvaluationManager;
import interdroid.swan.sensors.impl.wear.shared.RemoteSensorManager;
import interdroid.swancore.swanmain.ActuatorManager;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.TriState;

/**
 * This broadcast receiver function is to intercept new value and state broadcasts and perform
 * actuations if there are actuators registered for the {@link Expression}. Receives the
 * {@link ActuatorManager#ACTUATOR_INTERCEPTOR} action.
 */
public class ActuatorInterceptor extends BroadcastReceiver {

    private static final String TAG = ActuatorInterceptor.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(ExpressionManager.EXTRA_NEW_TRISTATE)) {
            switch (TriState.valueOf(intent.getStringExtra(ExpressionManager.EXTRA_NEW_TRISTATE))) {
                case TRUE:
                    forwardExtraIntent(context, intent, ActuatorManager.EXTRA_FORWARD_TRUE, true);

                    actuate(context, intent.getStringExtra(ActuatorManager.EXTRA_EXPRESSION_ID), null);
                    break;
                case FALSE:
                    forwardExtraIntent(context, intent, ActuatorManager.EXTRA_FORWARD_FALSE, true);
                    break;
                case UNDEFINED:
                    forwardExtraIntent(context, intent, ActuatorManager.EXTRA_FORWARD_UNDEFINED, true);
                    break;
            }
        } else if (intent.hasExtra(ExpressionManager.EXTRA_NEW_VALUES)) {
            forwardExtraIntent(context, intent, ActuatorManager.EXTRA_FORWARD_NEW_VALUES, false);

            // do the conversion from Parcelable[] to
            // TimestampedValue[], casting doesn't work
            Parcelable[] parcelables = (Parcelable[]) intent
                    .getParcelableArrayExtra(ExpressionManager.EXTRA_NEW_VALUES);
            TimestampedValue[] timestampedValues = new TimestampedValue[parcelables.length];
            System.arraycopy(parcelables, 0, timestampedValues, 0, parcelables.length);

            actuate(context, intent.getStringExtra(ActuatorManager.EXTRA_EXPRESSION_ID), timestampedValues);
        }
    }

    /**
     * Fires the original intent specified by the application that registered the expression.
     *
     * @param context    the context
     * @param intent     the intercepted intent that contains the original intent to be fired
     * @param extra      the key for the original intent
     * @param isTriState copy tri state extras if true, copy new value extras otherwise
     */
    private void forwardExtraIntent(Context context, Intent intent, String extra, boolean isTriState) {
        Intent forward = intent.getParcelableExtra(extra);
        if (forward != null) {
            if (isTriState) {
                forward.putExtra(ExpressionManager.EXTRA_NEW_TRISTATE,
                        intent.getStringExtra(ExpressionManager.EXTRA_NEW_TRISTATE));
                forward.putExtra(ExpressionManager.EXTRA_NEW_TRISTATE_TIMESTAMP,
                        intent.getLongExtra(ExpressionManager.EXTRA_NEW_TRISTATE_TIMESTAMP, 0));
            } else {
                forward.putExtra(ExpressionManager.EXTRA_NEW_VALUES,
                        intent.getParcelableArrayExtra(ExpressionManager.EXTRA_NEW_VALUES));
            }

            context.sendBroadcast(forward);
        }
    }

    /**
     * Perform the actuation for the specified expression id
     *
     * @param expressionId the id of the expression
     * @param newValues    the new values given by the expression
     */
    private void actuate(Context context, String expressionId, TimestampedValue[] newValues) {
        if (expressionId == null) {
            Log.w(TAG, "Empty expressionId");
            return;
        }

        List<Actuator> actuators = ActuationManager.ACTUATORS.get(expressionId);


        if (actuators == null) {
            Log.w(TAG, "No actuator registered for id " + expressionId);
            return;
        }

        Log.d(TAG, "Performing actuator for id " + expressionId);
        for (Actuator actuator : actuators) {
            try {
                actuator.performAction(context, newValues);
            } catch (Exception e) {
                Log.e(TAG, "Exception while performing actuator action", e);
            }
        }

        if (ActuationManager.REMOTE_ACTUATORS.contains(expressionId)) {
            //TODO: Add TimestampedValue[] if needed
            RemoteSensorManager.getInstance(context).Actuate(expressionId);
        }
    }
}

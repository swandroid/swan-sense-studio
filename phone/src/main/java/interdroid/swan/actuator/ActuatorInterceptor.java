package interdroid.swan.actuator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import interdroid.swancore.swanmain.ActuatorManager;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swansong.TriState;

public class ActuatorInterceptor extends BroadcastReceiver {

    private static final String TAG = ActuatorInterceptor.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(ExpressionManager.EXTRA_NEW_TRISTATE)) {
            switch (TriState.valueOf(intent.getStringExtra(ExpressionManager.EXTRA_NEW_TRISTATE))) {
                case TRUE:
                    forwardExtraIntent(context, intent, ActuatorManager.EXTRA_FORWARD_TRUE, true);

                    actuate(intent.getStringExtra(ActuatorManager.EXTRA_EXPRESSION_ID));
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
            actuate(intent.getStringExtra(ActuatorManager.EXTRA_EXPRESSION_ID));
        }
    }

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

    private void actuate(String expressionId) {
        if (expressionId == null) {
            Log.w(TAG, "Empty expressionId");
            return;
        }

        Actuator actuator = ActuationManager.ACTUATORS.get(expressionId);

        if (actuator == null) {
            Log.w(TAG, "No actuator registered for id " + expressionId);
            return;
        }

        Log.d(TAG, "Performing actuator for id " + expressionId);
        actuator.performAction();
    }
}

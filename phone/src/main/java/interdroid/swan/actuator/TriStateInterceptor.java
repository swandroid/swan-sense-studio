package interdroid.swan.actuator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import interdroid.swancore.swanmain.ActuatorManager;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swansong.TriState;

public class TriStateInterceptor extends BroadcastReceiver {

    private static final String TAG = TriStateInterceptor.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (TriState.valueOf(intent.getStringExtra(ExpressionManager.EXTRA_NEW_TRISTATE))) {
            case TRUE:
                forwardExtraIntent(context, intent, ActuatorManager.EXTRA_FORWARD_TRUE);

                String expressionId = intent.getStringExtra(ActuatorManager.EXTRA_EXPRESSION_ID);

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
                break;
            case FALSE:
                forwardExtraIntent(context, intent, ActuatorManager.EXTRA_FORWARD_FALSE);
                break;
            case UNDEFINED:
                forwardExtraIntent(context, intent, ActuatorManager.EXTRA_FORWARD_UNDEFINED);
                break;
        }
    }

    private void forwardExtraIntent(Context context, Intent intent, String extra) {
        Intent forward = intent.getParcelableExtra(extra);
        if (forward != null) {
            forward.putExtra(ExpressionManager.EXTRA_NEW_TRISTATE,
                    intent.getStringExtra(ExpressionManager.EXTRA_NEW_TRISTATE));
            forward.putExtra(ExpressionManager.EXTRA_NEW_TRISTATE_TIMESTAMP,
                    intent.getLongExtra(ExpressionManager.EXTRA_NEW_TRISTATE_TIMESTAMP, 0));

            context.sendBroadcast(forward);
        }
    }
}

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
        Log.d(TAG, intent.toString());

        Intent forward = intent.getParcelableExtra(ActuatorManager.EXTRA_FORWARD);
        if (forward != null) {
            context.sendBroadcast(forward);
        }

        if (TriState.TRUE != TriState.valueOf(intent.getStringExtra(ExpressionManager.EXTRA_NEW_TRISTATE))) {
            return;
        }

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
    }
}

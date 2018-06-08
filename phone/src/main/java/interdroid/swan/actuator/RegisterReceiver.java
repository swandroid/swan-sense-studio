package interdroid.swan.actuator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import interdroid.swancore.swanmain.ActuatorManager;

public class RegisterReceiver extends BroadcastReceiver {

    private static final String TAG = RegisterReceiver.class.getSimpleName();

    private static final String EXTRA_EXPRESSION_ID = "expressionId";
    private static final String EXTRA_EXPRESSION = "expression";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) {
            Log.w(TAG, "Received intent with empty action");
            return;
        }

        String expressionId = intent.getStringExtra(EXTRA_EXPRESSION_ID);
        String expression = intent.getStringExtra(EXTRA_EXPRESSION);

        if (expressionId == null || expressionId.isEmpty()) {
            Log.w(TAG, "Received mpty expression id");
            return;
        }

        switch (action) {
            case ActuatorManager.ACTION_REGISTER:
                ActuationManager.registerActuator(context, expressionId, expression);
                break;
            case ActuatorManager.ACTION_UNREGISTER:
                ActuationManager.unregisterActuator(expressionId);
                break;
            default:
                Log.w(TAG, "Unknown intent action " + intent.getAction());
                break;
        }
    }
}

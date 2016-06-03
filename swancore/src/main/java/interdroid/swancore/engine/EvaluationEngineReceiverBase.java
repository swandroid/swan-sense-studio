package interdroid.swancore.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


// TODO: remove this class form the swan core, swanlake and swan wear can
// use BroadcastReceiver
public class EvaluationEngineReceiverBase extends BroadcastReceiver {

    /**
     * This receiver acts as a forwarder to the EvaluationEngineServiceBase.
     * <p/>
     * We don't want 3rd party applications to be able to invoke
     * Context.stopService(Intent) on the EvaluationEngineServiceBase, because that
     * will stop evaluations of expressions for other applications too.
     * Therefore the EvaluationEngineServiceBase is not public (e.g. not exported),
     * hence it cannot be started and stopped from outside the apk. However, we
     * allow intents to be sent to this receiver who will then forward it to the
     * service, but always using the Context.startService(Intent), rather than
     * the Context.stopService(Intent).
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // forward the intent to the service
        intent.setClass(context, EvaluationEngineServiceBase.class);
        context.startService(intent);
    }

}

package interdroid.swan.engine;

import android.os.Build;
import android.util.Log;

import java.io.IOException;

import interdroid.swan.crossdevice.ProximityManagerI;
import interdroid.swan.crossdevice.Pusher;
import interdroid.swan.crossdevice.beacon.BeaconInitializer;
import interdroid.swan.crossdevice.bluetooth.BTManager;
import interdroid.swancore.crossdevice.Converter;
import interdroid.swancore.engine.EvaluationEngineServiceBase;
import interdroid.swancore.swansong.Result;

public class EvaluationEngineService extends EvaluationEngineServiceBase{
    ProximityManagerI mProximityManager; //TODO consider moving this to EvaluationManagerBase class


    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mProximityManager = new BLEManager(this);
            mProximityManager = new BTManager(this);
            mProximityManager.init();
            BeaconInitializer.getInstance(getApplicationContext());

            // Re-initialize with correct EvaluationManager
            mEvaluationManager = new EvaluationManager(this, mProximityManager);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProximityManager.clean();
        }
    }

    @Override
    protected void sendUpdateToRemote(final String registrationId,
                                      final String expressionId, final Result result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // pusher is async
            try {
                if (mProximityManager.hasPeer(registrationId)) {
                    mProximityManager.send(registrationId, expressionId, ACTION_NEW_RESULT_REMOTE,
                            Converter.objectToString(result));
                } else {
                    Pusher.push(registrationId, expressionId, ACTION_NEW_RESULT_REMOTE,
                            Converter.objectToString(result));
                }
            } catch (IOException e) {
                Log.d(TAG, "Exception in converting result to string", e);
            }
        }
    }

}

package interdroid.swan.engine;


import android.util.Log;
import java.io.IOException;

import interdroid.swan.crossdevice.ble.BLEManager;
import interdroid.swancore.engine.EvaluationEngineServiceBase;
import interdroid.swancore.crossdevice.Converter;
import interdroid.swan.crossdevice.Pusher;
import interdroid.swan.crossdevice.beacon.BeaconInitializer;
import interdroid.swan.crossdevice.ProximityManagerI;
import interdroid.swan.crossdevice.bluetooth.BTManager;
import interdroid.swancore.swansong.Result;


public class EvaluationEngineService extends EvaluationEngineServiceBase{
    ProximityManagerI mProximityManager; //TODO consider moving this to EvaluationManagerBase class


    @Override
    public void onCreate() {
        super.onCreate();

//        mProximityManager = new BLEManager(this);
        mProximityManager = new BTManager(this);
        mProximityManager.init();
        BeaconInitializer.getInstance(getApplicationContext());

        // Re-initialize with correct EvaluationManager
        mEvaluationManager = new EvaluationManager(this, mProximityManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProximityManager.clean();
    }

    @Override
    protected void sendUpdateToRemote(final String registrationId,
                                      final String expressionId, final Result result) {
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

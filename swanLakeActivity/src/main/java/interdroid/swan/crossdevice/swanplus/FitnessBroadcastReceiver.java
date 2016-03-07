package interdroid.swan.crossdevice.swanplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import interdroid.swan.sensors.impl.FitnessSensor;

/**
 * Created by vladimir on 2/4/16.
 */
public class FitnessBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "FitnessBcastReceiver";

    FitnessSensor.FitnessDataPoller fitnessDataPoller;

    public FitnessBroadcastReceiver(FitnessSensor.FitnessDataPoller fitnessDataPoller) {
        this.fitnessDataPoller = fitnessDataPoller;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(FitnessSensor.ACTION_SEND_FITNESS_DATA.equals(action)) {
            String avgSpeed = intent.getStringExtra("avg_speed");
            fitnessDataPoller.updateValues(avgSpeed);
            Log.d(TAG, "fitness receiver: " + avgSpeed);
        }
    }
}

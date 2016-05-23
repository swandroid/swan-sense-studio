package interdroid.swan.crossdevice.swanplus.run2gether;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import interdroid.swan.sensors.impl.FitnessSensor;

/**
 * Created by vladimir on 2/4/16.
 */
public class Run2getherBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "R2gBroadcastReceiver";

    ActivityRun2gether r2gActivity;

    public Run2getherBroadcastReceiver(ActivityRun2gether r2gActivity) {
        this.r2gActivity = r2gActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (FitnessSensor.ACTION_REQ_FITNESS_DATA.equals(action)) {
            Intent replyIntent = new Intent();
            replyIntent.setAction(FitnessSensor.ACTION_SEND_FITNESS_DATA);
            replyIntent.putExtra("avg_speed", r2gActivity.getRunningData());
            context.sendBroadcast(replyIntent);
            Log.d(TAG, "sent fitness data");

        }
    }
}

package interdroid.swan.sensors.impl;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

public class ActivityRecognitionSensor extends AbstractSwanSensor implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    // The desired time between activity detections
    // Larger values will result in fewer activity detections while improving battery life.
    // Smaller values will result in more frequent activity detections but will consume more power since the device must be woken up more frequently.
    // Long.MAX_VALUE means it only monitors the results requested by other clients without consuming additional power.
    // A value of 0 will result in activity detections at the fastest possible rate.
    private static final long DETECTION_INTERVAL_MILLIS = 3000;
    private static final int REQUEST_CODE_SERVICE_TRACKING = 0;

    /**
     * The level field.
     */
    public static final String ACTIVITY_TYPE = "type";

    private GoogleApiClient mApiClient;

    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.user_activity_preferences;
        }
    }

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void unregister(String id) {

    }

    @Override
    public String[] getValuePaths() {
        return new String[] {ACTIVITY_TYPE};
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        if (registeredConfigurations.size() == 1) {
            mApiClient = new GoogleApiClient.Builder(this)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, getClass());
        PendingIntent pendingIntent = PendingIntent.getService(this, REQUEST_CODE_SERVICE_TRACKING,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient,
                DETECTION_INTERVAL_MILLIS, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        int maxConfidence = 0;
        int type = 0;
        for (DetectedActivity activity : probableActivities) {
            if (maxConfidence < activity.getConfidence()){
                maxConfidence = activity.getConfidence();
                type = activity.getType();
            }
        }

        Log.e(getClass().getSimpleName(), type + "");
        putValueTrimSize(ACTIVITY_TYPE, null, System.currentTimeMillis(), type);
    }
}

package interdroid.swan.actuator.impl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import interdroid.swan.actuator.Actuator;
import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * An {@link Actuator} that turns the flashlight of the phone on or off.
 */
public class FlashlightActuator extends Actuator {

    private static final String TAG = FlashlightActuator.class.getSimpleName();

    public static final String ENTITY = "flashlight";

    private static final String[] KEYS = new String[]{};

    private static final String[] PATHS = new String[]{"on", "off"};

    private static Camera camera;

    private final boolean on;

    private final CameraManager camManager;

    /**
     * Create a {@link FlashlightActuator} object.
     *
     * @param context the context
     * @param on      whether to turn the flashlight on or off
     */
    private FlashlightActuator(Context context, boolean on) {
        this.on = on;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        } else {
            camManager = null;
        }
    }

    @Override
    public void performAction(Context context, TimestampedValue[] newValues) throws CameraAccessException {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Log.w(TAG, "Device has no flash!");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (camManager != null) {
                // Usually front camera is at 0 position.
                String cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, on);
            }
        } else {
            if (on) {
                camera = Camera.open();
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
            } else if (camera != null) {
                camera.stopPreview();
                camera.release();
            }
        }
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            boolean on = "on".equals(expression.getValuePath());
            return new FlashlightActuator(context, on);
        }
    }

    public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[]{};
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            return ENTITY;
        }
    }
}

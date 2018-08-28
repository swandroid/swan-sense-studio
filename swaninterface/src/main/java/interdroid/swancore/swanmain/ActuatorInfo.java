package interdroid.swancore.swanmain;

import android.content.ComponentName;
import android.content.Intent;

public class ActuatorInfo {

    private final String entityId;

    private final ComponentName componentName;

    public ActuatorInfo(String entityId, ComponentName componentName) {
        this.entityId = entityId;
        this.componentName = componentName;
    }

    public String getEntityId() {
        return entityId;
    }

    public Intent getConfigurationIntent() {
        Intent i = new Intent();
        i.setComponent(componentName);
        return i;
    }
}

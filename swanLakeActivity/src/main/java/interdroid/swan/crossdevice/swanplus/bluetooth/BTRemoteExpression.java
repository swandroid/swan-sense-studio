package interdroid.swan.crossdevice.swanplus.bluetooth;

import android.bluetooth.BluetoothDevice;

import interdroid.swan.crossdevice.swanplus.SwanUser;
import interdroid.swan.swansong.Expression;

/**
 * Created by vladimir on 3/17/16.
 */
public class BTRemoteExpression {
    private String id;
    private SwanUser user;
    private String expression;

    public BTRemoteExpression(String id, SwanUser user, String expression) {
        this.id = id;
        this.user = user;
        this.expression = expression;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public SwanUser getUser() {
        return user;
    }

    public void setUser(SwanUser user) {
        this.user = user;
    }

}

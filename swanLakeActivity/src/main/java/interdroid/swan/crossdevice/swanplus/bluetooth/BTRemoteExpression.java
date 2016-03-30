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
    private String action;

    public BTRemoteExpression(String id, SwanUser user, String expression, String action) {
        this.id = id;
        this.user = user;
        this.expression = expression;
        this.action = action;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    @Override
    public String toString() {
        return "RemoteExpr[" + id + ", " + user + ", " + expression + ", " + action + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BTRemoteExpression that = (BTRemoteExpression) o;

        if (!id.equals(that.id)) return false;
        if (!user.equals(that.user)) return false;
        if (!expression.equals(that.expression)) return false;
        return action.equals(that.action);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + expression.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }
}

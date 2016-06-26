package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by vladimir on 3/17/16.
 */
public class BTRemoteExpression {

    private static int exprCounter = 0;

    private String id;
    private BluetoothDevice remoteDevice;
    private String expression;
    private String action;

    public BTRemoteExpression(String baseId, BluetoothDevice remoteDevice, String expression, String action) {
        this.id = getNewId(baseId);
        this.remoteDevice = remoteDevice;
        this.expression = expression;
        this.action = action;
    }

    public BTRemoteExpression(BTRemoteExpression expr) {
        this.id = getNewId(expr.getBaseId());
        this.remoteDevice = expr.getRemoteDevice();
        this.expression = expr.getExpression();
        this.action = expr.getAction();
    }

    /**
     * increment expression counter
     */
    private synchronized int incCounter() {
        return exprCounter++;
    }

    private String getNewId(String baseId) {
        return baseId + "/" + incCounter();
    }

    public String getBaseId() {
        return id.replaceAll("/.*", "");
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

    public BluetoothDevice getRemoteDevice() {
        return remoteDevice;
    }

    @Override
    public String toString() {
        return "RemoteExpr[" + id + ", " + remoteDevice.getName() + ", " + expression + ", " + action + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BTRemoteExpression that = (BTRemoteExpression) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

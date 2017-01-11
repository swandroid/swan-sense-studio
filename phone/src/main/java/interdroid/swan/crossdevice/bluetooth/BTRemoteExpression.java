package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;

import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;

/**
 * Created by vladimir on 3/17/16.
 */
public class BTRemoteExpression {

    private static int exprCounter = 0;

    private String id;
    private String expressionString;
    private Expression expression;

    public BTRemoteExpression(String baseId, String expression) throws ExpressionParseException {
        this.id = getNewId(baseId);
        this.expressionString = expression;
        this.expression = ExpressionFactory.parse(expressionString);
    }

    public void renewId() {
        id = getNewId(getBaseId());
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

    public Expression getExpression() {
        return expression;
    }

    public String getExpressionString() {
        return expressionString;
    }

    @Override
    public String toString() {
        return "RemoteExpr[" + id + ", " + expressionString + "]";
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

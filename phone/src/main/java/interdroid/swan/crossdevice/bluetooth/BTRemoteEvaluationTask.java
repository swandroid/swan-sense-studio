package interdroid.swan.crossdevice.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vladimir on 6/27/16.
 */
public class BTRemoteEvaluationTask {

    private BTSwanDevice swanDevice;
    private List<BTRemoteExpression> expressions;

    public BTRemoteEvaluationTask(BTSwanDevice swanDevice) {
        this.swanDevice = swanDevice;
        expressions = new ArrayList<BTRemoteExpression>();

        for(Map.Entry<String, String> entry : swanDevice.getRegisteredExpressions().entrySet()) {
            expressions.add(new BTRemoteExpression(entry.getKey(), entry.getValue()));
        }
    }

    public boolean hasExpressions() {
        return !expressions.isEmpty();
    }

    public List<BTRemoteExpression> getExpressions() {
        return expressions;
    }

    public BTRemoteExpression getRemoteExpression(String expressionId) {
        for(BTRemoteExpression expression : expressions) {
            if(expression.getId().equals(expressionId)) {
                return expression;
            }
        }
        return null;
    }

    public void removeExpression(BTRemoteExpression expression) {
        expressions.remove(expression);
    }

    public List<String> getExpressionIds() {
        List<String> ids = new ArrayList<String>();
        for(BTRemoteExpression expression : expressions) {
            ids.add(expression.getId());
        }
        return ids;
    }

    public BTSwanDevice getSwanDevice() {
        return swanDevice;
    }

    @Override
    public String toString() {
        return "EvalTask(" + swanDevice.getName() + ")" + expressions;
    }
}

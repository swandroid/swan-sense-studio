package interdroid.swan.actuator;

import android.content.Context;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import interdroid.swan.actuator.impl.VibratorActuator;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.SensorValueExpression;

public class ActuationManager {

    private static final String TAG = ActuationManager.class.getSimpleName();

    static final Map<String, Actuator> ACTUATORS = new ConcurrentHashMap<>();

    public static void registerActuator(Context context, String expressionId, String expression) {
        Actuator actuator;
        try {
            actuator = parseActuatorExpression(context, expression);
        } catch (ExpressionParseException | SwanException e) {
            Log.w(TAG, "Failed to parse expression", e);
            return;
        }

        if (ACTUATORS.containsKey(expressionId)) {
            // TODO: 2018-06-06 Add support for multiple actuators
            Log.w(TAG, "There is already an actuator registered for expression " + expressionId
                    + ". Replacing...");
        }

        ACTUATORS.put(expressionId, actuator);

        Log.d(TAG, "Registered actuator for " + expressionId);
    }

    public static void unregisterActuator(String expressionId) {
        Actuator removed = ACTUATORS.remove(expressionId);

        if (removed == null) {
            Log.d(TAG, "No actuator for " +  expressionId + " to be removed");
        } else {
            Log.d(TAG, "Removed actuator for " +  expressionId);
        }
    }

    private static Actuator parseActuatorExpression(Context context, String actExpression)
            throws ExpressionParseException, SwanException {

        Expression expression = ExpressionFactory.parse(actExpression);

        if (expression == null) {
            throw new SwanException("null actuator expression");
        }

        if (!(expression instanceof SensorValueExpression)) {
            // TODO: 2018-06-06 is this correct?
            throw new SwanException("bad actuator expression");
        }

        SensorValueExpression sve = (SensorValueExpression) expression;

        if (Expression.LOCATION_SELF.equals(sve.getLocation())) {
            return expressionToActuator(context, sve);
        } else {
            // TODO: 2018-06-06 other locations
            return null;
        }
    }

    private static Actuator expressionToActuator(Context context, SensorValueExpression sve) {
        switch (sve.getEntity()) {
            case VibratorActuator.ENTITY:
                long duration = Long.parseLong(sve.getConfiguration().getString("duration"));
                return new VibratorActuator(context, duration);
            default:
                Log.w(TAG, "Unknown actuator entity");
                return null;
        }
    }
}

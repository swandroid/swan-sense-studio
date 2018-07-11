package interdroid.swan.actuator;

import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import interdroid.swan.actuator.impl.*;
import interdroid.swancore.swanmain.ActuatorManager;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.SensorValueExpression;

/**
 * Class that manages actuator registration and unregistration.
 */
public class ActuationManager {

    private static final String TAG = ActuationManager.class.getSimpleName();

    private static final Map<String, Actuator.Factory> FACTORY_MAP;

    static final Map<String, List<Actuator>> ACTUATORS = new ConcurrentHashMap<>();

    static {
        Map<String, Actuator.Factory> factoryMap = new HashMap<>();

        factoryMap.put(VibratorActuator.ENTITY, new VibratorActuator.Factory());
        factoryMap.put(LogActuator.ENTITY, new LogActuator.Factory());
        factoryMap.put(HttpActuator.ENTITY, new HttpActuator.Factory());
        factoryMap.put(BrightnessActuator.ENTITY, new BrightnessActuator.Factory());
        factoryMap.put(NotificationActuator.ENTITY, new NotificationActuator.Factory());
        factoryMap.put(SmsActuator.ENTITY, new SmsActuator.Factory());
        factoryMap.put(FlashlightActuator.ENTITY, new FlashlightActuator.Factory());
        factoryMap.put(VolumeActuator.ENTITY, new VolumeActuator.Factory());
        factoryMap.put(FileActuator.ENTITY, new FileActuator.Factory());
        factoryMap.put(IntentActuator.ENTITY, new IntentActuator.Factory());

        FACTORY_MAP = Collections.unmodifiableMap(factoryMap);
    }

    /**
     * Register an actuator. If another actuator exists with the given id, it will be replaced.
     *
     * @param context      the context
     * @param expressionId the id of the expression
     * @param expression   the actuator expression to be registered
     */
    public static void registerActuator(Context context, String expressionId, String expression) {
        List<Actuator> actuators;
        try {
            actuators = parseActuatorExpression(context, expression);
        } catch (Exception e) {
            Log.w(TAG, "Failed to parse expression", e);
            return;
        }

        if (ACTUATORS.containsKey(expressionId)) {
            // TODO: 2018-06-06 Add support for multiple actuators
            Log.w(TAG, "There is already an actuator registered for expression " + expressionId
                    + ". Replacing...");
        }

        ACTUATORS.put(expressionId, actuators);

        Log.d(TAG, "Registered actuator for " + expressionId);
    }

    /**
     * Unregister an actuator.
     *
     * @param expressionId the id of the expression
     */
    public static void unregisterActuator(String expressionId) {
        List<Actuator> removed = ACTUATORS.remove(expressionId);

        if (removed == null) {
            Log.d(TAG, "No actuator for " + expressionId + " to be removed");
        } else {
            Log.d(TAG, "Removed actuator for " + expressionId);
        }
    }

    /**
     * Create a {@link Actuator} object from the given SWAN song string.
     *
     * @param context       the context
     * @param actExpression the expression of the actuator
     * @return the {@link Actuator} object parsed from the given string. null if it cannot be parsed
     * @throws ExpressionParseException if the parse fails
     * @throws SwanException            parsing returns null or the expresion is not a {@link SensorValueExpression}
     */
    private static List<Actuator> parseActuatorExpression(Context context, String actExpression)
            throws ExpressionParseException, SwanException {

        List<Actuator> actuators = new LinkedList<>();

        for (String s : actExpression.split(ActuatorManager.MULTIPLE_ACTUATOR_SEPARATOR)) {
            Expression expression = ExpressionFactory.parse(s);

            if (expression == null) {
                throw new SwanException("null actuator expression");
            }

            if (!(expression instanceof SensorValueExpression)) {
                // TODO: 2018-06-06 is this correct?
                throw new SwanException("bad actuator expression");
            }

            SensorValueExpression sve = (SensorValueExpression) expression;

            if (Expression.LOCATION_SELF.equals(sve.getLocation())) {
                actuators.add(expressionToActuator(context, sve));
            }

            // TODO: 2018-06-06 other locations
        }

        return actuators;
    }

    /**
     * Create a {@link Actuator} object from the given {@link SensorValueExpression} object.
     *
     * @param context the context
     * @param sve     the {@link SensorValueExpression} object to create an {@link Actuator} from
     * @return the created {@link Actuator}
     */
    private static Actuator expressionToActuator(Context context, SensorValueExpression sve) {
        Actuator.Factory factory = FACTORY_MAP.get(sve.getEntity());

        if (factory != null) {
            return factory.create(context, sve);
        } else {
            Log.w(TAG, "Unknown actuator entity");
            return null;
        }
    }
}

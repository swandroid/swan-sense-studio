package interdroid.swan.engine;

import android.content.Context;
import android.os.Bundle;

import interdroid.swancore.engine.EvaluationEngineServiceBase;
import interdroid.swancore.engine.EvaluationManagerBase;
import interdroid.swan.crossdevice.Pusher;
import interdroid.swancore.crossdevice.Registry;
import interdroid.swan.crossdevice.swanplus.ProximityManagerI;
import interdroid.swancore.swansong.ComparisonExpression;
import interdroid.swancore.swansong.ConstantValueExpression;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.LogicExpression;
import interdroid.swancore.swansong.MathValueExpression;
import interdroid.swancore.swansong.SensorValueExpression;

import interdroid.swancore.engine.SensorSetupFailedException;


public class EvaluationManager extends EvaluationManagerBase{

    /**
     * proximity manager for connecting to nearby devices
     */
    ProximityManagerI mProximityManager;

    /**
     * proximity manager for connecting to nearby devices
     *
     * @param context
     */
    public EvaluationManager(Context context, ProximityManagerI proximityManager) {
        super(context);
        this.mProximityManager = proximityManager;
    }

    @Override
    protected void initializeRemote(String id, Expression expression,
                                  String resolvedLocation) throws SensorSetupFailedException {
        if (resolvedLocation.equals(Expression.LOCATION_NEARBY) || mProximityManager.hasPeer(resolvedLocation)) {
            // get sensor info from nearby devices
            mProximityManager.registerExpression(id, toCrossDeviceString(expression, resolvedLocation),
                    resolvedLocation, EvaluationEngineService.ACTION_REGISTER_REMOTE);
        } else {
            // send a push message with 'register' instead of 'initialize',
            // disadvantage is that we will only later on get exceptions
            String fromRegistrationId = Registry.get(mContext,
                    Expression.LOCATION_SELF);
            if (fromRegistrationId == null) {
                throw new SensorSetupFailedException(
                        "Device not registered with Google Cloud Messaging, unable to use remote sensors.");
            }
            String toRegistrationId = Registry.get(mContext, resolvedLocation);
            if (toRegistrationId == null) {
                throw new SensorSetupFailedException(
                        "No registration id known for location: "
                                + resolvedLocation);
            }
            // resolve all remote locations in the expression with respect to the
            // new location.
            Pusher.push(fromRegistrationId, toRegistrationId, id,
                    EvaluationEngineService.ACTION_REGISTER_REMOTE,
                    toCrossDeviceString(expression, toRegistrationId));
            // expression.toCrossDeviceString(mContext,
            // expression.getLocation()));
        }
    }

    protected String toCrossDeviceString(Expression expression,
                                         String toRegistrationId) {
        String registrationId = Registry.get(mContext, expression.getLocation());

        // we check if we have a wildcard as location or if the remote device is in proximity
        if (registrationId == null) {
            if (mProximityManager.hasPeer(toRegistrationId) || toRegistrationId.equals(Expression.LOCATION_NEARBY)) {
                registrationId = toRegistrationId;
            }
        }

        if (expression instanceof SensorValueExpression) {
            String result = ((registrationId.equals(toRegistrationId)) ? Expression.LOCATION_SELF
                    : registrationId)
                    + "@"
                    + ((SensorValueExpression) expression).getEntity()
                    + ":" + ((SensorValueExpression) expression).getValuePath();
            Bundle config = ((SensorValueExpression) expression)
                    .getConfiguration();
            if (config != null && config.size() > 0) {
                boolean first = true;
                for (String key : config.keySet()) {
                    result += (first ? "?" : "&") + key + "="
                            + config.getString(key);
                    first = false;
                }
            }
            result += "{"
                    + ((SensorValueExpression) expression)
                    .getHistoryReductionMode().toParseString() + ","
                    + ((SensorValueExpression) expression).getHistoryLength()
                    + "}";
            return result;
        } else if (expression instanceof LogicExpression) {
            if (((LogicExpression) expression).getRight() == null) {
                return ((LogicExpression) expression).getOperator()
                        + " "
                        + toCrossDeviceString(
                        ((LogicExpression) expression).getLeft(),
                        toRegistrationId);
            }
            return "("
                    + toCrossDeviceString(
                    ((LogicExpression) expression).getLeft(),
                    registrationId)
                    + " "
                    + ((LogicExpression) expression).getOperator()
                    + " "
                    + toCrossDeviceString(
                    ((LogicExpression) expression).getRight(),
                    registrationId) + ")";
        } else if (expression instanceof ComparisonExpression) {
            return "("
                    + toCrossDeviceString(
                    ((ComparisonExpression) expression).getLeft(),
                    registrationId)
                    + " "
                    + ((ComparisonExpression) expression).getComparator()
                    .toParseString()
                    + " "
                    + toCrossDeviceString(
                    ((ComparisonExpression) expression).getRight(),
                    registrationId) + ")";
        } else if (expression instanceof MathValueExpression) {
            return "("
                    + toCrossDeviceString(
                    ((MathValueExpression) expression).getLeft(),
                    registrationId)
                    + " "
                    + ((MathValueExpression) expression).getOperator()
                    .toParseString()
                    + " "
                    + toCrossDeviceString(
                    ((MathValueExpression) expression).getRight(),
                    registrationId) + ")";
        } else if (expression instanceof ConstantValueExpression) {
            return ((ConstantValueExpression) expression).toParseString();
        }
        throw new RuntimeException("Unknown expression type: " + expression);
    }

    // send a push message with 'unregister'
    @Override
    protected void stopRemote(String id, Expression expression) {
        String resolvedLocation = expression.getLocation();

        if (resolvedLocation.equals(Expression.LOCATION_NEARBY) || mProximityManager.hasPeer(resolvedLocation)) {
            // get sensor info from nearby devices
            mProximityManager.registerExpression(id, toCrossDeviceString(expression, resolvedLocation),
                    resolvedLocation, EvaluationEngineServiceBase.ACTION_UNREGISTER_REMOTE);
        } else {
            // for some reason this was set to null in the original version of swan, which made it impossible
            // to unregister remotely
            String fromRegistrationId = Registry.get(mContext,
                    Expression.LOCATION_SELF);
            if (fromRegistrationId == null) {
                throw new RuntimeException(
                        "Device not registered with Google Cloud Messaging, unable to use remote sensors.");
            }

            String toRegistrationId = Registry.get(mContext,
                    expression.getLocation());
            if (toRegistrationId == null) {
                // this should not happen, kill swan
                throw new RuntimeException(
                        "No registration id known for location: "
                                + expression.getLocation());
            }
            // resolve all remote locations in the expression with respect to the
            // new location.
            Pusher.push(fromRegistrationId, toRegistrationId, id,
                    EvaluationEngineServiceBase.ACTION_UNREGISTER_REMOTE,
                    toCrossDeviceString(expression, toRegistrationId));
        }
    }
}

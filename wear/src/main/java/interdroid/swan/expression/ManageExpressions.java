package interdroid.swan.expression;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;

import interdroid.swancore.swanmain.ActuatorManager;
import interdroid.swancore.swanmain.ExpressionListener;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swanmain.TriStateExpressionListener;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.TriState;
import interdroid.swancore.swansong.TriStateExpression;
import interdroid.swancore.swansong.ValueExpression;
import interdroid.swan.DeviceClient;

import static interdroid.swancore.swanmain.ActuatorManager.EXTRA_EXPRESSION_ID;
import static interdroid.swancore.swanmain.ActuatorManager.EXTRA_FORWARD_FALSE;
import static interdroid.swancore.swanmain.ActuatorManager.EXTRA_FORWARD_NEW_VALUES;
import static interdroid.swancore.swanmain.ActuatorManager.EXTRA_FORWARD_TRUE;
import static interdroid.swancore.swanmain.ActuatorManager.EXTRA_FORWARD_UNDEFINED;

/**
 * Created by Veaceslav Munteanu on 5/24/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class ManageExpressions {

    private static final String TAG = "ManageExpressions";
    Context context;
    private static final String ACTUATOR_SEPARATOR = "THEN";

    HashMap<String,String> expressionSuffix = new HashMap<>();

    public ManageExpressions(Context context){
        this.context = context;
    }


    public void registerExpression(String id, String expression, boolean wearActuation, final boolean phoneActuation) {

        try {
            Expression checkExpression =  ExpressionFactory.parse(expression);
            ActuatorManager.registerActuator(context, checkAndRemoveSuffixes(id), checkExpression,
                    null, new ExpressionListener() {
                        @Override
                        public void onNewState(String id, long timestamp, TriState newState) {
                            Log.d(TAG, "new State");
                            if(phoneActuation){
                                Log.d(TAG, "phone actuation true");
                                Result result = new Result(timestamp, newState);
                                result.setDeferUntilGuaranteed(false);
                                DeviceClient.getInstance(context).sendExpressionData(checkAndAddSuffixes(id), result);

                            }

                        }

                        @Override
                        public void onNewValues(String id, TimestampedValue[] newValues) {

                            Log.d(TAG, "new values");
                            if(phoneActuation){
                                if(newValues.length > 0) {
                                    TimestampedValue[] recentValue = new TimestampedValue[1];
                                    recentValue[0] =  newValues[newValues.length-1];
                                    DeviceClient.getInstance(context).sendExpressionData(checkAndAddSuffixes(id), new Result(recentValue,
                                            newValues[newValues.length - 1].getTimestamp()));
                                    String value = newValues[0].getValue().toString();
                                }
                            }


                        }
                    });
        } catch (SwanException e) {
            e.printStackTrace();
        } catch (ExpressionParseException e) {
            e.printStackTrace();
        }

            //registerSensorExpression(id,expression);



    }



    public void registerSensorExpression(String id, String expression){

        Log.d(TAG, "registering Expression" + expression);
        try {
            Expression checkExpression =  ExpressionFactory.parse(expression);

            //ValueExpression exp = (ValueExpression)ExpressionFactory.parse(expression);

            if(checkExpression instanceof ValueExpression) {

                ExpressionManager.registerValueExpression(context, checkAndRemoveSuffixes(id),
                        (ValueExpression) ExpressionFactory.parse(expression),
                        new ValueExpressionListener() {

                            /* Registering a listener to process new values from the registered sensor*/
                            @Override
                            public void onNewValues(String id,
                                                    TimestampedValue[] arg1) {
                                if (arg1 != null && arg1.length > 0) {

                                    DeviceClient.getInstance(context).sendExpressionData(checkAndAddSuffixes(id), new Result(arg1,
                                            arg1[arg1.length - 1].getTimestamp()));
                                    String value = arg1[0].getValue().toString();
                                }
                            }
                        });
            }
            else if (checkExpression instanceof TriStateExpression){
                ExpressionManager.registerTriStateExpression(context, checkAndRemoveSuffixes(id),
                        (TriStateExpression) ExpressionFactory.parse(expression), new TriStateExpressionListener() {
                            @Override
                            public void onNewState(String id, long timestamp, TriState newState) {

                                Result result = new Result(timestamp, newState);
                                result.setDeferUntilGuaranteed(false);
                                DeviceClient.getInstance(context).sendExpressionData(checkAndAddSuffixes(id), result);

                            }
                        });


            }
            else {
                    Log.d("Error", "This should not happen");
            }

        } catch (SwanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExpressionParseException e) {
            // TODO Auto-generated catch block
            Log.d("Expression", "Expression Parser exception");
            e.printStackTrace();
        }

    }


    public void unregisterSWANExpression(String id){

        String newId = checkAndRemoveSuffixes(id);
        ExpressionManager.unregisterExpression(context, newId);

        if(expressionSuffix.containsKey(newId)){
            expressionSuffix.remove(newId);
        }

    }


    private String checkAndRemoveSuffixes(String id){

        for (String suffix : Expression.RESERVED_SUFFIXES) {
            if (id.endsWith(suffix)) {
                id = id.replace(suffix,"");
                if(!expressionSuffix.containsKey(id)) {
                    expressionSuffix.put(id, suffix);
                }
            }
        }

        return id;
    }

    private String checkAndAddSuffixes(String id){

        String idWithSuffix;
        if(expressionSuffix.containsKey(id)){

            idWithSuffix = id + expressionSuffix.get(id);
        }
        else{
            idWithSuffix = id;
        }

        return idWithSuffix;
    }



}

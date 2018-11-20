package interdroid.swan.expression;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.HashSet;

import interdroid.swan.actuator.ActuationManager;
import interdroid.swan.remote.HighBandwidthNetworking;
import interdroid.swan.remote.cloud.CloudManager;
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
    HashSet<String> cloudActuationSet = new HashSet<>();

    public ManageExpressions(Context context){
        this.context = context;
    }
    HighBandwidthNetworking highBandwidthNetworking;

    public void registerExpression(String id, final String expression, boolean wearActuation, final boolean cloudActuation, final boolean phoneActuation) {

        if(cloudActuation){
            cloudActuationSet.add(id);
            initializeAndStartHighBandwidthNetworking();
        }
        try {
            Expression checkExpression =  ExpressionFactory.parse(expression);
            ActuatorManager.registerActuator(context, checkAndRemoveSuffixes(id), checkExpression,
                    null, new ExpressionListener() {
                        @Override
                        public void onNewState(String id, long timestamp, TriState newState) {
                            Log.d(TAG, "new State");
                            //TODO: do this part in a new thread to avoid UI thread being used
                            if(phoneActuation){
                                Log.d(TAG, "phone actuation true");
                                Result result = new Result(timestamp, newState);
                                result.setDeferUntilGuaranteed(false);
                                DeviceClient.getInstance(context).sendExpressionData(checkAndAddSuffixes(id), result);

                            }
                            if(cloudActuation){
                                Log.d(TAG, "cloud actuation true for expression: "+expression);
                                Result result = new Result(timestamp, newState);
                                if(highBandwidthNetworking.NETWORK_AVAILABLE) {
                                    CloudManager.getInstance(context).Actuate(id, Expression.LOCATION_CLOUD, result);
                                }
                                else{
                                    Log.d(TAG, "highBandwidthNetworking not available");
                                }
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
                                    //TODO: if needed send as float
                                    //DeviceClient.getInstance(context).sendExpressionDataAsFloat(checkAndAddSuffixes(id), (Float) newValues[newValues.length-1].getValue());
                                    String value = newValues[0].getValue().toString();
                                    Log.d(TAG,"Firebase token:"+FirebaseInstanceId.getInstance().getToken());

                                }
                            }
                            if(cloudActuation){
                                if(newValues.length > 0) {
                                    TimestampedValue[] recentValue = new TimestampedValue[1];
                                    recentValue[0] =  newValues[newValues.length-1];
                                    Log.d(TAG, "cloud actuation true for expression: "+expression);
                                    if(highBandwidthNetworking.NETWORK_AVAILABLE) {
                                        CloudManager.getInstance(context).Actuate(id, Expression.LOCATION_CLOUD, new Result(recentValue,
                                                newValues[newValues.length - 1].getTimestamp()));
                                        //TODO: if needed send as float
                                        //CloudManager.getInstance(context).ActuateAsFloat(id, Expression.LOCATION_CLOUD, (Float) recentValue[0].getValue());

                                    }
                                    else{
                                        Log.d(TAG, "highBandwidthNetworking not available");
                                    }
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
        if(cloudActuationSet.contains(id)){
            highBandwidthNetworking.releaseHighBandwidthNetwork();
            cloudActuationSet.remove(id);
        }
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


    private void initializeAndStartHighBandwidthNetworking(){

        highBandwidthNetworking = HighBandwidthNetworking.getInstance((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        //while()
        highBandwidthNetworking.requestHighBandwidthNetwork();
    }


}

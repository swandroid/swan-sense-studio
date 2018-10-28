package interdroid.swan.remote.cloud;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import interdroid.swan.engine.EvaluationEngineService;
import interdroid.swan.remote.IRemoteManager;

/**
 * Created by Roshan Bharath Das on 27/06/16.
 */
public class CloudManager implements IRemoteManager {


    private Context context;
    private static CloudManager instance;

    static String SWAN_REGISTER = "/swan/register/";
    static String SWAN_UNREGISTER = "/swan/unregister/";

    static String SWAN_ACTUATION_REGISTER = "/swan/actuation/register/";
    static String SWAN_ACTUATION_UNREGISTER = "/swan/actuation/unregister/";
    static String SWAN_ACTUATION_ACTUATE = "/swan/actuation/actuate/";



    public static synchronized CloudManager getInstance(Context context) {
        if (instance == null) {
            instance = new CloudManager(context);
        }

        return instance;
    }

    CloudManager(Context context){

        this.context = context;
    }


    public static synchronized CloudManager getCreatedInstance(){
        return instance;
    }


    @Override
    public void registerExpression(String id, String expression, String location) {


        CloudCommunication cloudCommunication = new CloudCommunication();
        cloudCommunication.sendRegisterRequest(id,expression,location, SWAN_REGISTER);

    }

    @Override
    public void unregisterExpression(String id, String location) {

        CloudCommunication cloudCommunication = new CloudCommunication();
        cloudCommunication.sendUnregisterRequest(id,location, SWAN_UNREGISTER);

    }


    public void registerActuationExpression(String id, String expression, String location) {


        CloudCommunication cloudCommunication = new CloudCommunication();
        cloudCommunication.sendRegisterRequest(id,expression,location, SWAN_ACTUATION_REGISTER);

    }

    public void unregisterActuationExpression(String id, String location) {

        CloudCommunication cloudCommunication = new CloudCommunication();
        cloudCommunication.sendUnregisterRequest(id,location, SWAN_ACTUATION_UNREGISTER);

    }


    public void Actuate(String id,String location) {

        CloudCommunication cloudCommunication = new CloudCommunication();
        cloudCommunication.sendUnregisterRequest(id,location, SWAN_ACTUATION_ACTUATE);

    }



    @Override
    public void sendResult(String id, String action, String data) {

        Intent notifyIntent = new Intent(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE);
        notifyIntent.setClass(context, EvaluationEngineService.class);
        notifyIntent.putExtra("id", id);
        if(data!=null) {
            notifyIntent.putExtra("data", data);
        }
        context.startService(notifyIntent);
        //context.sendBroadcast(notifyIntent);


    }



}

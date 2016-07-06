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
    public void registerExpression(String id, String expression) {

        CloudCommunication cloudCommunication = new CloudCommunication();
        cloudCommunication.sendRegisterRequest(id,expression);

    }

    @Override
    public void unregisterExpression(String id) {

        CloudCommunication cloudCommunication = new CloudCommunication();
        cloudCommunication.sendUnregisterRequest(id);

    }



    @Override
    public void sendResult(String id, String action, String data) {

        Intent notifyIntent = new Intent(EvaluationEngineService.ACTION_NEW_RESULT_REMOTE);
        notifyIntent.setClass(context, EvaluationEngineService.class);
        notifyIntent.putExtra("id", id);
        notifyIntent.putExtra("data", data);
        context.startService(notifyIntent);
        //context.sendBroadcast(notifyIntent);


    }



}

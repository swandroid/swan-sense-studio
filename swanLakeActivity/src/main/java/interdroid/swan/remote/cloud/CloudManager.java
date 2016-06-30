package interdroid.swan.remote.cloud;

import interdroid.swan.remote.IRemoteManager;

/**
 * Created by Roshan Bharath Das on 27/06/16.
 */
public class CloudManager implements IRemoteManager {


    private static CloudManager instance;


    public static synchronized CloudManager getInstance() {
        if (instance == null) {
            instance = new CloudManager();
        }

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
    public void sendResult() {



    }



}

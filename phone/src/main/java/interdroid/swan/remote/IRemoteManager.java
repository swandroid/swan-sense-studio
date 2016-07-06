package interdroid.swan.remote;

/**
 * Created by Roshan Bharath Das on 27/06/16.
 */
public interface IRemoteManager {

    void registerExpression(String id, String expression);

    void unregisterExpression(String id);

    void sendResult(String id, String action, String data);

}

package interdroid.swan.sensors.impl;

import retrofit.client.Response;

/**
 * Created by Roshan Bharath Das on 27/11/15.
 */
public interface ServerCallbackInterface {



    void execute(String result, Response response);


}

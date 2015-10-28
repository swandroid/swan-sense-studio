package interdroid.swan.jsonsensor.pojos;

import java.util.ArrayList;

/**
 * Created by steven on 02/09/15.
 */
public class JsonRequestComplete {

    public int id;
    public String name;
    public String url;
    public String requestType;
    public ArrayList<Parameter> parameterList;
    public PathToValue pathToValue;

    public JsonRequestComplete(JsonRequestInfo jsonRequestInfo, PathToValue pathToValue) {
        this.id = jsonRequestInfo.id;
        this.name = jsonRequestInfo.name;
        this.url = jsonRequestInfo.url;
        this.requestType = jsonRequestInfo.requestType;
        this.parameterList = jsonRequestInfo.parameterList;
        this.pathToValue = pathToValue;
    }

}

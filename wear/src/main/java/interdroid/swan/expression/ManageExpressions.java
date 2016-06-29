package interdroid.swan.expression;

import android.content.Context;
import android.util.Log;

import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.ValueExpression;
import interdroid.swan.DeviceClient;

/**
 * Created by Veaceslav Munteanu on 5/24/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class ManageExpressions {

    Context context;

    public ManageExpressions(Context context){
        this.context = context;
    }

    public void registerValueExpression(String id, String expression){

        Log.d("fsfd", "registering Expression" + expression);
        try {
            ValueExpression exp = (ValueExpression)ExpressionFactory.parse(expression);
            if(exp == null){
                Log.d("Error", "This should not happen");
            }
            ExpressionManager.registerValueExpression(context, id,
                    (ValueExpression) ExpressionFactory.parse(expression),
                    new ValueExpressionListener() {

                        /* Registering a listener to process new values from the registered sensor*/
                        @Override
                        public void onNewValues(String id,
                                                TimestampedValue[] arg1) {
                            if (arg1 != null && arg1.length > 0) {

                                DeviceClient.getInstance(context).sendExpressionData(id,new Result(arg1,
                                        arg1[arg1.length-1].getTimestamp()));
                                String value = arg1[0].getValue().toString();
                            }
                        }
                    });
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
        ExpressionManager.unregisterExpression(context, id);
    }
}

package interdroid.swan.expression;

import android.content.Context;
import android.util.Log;

import interdroid.swan.swanmain.ExpressionManager;
import interdroid.swan.swanmain.SwanException;
import interdroid.swan.swanmain.ValueExpressionListener;
import interdroid.swan.swansong.ExpressionFactory;
import interdroid.swan.swansong.ExpressionParseException;
import interdroid.swan.swansong.TimestampedValue;
import interdroid.swan.swansong.ValueExpression;

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

        try {
            ExpressionManager.registerValueExpression(context, id,
                    (ValueExpression) ExpressionFactory.parse(expression),
                    new ValueExpressionListener() {

                        /* Registering a listener to process new values from the registered sensor*/
                        @Override
                        public void onNewValues(String id,
                                                TimestampedValue[] arg1) {
                            if (arg1 != null && arg1.length > 0) {
                                String value = arg1[0].getValue().toString();
                                //tv.setText("Value = "+value);
                                Log.d("Wear","Got value+++++++++++" + value);
                            }
                        }
                    });
        } catch (SwanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExpressionParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void unregisterSWANExpression(String id){
        ExpressionManager.unregisterExpression(context, id);
    }
}

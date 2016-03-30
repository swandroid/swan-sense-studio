package interdroid.swan.crossdevice.swanplus.run2gether;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interdroid.swan.ExpressionManager;
import interdroid.swan.R;
import interdroid.swan.SwanException;
import interdroid.swan.ValueExpressionListener;
import interdroid.swan.swansong.ExpressionFactory;
import interdroid.swan.swansong.ExpressionParseException;
import interdroid.swan.swansong.TimestampedValue;
import interdroid.swan.swansong.ValueExpression;

public class NearbyRunnersActivity extends ListActivity {

    public final int REQUEST_CODE = 123;
    private final String TAG = "NearbyRunnersActivity";

    List<Runner> nearbyRunners = new ArrayList<Runner>();
    NearbyRunnersAdapter runnersAdapter = new NearbyRunnersAdapter();

    class NearbyRunnersAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return nearbyRunners.size();
        }

        @Override
        public Object getItem(int position) {
            return nearbyRunners.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(NearbyRunnersActivity.this)
                        .inflate(R.layout.run2gether_list_item, null);
            }

            ((TextView) (convertView.findViewById(R.id.username)))
                    .setText(((Runner)getItem(position)).getUsername());
            ((TextView) (convertView.findViewById(R.id.goal)))
                    .setText("Goal: " + ((Runner) getItem(position)).getGoal());
            ((TextView) (convertView.findViewById(R.id.level)))
                    .setText("Level: " + ((Runner)getItem(position)).getLevel());
            ((TextView) (convertView.findViewById(R.id.gender)))
                    .setText("Gender: " + ((Runner) getItem(position)).getGender());
            ((TextView) (convertView.findViewById(R.id.age)))
                    .setText("Age: " + ((Runner)getItem(position)).getAge());
            ((TextView) (convertView.findViewById(R.id.weight)))
                    .setText("Weight: " + ((Runner)getItem(position)).getWeight());
            ((TextView) (convertView.findViewById(R.id.height)))
                    .setText("Height: " + ((Runner) getItem(position)).getHeight());

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Nearby Runners");
        getListView().setAdapter(runnersAdapter);

        Runner runner = new Runner("Gigel");
        nearbyRunners.add(runner);
        runnersAdapter.notifyDataSetChanged();

        // register sensor
        registerFitnessSensor();
    }

    public void registerFitnessSensor() {
        String myExpression = "SWAN1@fitness:avg_speed{ANY,0}";
        registerSWANSensor(myExpression);
    }

    /* Register expression to SWAN */
    private void registerSWANSensor(String myExpression){
        try {
            ExpressionManager.registerValueExpression(this, String.valueOf(REQUEST_CODE),
                (ValueExpression) ExpressionFactory.parse(myExpression),
                new ValueExpressionListener() {

                    /* Registering a listener to process new values from the registered sensor*/
                    @Override
                    public void onNewValues(String id, TimestampedValue[] arg1) {
                        if(arg1.length > 0) {
                            String data = arg1[0].getValue().toString();
                            Log.d(TAG, "Received fitness data: " + data);

                            Runner runner = nearbyRunners.get(0);
                            Map<String, String> dataMap = getRunningData(data);
                            runner.setUsername(dataMap.get("username"));
                            runner.setGoal(dataMap.get("goal"));
                            runner.setLevel(dataMap.get("runLevel"));
                            runner.setGender(dataMap.get("gender"));
                            runner.setAge(dataMap.get("age"));
                            runner.setWeight(dataMap.get("weight"));
                            runner.setHeight(dataMap.get("height"));

                            runnersAdapter.notifyDataSetChanged();
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

    private Map<String, String> getRunningData(String data) {
        Map<String, String> dataMap = new HashMap<String, String>();
        String[] dataParts = data.split("&");

        for(String dataItem : dataParts) {
            String[] dataPair = dataItem.split("=", -1);
            dataMap.put(dataPair[0], dataPair[1]);
        }

        return dataMap;
    }
}

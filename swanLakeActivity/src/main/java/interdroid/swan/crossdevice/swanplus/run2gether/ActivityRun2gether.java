package interdroid.swan.crossdevice.swanplus.run2gether;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import interdroid.swan.ExpressionManager;
import interdroid.swan.R;
import interdroid.swan.SwanException;
import interdroid.swan.ValueExpressionListener;
import interdroid.swan.crossdevice.swanplus.FitnessBroadcastReceiver;
import interdroid.swan.sensors.impl.FitnessSensor;
import interdroid.swan.swansong.ExpressionFactory;
import interdroid.swan.swansong.ExpressionParseException;
import interdroid.swan.swansong.TimestampedValue;
import interdroid.swan.swansong.ValueExpression;

public class ActivityRun2gether extends Activity {

	private final String TAG = "NearbyRunnersActivity";

	BroadcastReceiver r2gBcastReceiver;
	IntentFilter r2gIntentFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run2gether);
		
		Spinner spinner1 = (Spinner) findViewById(R.id.run_level_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
		        R.array.run_levels_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner1.setAdapter(adapter1);

		// register receiver for getting requests from fitness sensor
		r2gBcastReceiver = new Run2getherBroadcastReceiver(this);
		r2gIntentFilter = new IntentFilter();
		r2gIntentFilter.addAction(FitnessSensor.ACTION_REQ_FITNESS_DATA);
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(r2gBcastReceiver, r2gIntentFilter);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(r2gBcastReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run2gether, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_nearby) {
			startActivity(new Intent(this, NearbyRunnersActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void run2gether(View view) {
	}

	public String getRunningData() {
		String data = "";

		EditText usernameEdit = (EditText) findViewById(R.id.username);
		String username = usernameEdit.getText().toString();
		data += "username=" + username;

		EditText goalEdit = (EditText) findViewById(R.id.goal);
		String goal = goalEdit.getText().toString();
		data += "&goal=" + goal;

		Spinner spinner = (Spinner) findViewById(R.id.run_level_spinner);
		String runningLevel = spinner.getSelectedItem().toString();
		data += "&runLevel=" + runningLevel;

		String gender = "";
		RadioButton maleRadio = (RadioButton) findViewById(R.id.radio_male);
		if(maleRadio.isChecked()) {
			gender = "male";
		}
		RadioButton femaleRadio = (RadioButton) findViewById(R.id.radio_female);
		if(femaleRadio.isChecked()) {
			gender = "female";
		}
		data += "&gender=" + gender;

		EditText ageEdit = (EditText) findViewById(R.id.age);
		String age = ageEdit.getText().toString();
		data += "&age=" + age;

		EditText weightEdit = (EditText) findViewById(R.id.weight);
		String weight = weightEdit.getText().toString();
		data += "&weight=" + weight;

		EditText heightEdit = (EditText) findViewById(R.id.height);
		String height = heightEdit.getText().toString();
		data += "&height=" + height;

		return data;
	}

}

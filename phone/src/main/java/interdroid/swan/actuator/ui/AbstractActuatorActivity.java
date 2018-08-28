package interdroid.swan.actuator.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import interdroid.swan.R;
import interdroid.swan.actuator.Actuator;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.SensorValueExpression;

/**
 * An abstract {@link Activity} for configuring {@link Actuator Actuators}. Each actuator needs an
 * activity that extends this class and those activities only need to implement the abstract
 * functions.
 */
public abstract class AbstractActuatorActivity extends Activity implements ConfigurationAdapter.OnClickListener {

    private static final String TAG = AbstractActuatorActivity.class.getSimpleName();

    private TextView mPathText;

    private ConfigurationAdapter mAdapter;

    private String mPath;

    private String[] mAvailablePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuator);

        setTitle(getEntity());

        mAvailablePaths = getPaths();

        RecyclerView configurationList = (RecyclerView) findViewById(R.id.configuration_list);
        configurationList.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ConfigurationAdapter(this);

        mAdapter.swap(getParameterKeys(), getParameterDefaultValues());
        configurationList.setAdapter(mAdapter);

        mPath = mAvailablePaths[0];

        String expressionStr = getIntent().getStringExtra("expression");
        loadExpressionValues(expressionStr);

        mPathText = (TextView) findViewById(R.id.path);
        mPathText.setText(mPath);

        mAdapter.notifyDataSetChanged();
    }

    /**
     * @return the keys of the parameters that this actuator accepts
     */
    protected abstract String[] getParameterKeys();

    /**
     * @return the default values for the parameters that this actuator accepts. it has to be in the
     * same order as the one that {@link AbstractActuatorActivity#getParameterKeys()} returns
     */
    protected abstract String[] getParameterDefaultValues();

    /**
     * @return the supported value paths
     */
    protected abstract String[] getPaths();

    /**
     * @return the entity of the actuator
     */
    protected abstract String getEntity();

    @Override
    public void onClick(final int position, String key, String value) {
        final EditText edittext = new EditText(this);
        edittext.setText(value);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Edit parameter")
                .setMessage(key)
                .setView(edittext)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mAdapter.swap(position, edittext.getText().toString());
                        mAdapter.notifyDataSetChanged();
                    }
                })

                .setNegativeButton("Cancel", null);

        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("expression", buildExpression());
        setResult(Activity.RESULT_OK, result);

        finish();
    }

    /**
     * Called when the path is clicked on. It opens a dialog where the user can choose from the
     * available value paths.
     *
     * @param v the view that fired the click event
     */
    public void onPathClicked(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a path")
                .setItems(mAvailablePaths, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mPath = getPaths()[which];
                        mPathText.setText(mPath);
                    }
                });
        builder.show();
    }

    /**
     * Load the path and parameters from the given expression string. It is used when the user is
     * editing an expression that was in the intent.
     *
     * @param expressionStr the string representation of the {@link SensorValueExpression}
     */
    private void loadExpressionValues(String expressionStr) {
        // no extra provided
        if (expressionStr == null) {
            return;
        }

        Expression expression;
        try {
            expression = ExpressionFactory.parse(expressionStr);

            if (expression == null) {
                Log.w(TAG, "failed to parse extra expression");
                return;
            }

        } catch (ExpressionParseException e) {
            Log.w(TAG, "failed to parse extra expression", e);
            return;
        }

        if (!(expression instanceof SensorValueExpression)) {
            Log.w(TAG, "expression is not a SensorValueExpresion object");
            return;
        }

        SensorValueExpression sve = (SensorValueExpression) expression;

        String path = sve.getValuePath();
        if (!isValidPath(path)) {
            Log.w(TAG, "unsupported value path");
        }

        mPath = path;

        Bundle config = sve.getConfiguration();

        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            String key = mAdapter.getKey(i);

            String value = config.getString(key);

            mAdapter.swap(i, value);
        }
    }

    /**
     * Checks whether the specified path is supported or not
     *
     * @param path the path to check
     * @return true if valid
     */
    private boolean isValidPath(String path) {
        for (String validPath : mAvailablePaths) {
            if (validPath.equals(path)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return Build an expression string to be returned to the calling activity in the result
     * intent.
     */
    private String buildExpression() {
        StringBuilder builder = new StringBuilder();

        // TODO: 2018-07-07 only self location supported
        builder.append("self@")
                .append(getEntity())
                .append(':')
                .append(mPath);

        int paramCount = mAdapter.getNonNullCount();
        if (paramCount > 0) {
            builder.append('?');

            boolean separator = false;

            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                String value = mAdapter.getValue(i);
                if (value != null && !value.isEmpty()) {

                    if (separator) {
                        builder.append('#');
                    } else {
                        separator = true;
                    }

                    builder.append(mAdapter.getKey(i))
                            .append("='")
                            .append(value)
                            .append('\'');
                }
            }
        }

        return builder.toString();
    }
}

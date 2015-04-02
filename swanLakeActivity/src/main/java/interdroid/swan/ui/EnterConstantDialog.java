package interdroid.swan.ui;

import interdroid.swan.R;
import interdroid.swan.swansong.ConstantValueExpression;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class EnterConstantDialog extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        setContentView(R.layout.expression_builder_enter_constant_dialog);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Object constant;
                String constantString = ((EditText) findViewById(R.id.constant))
                        .getText().toString();

                int typeId = ((RadioGroup) findViewById(R.id.type))
                        .getCheckedRadioButtonId();

                if (typeId == R.id.double_type) {
                    constant = Double.parseDouble(constantString);
                } else if (typeId == R.id.integer_type) {
                    constant = Integer.parseInt(constantString);
                } else if (typeId == R.id.long_type) {
                    constant = Long.parseLong(constantString);
                } else if (typeId == R.id.float_type) {
                    constant = Float.parseFloat(constantString);
                } else {
                    constant = constantString;
                }
                Intent result = new Intent();
                result.putExtra("Expression", new ConstantValueExpression(
                        constant).toParseString());
                setResult(RESULT_OK, result);
                finish();
            }
        });

    }
}

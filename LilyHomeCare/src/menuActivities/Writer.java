package menuActivities;

import lily.homecare.R;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Writer extends Activity {

	private EditText editTextData;
	private String tagType = "door";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_writer);
		RadioGroup radioGroup=(RadioGroup)(findViewById(R.id.radioGroupOptions));
		radioGroup.check(R.id.door);
		editTextData = (EditText) findViewById(R.id.editTextMessage);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
		case R.id.task:
			if (checked) {
				tagType="task";
			}

			break;
		case R.id.door:
			if (checked) {
				tagType="door";
			}

		default:
			break;
		}
	}

	public void writeOnTag(View view) {
		String message = editTextData.getText().toString().trim();

		if (tagType.equals("task") || tagType.equals("door")) {
			if (message != null && !message.equals("")) {
				Intent intent = new Intent(this, Write.class);
				intent.putExtra("message", message);
				intent.putExtra("type", tagType);
				startActivity(intent);
			} else {
				Toast.makeText(this, "Please Enter data and type",
						Toast.LENGTH_SHORT).show();
				;
			}
		}

	}

}

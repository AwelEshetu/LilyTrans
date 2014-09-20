package com.example.translily;

import java.util.Calendar;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {
	private EditText name;
	private EditText from;
	private EditText to;
	private EditText phoneNumber;
	private Button datePicker;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		to = (EditText) findViewById(R.id.to);
		from = (EditText) findViewById(R.id.from);
		phoneNumber = (EditText) findViewById(R.id.phoneNumber);
		name = (EditText) findViewById(R.id.name);
		datePicker = (Button)findViewById(R.id.datePicker);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Setting, about page
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void askForRide(View V) {
		Intent intent = new Intent(this, AskRideActivity.class);
		intent.putExtra("name", name.getText().toString());
		intent.putExtra("from", from.getText().toString());
		intent.putExtra("to", to.getText().toString());
		intent.putExtra("contact", phoneNumber.getText().toString());
		intent.putExtra("date", datePicker.getText());
		// System.out.println(from.getText().toString());
		startActivity(intent);
	}

	

	@SuppressLint("NewApi")
	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		Button datePicker;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int currentYear = c.get(Calendar.YEAR);
			int currentMonth = c.get(Calendar.MONTH);
			int currentDay = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, currentYear, currentMonth, currentDay);
		}

		@Override
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			Log.d("arg0", arg0.toString());
			
			datePicker=(Button)getActivity().findViewById(R.id.datePicker);
			String date= Integer.toString(arg3)+"-"+Integer.toString(arg2)+"-"+Integer.toString(arg1);
					
			datePicker.setText(date);
		
		}
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "Date Picker");
	}

}
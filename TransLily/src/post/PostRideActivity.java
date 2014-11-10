package post;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.translili.R;

import data.LilyUrls;

public class PostRideActivity extends ActionBarActivity implements
		OnItemSelectedListener {

	private EditText editTextTaxiID;
	private TextView taxiIdTV;
	private EditText editTextStartingPoint;
	private EditText editTextDestinationPoint;
	private EditText editTextNumberOfPerson;
	private EditText editTextComment;
	private Button buttonTime;
	private Button buttonDate;
	private String phone;
	private String name;
	String serviceProvider;

	private Spinner serviceProviderSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_ride);

		serviceProviderSpinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.service_providers, R.layout.spiner_layout);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

		serviceProviderSpinner.setAdapter(adapter);
		serviceProvider = serviceProviderSpinner.getSelectedItem().toString();
		serviceProviderSpinner.setOnItemSelectedListener(this);
		editTextTaxiID = (EditText) findViewById(R.id.taxi_id_post);
		editTextStartingPoint = (EditText) findViewById(R.id.starting_point_post);
		editTextDestinationPoint = (EditText) findViewById(R.id.destination_point_post);
		editTextNumberOfPerson = (EditText) findViewById(R.id.number_of_person_post);
		editTextComment = (EditText) findViewById(R.id.coment_post);
		buttonTime = (Button) findViewById(R.id.time_picker_post);
		buttonDate = (Button) findViewById(R.id.date_picker_post);
		taxiIdTV = (TextView) findViewById(R.id.textview_post_ride);

		SharedPreferences sharedPref = PostRideActivity.this
				.getSharedPreferences(getString(R.string.com_lily_pre), 0);

		phone = sharedPref.getString("phone", "").trim();
		name = sharedPref.getString("name", "").trim();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.post_ride, menu);
		return true;
	}

	public void ResetFields() {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean isFormFiledProperly() {

		if (editTextStartingPoint.getText().toString().trim().equals("")) {
			return false;
		}
		if (editTextDestinationPoint.getText().toString().trim().equals("")) {
			return false;
		}
		if (buttonDate.getText().toString().trim()
				.equalsIgnoreCase("Pick date")) {
			return false;
		}
		if (buttonTime.getText().toString().trim()
				.equalsIgnoreCase("Pick time")) {
			return false;
		}

		return true;
	}

	/**
	 * date Picker
	 * 
	 * @author Lily
	 * 
	 */
	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		private Button datePicker;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int currentYear = c.get(Calendar.YEAR);
			int currentMonth = c.get(Calendar.MONTH);
			int currentDay = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, currentYear,
					currentMonth, currentDay);
		}

		@Override
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			datePicker = (Button) getActivity().findViewById(
					R.id.date_picker_post);
			String date = Integer.toString(arg3) + "-"
					+ Integer.toString(arg2 + 1) + "-" + Integer.toString(arg1);
			datePicker.setText(date);

		}
	}

	public void showDatePickerDialogPost(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "Date Picker");
	}

	/**
	 * Time picker
	 * 
	 * @author Thinkpad
	 * 
	 */

	public static class TimePicerDialog extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {
		private Button timePicker;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of DatePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			timePicker = (Button) getActivity().findViewById(
					R.id.time_picker_post);
			String time = Integer.toString(hourOfDay) + ":"
					+ Integer.toString(minute);
			timePicker.setText(time);

		}

	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePicerDialog();
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}

	@SuppressLint("SimpleDateFormat")
	public void postRide(View view) {

		if (isFormFiledProperly()) {

			String startingPoint = editTextStartingPoint.getText().toString();
			String destination = editTextDestinationPoint.getText().toString();
			String groupId=editTextTaxiID.getText().toString();
			String numberOfPerson = editTextNumberOfPerson.getText().toString();

			String comment = editTextComment.getText().toString();
			String pickUpTime = buttonTime.getText().toString();

			String date = buttonDate.getText().toString();

			Date mysqlformatdate = null;
			String mySqldate = null;
			SimpleDateFormat dateForamt = new SimpleDateFormat("d-MM-yyyy");
			try {
				mysqlformatdate = dateForamt.parse(date);
				String dateFormat = "yyyy-MM-dd";
				mySqldate = DateFormat.format(dateFormat, mysqlformatdate)
						.toString();

			} catch (ParseException e) {
				e.printStackTrace();
			}

			new PostRidesAsynckTask().execute(serviceProvider, groupId,
					startingPoint, destination, phone, pickUpTime, mySqldate,
					numberOfPerson, comment);
		} else {
			Toast.makeText(this, "Please complete the forms",
					Toast.LENGTH_SHORT).show();
		}

	}

	private class PostRidesAsynckTask extends AsyncTask<String, Void, String> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(PostRideActivity.this,
					"Saving", "Saving....");
		}

		@Override
		protected String doInBackground(String... params) {
		
			String content = null;
			try {

				System.out.println(params[0]);
				// get the first argument passed to the execute method

				List<NameValuePair> nameValuePaire = new ArrayList<NameValuePair>(10);

				nameValuePaire.add(new BasicNameValuePair("name", params[0]));
				
				nameValuePaire.add(new BasicNameValuePair("id", params[1]));
				nameValuePaire
						.add(new BasicNameValuePair("starting", params[2]));
				nameValuePaire.add(new BasicNameValuePair("destination",
						params[3]));
				nameValuePaire.add(new BasicNameValuePair("phonenumber",
						params[4]));
				nameValuePaire.add(new BasicNameValuePair("time", params[5]));
				nameValuePaire.add(new BasicNameValuePair("date", params[6]));
				nameValuePaire
						.add(new BasicNameValuePair("capacity", params[7]));
				nameValuePaire
						.add(new BasicNameValuePair("comment", params[8]));
				nameValuePaire.add(new BasicNameValuePair("ride_post_name", name));

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(LilyUrls.POST_URL);
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePaire));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream inputStream = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line);
				}
				inputStream.close(); // free memory

				content = stringBuffer.toString();
				return content;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(PostRideActivity.this, result, Toast.LENGTH_LONG)
					.show();
			progressDialog.setMessage(result);
			progressDialog.dismiss();

		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

		serviceProvider = arg0.getItemAtPosition(arg2).toString().trim();

		if (serviceProvider.equalsIgnoreCase("Taxi")) {

			editTextTaxiID.setVisibility(View.VISIBLE);
			taxiIdTV.setVisibility(View.VISIBLE);
		} else {
			editTextTaxiID.setText("");
			editTextTaxiID.setVisibility(View.GONE);
			taxiIdTV.setVisibility(View.GONE);
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	
	}

}

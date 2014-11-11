package request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.translili.R;
import com.example.translili.Setting;

import data.LilyUrls;
import data.Parser;
import data.ScheduleList;

@SuppressLint("SimpleDateFormat")
public class AskRideActivity extends ActionBarActivity {
	final static int ONE = 1;
	private Vector<ScheduleList> schedules = null;
	private String phonenumber;
	private ListView listView;
	private TextView tvStartingPoint;
	private TextView tvDestination;
	private Button buttonDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ask_ride);
		listView = (ListView) findViewById(R.id.ask_ride_lv);
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ScheduleList sd = (ScheduleList) parent
						.getItemAtPosition(position);
				Intent intent = new Intent(AskRideActivity.this,
						SearchRideActivity.class);
				intent.putExtra("serviceGroup", sd.getServiceGroup());
				intent.putExtra("taxiId", sd.getTaxiID());
				intent.putExtra("name", sd.getName());
				intent.putExtra("phone", sd.getPhonenumber());
				intent.putExtra("transportId", sd.getTransportID());
				intent.putExtra("starting", sd.getStartingPoint());
				intent.putExtra("destination", sd.getDestinationPoint());
				intent.putExtra("date", sd.getDate());
				intent.putExtra("pickingTime", sd.getPickUpTime());
				intent.putExtra("status", sd.getStatus());
				startActivity(intent);

			}
		});
		
	

		tvStartingPoint = (TextView) findViewById(R.id.ask_ride_from);
		tvStartingPoint.setText("Starting: any");
		buttonDate = (Button) findViewById(R.id.datePicker);
		tvDestination = (TextView) findViewById(R.id.ask_ride_to);
		tvDestination.setText("Destination: any");
		buttonDate.setText(DateFormat.format("d-MM-yyyy", new Date()));

		// getting phone number
		SharedPreferences sharedPref = AskRideActivity.this
				.getSharedPreferences(getString(R.string.com_lily_pre), 0);

		phonenumber = sharedPref.getString("phone", "").trim();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		filterSearches(new View(this));
	}

	/**
	 * Setting, about page
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, Setting.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void filterSearches(View view) {
		String startPoint = tvStartingPoint.getText().toString().trim();
		String destination = tvDestination.getText().toString().trim();
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
		if (startPoint.equalsIgnoreCase("Starting: any")) {
			startPoint="";
		}
		if (destination.equalsIgnoreCase("Destination: any")) {
			destination="";
		}

		new SearchAvilableTransports().execute(startPoint, destination,
				mySqldate);

	}

	public void filterSearchRide(View V) {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_sort_searging, null);

		final EditText fromEditText = (EditText) layout.findViewById(R.id.from);
		final EditText toEditText = (EditText) layout.findViewById(R.id.to);

		// Building dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);

		builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(fromEditText.getText().toString().trim().equalsIgnoreCase("")){
					tvStartingPoint.setText("Starting: any");
				}else{
					tvStartingPoint.setText(fromEditText.getText().toString().trim());
				}
				if(toEditText.getText().toString().trim().equalsIgnoreCase("")){
					tvDestination.setText("Destination: any");
				}else{
					tvDestination.setText(toEditText.getText().toString().trim());
				}
			
			
				dialog.dismiss();

			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

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
			return new DatePickerDialog(getActivity(), this, currentYear,
					currentMonth, currentDay);
		}

		@Override
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub

			datePicker = (Button) getActivity().findViewById(R.id.datePicker);

			String date = Integer.toString(arg3) + "-"
					+ Integer.toString(arg2 + ONE) + "-"
					+ Integer.toString(arg1);
			datePicker.setText(date);

		}
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "Date Picker");
	}

	// should be search for to day
	/**
	 * Desplay Todays Ride
	 * 
	 * @author bereket
	 * 
	 */
	private class ScheduleArrayAdapter extends ArrayAdapter<ScheduleList> {
		Vector<ScheduleList> scheduleList;
		Context context;

		public ScheduleArrayAdapter(Context context,
				Vector<ScheduleList> resource) {
			super(context, R.layout.dialog_my_post_rides, resource);
			this.scheduleList = resource;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.ask_ride_list_view,
						parent, false);
			}

			TextView tvStartingDestination = (TextView) convertView
					.findViewById(R.id.starting_destination_point_tv_ask);
			TextView pickingTime = (TextView) convertView
					.findViewById(R.id.picking_time_tv);
			TextView serviceGroup = (TextView) convertView
					.findViewById(R.id.service_group_tv);
			tvStartingDestination.setText(scheduleList.get(position)
					.getStartingPoint()
					+ " To "
					+ scheduleList.get(position).getDestinationPoint());

			pickingTime.setText(scheduleList.get(position).getPickUpTime());

			serviceGroup.setText(scheduleList.get(position).getServiceGroup());
			TextView requestTv = (TextView) convertView
					.findViewById(R.id.ask_ride_request_tv);

			if (scheduleList.get(position).getStatus()
					.equalsIgnoreCase("Approved")) {
				requestTv.setText("Approved");
				requestTv.setTextColor(Color.GREEN);

			} else if (scheduleList.get(position).getStatus()
					.equalsIgnoreCase("Cancled")) {

				requestTv.setText("Request Canceled: ");
				requestTv.setTextColor(Color.RED);

			} else if (scheduleList.get(position).getStatus()
					.equalsIgnoreCase("Waiting")) {
				requestTv.setText("Waiting");
				requestTv.setTextColor(Color.DKGRAY);

			} else {
				requestTv.setText("Request");

			}

			return convertView;
		}
	}

	/**
	 * Searching Today's rides
	 * 
	 * @author Tedo
	 * 
	 */

	private class SearchAvilableTransports extends
			AsyncTask<String, Void, String> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(AskRideActivity.this,
					"Searching", "Searching avilable rides for today");
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String content = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(
						LilyUrls.SEARCH_AVILABLE_TRANSPORTS);
				List<NameValuePair> nameValuePaire = new ArrayList<NameValuePair>(
						4);

				nameValuePaire
						.add(new BasicNameValuePair("starting", params[0]));
				nameValuePaire.add(new BasicNameValuePair("destination",
						params[1]));
				nameValuePaire.add(new BasicNameValuePair("date", params[2]));
				nameValuePaire.add(new BasicNameValuePair("phonenumber",
						phonenumber));

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
			progressDialog.dismiss();
			System.out.println(result);
			try {

				schedules = Parser.parse(result);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			listView.setAdapter(new ScheduleArrayAdapter(AskRideActivity.this,
					schedules));
		}

	}

}

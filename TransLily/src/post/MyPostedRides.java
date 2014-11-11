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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.translili.R;

import data.LilyUrls;
import data.Parser;
import data.ScheduleList;

@SuppressLint("SimpleDateFormat")
public class MyPostedRides extends ActionBarActivity {
	private String phone;
	private Button dateButton;
	private TextView dateTv;
	private ListView listView;
	private AlertDialog dialog;
	String selectedItem=null;
	int transportId=0;
	String dateSqlFormat=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_post_rides);
		dateButton = (Button) findViewById(R.id.date_picker_my_post);
		dateTv = (TextView) findViewById(R.id.datetv_my_post_ride);

		SharedPreferences sharedPref = MyPostedRides.this.getSharedPreferences(
				getString(R.string.com_lily_pre), 0);
		phone = sharedPref.getString("phone", "").trim();

		listView = (ListView) findViewById(R.id.my_posted_list);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ScheduleList sd = (ScheduleList) parent.getItemAtPosition(position);
				
				Intent intent = new Intent(MyPostedRides.this,
						ReservedPostRides.class);
				intent.putExtra("serviceGroup", sd.getServiceGroup());
				intent.putExtra("taxiId", sd.getTaxiID());
				intent.putExtra("name", sd.getName());
				intent.putExtra("id", sd.getTransportID());
				intent.putExtra("starting", sd.getStartingPoint());
				intent.putExtra("destination", sd.getDestinationPoint());
				intent.putExtra("date", sd.getDate());
				intent.putExtra("pickingTime", sd.getPickUpTime());
				startActivity(intent);

			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View arg1, int arg2, long arg3) {
				ScheduleList sd = (ScheduleList) parent.getItemAtPosition(arg2);
				 selectedItem=sd.getStartingPoint()+" - " + sd.getDestinationPoint();
				 transportId=sd.getTransportID();
				filterSearchRide();
				return true;
			}
		});

		String dateSqlFormat = DateFormat.format("yyyy-MM-dd", new Date())
				.toString();
		dateTv.setText(DateFormat.format("dd-MM-yyyy", new Date()).toString()
				+ " >= ");

		new SearchMyPostAsynckTask().execute(phone, dateSqlFormat);
	}
	@Override
	protected void onResume() {
		super.onResume();
		new SearchMyPostAsynckTask().execute(phone, dateSqlFormat);
	}

	public void retivePostRides(View view) {

		String date = dateButton.getText().toString();
		if (date.equalsIgnoreCase("pick date")) {
			Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT)
					.show();
			return;
		}
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
		new SearchMyPostAsynckTask().execute(phone, mySqldate);
	}

	public void filterSearchRide() {
	
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_my_post_rides, null);
		TextView tvHeading= (TextView)layout.findViewById(R.id.tv_my_post_rides);
		tvHeading.setText(selectedItem);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);

		dialog = builder.create();
		dialog.show();

	}
	public void dialogButtonCancel(View view){
		dialog.dismiss();
	}
	public void dialogButtonDelete(View view){
		dialog.dismiss();
		new DeletePostRideAsynckTask().execute(transportId);
		onResume();
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		private Button datePicker;
		private TextView tvDate;

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
			Log.d("arg0", arg0.toString());

			datePicker = (Button) getActivity().findViewById(
					R.id.date_picker_my_post);
			tvDate = (TextView) getActivity().findViewById(
					R.id.datetv_my_post_ride);

			String date = Integer.toString(arg3) + "-"
					+ Integer.toString(arg2 + 1) + "-" + Integer.toString(arg1);

			datePicker.setText(date);
			tvDate.setText(date + " >=");

		}
	}

	public void showDatePickerDialogMYPost(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "Date Picker");
	}

	// Search

	private class SearchMyPostAsynckTask extends
			AsyncTask<String, Void, String> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(MyPostedRides.this,
					"Searching ", "Searching post rides....");
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String content = null;
			try {

				System.out.println(params[0]);
				// get the first argument passed to the execute method

				List<NameValuePair> nameValuePaire = new ArrayList<NameValuePair>(
						2);

				nameValuePaire.add(new BasicNameValuePair("phonenumber",
						params[0]));
				nameValuePaire.add(new BasicNameValuePair("date", params[1]));

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						LilyUrls.URL_FOR_SEARCHING_POST_RIDES);
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
			try {
				System.out.println(result);
				Parser.parse(result);
				listView.setAdapter(new MyPostRideArrayAdapter(
						MyPostedRides.this, Parser.parse(result)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 */
	private class MyPostRideArrayAdapter extends ArrayAdapter<ScheduleList> {
		Vector<ScheduleList> scheduleList;
		Context context;

		public MyPostRideArrayAdapter(Context context,
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
			requestTv.setText("show requests for this ride");

			return convertView;
		}
	}

	/**
	 * Delete Post rides
	 * 
	 * @author bereket
	 * 
	 */
	// TODO
	private class DeletePostRideAsynckTask extends
			AsyncTask<Integer, Void, String> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(MyPostedRides.this,
					"Deleteting", "Deleting post ride....");
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String content = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(LilyUrls.DELETE_POST_URL);
				List<NameValuePair> nameValuePaire = new ArrayList<NameValuePair>(
						1);

				nameValuePaire.add(new BasicNameValuePair("transportId",
						Integer.toString(params[0])));

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
			Toast.makeText(MyPostedRides.this, result, Toast.LENGTH_SHORT).show();
			progressDialog.dismiss();
		}

	}

}

package request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.translili.R;

import data.LilyUrls;

/**
 * Searching and booking
 * 
 * @author Thinkpad
 * 
 */
public class SearchRideActivity extends ActionBarActivity {
	
	private TextView userProfile;


	private String userPhonenumber = null;
	private String userName;
	
	private Button buttonRequestCancel;
	private ImageButton imageButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_ride);
		userProfile = (TextView) findViewById(R.id.userProfile);
		buttonRequestCancel= (Button)findViewById(R.id.button_request_cancel);
		
		// setting phone number for image button 
		imageButton =(ImageButton)findViewById(R.id.ib_search_ride);
		imageButton.setTag(getIntent().getStringExtra("phone"));
		//seting phone number text edit 
		TextView tvPhone=(TextView)findViewById(R.id.tv_phonenumber_search_ride);
		tvPhone.setText(getIntent().getStringExtra("phone"));
		
		SharedPreferences sharedPref = SearchRideActivity.this
				.getSharedPreferences(getString(R.string.com_lily_pre), 0);
		userName = sharedPref.getString("name", "").trim();
	
		userPhonenumber = sharedPref.getString("phone", "").trim();
		
		String serviceGroup =getIntent().getStringExtra("serviceGroup");
		String taxiId=getIntent().getStringExtra("taxiId");
		String serviceProviderName=getIntent().getStringExtra("name");
		String pickingTime=getIntent().getStringExtra("pickingTime");
		String from = getIntent().getStringExtra("starting");
		String to = getIntent().getStringExtra("destination");
		String date = getIntent().getStringExtra("date");
		
		userProfile.setText(Html.fromHtml("<b>Service Group<b/>"+ serviceGroup + "<br/>"+
											"<b>Taxi Id:  <b/>: " + taxiId + "<br/>" +
										"<b>Name <b/>" + serviceProviderName + "<br/>" +
										"<b>From: <b/>: "+ from + "<br/>" + 
										"<b>To: <b/>: " + to + "<br/>" +
											"<b>Picking Time: <b/>: " + pickingTime + "<br/>" 
										+ "<b>Date <b/>: " + date));
		
		int transportId=getIntent().getIntExtra("transportId", 0);
		buttonRequestCancel.setTag(transportId);
		if(getIntent().getStringExtra("status").equalsIgnoreCase("waiting")){
			buttonRequestCancel.setText("Wating: click to cancel your request");
			buttonRequestCancel.setTextColor(Color.DKGRAY);
		}else if (getIntent().getStringExtra("status").equalsIgnoreCase("approved")){
			buttonRequestCancel.setText("Approved: Click to cancel your request");
			buttonRequestCancel.setTextColor(Color.DKGRAY);
		}else if(getIntent().getStringExtra("status").equalsIgnoreCase("Cancled")){
			buttonRequestCancel.setText("Request: Canceled");
			buttonRequestCancel.setEnabled(false);
			buttonRequestCancel.setTextColor(Color.RED);
		}else{
			buttonRequestCancel.setText("Request");
		}
		

	}

	public void callNumber(View view) {
		ImageButton im = (ImageButton) view;

		try {
			String str = (String) im.getTag();

			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + str));
			startActivity(callIntent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * book the transport
	 * 
	 * @param view
	 */
	public void bookTransport(View view) {
		Button button = (Button) view;
		int id = (Integer) button.getTag();
		if (button.getText().toString().equalsIgnoreCase("Request")) {
			button.setText("Wating: click to cancel your request");
			button.setTextColor(Color.DKGRAY);
			new BookRideAsynckTask().execute(id);
		} else {
			button.setText("Request");
			button.setTextColor(getResources().getColor(R.color.text_color));
			new CancelRideAsynckTask().execute(id);
		}

	}

	

	/**
	 * booking time
	 * 
	 * @author Thinkpad
	 * 
	 */

	private class BookRideAsynckTask extends AsyncTask<Integer, Void, String> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(SearchRideActivity.this,
					"Requesting", "Sending request....");
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String content = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(LilyUrls.BOOKING_URL);
				List<NameValuePair> nameValuePaire = new ArrayList<NameValuePair>(
						3);
				nameValuePaire.add(new BasicNameValuePair("name", userName));
				nameValuePaire.add(new BasicNameValuePair("phonenumber",
						userPhonenumber));
				nameValuePaire.add(new BasicNameValuePair("status", "Waiting"));
				nameValuePaire.add(new BasicNameValuePair("id", Integer
						.toString(params[0])));

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
			System.out.println(result);
			progressDialog.dismiss();
		}

	}

	// for cancling the app
	private class CancelRideAsynckTask extends AsyncTask<Integer, Void, String> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(SearchRideActivity.this,
					"Cancling", "Cancling request....");
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String content = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(
						LilyUrls.USER_CANCEL_REQUEST_URL);
				List<NameValuePair> nameValuePaire = new ArrayList<NameValuePair>(
						2);

				nameValuePaire.add(new BasicNameValuePair("id", Integer
						.toString(params[0])));
				nameValuePaire
						.add(new BasicNameValuePair("phone", userPhonenumber));

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
		}

	}
}

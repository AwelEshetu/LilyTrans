package server.fi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lily.homecare.TaskObsorver;
import model.Customer;
import model.Tasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Upload and Download data from server
 * 
 * @author bereket
 * 
 */
public class Server implements TaskDataDownloder {
	private Customer tasks = null;
	private Context context = null;
	private final static String url = "http://tutbereket.net/lily_homecare/get_data_nfc_tag_id.php";
	private final static String uploadUrl = "http://tutbereket.net/lily_homecare/db/add_logs.php";
	private TaskObsorver mObsorver;
	private String tagData;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public Server(Context context) {
		context = this.context;
	}

	@Override
	public void retriveData() {
		
		new DownLoader().execute(url, tagData);
	}

	@Override
	public void uploadData(Tasks task, String customerName) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		String startTime = sdf.format(task.getTaskStartingTime());
		String endTime= sdf.format(task.getTaskEndingTime());
		new UploadLogsAsynckTask().execute(
				task.getCareGiver(),
				task.getTaskName(),
				startTime,
				endTime,
				task.getTaskEndingTime().toString(),
				customerName);
	}

	public void setmObsorver(TaskObsorver mObsorver) {
		this.mObsorver = mObsorver;
	}

	public void setTagData(String tagData) {
		this.tagData = tagData;
	}

	public boolean checkNetwork() {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		Log.d("CheckNetwork", "No network, cannot initiate retrieval!");
		return false;
	}

	/**
	 * getting tasks
	 * 
	 * @return
	 */

	public Customer getTasks() {
		return tasks;
	}

	/**
	 * Request available transports, by day, starting point and destination
	 * point
	 * 
	 * @author Thinkpad
	 * 
	 */
	private class DownLoader extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			mObsorver.downloadingStarted();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String content = null;
			try {

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(params[0]);
				List<NameValuePair> nameValuePaire = new ArrayList<NameValuePair>(
						1);

				nameValuePaire.add(new BasicNameValuePair("tagId", params[1]));

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
			// super.onPostExecute(result);
			System.out.println(result);
			try {
				tasks = Parser.parse(result);
				mObsorver.downloadCompleted();
			} catch (JSONException e) {
				mObsorver.error(" Error Occured During Parsing, ");
				e.printStackTrace();
			}

		}

	}

	private class UploadLogsAsynckTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String content = null;
			try {

				System.out.println(params[0]);
				// get the first argument passed to the execute method

				List<NameValuePair> nameValuePaire = new ArrayList<NameValuePair>(6);

				nameValuePaire.add(new BasicNameValuePair("caregiver",	params[0]));
				nameValuePaire.add(new BasicNameValuePair("taskName", params[1]));
				nameValuePaire.add(new BasicNameValuePair("start", params[2]));
				nameValuePaire.add(new BasicNameValuePair("end", params[3]));
				nameValuePaire.add(new BasicNameValuePair("date", params[4]));
				nameValuePaire.add(new BasicNameValuePair("name", params[5]));

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(uploadUrl);
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
			System.out.println();

		}

	}

}

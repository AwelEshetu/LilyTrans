package com.example.translili;

import post.MyPostedRides;
import post.PostRideActivity;
import request.AskRideActivity;
import request.BookedRideActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkNamePhoneNumberSaved();

	}

	@Override
	protected void onResume() {
		super.onResume();
		//checkNamePhoneNumberSaved();
	}

	public void requestRide(View view) {
		Intent intent = new Intent(this, AskRideActivity.class);
		startActivity(intent);
	}

	public void postRide(View view) {

		Intent intent = new Intent(this, PostRideActivity.class);
		startActivity(intent);
	}

	public void myRequestRides(View view) {
		Intent intent = new Intent(this, BookedRideActivity.class);
		startActivity(intent);

	}

	public void myPostRides(View view) {
		Intent intent = new Intent(this, MyPostedRides.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
		}
		return super.onOptionsItemSelected(item);
	}

	public void checkNamePhoneNumberSaved() {

		SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(
				getString(R.string.com_lily_pre), 0);

		String name = sharedPref.getString("name", "").trim();
		String phone = sharedPref.getString("phone", "").trim();
		System.out.println(name);
		System.out.println(phone + "****************");
		if (name.equalsIgnoreCase("") || phone.equalsIgnoreCase("")) {
			Intent intent = new Intent(this, BasicInfoActivity.class);
			startActivity(intent);
		}

	}

}

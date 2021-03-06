package com.example.translili;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setting extends Activity  {
	EditText etName;
	EditText etPhone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		 etName= (EditText)findViewById(R.id.et_name_setting);
		 etPhone= (EditText)findViewById(R.id.et_phoneNumber_setting);
		
		SharedPreferences sharedPref = Setting.this
				.getSharedPreferences(getString(R.string.com_lily_pre), 0);

		String name = sharedPref.getString("name", "").trim();
		String phone = sharedPref.getString("phone", "").trim();
		etName.setText(name);
		etPhone.setText(phone);
		
		
	}
	
	
	public boolean isValidInfo(){
		String name = etName.getText().toString().trim();
		String phoneOne = etPhone.getText().toString().trim();
		if(name.isEmpty() || phoneOne.isEmpty()){
			Toast.makeText(this, "Please Fill Phone Number", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public void saveChanges(View view){
		if (!isValidInfo()) {
			return;
		}
		String name = etName.getText().toString();
		String phoneOne = etPhone.getText().toString();

		SharedPreferences sharedPre = Setting.this
				.getSharedPreferences(getString(R.string.com_lily_pre), 0);
		Editor editor = sharedPre.edit();
		editor.putString("name", name);
		editor.putString("phone", phoneOne);
		editor.commit();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}


}

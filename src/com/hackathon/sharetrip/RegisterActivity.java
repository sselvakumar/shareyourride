package com.hackathon.sharetrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hackathon.sharetrip.utilities.Preferences;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;

/**
 * @author selva
 * 
 */

public class RegisterActivity extends Activity implements OnClickListener,
		ValidationListener {

	Button btn_login;
	@Required(order = 1)
	EditText ed_username;
	@Required(order = 2)
	EditText ed_phoneNumber;
	Spinner gender;
	Validator validator;
	Preferences prefs;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = new Preferences(this);
		if (!prefs.getRegStatus()) {
			setContentView(R.layout.activity_home);
			ed_username = (EditText) findViewById(R.id.username);
			ed_phoneNumber = (EditText) findViewById(R.id.phonenumber);
			gender=(Spinner) findViewById(R.id.gender);
			btn_login = (Button) findViewById(R.id.btn_reg);
			btn_login.setOnClickListener(this);
			validator = new Validator(this);
			validator.setValidationListener(this);
		} else {
			Intent in = new Intent(this, MainActivity.class);
			startActivity(in);
			finish();
		}

	}

	@Override
	public void preValidation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		prefs.setName(ed_username.getText().toString());
		prefs.setPhoneNumber(ed_phoneNumber.getText().toString());
		prefs.setGender(gender.getSelectedItem().toString());
		prefs.setRegStatus(true);
		Intent in = new Intent(this, MainActivity.class);
		startActivity(in);
		finish();
	}

	@Override
	public void onFailure(View failedView, Rule<?> failedRule) {
		// TODO Auto-generated method stub
		String message = failedRule.getFailureMessage();

		if (failedView instanceof EditText) {
			failedView.requestFocus();
			((EditText) failedView).setError(message);
		} else {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onValidationCancelled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_reg:
			validator.validateAsync();
			break;

		default:
			break;
		}

	}
}

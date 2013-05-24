//package com.hangapp.android.activity.register;
//
//import roboguice.activity.RoboActivity;
//import roboguice.inject.ContentView;
//import roboguice.inject.InjectView;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.inject.Inject;
//import com.hangapp.android.R;
//import com.hangapp.android.activity.HomeActivity;
//import com.hangapp.android.model.User;
//import com.hangapp.android.network.rest.RestClient;
//import com.hangapp.android.network.xmpp.XMPP;
//import com.hangapp.android.util.Keys;
//import com.hangapp.android.util.Utils;
//
//@ContentView(R.layout.activity_register_details)
//public final class RegisterDetailsActivity extends RoboActivity {
//
//	// EditText editTextFirstName;
//	// EditText editTextLastName;
//
//	@InjectView(R.id.editTextFirstName) private EditText editTextFirstName;
//	@InjectView(R.id.editTextLastName) private EditText editTextLastName;
//
//	@Inject RestClient parseAreSomeClowns;
//	@Inject XMPP xmpp;
//	@Inject SharedPreferences prefs;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		// setContentView(R.layout.activity_register_details);
//		//
//		// editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
//		// editTextLastName = (EditText) findViewById(R.id.editTextLastName);
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		// If this user already finished registering his details, skip this
//		// whole
//		// Activity.
//		// SharedPreferences prefs = getSharedPreferences(Keys.PREFS_NS, 0);
//		if (prefs.getBoolean(Keys.REGISTERED_DETAILS, false) == true) {
//			skipToHomeActivity();
//		}
//	}
//
//	public void registerDetails(View v) {
//		if (!Utils.isNetworkAvailable(getApplicationContext())) {
//			Toast.makeText(getApplicationContext(),
//					"Can't register user: not connected to Internet",
//					Toast.LENGTH_LONG).show();
//			return;
//		}
//
//		String firstName = editTextFirstName.getText().toString();
//		String lastName = editTextLastName.getText().toString();
//
//		// Sanity checks on the first/last name.
//		if (firstName.trim().equals("")) {
//			Toast.makeText(getApplicationContext(),
//					"Please enter your first name", Toast.LENGTH_SHORT).show();
//			return;
//		}
//		if (lastName.trim().equals("")) {
//			Toast.makeText(getApplicationContext(),
//					"Please enter your last name", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		// Retrieve user's phone number from before and use it to setup the
//		// User object that you want to upload to the server.
//		int userPhoneNumber = prefs.getInt(Keys.USER_PHONE_NUMBER, 0);
//
//		User newUser = new User(userPhoneNumber, firstName, lastName, null,
//				null);
//
//		// Upload this User to the XMPP and Parse
//		xmpp.register(newUser.getJid());
//		parseAreSomeClowns.registerUser(newUser);
//
//		// Save the fact that the user confirmed his phone number in
//		// SharedPrefs.
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.putBoolean(Keys.REGISTERED_DETAILS, true);
//		editor.putInt(Keys.JID, userPhoneNumber);
//		editor.putString(Keys.FIRST_NAME, firstName);
//		editor.putString(Keys.LAST_NAME, lastName);
//		editor.commit();
//
//		skipToHomeActivity();
//	}
//
//	private void skipToHomeActivity() {
//		finish();
//		startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//	}
//}

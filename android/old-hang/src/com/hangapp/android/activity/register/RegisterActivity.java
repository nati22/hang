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
//import com.hangapp.android.util.Keys;
//
//@ContentView(R.layout.activity_register)
//public final class RegisterActivity extends RoboActivity {
//
//	// EditText editTextPhoneNumber;
//
//	@InjectView(R.id.editTextPhoneNumber) EditText editTextPhoneNumber;
//	@Inject SharedPreferences prefs;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		// setContentView(R.layout.activity_register);
//		//
//		// editTextPhoneNumber = (EditText)
//		// findViewById(R.id.editTextPhoneNumber);
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		// If this user already set his phone number, skip this whole Activity.
//		// SharedPreferences prefs = getSharedPreferences(Keys.PREFS_NS, 0);
//		if (prefs.getInt(Keys.USER_PHONE_NUMBER, 0) != 0) {
//			skipToRegisterConfirmationActivity();
//		}
//	}
//
//	/**
//	 * This is a MUCH better way of calling methods in Android. This is as
//	 * opposed to calling setOnClickListener() from before.
//	 * 
//	 * See
//	 * <http://smartcloudblog.blogspot.com/2011/09/android-onclicklisteners-vs
//	 * .html>
//	 */
//	public void registerPhoneNumber(View v) {
//		int userPhoneNumber = 0;
//
//		// Parse the user's phone number out of the "phone EditText". The fact
//		// that the EditText is of type "phone" should guarantee that it's an
//		// int.
//		try {
//			userPhoneNumber = Integer.parseInt(editTextPhoneNumber.getText()
//					.toString());
//		} catch (NumberFormatException e) {
//			Toast.makeText(getApplicationContext(),
//					"Invalid phone number input", Toast.LENGTH_LONG).show();
//			return;
//		}
//
//		// Register user phone number here.
//
//		// Saved the parsed user's phone number in SharedPrefs.
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.putInt(Keys.USER_PHONE_NUMBER, userPhoneNumber);
//		editor.commit();
//
//		skipToRegisterConfirmationActivity();
//	}
//
//	/**
//	 * Closes this activity and opens up the RegisterConfirmationActivity, and
//	 * prevents the user from clicking "Back" to go back to this Activity.
//	 */
//	public void skipToRegisterConfirmationActivity() {
//		// Calling these two activities like this should be fine:
//		// <http://stackoverflow.com/questions/4182761/finish-old-activity-and-start-a-new-one-or-vice-versa>
//		finish();
//		startActivity(new Intent(getApplicationContext(),
//				RegisterConfirmationActivity.class));
//	}
//}

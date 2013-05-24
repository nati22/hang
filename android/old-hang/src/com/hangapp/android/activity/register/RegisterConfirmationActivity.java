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
//@ContentView(R.layout.activity_register_confirmation)
//public final class RegisterConfirmationActivity extends RoboActivity {
//
//	// EditText editTextCode;
//	@InjectView(R.id.editTextConfirmationCode) private EditText editTextCode;
//	@Inject SharedPreferences prefs;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		// setContentView(R.layout.activity_register_confirmation);
//		//
//		// editTextCode = (EditText)
//		// findViewById(R.id.editTextConfirmationCode);
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		// If this user already confirmed his phone number, skip this whole
//		// Activity.
//		// SharedPreferences prefs = getSharedPreferences(Keys.PREFS_NS, 0);
//		if (prefs.getBoolean(Keys.USER_PHONE_NUMBER_CONFIRMED, false) == true) {
//			skipToRegistrationDetailsActivity();
//		}
//	}
//
//	public void verifyConfirmationCode(View v) {
//		int confirmationCode = 0;
//
//		// Parse the user's phone number out of the "phone EditText". The fact
//		// that the EditText is of type "phone" should guarantee that it's an
//		// int.
//		try {
//			confirmationCode = Integer.parseInt(editTextCode.getText()
//					.toString());
//		} catch (NumberFormatException e) {
//			Toast.makeText(getApplicationContext(),
//					"Invalid confirmation number", Toast.LENGTH_LONG).show();
//			return;
//		}
//
//		// Upload confirmation code here
//
//		Toast.makeText(getApplicationContext(),
//				"Confirmation code: " + confirmationCode, Toast.LENGTH_SHORT)
//				.show();
//
//		// Saved the fact that the user confirmed his phone number in
//		// SharedPrefs.
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.putBoolean(Keys.USER_PHONE_NUMBER_CONFIRMED, true);
//		editor.commit();
//
//		skipToRegistrationDetailsActivity();
//	}
//
//	/**
//	 * Closes this activity and opens up the RegisterConfirmationActivity, and
//	 * prevents the user from clicking "Back" to go back to this Activity.
//	 */
//	public void skipToRegistrationDetailsActivity() {
//		// Calling these two activities like this should be fine:
//		// <http://stackoverflow.com/questions/4182761/finish-old-activity-and-start-a-new-one-or-vice-versa>
//		finish();
//		startActivity(new Intent(getApplicationContext(),
//				RegisterDetailsActivity.class));
//	}
//
//}

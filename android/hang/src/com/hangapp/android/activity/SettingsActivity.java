package com.hangapp.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.LoginFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.util.BaseActivity;

/**
 * Get to this Activity from {@link HomeActivity} -> menu button -> Settings. <br />
 * <br />
 * This Activity currently does one thing: show a Facebook Logout button. It
 * uses Facebook's {@link UiLifecycleHelper} to "watch" for facebook session
 * state changes. If it detects a logout, then it clears the database and
 * returns to {@link HomeActivity}. {@code HomeActivity} will then do its own
 * Facebook session state check to see if the user is logged out, and show
 * {@link LoginFragment} once it realizes that the user is logged out (since we
 * pressed the logout button here in {@link SettingsActivity}).
 */
public final class SettingsActivity extends BaseActivity {

	// Dependencies.
	private Database database;

	// Facebook SDK stuff.
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Instantiate dependencies.
		database = Database.getInstance();
	};

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i("SettingsActivity.onSessionStateChange", "Logged in...");
		} else if (state.isClosed()) {
			Log.i("SettingsActivity.onSessionStateChange", "Logged out...");

			Toast.makeText(getApplicationContext(),
					"Logged out, cleared database", Toast.LENGTH_SHORT).show();
			database.clear();

			// TODO: Logout of XMPP.
			// xmpp.logout();
			finish();
		}
	}
}

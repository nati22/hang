package com.hangapp.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.util.BaseActivity;

public final class SettingsActivity extends BaseActivity {

	/**
	 * The UiLifecycleHelper class helps to create, automatically open (if
	 * applicable), save, and restore the Active Session in a way that is
	 * similar to Android UI lifecycles. When using this class, clients MUST
	 * call all the public methods from the respective methods in either an
	 * Activity or Fragment. Failure to call all the methods can result in
	 * improperly initialized or uninitialized Sessions.
	 */
	private UiLifecycleHelper uiHelper;

	private Database database;	

	/**
	 * Provides asynchronous notification of Session state changes. A Session is
	 * used to authenticate a user and manage the user's session with Facebook.
	 */
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

		String myJid = database.getMyJid();
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
			// XMPP.getInstance().logout();
			finish();
		}
	}
}

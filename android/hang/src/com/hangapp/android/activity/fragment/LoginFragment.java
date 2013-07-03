package com.hangapp.android.activity.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.hangapp.android.R;
import com.hangapp.android.activity.HomeActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.util.Keys;

/**
 * The fragment that is shown automatically by {@link HomeActivity} whenever
 * {@code HomeActivity} detects that you're not logged into Facebook.
 */
public final class LoginFragment extends SherlockFragment {

	// Dependencies.
	private SharedPreferences prefs;
	private Database database;

	// Facebook SDK member variables.
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

		// Setup the Facebook SDK helper object.
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);

		// Instantiate dependencies.
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		database = Database.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, null);

		return view;
	}

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

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(Keys.REGISTERED, true);
			editor.commit();
		} else if (state.isClosed()) {
			Log.i("SettingsActivity.onSessionStateChange", "Logged out...");

			database.clear();
		}
	}
}

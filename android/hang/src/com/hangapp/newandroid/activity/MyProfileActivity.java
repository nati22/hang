package com.hangapp.newandroid.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.model.Availability.Status;
import com.hangapp.newandroid.model.callback.IncomingBroadcastsListener;
import com.hangapp.newandroid.model.callback.MyUserDataListener;
import com.hangapp.newandroid.model.callback.OutgoingBroadcastsListener;
import com.hangapp.newandroid.util.BaseFragmentActivity;
import com.hangapp.newandroid.util.HangLog;

public final class MyProfileActivity extends BaseFragmentActivity implements
		MyUserDataListener, IncomingBroadcastsListener,
		OutgoingBroadcastsListener {

	/**
	 * The UiLifecycleHelper class helps to create, automatically open (if
	 * applicable), save, and restore the Active Session in a way that is similar
	 * to Android UI lifecycles. When using this class, clients MUST call all the
	 * public methods from the respective methods in either an Activity or
	 * Fragment. Failure to call all the methods can result in improperly
	 * initialized or uninitialized Sessions.
	 */
	private UiLifecycleHelper uiHelper;

	private ProfilePictureView profilePictureView;
	private View profileStatusBar;
	private TextView textViewMyName;
	private Button buttonOutgoingBroadcasts;
	private Button buttonIncomingBroadcasts;

	private Database database;

	/**
	 * Provides asynchronous notification of Session state changes. A Session is
	 * used to authenticate a user and manage the user's session with Facebook.
	 */
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		// Reference views
		profilePictureView = (ProfilePictureView) findViewById(R.id.profilePictureViewMyIcon);
		textViewMyName = (TextView) findViewById(R.id.textViewMyName);
		buttonOutgoingBroadcasts = (Button) findViewById(R.id.buttonOutgoingBroadcasts);
		buttonIncomingBroadcasts = (Button) findViewById(R.id.buttonIncomingBroadcasts);
		profileStatusBar = (View) findViewById(R.id.profile_status_color_bar);

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
		profilePictureView.setProfileId(myJid);
		
		// Set color of availability bar (below profile pic)
		if (database.getMyAvailability().equals(Status.BUSY)) {
			profileStatusBar.setBackgroundColor(getResources().getColor(R.color.red));
		} else if (database.getMyAvailability().equals(Status.FREE)) {
			profileStatusBar.setBackgroundColor(getResources().getColor(R.color.green));
		} else profileStatusBar.setBackgroundColor(getResources().getColor(R.color.gray));
		

		textViewMyName.setText(database.getMyFullName());
		// TODO: change these to a single style
		textViewMyName.setTextSize(R.dimen.title_font_size);
		textViewMyName.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/AUBREY.TTF"));

		database.addMyUserDataListener(this);

		// Send a nudge to myself
		/*
		 * profilePictureView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 *//**
		 * A List is expected by {@link BasePutRequestAsyncTask} so this List
		 * will hold the only relevant parameter, the nudge recipient's jid
		 */
		/*
		 * List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		 * parameters.add(new BasicNameValuePair(Keys.NUDGEE_JID, Database
		 * .getInstance().getMyJid()));
		 * 
		 * // new SendNudgeAsyncTask(getApplicationContext(), //
		 * Database.getInstance().getMyJid(), parameters).execute();
		 * HangLog.toastD(getApplicationContext(), "Self nudge",
		 * "nudging myself with jid " + Database.getInstance().getMyJid()); } });
		 */

		onOutgoingBroadcastsUpdate(database.getMyOutgoingBroadcasts());
		onIncomingBroadcastsUpdate(database.getMyIncomingBroadcasts());
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

		database.removeMyUserDataListener(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i("MyProfileActivity.onSessionStateChange", "Logged in...");
		} else if (state.isClosed()) {
			Log.i("MyProfileActivity.onSessionStateChange", "Logged out...");

			HangLog.toastD(getApplicationContext(),
					"MyProfileActivity.onSessionStateChange",
					"Logged out, cleared database");
			database.clear();
			// TODO: Logout of XMPP.
			// XMPP.getInstance().logout();
			finish();
		}
	}

	@Override
	public void onMyUserDataUpdate(User me) {
		textViewMyName.setText(me.getFullName());
	}

	public void startOutgoingBroadcastsActivity(View v) {
		startActivity(new Intent(this, OutgoingBroadcastsActivity.class));
	}

	public void startIncomingBroadcastsActivity(View v) {
		startActivity(new Intent(this, IncomingBroadcastsActivity.class));
	}

	@Override
	public void onOutgoingBroadcastsUpdate(List<User> outgoingBroadcasts) {
		if (outgoingBroadcasts.size() == 1) {
			buttonOutgoingBroadcasts.setText("1 Outgoing Broadcast");
		} else {
			buttonOutgoingBroadcasts.setText(outgoingBroadcasts.size()
					+ " Outgoing Broadcasts");
		}
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		if (incomingBroadcasts.size() == 1) {
			buttonIncomingBroadcasts.setText("1 Incoming Broadcast");
		} else {
			buttonIncomingBroadcasts.setText(incomingBroadcasts.size()
					+ " Incoming Broadcasts");
		}

	}
}

package com.hangapp.newandroid.activity;

import java.util.List;

import android.content.Intent;
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
import com.hangapp.newandroid.model.OldAvailability;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.model.callback.IncomingBroadcastsListener;
import com.hangapp.newandroid.model.callback.MyStatusListener;
import com.hangapp.newandroid.model.callback.MyUserDataListener;
import com.hangapp.newandroid.model.callback.OutgoingBroadcastsListener;
import com.hangapp.newandroid.network.xmpp.XMPP;
import com.hangapp.newandroid.util.BaseFragmentActivity;
import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.Utils;

public final class MyProfileActivity extends BaseFragmentActivity implements
		MyUserDataListener, MyStatusListener, IncomingBroadcastsListener,
		OutgoingBroadcastsListener {

	private UiLifecycleHelper uiHelper;

	private ProfilePictureView profilePictureView;
	private TextView textViewMyName;
	private TextView textViewMyStatus;
	private Button buttonOutgoingBroadcasts;
	private Button buttonIncomingBroadcasts;

	private Database database;

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

		// Reference views
		profilePictureView = (ProfilePictureView) findViewById(R.id.profilePictureViewMyIcon);
		textViewMyName = (TextView) findViewById(R.id.textViewMyName);
		textViewMyStatus = (TextView) findViewById(R.id.textViewMyStatus);
		buttonOutgoingBroadcasts = (Button) findViewById(R.id.buttonOutgoingBroadcasts);
		buttonIncomingBroadcasts = (Button) findViewById(R.id.buttonIncomingBroadcasts);

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

		OldAvailability myStatus = database.getMyOldAvailability();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		String myJid = database.getMyJid();
		profilePictureView.setProfileId(myJid);

		textViewMyName.setText(database.getMyFullName());
		textViewMyStatus.setText(myStatus != null ? myStatus.getDescription()
				: "No OldAvailability");

		database.addMyUserDataListener(this);
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
			XMPP.getInstance().logout();
			finish();
		}
	}

	@Override
	public void onMyUserDataUpdate(User me) {
		textViewMyName.setText(me.getFullName());
	}

	@Override
	public void onMyStatusUpdate(OldAvailability status) {
		textViewMyStatus.setText(status != null ? status.getDescription()
				: "No OldAvailability.");
	}

	public void startOutgoingBroadcastsActivity(View v) {
		startActivity(new Intent(this, OutgoingBroadcastsActivity.class));
	}

	public void startIncomingBroadcastsActivity(View v) {
		startActivity(new Intent(this, IncomingBroadcastsActivity.class));
	}

	@Override
	public void onOutgoingBroadcastsUpdate(List<User> outgoingBroadcasts) {
		buttonOutgoingBroadcasts.setText(outgoingBroadcasts.size()
				+ " Outgoing Broadcasts");
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		buttonIncomingBroadcasts.setText(incomingBroadcasts.size()
				+ " Incoming Broadcasts");
	}
}

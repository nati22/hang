package com.hangapp.newandroid.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.UserDatabase;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.util.BaseFragmentActivity;
import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.Keys;

public class ProfileActivity extends BaseFragmentActivity {

	private ProfilePictureView profilePictureView;
	private TextView textViewFriendName;

	private UserDatabase database;

	private User friend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		// Instantiate dependencies
		database = UserDatabase.getInstance();

		// Set who the friend is.
		String hostJid = getIntent().getStringExtra(Keys.HOST_JID);
		friend = database.getIncomingUser(hostJid);

		// Friend not in UserDatabase sanity check.
		if (friend == null) {
			HangLog.toastE(this, "ProfileActivity.onCreate", "Host with jid: "
					+ hostJid + " was null in the UserDatabase.");
			finish();
		}

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Reference Views.
		profilePictureView = (ProfilePictureView) findViewById(R.id.profilePictureViewIcon);
		textViewFriendName = (TextView) findViewById(R.id.textViewFriendName);

		// Populate Views.
		profilePictureView.setProfileId(friend.getJid());
		textViewFriendName.setText(friend.getFullName());
	}
}

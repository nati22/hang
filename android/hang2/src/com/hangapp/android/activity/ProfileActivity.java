package com.hangapp.android.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.User;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.AvailabilityButton;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.Utils;

public final class ProfileActivity extends BaseActivity {

	private ProfilePictureView profilePictureView;
	private TextView textViewFriendName;
	private AvailabilityButton[] buttonsAvailability;

	private Database database;
	private RestClient restClient;

	private User friend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		// Instantiate dependencies
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getApplicationContext());

		// Set who the friend is.
		String hostJid = getIntent().getStringExtra(Keys.HOST_JID);
		friend = database.getIncomingUser(hostJid);

		// Friend not in Database sanity check.
		if (friend == null) {
			Log.e("ProfileActivity.onCreate", "Host with jid: " + hostJid
					+ " was null in the Database.");
			finish();
		}

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Reference Views.
		profilePictureView = (ProfilePictureView) findViewById(R.id.profilePictureView);
		textViewFriendName = (TextView) findViewById(R.id.textViewFriendName);
		buttonsAvailability = new AvailabilityButton[] {
				(AvailabilityButton) findViewById(R.id.buttonAvailability00),
				(AvailabilityButton) findViewById(R.id.buttonAvailability01),
				(AvailabilityButton) findViewById(R.id.buttonAvailability02),
				(AvailabilityButton) findViewById(R.id.buttonAvailability03),
				(AvailabilityButton) findViewById(R.id.buttonAvailability04),
				(AvailabilityButton) findViewById(R.id.buttonAvailability05),
				(AvailabilityButton) findViewById(R.id.buttonAvailability06),
				(AvailabilityButton) findViewById(R.id.buttonAvailability07),
				(AvailabilityButton) findViewById(R.id.buttonAvailability08),
				(AvailabilityButton) findViewById(R.id.buttonAvailability09),
				(AvailabilityButton) findViewById(R.id.buttonAvailability10),
				(AvailabilityButton) findViewById(R.id.buttonAvailability11) };

		// Populate Views.
		profilePictureView.setProfileId(friend.getJid());
		textViewFriendName.setText(friend.getFullName());

		// Set OnClickListeners.
		profilePictureView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),
						"Nudging " + friend.getFirstName(), Toast.LENGTH_SHORT)
						.show();
				restClient.sendNudge(friend.getJid());
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Update the Availability strip's views.
		Utils.initializeAvailabilityButtons(buttonsAvailability);
		Utils.updateAvailabilityStripColors(buttonsAvailability,
				friend.getAvailability(), this);
	}
}

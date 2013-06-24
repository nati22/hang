package com.hangapp.android.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.User;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;

public final class ProfileActivity extends BaseActivity {

	private ProfilePictureView profilePictureViewFriendIcon;
	private TextView textViewFriendsName;
	private ImageButton imageButtonFriendsAvailability;
	private TextView textViewStatus;
	private TextView textViewProposal;
	private TextView textViewProposalDescription;
	private TextView textViewProposalLocation;
	private TextView textViewProposalStartTime;
	private TextView textViewProposalInterestedCount;

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
		profilePictureViewFriendIcon = (ProfilePictureView) findViewById(R.id.profilePictureViewFriendsIcon);
		textViewFriendsName = (TextView) findViewById(R.id.textViewFriendsName);
		imageButtonFriendsAvailability = (ImageButton) findViewById(R.id.imageButtonFriendsAvailability);
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		textViewProposal = (TextView) findViewById(R.id.textViewMyProposal);
		textViewProposalDescription = (TextView) findViewById(R.id.textViewMyProposalDescription);
		textViewProposalLocation = (TextView) findViewById(R.id.textViewMyProposalLocation);
		textViewProposalStartTime = (TextView) findViewById(R.id.textViewMyProposalStartTime);
		textViewProposalInterestedCount = (TextView) findViewById(R.id.textViewMyProposalInterestedCount);

		// Set fonts
		Typeface champagneLimousinesFontBold = Typeface.createFromAsset(
				getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
		Typeface champagneLimousinesFont = Typeface
				.createFromAsset(getApplicationContext().getAssets(),
						Fonts.CHAMPAGNE_LIMOUSINES);
		textViewProposal.setTypeface(champagneLimousinesFontBold);
		textViewProposalDescription.setTypeface(champagneLimousinesFontBold);
		textViewProposalLocation.setTypeface(champagneLimousinesFontBold);
		textViewProposalStartTime.setTypeface(champagneLimousinesFont);
		textViewProposalInterestedCount
				.setTypeface(champagneLimousinesFontBold);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Populate Views.
		profilePictureViewFriendIcon.setProfileId(friend.getJid());
		textViewFriendsName.setText(friend.getFullName());
		textViewProposal.setText(friend.getFirstName() + "'s Proposal");
	}
}

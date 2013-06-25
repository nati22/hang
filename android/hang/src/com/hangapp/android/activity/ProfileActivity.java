package com.hangapp.android.activity;

import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability.Status;
import com.hangapp.android.model.User;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.Utils;

public final class ProfileActivity extends BaseActivity {

	private ProfilePictureView profilePictureViewFriendIcon;
	private TextView textViewFriendsName;
	private ImageButton imageButtonFriendsAvailability;
	private TextView textViewFriendsAvailabilityExpirationDate;
	private TextView textViewStatus;
	private RelativeLayout relativeLayoutFriendsProposal;
	private TextView textViewProposal;
	private TextView textViewProposalDescription;
	private TextView textViewProposalLocation;
	private TextView textViewProposalStartTime;
	private CheckBox checkBoxInterested;
	private TextView textViewProposalInterestedCount;
	private HorizontalScrollView horizontalScrollViewInterestedUsers;
	private ProfilePictureView[] profilePictureViewArrayInterestedUsers;

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
		textViewFriendsAvailabilityExpirationDate = (TextView) findViewById(R.id.textViewFriendsAvailabilityExpirationDate);
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		relativeLayoutFriendsProposal = (RelativeLayout) findViewById(R.id.relativeLayoutFriendsProposal);
		textViewProposal = (TextView) findViewById(R.id.textViewMyProposal);
		textViewProposalDescription = (TextView) findViewById(R.id.textViewMyProposalDescription);
		textViewProposalLocation = (TextView) findViewById(R.id.textViewMyProposalLocation);
		textViewProposalStartTime = (TextView) findViewById(R.id.textViewMyProposalStartTime);
		textViewProposalInterestedCount = (TextView) findViewById(R.id.textViewMyProposalInterestedCount);
		checkBoxInterested = (CheckBox) findViewById(R.id.checkBoxInterested);
		horizontalScrollViewInterestedUsers = (HorizontalScrollView) findViewById(R.id.horizontalScrollViewInterestedUsers);
		profilePictureViewArrayInterestedUsers = new ProfilePictureView[] {
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested00),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested01),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested02),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested03),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested04),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested05),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested06),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested07),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested08),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested09),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested10),
				(ProfilePictureView) findViewById(R.id.profilePictureViewInterested11) };

		// Set CheckBox.
		checkBoxInterested
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						List<String> friendsInterestedList = friend
								.getProposal().getInterested();
						String myJid = database.getMyJid();

						// Add yourself to the Interested list of this user.
						if (isChecked) {
							friendsInterestedList.add(myJid);
							restClient.setInterested(myJid);
						}
						// Remove yourself from the Interested list of this
						// user.
						else {
							friendsInterestedList.remove(myJid);
							restClient.deleteInterested(myJid);
						}

						updateInterestedList(friendsInterestedList);
					}
				});

		// Set fonts
		Typeface champagneLimousinesFontBold = Typeface.createFromAsset(
				getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
		Typeface champagneLimousinesFont = Typeface
				.createFromAsset(getApplicationContext().getAssets(),
						Fonts.CHAMPAGNE_LIMOUSINES);
		textViewStatus.setTypeface(champagneLimousinesFont);
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

		// Grey.
		if (friend.getAvailability() == null
				|| !friend.getAvailability().isActive()) {
			imageButtonFriendsAvailability.setImageDrawable(getResources()
					.getDrawable(R.drawable.status_grey));
			textViewFriendsAvailabilityExpirationDate.setVisibility(View.GONE);
		}
		// Free.
		else if (friend.getAvailability().getStatus() == Status.FREE) {
			imageButtonFriendsAvailability.setImageDrawable(getResources()
					.getDrawable(R.drawable.status_green));
			textViewFriendsAvailabilityExpirationDate
					.setVisibility(View.VISIBLE);
			int hoursRemaining = Utils.getRemainingHours(friend
					.getAvailability().getExpirationDate());
			textViewFriendsAvailabilityExpirationDate.setText(hoursRemaining
					+ "h");
		}
		// Busy.
		else if (friend.getAvailability().getStatus() == Status.BUSY) {
			imageButtonFriendsAvailability.setImageDrawable(getResources()
					.getDrawable(R.drawable.status_red));
			textViewFriendsAvailabilityExpirationDate
					.setVisibility(View.VISIBLE);
			int hoursRemaining = Utils.getRemainingHours(friend
					.getAvailability().getExpirationDate());
			textViewFriendsAvailabilityExpirationDate.setText(hoursRemaining
					+ "h");
		}
		// Error state.
		else {
			Log.e("ProfileActivity.onResume",
					"Error state: " + friend.getAvailability());
			imageButtonFriendsAvailability.setImageDrawable(getResources()
					.getDrawable(R.drawable.status_grey));
			textViewFriendsAvailabilityExpirationDate.setVisibility(View.GONE);
		}

		if (friend.getProposal() == null) {
			Log.e("ProfileActivity.onResume", friend.getFirstName()
					+ "'s proposal was null");
			relativeLayoutFriendsProposal.setVisibility(View.GONE);
		} else {
			relativeLayoutFriendsProposal.setVisibility(View.VISIBLE);
			textViewProposal.setText(friend.getFirstName() + "'s Proposal");
			textViewProposalDescription.setText(friend.getProposal()
					.getDescription());
			textViewProposalLocation
					.setText(friend.getProposal().getLocation());
			textViewProposalStartTime.setText(friend.getProposal()
					.getStartTime().toString("h:mm aa"));

			updateInterestedList(friend.getProposal().getInterested());
		}
	}

	private void updateInterestedList(List<String> interestedUsers) {
		// Hide the Interested users if there are none.
		if (interestedUsers == null) {
			Log.i("MyProposalFragment.onMyProposalUpdate",
					"Proposal interested is null");
			horizontalScrollViewInterestedUsers.setVisibility(View.GONE);
			textViewProposalInterestedCount.setText("0 interested");
		} else if (interestedUsers.size() == 0) {
			Log.i("MyProposalFragment.onMyProposalUpdate",
					"Proposal interested is empty");
			horizontalScrollViewInterestedUsers.setVisibility(View.GONE);
			textViewProposalInterestedCount.setText(interestedUsers.size()
					+ " interested");
		} else {
			horizontalScrollViewInterestedUsers.setVisibility(View.VISIBLE);
			textViewProposalInterestedCount.setText(interestedUsers.size()
					+ " interested");
		}

		// Show Facebook icons for friends that are there.
		for (int i = 0; i < interestedUsers.size(); i++) {
			profilePictureViewArrayInterestedUsers[i]
					.setVisibility(View.VISIBLE);
			profilePictureViewArrayInterestedUsers[i]
					.setProfileId(interestedUsers.get(i));
		}
		// Hide all the other Facebook icons.
		for (int i = interestedUsers.size(); i < profilePictureViewArrayInterestedUsers.length; i++) {
			profilePictureViewArrayInterestedUsers[i].setVisibility(View.GONE);
		}
	}
}

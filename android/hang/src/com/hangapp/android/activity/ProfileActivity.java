package com.hangapp.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.FeedFragment;
import com.hangapp.android.activity.fragment.ProposalFragment;
import com.hangapp.android.activity.fragment.YouFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability.Status;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.Utils;

/**
 * Get to this activity by clicking on a user in {@link FeedFragment}. <br />
 * <br />
 * This Activity shows a target user's availability (similar to how
 * {@link YouFragment} shows your own Availability). It implements
 * {@link IncomingBroadcastsActivity} in order to subscribe itself to any
 * changes in state for this target user.
 */
public final class ProfileActivity extends BaseActivity implements
		IncomingBroadcastsListener {

	// UI widgets.
	private ProfilePictureView profilePictureViewFriendIcon;
	private TextView textViewFriendsName;
	private ImageButton imageButtonFriendsAvailability;
	private TextView textViewFriendsAvailabilityExpirationDate;
	private TextView textViewStatus;
	private RelativeLayout relativeLayoutFriendsProposal;
	private ImageView imageViewChat;
	private TextView textViewProposalDescription;
	private TextView textViewProposalLocation;
	private TextView textViewProposalStartTime;
	private CheckBox checkBoxInterested;
	private TextView textViewProposalInterestedCount;
	// private HorizontalScrollView horizontalScrollViewInterestedUsers;
	// private ProfilePictureView[] profilePictureViewArrayInterestedUsers;
	private LinearLayout linLayoutInterested;

	// Member datum.
	private List<String> listInterestedJids = new ArrayList<String>();
	private User friend;

	// Dependencies.
	private Database database;
	private RestClient restClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		// Instantiate dependencies
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getApplicationContext());

		// Setup listener
		database.addIncomingBroadcastsListener(this);

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
		imageViewChat = (ImageView) findViewById(R.id.imageViewChat);
		textViewProposalDescription = (TextView) findViewById(R.id.textViewMyProposalDescription);
		textViewProposalLocation = (TextView) findViewById(R.id.textViewMyProposalLocation);
		textViewProposalStartTime = (TextView) findViewById(R.id.textViewMyProposalStartTime);
		textViewProposalInterestedCount = (TextView) findViewById(R.id.textViewMyProposalInterestedCount);
		checkBoxInterested = (CheckBox) findViewById(R.id.checkBoxInterested);
		// horizontalScrollViewInterestedUsers = (HorizontalScrollView)
		// findViewById(R.id.horizontalScrollViewInterestedUsers);
		linLayoutInterested = (LinearLayout) findViewById(R.id.linearLayoutInterested);

		// Set OnClickListeners.
		imageViewChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatActivityIntent = new Intent(ProfileActivity.this,
						ChatActivity.class);
				chatActivityIntent.putExtra(Keys.HOST_JID, friend.getJid());
				startActivity(chatActivityIntent);
			}
		});

		// If User is Interested/Confirmed, check the appropriate ToggleButton
		if (friend.getProposal() != null) {
			if (friend.getProposal().getInterested() != null) {
				if (friend.getProposal().getInterested()
						.contains(database.getMyJid()))
					checkBoxInterested.setChecked(true);
			}
		}

		// Set CheckBox.
		checkBoxInterested
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// Add yourself to the Interested list of this user.
						if (isChecked) {
							addMeToHostInterestedList();
						}
						// Remove yourself from the Interested list of this
						// user.
						else {
							removeMeFromHostInterestedList();
						}
					}
				});

		// Set fonts
		Typeface champagneLimousinesFontBold = Typeface.createFromAsset(
				getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
		Typeface champagneLimousinesFont = Typeface.createFromAsset(
				getApplicationContext().getAssets(), Fonts.CHAMPAGNE_LIMOUSINES);
		textViewStatus.setTypeface(champagneLimousinesFont);
		textViewProposalDescription.setTypeface(champagneLimousinesFontBold);
		textViewProposalLocation.setTypeface(champagneLimousinesFontBold);
		textViewProposalStartTime.setTypeface(champagneLimousinesFont);
		textViewProposalInterestedCount.setTypeface(champagneLimousinesFontBold);
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		// If friend is still broadcasting to you
		if (database.getIncomingUser(friend.getJid()) != null) {
			// If they still have a proposal
			if (database.getIncomingUser(friend.getJid()).getProposal() != null) {
				Log.i(ProposalFragment.class.getSimpleName(),
						"onIncomingBroadcastsUpdate called with "
								+ database.getIncomingUser(friend.getJid())
										.getProposal().getInterested().size()
								+ " interested, and "
								+ database.getIncomingUser(friend.getJid())
										.getProposal().getConfirmed().size()
								+ " confirmed.");

				// Find out if User's Interested was updated
				if (!database.getIncomingUser(friend.getJid()).getProposal()
						.getInterested().equals(listInterestedJids)) {

					listInterestedJids.clear();
					listInterestedJids.addAll(database
							.getIncomingUser(friend.getJid()).getProposal()
							.getInterested());

					updateHorizontalList(listInterestedJids, linLayoutInterested);
				}
			} else {
				Toast.makeText(this,
						"Proposal deleted for " + friend.getFirstName(),
						Toast.LENGTH_SHORT).show();
				Log.i(ProposalFragment.class.getSimpleName(),
						"onIncomingBroadcastsUpdate called with NO PROPOSAL.");
				this.finish();
			}
		}
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
			textViewFriendsAvailabilityExpirationDate.setVisibility(View.VISIBLE);
			int hoursRemaining = Utils.getRemainingHours(friend.getAvailability()
					.getExpirationDate());
			textViewFriendsAvailabilityExpirationDate
					.setText(hoursRemaining + "h");
		}
		// Busy.
		else if (friend.getAvailability().getStatus() == Status.BUSY) {
			imageButtonFriendsAvailability.setImageDrawable(getResources()
					.getDrawable(R.drawable.status_red));
			textViewFriendsAvailabilityExpirationDate.setVisibility(View.VISIBLE);
			int hoursRemaining = Utils.getRemainingHours(friend.getAvailability()
					.getExpirationDate());
			textViewFriendsAvailabilityExpirationDate
					.setText(hoursRemaining + "h");
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
			textViewProposalDescription.setText(friend.getProposal()
					.getDescription());
			textViewProposalLocation.setText(friend.getProposal().getLocation());
			textViewProposalStartTime.setText(friend.getProposal().getStartTime()
					.toString("h:mm aa"));

			// Set "my" interested checkbox
			if (friend.getProposal().getInterested() != null) {
				if (friend.getProposal().getInterested()
						.contains(database.getMyJid()))
					checkBoxInterested.setChecked(true);
			}

			// Refresh list
			onIncomingBroadcastsUpdate(database.getMyIncomingBroadcasts());

			// Add this proposal to the Users list of "seen proposals"
			if (!database.getMySeenProposals().contains(friend.getJid())) {
				database.addSeenProposal(friend.getJid());
				restClient.setSeenProposal(friend.getJid());
				Log.i("ProfileActivity",
						"first time seeing " + friend.getFirstName() + "'s prop");
			} else {
				Log.i("ProfileActivity", friend.getFirstName()
						+ "'s proposal already seen");
			}


		}
	}

	private void addMeToHostInterestedList() {
		restClient.setInterested(friend.getJid());
	}

	private void removeMeFromHostInterestedList() {
		restClient.deleteInterested(friend.getJid());
	}

	public void updateHorizontalList(List<String> jids, LinearLayout linLayout) {
		Log.i(ProposalFragment.class.getSimpleName(), "jids has " + jids.size()
				+ " elements");

		Log.i(ProposalFragment.class.getSimpleName(),
				"removed " + linLayout.getChildCount() + " elements from linLayout");
		linLayout.removeAllViews();

		for (int i = 0; i < jids.size(); i++) {
			String jid = jids.get(i);

			// Get the cell
			View view = LayoutInflater.from(this).inflate(
					R.layout.cell_profile_icon, null);

			// Set the FB Profile pic
			ProfilePictureView icon = (ProfilePictureView) view
					.findViewById(R.id.profilePictureIcon);
			Log.i(ProposalFragment.class.getSimpleName(),
					"Creating fb icon with jid " + jid);
			icon.setProfileId(jid);

			linLayout.addView(view);

		}

		textViewProposalInterestedCount.setText(jids.size() + " interested");

	}

}

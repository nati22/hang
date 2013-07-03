package com.hangapp.android.activity.fragment;

import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.ChatActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.callback.MyProposalListener;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;

/**
 * This fragment is shown within {@link YouFragment}, at the bottom.
 * {@code YouFragment} will dynamically display either this or
 * {@link CreateProposalFragment} based on whether or not you have a Proposal.
 */
public final class MyProposalFragment extends SherlockFragment implements
		MyProposalListener {

	// UI widgets.
	private TextView textViewMyProposal;
	private ImageView imageViewChat;
	private TextView textViewMyProposalDescription;
	private TextView textViewMyProposalLocation;
	private TextView textViewMyProposalStartTime;
	private TextView textViewMyProposalInterestedCount;
	private HorizontalScrollView horizontalScrollViewInterestedUsers;
	private ProfilePictureView[] profilePictureViewArrayInterestedUsers;
	private ImageView imageViewDeleteMyProposal;

	// Dependencies.
	private Database database;

	// Member datum.
	private Proposal myProposal;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate dependencies.
		database = Database.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_proposal, container,
				false);

		// Reference views.
		textViewMyProposal = (TextView) view
				.findViewById(R.id.textViewMyProposal);
		imageViewChat = (ImageView) view.findViewById(R.id.imageViewChat);
		textViewMyProposalDescription = (TextView) view
				.findViewById(R.id.textViewMyProposalDescription);
		textViewMyProposalLocation = (TextView) view
				.findViewById(R.id.textViewMyProposalLocation);
		textViewMyProposalStartTime = (TextView) view
				.findViewById(R.id.textViewMyProposalStartTime);
		textViewMyProposalInterestedCount = (TextView) view
				.findViewById(R.id.textViewMyProposalInterestedCount);
		horizontalScrollViewInterestedUsers = (HorizontalScrollView) view
				.findViewById(R.id.horizontalScrollViewInterestedUsers);
		profilePictureViewArrayInterestedUsers = new ProfilePictureView[] {
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested00),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested01),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested02),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested03),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested04),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested05),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested06),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested07),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested08),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested09),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested10),
				(ProfilePictureView) view
						.findViewById(R.id.profilePictureViewInterested11) };
		imageViewDeleteMyProposal = (ImageView) view
				.findViewById(R.id.imageViewDeleteMyProposal);

		// Set fonts
		Typeface champagneLimousinesFontBold = Typeface.createFromAsset(
				getActivity().getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
		Typeface champagneLimousinesFont = Typeface.createFromAsset(
				getActivity().getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES);
		textViewMyProposal.setTypeface(champagneLimousinesFontBold);
		textViewMyProposalDescription.setTypeface(champagneLimousinesFontBold);
		textViewMyProposalLocation.setTypeface(champagneLimousinesFontBold);
		textViewMyProposalStartTime.setTypeface(champagneLimousinesFont);
		textViewMyProposalInterestedCount
				.setTypeface(champagneLimousinesFontBold);

		// Set OnClickListeners
		imageViewChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatActivityIntent = new Intent(MyProposalFragment.this
						.getActivity().getApplicationContext(),
						ChatActivity.class);
				chatActivityIntent.putExtra(Keys.HOST_JID, database.getMyJid());
				startActivity(chatActivityIntent);
			}
		});

		imageViewDeleteMyProposal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Open the Delete Proposal dialog.
				FragmentManager fm = getActivity().getSupportFragmentManager();

				DeleteProposalDialogFragment deleteProposalDialogFragment = new DeleteProposalDialogFragment();
				deleteProposalDialogFragment.show(fm,
						"fragment_delete_proposal");
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Load up my Proposal from the database.
		myProposal = database.getMyProposal();
		onMyProposalUpdate(myProposal);
	}

	@Override
	public void onMyProposalUpdate(Proposal proposal) {
		myProposal = proposal;

		if (myProposal == null) {
			Log.e("MyProposalFragment", "myProposal is null");
			return;
		}

		textViewMyProposalDescription.setText(myProposal.getDescription());

		// Proposal location is optional.
		if (myProposal.getLocation() == null
				|| myProposal.getLocation().trim().equals("")) {
			textViewMyProposalLocation.setVisibility(View.GONE);
		} else {
			textViewMyProposalLocation.setText(myProposal.getLocation());
			textViewMyProposalLocation.setVisibility(View.VISIBLE);
		}

		textViewMyProposalStartTime.setText(myProposal.getStartTime().toString(
				"h:mm aa"));
		textViewMyProposalInterestedCount.setText(myProposal.getInterested()
				.size() + " interested");

		// Hide the Interested users if there are none.
		if (proposal.getInterested() == null) {
			Log.i("MyProposalFragment.onMyProposalUpdate",
					"Proposal interested is null");
			horizontalScrollViewInterestedUsers.setVisibility(View.GONE);
		} else if (proposal.getInterested().size() == 0) {
			Log.i("MyProposalFragment.onMyProposalUpdate",
					"Proposal interested is empty");
			horizontalScrollViewInterestedUsers.setVisibility(View.GONE);
		} else {
			horizontalScrollViewInterestedUsers.setVisibility(View.VISIBLE);
			updateHorizontalScrollView(proposal.getInterested());
		}
	}

	private void updateHorizontalScrollView(List<String> interestedUsers) {
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

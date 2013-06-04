package com.hangapp.newandroid.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.activity.ChatActivity;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.network.rest.RestClient;
import com.hangapp.newandroid.network.rest.RestClientImpl;
import com.hangapp.newandroid.util.Keys;

public final class ProposalFragment extends SherlockFragment {

	private ScrollView scrollViewProposal;
	private RelativeLayout emptyView;

	private TextView textViewProposalTitle;
	private TextView textViewProposalDescription;
	private TextView textViewProposalLocation;
	private TextView textViewProposalStartTime;
	private ImageView imageViewInterested;
	private ImageView imageViewConfirmed;
	private ImageView buttonChat;
	private ImageView buttonDeleteProposal;

	private String hostJid;
	private User host;

	private Database database;
	private RestClient restClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate dependencies.
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getActivity()
				.getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater
				.inflate(R.layout.fragment_proposal, container, false);

		// Reference views.
		textViewProposalTitle = (TextView) view
				.findViewById(R.id.textViewProposalTitle);
		textViewProposalDescription = (TextView) view
				.findViewById(R.id.textViewProposalDescription);
		textViewProposalLocation = (TextView) view
				.findViewById(R.id.textViewProposalLocation);
		textViewProposalStartTime = (TextView) view
				.findViewById(R.id.textViewProposalStartTime);
		scrollViewProposal = (ScrollView) view
				.findViewById(R.id.scrollViewProposal);
		emptyView = (RelativeLayout) view.findViewById(android.R.id.empty);
		imageViewInterested = (ImageView) view
				.findViewById(R.id.imageViewInterested);
		imageViewConfirmed = (ImageView) view
				.findViewById(R.id.imageViewConfirmed);
		buttonChat = (ImageView) view.findViewById(R.id.buttonChat);
		buttonDeleteProposal = (ImageView) view
				.findViewById(R.id.buttonDeleteProposal);

		// Hide the "delete" button, since this isn't your own Proposal.
		buttonDeleteProposal.setVisibility(View.INVISIBLE);

		// Populate member datum
		hostJid = getActivity().getIntent().getStringExtra(Keys.HOST_JID);

		// Set OnClickListeners.
		imageViewInterested.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!imageViewInterested.isPressed()) {
					Toast.makeText(getActivity(), "Interesting...",
							Toast.LENGTH_SHORT).show();

					addMeToHostInterestedList();
					imageViewInterested.setPressed(true);
				} else {
					Toast.makeText(getActivity(), "Not so interesting...",
							Toast.LENGTH_SHORT).show();

					removeMeFromHostInterestedList();
					imageViewInterested.setPressed(false);
				}

			}
		});
		imageViewConfirmed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!imageViewInterested.isPressed()) {
					Toast.makeText(getActivity(), "I'm confirming",
							Toast.LENGTH_SHORT).show();

					addMeToHostConfirmedList();
					imageViewConfirmed.setPressed(true);
				} else {
					Toast.makeText(getActivity(), "I'm a flake :(",
							Toast.LENGTH_SHORT).show();

					removeMeFromHostConfirmedList();
					imageViewConfirmed.setPressed(false);
				}
			}
		});
		buttonChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Start the ChatActivity.
				Intent chatActivityIntent = new Intent(getActivity(),
						ChatActivity.class);
				chatActivityIntent.putExtra(Keys.HOST_JID, hostJid);
				startActivity(chatActivityIntent);
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Reload the host (and his Proposal) from the database.
		host = database.getIncomingUser(hostJid);

		if (host.getProposal() != null) {
			// Turn off the Empty View.
			scrollViewProposal.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.INVISIBLE);

			// Populate the Views.
			String proposalTitle = String.format(
					getString(R.string.someones_proposal), host.getFirstName());
			textViewProposalTitle.setText(proposalTitle);
			textViewProposalDescription.setText(host.getProposal()
					.getDescription());
			textViewProposalLocation.setText(host.getProposal().getLocation());
			textViewProposalStartTime.setText(host.getProposal().getStartTime()
					.toString("h aa"));
		} else {
			// Turn on the Empty View.
			scrollViewProposal.setVisibility(View.INVISIBLE);
			emptyView.setVisibility(View.VISIBLE);
		}
	}

	private void addMeToHostInterestedList() {
		removeMeFromHostConfirmedList();
		Log.v("addMeToHostInterestedList", "added to host interested");
		restClient.setInterested(host.getJid());
	}

	private void removeMeFromHostInterestedList() {

		restClient.deleteInterested(host.getJid());

	}

	private void addMeToHostConfirmedList() {
		removeMeFromHostInterestedList();

		restClient.setConfirmed(host.getJid());
	}

	private void removeMeFromHostConfirmedList() {
		restClient.deleteConfirmed(host.getJid());
	}

}

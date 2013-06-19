package com.hangapp.android.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.android.R;
import com.hangapp.android.activity.ChatActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.callback.MyProposalListener;
import com.hangapp.android.util.Keys;

public final class MyProposalFragment extends SherlockFragment implements
		MyProposalListener {

	private ScrollView scrollViewProposal;
	private RelativeLayout emptyView;
	private ImageView imageViewNoProposal;
	private TextView textViewProposalDescription;
	private TextView textViewProposalLocation;
	private TextView textViewProposalStartTime;
	private ImageView imageViewInterested;
	private ImageView imageViewConfirmed;
	private ImageView buttonChat;
	private ImageView buttonDeleteProposal;

	private Proposal myProposal;
	private Database database;

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
		textViewProposalDescription = (TextView) view
				.findViewById(R.id.textViewProposalDescription);
		textViewProposalLocation = (TextView) view
				.findViewById(R.id.textViewProposalLocation);
		textViewProposalStartTime = (TextView) view
				.findViewById(R.id.textViewProposalStartTime);
		scrollViewProposal = (ScrollView) view
				.findViewById(R.id.scrollViewProposal);
		emptyView = (RelativeLayout) view.findViewById(android.R.id.empty);
		imageViewNoProposal = (ImageView) view
				.findViewById(R.id.imageViewNoProposal);
		imageViewInterested = (ImageView) view
				.findViewById(R.id.imageViewInterested);
		imageViewConfirmed = (ImageView) view
				.findViewById(R.id.imageViewConfirmed);
		buttonChat = (ImageView) view.findViewById(R.id.buttonChat);
		buttonDeleteProposal = (ImageView) view
				.findViewById(R.id.buttonDeleteProposal);

		// Populate member datum
		myProposal = database.getMyProposal();

		// Disable the Interested and Confirmed buttons, since this is your own
		// Proposal
		imageViewInterested.setVisibility(View.GONE);
		imageViewConfirmed.setVisibility(View.GONE);

		// Set the OnClickListeners.
		imageViewNoProposal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Open the Create New Proposal dialog.
				FragmentManager fm = getActivity().getSupportFragmentManager();

				CreateProposalDialogFragment createProposalDialogFragment = new CreateProposalDialogFragment();
				createProposalDialogFragment.show(fm,
						"fragment_create_proposal");
			}
		});
		buttonChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Start the ChatActivity.
				Intent chatActivityIntent = new Intent(getActivity(),
						ChatActivity.class);
				chatActivityIntent.putExtra(Keys.HOST_JID, database.getMyJid());
				startActivity(chatActivityIntent);
			}
		});
		buttonDeleteProposal.setOnClickListener(new OnClickListener() {
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

		database.addMyProposalListener(this);

		// Load up my Proposal from the database.
		myProposal = database.getMyProposal();

		// Refresh the Proposal for this Fragment.
		onMyProposalUpdate(myProposal);
	}

	@Override
	public void onPause() {
		super.onPause();

		database.removeMyProposalListener(this);
	}

	@Override
	public void onMyProposalUpdate(Proposal proposal) {
		myProposal = proposal;

		if (myProposal != null) {
			// Turn off the Empty View.
			scrollViewProposal.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.INVISIBLE);

			// Populate the Views.
			textViewProposalDescription.setText(myProposal.getDescription());
			textViewProposalLocation.setText(myProposal.getLocation());
			textViewProposalStartTime.setText(myProposal.getStartTime()
					.toString("h aa"));
		} else {
			// Turn on the Empty View.
			scrollViewProposal.setVisibility(View.INVISIBLE);
			emptyView.setVisibility(View.VISIBLE);
		}
	}
}

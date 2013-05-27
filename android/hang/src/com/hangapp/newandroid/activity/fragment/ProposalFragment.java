package com.hangapp.newandroid.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.activity.ChatActivity;
import com.hangapp.newandroid.activity.HomeActivity;
import com.hangapp.newandroid.activity.ProfileActivity;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.Proposal;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.util.Keys;

public class ProposalFragment extends SherlockFragment {

	private TextView textViewProposalTitle;
	private ImageView buttonChat;
	private ImageView buttonDeleteProposal;

	private Proposal proposal;

	private Database db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate dependencies.
		db = Database.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_proposal, container,
				false);

		// Reference views.
		textViewProposalTitle = (TextView) view
				.findViewById(R.id.textViewProposalTitle);
		buttonChat = (ImageView) view.findViewById(R.id.buttonChat);
		buttonDeleteProposal = (ImageView) view
				.findViewById(R.id.buttonDeleteProposal);

		// Set OnClickListeners
		buttonChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatActivityIntent = new Intent(getActivity(),
						ChatActivity.class);
				startActivity(chatActivityIntent);
			}
		});

		// Populate member datum, based on if it's your Proposal or not.
		if (getActivity() instanceof HomeActivity) {
			// If this Fragment is within HomeActivity, then it's your Proposal.
			// Set Proposal member datum straight away.
			proposal = db.getMyProposal();
			textViewProposalTitle
					.setText(getString(R.string.my_current_proposal));
			buttonDeleteProposal.setVisibility(View.VISIBLE);
		} else if (getActivity() instanceof ProfileActivity) {
			// If this Fragment is within ProfileActivity, then it's someone
			// else's Proposal. Retrieve this User.
			String hostJid = getActivity().getIntent().getStringExtra(
					Keys.HOST_JID);
			User host = db.getIncomingUser(hostJid);

			// Set Proposal member datum.
			proposal = host.getProposal();
			String proposalTitle = String.format(
					getString(R.string.someones_proposal), host.getFirstName());
			textViewProposalTitle.setText(proposalTitle);
			buttonDeleteProposal.setVisibility(View.INVISIBLE);
		} else {
			Log.e("ProposalFragment.onCreateView",
					"ProposalFragment called from within unknown Activity");
		}

		return view;
	}

}

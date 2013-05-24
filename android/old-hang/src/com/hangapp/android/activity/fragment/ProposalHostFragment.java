package com.hangapp.android.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.android.R;
import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.Proposal;

public class ProposalHostFragment extends SherlockFragment {

	private ToggleButton toggleButtonInterested;
	private ToggleButton toggleButtonConfirmed;

	private TextView textViewDescription;
	private TextView textViewLocation;
	private TextView textViewTime;

	// @Inject
	private DefaultUser defaultUser;

	private Proposal proposal = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_proposal_details,
				container, false);

		// Instantiate dependencies
		defaultUser = DefaultUser.getInstance();

		toggleButtonInterested = (ToggleButton) view
				.findViewById(R.id.toggleButtonInterested);
		toggleButtonConfirmed = (ToggleButton) view
				.findViewById(R.id.toggleButtonConfirmed);

		toggleButtonInterested.setEnabled(false);
		toggleButtonConfirmed.setEnabled(false);

		textViewDescription = (TextView) view
				.findViewById(R.id.textViewDescription);
		textViewLocation = (TextView) view.findViewById(R.id.textViewLocation);
		textViewTime = (TextView) view.findViewById(R.id.textViewTime);

		proposal = defaultUser.getMyProposal();

		textViewDescription.setText(proposal.getDescription());
		textViewLocation.setText(proposal.getLocation());
		textViewTime.setText(proposal.getStartTime().toLocaleString());

		return view;
	}

}

package com.hangapp.newandroid.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.network.rest.RestClient;
import com.hangapp.newandroid.network.rest.RestClientImpl;

public class DeleteProposalDialogFragment extends DialogFragment {

	private Button buttonConfirmDeleteProposal;
	private Button buttonCancelDeleteProposal;

	private Database database;
	private RestClient restClient;

	public DeleteProposalDialogFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.dialog_delete_proposal, container);

		// Instantiate dependencies
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getActivity()
				.getApplicationContext());

		// Reference Views.
		buttonConfirmDeleteProposal = (Button) view
				.findViewById(R.id.buttonConfirmDeleteProposal);
		buttonCancelDeleteProposal = (Button) view
				.findViewById(R.id.buttonCancelDeleteProposal);

		// Set OnClickListeners.
		buttonConfirmDeleteProposal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteMyProposal();
			}
		});
		buttonCancelDeleteProposal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Dismiss the dialog without doing anything.
				DeleteProposalDialogFragment.this.dismiss();
			}
		});

		// Set this dialog's title.
		getDialog().setTitle(getString(R.string.delete_proposal));

		return view;
	}

	private void deleteMyProposal() {

		// Delete my Proposal from the database, notifying any observers in the
		// process.
		database.deleteMyProposal();

		// Send a DELETE request to the server to delete my Proposal
		restClient.deleteMyProposal();

		// Dismiss the modal dialog.
		dismiss();
	}
}

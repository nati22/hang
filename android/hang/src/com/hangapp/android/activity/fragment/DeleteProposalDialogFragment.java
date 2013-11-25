package com.hangapp.android.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;

/**
 * This is the dialog that comes up when you try and delete your own Proposal
 * from {@link YouFragment}.
 */
public class DeleteProposalDialogFragment extends DialogFragment {

	private Button buttonConfirmDeleteProposal;
	private Button buttonCancelDeleteProposal;

	private Database database;
	private RestClient restClient;

	public DeleteProposalDialogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, 0); // remove title from dialogfragment
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

		// Set fonts.
		// Typeface tf = Typeface.createFromAsset(getActivity()
		// .getApplicationContext().getAssets(),
		// Fonts.CHAMPAGNE_LIMOUSINES_BOLD);

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

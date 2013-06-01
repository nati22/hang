package com.hangapp.newandroid.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hangapp.newandroid.R;

public class DeleteProposalDialogFragment extends DialogFragment {

	private Button buttonConfirmDeleteProposal;
	private Button buttonCancelDeleteProposal;

	public DeleteProposalDialogFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.dialog_delete_proposal, container);

		// Reference Views.
		buttonConfirmDeleteProposal = (Button) view
				.findViewById(R.id.buttonConfirmDeleteProposal);
		buttonCancelDeleteProposal = (Button) view
				.findViewById(R.id.buttonCancelDeleteProposal);

		// Set OnClickListeners.
		buttonConfirmDeleteProposal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteProposalDialogFragment.this.dismiss();
			}
		});
		buttonCancelDeleteProposal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteProposalDialogFragment.this.dismiss();
			}
		});

		// Set this dialog's title.
		getDialog().setTitle(getString(R.string.delete_proposal));

		return view;
	}
}

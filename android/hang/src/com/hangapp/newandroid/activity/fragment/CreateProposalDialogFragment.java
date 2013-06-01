package com.hangapp.newandroid.activity.fragment;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.Proposal;
import com.hangapp.newandroid.network.rest.RestClient;
import com.hangapp.newandroid.network.rest.RestClientImpl;

public class CreateProposalDialogFragment extends DialogFragment {

	private EditText editTextProposalDescription;
	private EditText editTextProposalLocation;
	private TimePicker timePickerProposalStartTime;
	private Button buttonDone;
	private Button buttonCancel;

	private Database database;
	private RestClient restClient;

	public CreateProposalDialogFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.dialog_create_proposal, container);

		// Instantiate dependencies.
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getActivity()
				.getApplicationContext());

		// Reference Views.
		editTextProposalDescription = (EditText) view
				.findViewById(R.id.editTextProposalDescription);
		editTextProposalLocation = (EditText) view
				.findViewById(R.id.editTextProposalLocation);
		timePickerProposalStartTime = (TimePicker) view
				.findViewById(R.id.timePickerProposalStartTime);
		buttonDone = (Button) view.findViewById(R.id.buttonDone);
		buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

		// Set OnClickListeners.
		buttonDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				done();
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Dismiss the dialog without doing anything.
				CreateProposalDialogFragment.this.dismiss();
			}
		});

		// Set this dialog's title.
		getDialog().setTitle(getString(R.string.create_new_proposal));

		return view;
	}

	private void done() {
		// Pull Strings out of the EditTexts
		String proposalDescription = editTextProposalDescription.getText()
				.toString();
		String proposalLocation = editTextProposalLocation.getText().toString();

		// Construct DateTime object for start time
		DateTime rightNow = new DateTime();
		DateTime proposalStartTime = new DateTime(rightNow.getYear(),
				rightNow.getMonthOfYear(), rightNow.getDayOfMonth(),
				timePickerProposalStartTime.getCurrentHour(),
				timePickerProposalStartTime.getCurrentMinute());

		// Construct the Proposal object.
		Proposal newProposal = new Proposal(proposalDescription,
				proposalLocation, proposalStartTime);

		// Save it to the database (alerting any listeners in the process)
		database.setMyProposal(newProposal);

		// Upload it to the server.
		restClient.updateMyProposal(newProposal);

		// Dismiss this modal dialog.
		dismiss();
	}
}

package com.hangapp.android.activity.fragment;

import org.joda.time.DateTime;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.Fonts;

public class CreateProposalFragment extends SherlockFragment {

	private static final int FIFTEEN = 15;
	private static final int THIRTY = 30;
	private static final int ONE = 1;

	private TextView textViewCreateProposal;
	private RadioGroup radioGroupNowLater;
	private RadioButton radioButtonNow;
	private RadioButton radioButtonLater;
	private TextView textViewProposalDescription;
	private EditText editTextProposalDescription;
	private TextView textViewProposalLocation;
	private EditText editTextProposalLocation;
	private TextView textViewHowSoon;
	private RadioGroup radioGroupHowSoon;
	private RadioButton radioButtonHowSoonNow;
	private RadioButton radioButtonHowSoon15mins;
	private RadioButton radioButtonHowSoon30mins;
	private RadioButton radioButtonHowSoon1hr;
	private Button buttonCreateProposal;

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

		View view = inflater.inflate(R.layout.fragment_create_proposal,
				container, false);

		// Reference Views.
		textViewCreateProposal = (TextView) view
				.findViewById(R.id.textViewCreateProposal);
		radioGroupNowLater = (RadioGroup) view
				.findViewById(R.id.radioGroupNowLater);
		radioButtonNow = (RadioButton) view.findViewById(R.id.radioButtonNow);
		radioButtonLater = (RadioButton) view
				.findViewById(R.id.radioButtonLater);
		textViewProposalDescription = (TextView) view
				.findViewById(R.id.textViewProposalDescription1);
		editTextProposalDescription = (EditText) view
				.findViewById(R.id.editTextProposalDescription1);
		textViewProposalLocation = (TextView) view
				.findViewById(R.id.textViewProposalLocation1);
		editTextProposalLocation = (EditText) view
				.findViewById(R.id.editTextProposalLocation1);
		textViewHowSoon = (TextView) view
				.findViewById(R.id.textViewProposalHowSoon);
		radioGroupHowSoon = (RadioGroup) view
				.findViewById(R.id.radioGroupHowSoon);
		radioButtonHowSoonNow = (RadioButton) view
				.findViewById(R.id.radioButtonNow1);
		radioButtonHowSoon15mins = (RadioButton) view
				.findViewById(R.id.radioButton15Min1);
		radioButtonHowSoon30mins = (RadioButton) view
				.findViewById(R.id.radioButton30Min1);
		radioButtonHowSoon1hr = (RadioButton) view
				.findViewById(R.id.radioButton1Hr1);
		buttonCreateProposal = (Button) view
				.findViewById(R.id.buttonCreateProposal);

		// Set fonts.
		Typeface tf = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES);
		textViewCreateProposal.setTypeface(tf);
		radioButtonNow.setTypeface(tf);
		radioButtonLater.setTypeface(tf);
		textViewProposalDescription.setTypeface(tf);
		textViewProposalLocation.setTypeface(tf);
		textViewHowSoon.setTypeface(tf);
		radioButtonHowSoonNow.setTypeface(tf);
		radioButtonHowSoon15mins.setTypeface(tf);
		radioButtonHowSoon30mins.setTypeface(tf);
		radioButtonHowSoon1hr.setTypeface(tf);
		buttonCreateProposal.setTypeface(tf);

		// Set OnClickListeners.
		buttonCreateProposal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createProposal();
			}
		});

		return view;
	}

	private void createProposal() {
		String proposalDescription = editTextProposalDescription.getText()
				.toString().trim();
		String proposalLocation = editTextProposalLocation.getText().toString()
				.trim();

		DateTime proposalStartTime = new DateTime();

		switch (radioGroupHowSoon.getCheckedRadioButtonId()) {
		case R.id.radioButtonNow1:
			// Do nothing. Proposal starts right now.
			break;
		case R.id.radioButton15Min1:
			proposalStartTime = proposalStartTime.plusMinutes(FIFTEEN);
			break;
		case R.id.radioButton30Min1:
			proposalStartTime = proposalStartTime.plusMinutes(THIRTY);
			break;
		case R.id.radioButton1Hr1:
			proposalStartTime = proposalStartTime.plusHours(ONE);
			break;
		default:
			Log.e("CreateProposalFragment", "Unknown radio button id: "
					+ radioGroupHowSoon.getCheckedRadioButtonId());
			return;
		}

		try {
			Proposal newProposal = Proposal.createProposal(proposalDescription,
					proposalLocation, proposalStartTime);

			database.setMyProposal(newProposal);
			restClient.updateMyProposal(newProposal);
		} catch (Exception e) {
			Toast.makeText(getActivity(),
					"Invalid proposal: " + e.getMessage(), Toast.LENGTH_SHORT)
					.show();
			return;
		}

	}
}

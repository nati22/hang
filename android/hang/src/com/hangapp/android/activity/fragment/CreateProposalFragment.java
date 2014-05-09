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

/**
 * This fragment is shown within {@link YouFragment}, at the bottom.
 * {@code YouFragment} will dynamically display either this or
 * {@link MyProposalFragment} based on whether or not you have a Proposal.
 */
public class CreateProposalFragment extends SherlockFragment {

    private static final String TAG = CreateProposalFragment.class.getSimpleName();

	// Constants.
	private static final int FIFTEEN = 15;
	private static final int THIRTY = 30;
	private static final int ONE = 1;

	// UI widgets.
	private TextView textViewCreateProposal;
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
	private Button buttonCancelCreateProposal;

	// Dependencies.
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
        Log.d(TAG, "onCreateView called");

		View view = inflater.inflate(R.layout.fragment_create_proposal,
				container, false);

		// Reference Views.
		textViewCreateProposal = (TextView) view
				.findViewById(R.id.textViewCreateProposal);
		radioButtonNow = (RadioButton) view.findViewById(R.id.radioButtonNow);
		radioButtonLater = (RadioButton) view.findViewById(R.id.radioButtonLater);
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
		buttonCancelCreateProposal = (Button) view
				.findViewById(R.id.buttonCancelCreateProposal);

		// Set fonts.
		Typeface tf = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
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
		buttonCancelCreateProposal.setTypeface(tf);

		// Set OnClickListeners.
		buttonCreateProposal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createProposal();
				ProposalsFragment.setupMyFragment(database, getActivity().getSupportFragmentManager());
			}
		});

		buttonCancelCreateProposal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// notify the expandableview to collapse
				ProposalsFragment.collapseView();
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
			proposalStartTime = proposalStartTime.plusSeconds(THIRTY);
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

			editTextProposalDescription.setText("");
			editTextProposalLocation.setText("");

			Toast.makeText(getActivity(),
					"Created Proposal: " + proposalDescription, Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			Toast.makeText(getActivity(), "Invalid proposal: " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
			return;
		}
	}
	
	
	
}

package com.hangapp.android.activity.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.util.Fonts;

public final class MyProposalFragment extends SherlockFragment {

	private TextView textViewMyProposal;
	private TextView textViewMyProposalDescription;
	private TextView textViewMyProposalLocation;
	private TextView textViewMyProposalStartTime;
	private TextView textViewMyProposalInterestedCount;
	private ImageView imageViewDeleteMyProposal;

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
		textViewMyProposal = (TextView) view
				.findViewById(R.id.textViewMyProposal);
		textViewMyProposalDescription = (TextView) view
				.findViewById(R.id.textViewMyProposalDescription);
		textViewMyProposalLocation = (TextView) view
				.findViewById(R.id.textViewMyProposalLocation);
		textViewMyProposalStartTime = (TextView) view
				.findViewById(R.id.textViewMyProposalStartTime);
		textViewMyProposalInterestedCount = (TextView) view
				.findViewById(R.id.textViewMyProposalInterestedCount);
		imageViewDeleteMyProposal = (ImageView) view
				.findViewById(R.id.imageViewDeleteMyProposal);

		// Set fonts
		Typeface champagneLimousinesFontBold = Typeface.createFromAsset(
				getActivity().getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
		Typeface champagneLimousinesFont = Typeface.createFromAsset(
				getActivity().getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES);
		textViewMyProposal.setTypeface(champagneLimousinesFontBold);
		textViewMyProposalDescription.setTypeface(champagneLimousinesFontBold);
		textViewMyProposalLocation.setTypeface(champagneLimousinesFontBold);
		textViewMyProposalStartTime.setTypeface(champagneLimousinesFont);
		textViewMyProposalInterestedCount
				.setTypeface(champagneLimousinesFontBold);

		// Set OnClickListeners
		imageViewDeleteMyProposal.setOnClickListener(new OnClickListener() {
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

		// Load up my Proposal from the database.
		myProposal = database.getMyProposal();

		if (myProposal == null) {
			Log.e("MyProposalFragment", "myProposal is null");
			return;
		}

		textViewMyProposalDescription.setText(myProposal.getDescription());

		// Proposal location is optional.
		if (myProposal.getLocation() == null
				|| myProposal.getLocation().trim().equals("")) {
			textViewMyProposalLocation.setVisibility(View.GONE);
		} else {
			textViewMyProposalLocation.setText(myProposal.getLocation());
			textViewMyProposalLocation.setVisibility(View.VISIBLE);
		}

		textViewMyProposalStartTime.setText(myProposal.getStartTime().toString(
				"h:mm aa"));
		textViewMyProposalInterestedCount.setText(myProposal.getInterested()
				.size() + " interested");
	}

}

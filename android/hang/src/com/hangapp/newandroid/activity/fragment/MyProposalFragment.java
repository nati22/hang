package com.hangapp.newandroid.activity.fragment;

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
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.activity.ChatActivity;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.Proposal;
import com.hangapp.newandroid.model.callback.MyProposalListener;
import com.hangapp.newandroid.util.Keys;

public final class MyProposalFragment extends SherlockFragment implements
		MyProposalListener {

	private ScrollView scrollViewProposal;
	private RelativeLayout emptyView;
	private ImageView imageViewNoProposal;
	private TextView textViewProposalTitle;
	private TextView textViewProposalDescription;
	private TextView textViewProposalLocation;
	private TextView textViewProposalStartTime;
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
		View view = inflater.inflate(R.layout.fragment_proposal, container,
				false);

		// Reference views.
		textViewProposalTitle = (TextView) view
				.findViewById(R.id.textViewProposalTitle);
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
		buttonChat = (ImageView) view.findViewById(R.id.buttonChat);
		buttonDeleteProposal = (ImageView) view
				.findViewById(R.id.buttonDeleteProposal);

		// Populate member datum
		myProposal = database.getMyProposal();
		textViewProposalTitle.setText(getString(R.string.my_current_proposal));

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
	public void onDestroy() {
		super.onDestroy();

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

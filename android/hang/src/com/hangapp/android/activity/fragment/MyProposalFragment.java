package com.hangapp.android.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.ChatActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.callback.MyProposalListener;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;

/**
 * This fragment is shown within {@link YouFragment}, at the bottom.
 * {@code YouFragment} will dynamically display either this or
 * {@link CreateProposalFragment} based on whether or not you have a Proposal.
 */
public final class MyProposalFragment extends SherlockFragment implements
		MyProposalListener {

	private final String TAG = MyProposalFragment.class.getSimpleName();

	// UI widgets.
	private TextView textViewMyProposal;
	private ImageView imageViewChat;
	private TextView textViewMyProposalDescription;
	private TextView textViewMyProposalLocation;
	private TextView textViewMyProposalStartTime;
	private TextView textViewMyProposalInterestedCount;
	private ImageView imageViewDeleteMyProposal;

	private List<String> listInterestedJids = new ArrayList<String>();
	private LinearLayout interestedLinLayout;

	private Proposal myProposal;
	private Database database;

	private Context context;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = activity.getApplicationContext();
	}

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
				.findViewById(R.id.myTextViewMyProposal);
		imageViewChat = (ImageView) view.findViewById(R.id.imageViewChat);
		textViewMyProposalDescription = (TextView) view
				.findViewById(R.id.myTextViewMyProposalDescription);
		textViewMyProposalLocation = (TextView) view
				.findViewById(R.id.myTextViewMyProposalLocation);
		textViewMyProposalStartTime = (TextView) view
				.findViewById(R.id.myTextViewMyProposalStartTime);
		textViewMyProposalInterestedCount = (TextView) view
				.findViewById(R.id.myTextViewMyProposalInterestedCount);
		imageViewDeleteMyProposal = (ImageView) view
				.findViewById(R.id.imageViewDeleteMyProposal);
		interestedLinLayout = (LinearLayout) view
				.findViewById(R.id.myLinLayoutInterested);

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
		imageViewChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatActivityIntent = new Intent(MyProposalFragment.this
						.getActivity().getApplicationContext(),
						ChatActivity.class);
				chatActivityIntent.putExtra(Keys.HOST_JID, database.getMyJid());
				startActivity(chatActivityIntent);
			}
		});

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

		database.addMyProposalListener(this);

		// onMyProposalUpdate(myProposal);
	}

	@Override
	public void onPause() {
		super.onPause();
		database.removeMyProposalListener(this);
	}

	@Override
	public void onMyProposalUpdate(Proposal proposal) {

		Log.d(TAG, "onMyProposalUpdate called with this ALREADY PRESENT:\n"
				+ ">>> myProposal desc: " + myProposal.getDescription() + "\n"
				+ ">>> myProposal loc: " + myProposal.getLocation() + "\n"
				+ ">>> myProposal time: " + myProposal.getStartTime() + "\n"
				+ ">>> myProposal int: "
				+ myProposal.getInterested().toString());

		Log.d(TAG, "onMyProposalUpdate called with this passed in :\n"
				+ ">>> proposal desc: " + proposal.getDescription() + "\n"
				+ ">>> proposal loc: " + proposal.getLocation() + "\n"
				+ ">>> proposal time: " + proposal.getStartTime() + "\n"
				+ ">>> proposal int: " + proposal.getInterested().toString());

		// Make sure Proposal isn't null
		if (myProposal == null) {
			Log.e("MyProposalFragment", "myProposal is null");
			return;
		}

		/*Log.d(TAG, "desc: \"" + myProposal.getDescription() + "\", new desc: "
				+ proposal.getDescription());
		Log.d(TAG, "loc: \n" + myProposal.getLocation() + "\n, new loc: "
				+ proposal.getLocation());
		Log.d(TAG, "int: \n" + myProposal.getInterested().toString());*/

		// Make sure Proposal has changed
		if (!myProposal.equals(proposal)) {
			myProposal = proposal;
		} else {
			Log.d(TAG, "Same Proposal");
			return;
		}

		// Set Proposal text
		textViewMyProposalDescription.setText(myProposal.getDescription());
		
		Log.d(TAG, "setting proposal to " + myProposal.getDescription());
		Log.d(TAG, "setting location to " + myProposal.getLocation());
		Log.d(TAG, "setting interested to " + myProposal.getInterested().toString());

		// Proposal location is optional.
		if (myProposal.getLocation() == null
				|| myProposal.getLocation().trim().equals("")) {
			textViewMyProposalLocation.setVisibility(View.GONE);
		} else {
			textViewMyProposalLocation.setText(myProposal.getLocation());
			textViewMyProposalLocation.setVisibility(View.VISIBLE);
		}

		// Set Proposal start time and Interested count
		textViewMyProposalStartTime.setText(myProposal.getStartTime().toString(
				"h:mm aa"));
		textViewMyProposalInterestedCount.setText(myProposal.getInterested()
				.size() + " interested");

		// Modify Interested users display according to list
		if (myProposal.getInterested() == null) {
			Log.i("MyProposalFragment.onMyProposalUpdate",
					"Proposal interested is null");
			interestedLinLayout.setVisibility(View.GONE);
		} else if (myProposal.getInterested().size() == 0) {
			Log.i("MyProposalFragment.onMyProposalUpdate",
					"Proposal interested is empty");
			interestedLinLayout.setVisibility(View.GONE);
		} else {
			interestedLinLayout.setVisibility(View.VISIBLE);
			if (!myProposal.getInterested().equals(listInterestedJids)) {
				listInterestedJids.clear();
				listInterestedJids.addAll(myProposal.getInterested());
			}
			updateHorizontalList(listInterestedJids, interestedLinLayout);
		}
	}

	public void updateHorizontalList(List<String> jids, LinearLayout linLayout) {

		Log.i(TAG, "jids has " + jids.size() + " elements");

		Log.i(TAG, "removed " + linLayout.getChildCount()
				+ " elements from linLayout");
		linLayout.removeAllViews();

		for (int i = 0; i < jids.size(); i++) {
			String jid = jids.get(i);

			// Get the cell
			View view = LayoutInflater.from(getSherlockActivity()).inflate(
					R.layout.cell_profile_icon, null);

			// Set the FB Profile pic
			ProfilePictureView icon = (ProfilePictureView) view
					.findViewById(R.id.profilePictureIcon);
			Log.i(TAG, "Creating fb icon with jid " + jid);
			icon.setProfileId(jid);

			linLayout.addView(view);
		}
	}
}

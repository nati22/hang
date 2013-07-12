package com.hangapp.android.activity.fragment;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.HomeActivity;
import com.hangapp.android.activity.IncomingBroadcastsActivity;
import com.hangapp.android.activity.OutgoingBroadcastsActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.Availability.Status;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.model.callback.MyAvailabilityListener;
import com.hangapp.android.model.callback.MyProposalListener;
import com.hangapp.android.model.callback.MyUserDataListener;
import com.hangapp.android.model.callback.OutgoingBroadcastsListener;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.Utils;

/**
 * The rightmost tab from {@link HomeActivity}.
 */
public final class YouFragment extends SherlockFragment implements
		MyUserDataListener, MyAvailabilityListener, MyProposalListener,
		IncomingBroadcastsListener, OutgoingBroadcastsListener {

	ProposalChangedListener propChangeListener;

	public interface ProposalChangedListener {
		public void notifyAboutProposalChange(Proposal proposal);
	}

	private ProfilePictureView profilePictureView;
	private TextView textViewMyName;
	private ImageButton imageButtonMyAvailability;
	private TextView textViewMyAvailabilityExpirationDate;
	private TextView textViewStatus;
	private Button buttonOutgoingBroadcasts;
	private Button buttonIncomingBroadcasts;

	private Proposal myProposal;

	private Database database;

	private Availability myCurrentAvailability;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			propChangeListener = (ProposalChangedListener) activity;
		} catch (ClassCastException e) {
			Log.e("YouFragment",
					"Couldn't connect to HomeActivity as a ProposalChangedListener");
			throw new ClassCastException(activity.toString());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		// Instantiate dependencies.
		database = Database.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_you, container, false);

		// Reference views
		profilePictureView = (ProfilePictureView) view
				.findViewById(R.id.profilePictureViewMyIcon);
		textViewMyName = (TextView) view.findViewById(R.id.textViewMyName);
		imageButtonMyAvailability = (ImageButton) view
				.findViewById(R.id.imageButtonMyAvailability);
		textViewMyAvailabilityExpirationDate = (TextView) view
				.findViewById(R.id.textViewMyAvailabilityExpirationDate);
		textViewStatus = (TextView) view.findViewById(R.id.textViewStatus);
		buttonOutgoingBroadcasts = (Button) view
				.findViewById(R.id.buttonOutgoingBroadcasts);
		buttonIncomingBroadcasts = (Button) view
				.findViewById(R.id.buttonIncomingBroadcasts);

		Typeface champagneLimousinesFont = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(), Fonts.CHAMPAGNE_LIMOUSINES);
		Typeface coolveticaFont = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(), Fonts.COOLVETICA);

		textViewMyName.setTypeface(coolveticaFont);
		textViewMyAvailabilityExpirationDate.setTypeface(coolveticaFont);
		textViewStatus.setTypeface(champagneLimousinesFont);
		buttonIncomingBroadcasts.setTypeface(champagneLimousinesFont);
		buttonOutgoingBroadcasts.setTypeface(champagneLimousinesFont);

		buttonOutgoingBroadcasts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(),
						OutgoingBroadcastsActivity.class));
			}
		});
		buttonIncomingBroadcasts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getActivity(),
						IncomingBroadcastsActivity.class));
			}
		});
		imageButtonMyAvailability.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Open the Delete Proposal dialog.
				FragmentManager fm = getActivity().getSupportFragmentManager();

				SetStatusDialogFragment setStatusDialogFragment = new SetStatusDialogFragment();
				setStatusDialogFragment.show(fm, "fragment_set_status");
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Set this Fragment as an AvailabilityListener
		database.addMyUserDataListener(this);
		database.addMyAvailabilityListener(this);
		database.addMyProposalListener(this);
		database.addIncomingBroadcastsListener(this);
		database.addOutgoingBroadcastsListener(this);

		// Retrieve my current Availability from the database.
		myCurrentAvailability = database.getMyAvailability();
		myProposal = database.getMyProposal();

		// Refresh the Proposal for this Fragment.
		onMyAvailabilityUpdate(myCurrentAvailability);
		// onMyProposalUpdate(myProposal);
	}

	@Override
	public void onPause() {
		super.onPause();

		database.removeMyUserDataListener(this);
		database.removeMyAvailabilityListener(this);
		database.removeMyProposalListener(this);
		database.removeIncomingBroadcastsListener(this);
		database.removeOutgoingBroadcastsListener(this);
	}

	@Override
	public void onMyAvailabilityUpdate(Availability newAvailability) {
		if (newAvailability == null) {
			Log.e("YouFragment.onMyAvailabilityUpdate", "Availability was null");
			return;
		} else if (newAvailability.getDescription() == null) {
			Log.e("YouFragment.onMyAvailabilityUpdate", "Description was null");
			return;
		} else if (newAvailability.getExpirationDate() == null) {
			Log.e("YouFragment.onMyAvailabilityUpdate", "Expiration date was null");
			return;
		}

		Log.i("YouFragment.onMyAvailabilityUpdate", "onMyAvailabilityUpdate: "
				+ newAvailability.getExpirationDate().toString());

		// propChangeListener.onProposalChangedListenerNotified(null);

		myCurrentAvailability = newAvailability;

		// If the Availability is invalid for some reason, display as grey.
		if (myCurrentAvailability == null
				|| myCurrentAvailability.getStatus() == null
				|| !myCurrentAvailability.isActive()) {
			imageButtonMyAvailability.setImageDrawable(getResources().getDrawable(
					R.drawable.imagebutton_status_grey));
			textViewStatus.setVisibility(View.GONE);
		}
		// Availability is FREE.
		else if (myCurrentAvailability.getStatus() == Status.FREE) {
			imageButtonMyAvailability.setImageDrawable(getResources().getDrawable(
					R.drawable.imagebutton_status_green));
			textViewStatus.setVisibility(View.VISIBLE);
			textViewStatus.setText(myCurrentAvailability.getDescription());

			int remainingHours = Utils.getRemainingHours(myCurrentAvailability
					.getExpirationDate());
			textViewMyAvailabilityExpirationDate.setText(remainingHours + "h");
		}
		// Availability is BUSY.
		else if (myCurrentAvailability.getStatus() == Status.BUSY) {
			imageButtonMyAvailability.setImageDrawable(getResources().getDrawable(
					R.drawable.imagebutton_status_red));
			textViewStatus.setVisibility(View.VISIBLE);
			textViewStatus.setText(myCurrentAvailability.getDescription());

			int remainingHours = Utils.getRemainingHours(myCurrentAvailability
					.getExpirationDate());
			textViewMyAvailabilityExpirationDate.setText(remainingHours + "h");
		}
		// Error state.
		else {
			Log.e("YouFragment.onMyAvailabilityUpdate",
					"Unknown availability state: " + myCurrentAvailability);
			return;
		}
	}

	@Override
	public void onOutgoingBroadcastsUpdate(List<User> outgoingBroadcasts) {
		if (outgoingBroadcasts == null) {
			buttonOutgoingBroadcasts.setText("0 outgoing");
		} else {
			buttonOutgoingBroadcasts.setText(outgoingBroadcasts.size()
					+ " outgoing");
		}
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		if (incomingBroadcasts == null) {
			buttonOutgoingBroadcasts.setText("0 incoming");
		} else {
			buttonIncomingBroadcasts.setText(incomingBroadcasts.size()
					+ " incoming");
		}
	}

	@Override
	public void onMyUserDataUpdate(User me) {
		profilePictureView.setProfileId(me.getJid());
		textViewMyName.setText(me.getFullName().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public void onMyProposalUpdate(Proposal proposal) {

		Log.d("YouFragment", "onMyProposalUpdate called");
		if (proposal != null) {
			Log.d("YouFragment.proposal.desc", "" + proposal.getDescription());
		}

		myProposal = proposal;

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		if (myProposal == null) {
			CreateProposalFragment fragment = new CreateProposalFragment();
			fragmentTransaction.replace(R.id.frameLayoutYouFragment, fragment,
					Keys.CREATE_PROPOSAL_FRAGMENT_TAG);
			fragmentTransaction.commit();
		} else {
			Log.d("", "myProposal != null");
			
			// Try to get an already existing MyProposalFragment
			MyProposalFragment preexistingFragment = (MyProposalFragment) fragmentManager
					.findFragmentByTag(Keys.MY_PROPOSAL_FRAGMENT_TAG);
			
			// If the FragmentManager already has a MyProposalFragment
			if (preexistingFragment != null) {
				Log.d("", "preexistingFragment != null");
				// If the MyProposalFragment is showing
				if (preexistingFragment.isVisible()) {
					propChangeListener.notifyAboutProposalChange(proposal);
				} else {
					fragmentTransaction.replace(R.id.frameLayoutYouFragment, preexistingFragment,
							Keys.MY_PROPOSAL_FRAGMENT_TAG);
					fragmentTransaction.commit();
				}
				
			} else {
				MyProposalFragment fragment = new MyProposalFragment();
				fragmentTransaction.replace(R.id.frameLayoutYouFragment, fragment,
						Keys.MY_PROPOSAL_FRAGMENT_TAG);
				fragmentTransaction.commit();
			}
			
			// Pass in the proposal here
			propChangeListener.notifyAboutProposalChange(myProposal);
		}

		
	}

}

package com.hangapp.android.activity.fragment;

import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.hangapp.android.activity.IncomingBroadcastsActivity;
import com.hangapp.android.activity.OutgoingBroadcastsActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.model.callback.MyAvailabilityListener;
import com.hangapp.android.model.callback.MyProposalListener;
import com.hangapp.android.model.callback.MyUserDataListener;
import com.hangapp.android.model.callback.OutgoingBroadcastsListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.Fonts;

public final class YouFragment extends SherlockFragment implements
		MyUserDataListener, MyAvailabilityListener, MyProposalListener,
		IncomingBroadcastsListener, OutgoingBroadcastsListener {

	private ProfilePictureView profilePictureView;
	private TextView textViewMyName;
	private ImageButton imageButtonMyAvailability;
	private TextView textViewStatus;
	private Button buttonOutgoingBroadcasts;
	private Button buttonIncomingBroadcasts;

	private Proposal myProposal;

	private Database database;
	private RestClient restClient;

	private Availability myCurrentAvailability;

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
		View view = inflater.inflate(R.layout.fragment_you, container, false);

		// Reference views
		profilePictureView = (ProfilePictureView) view
				.findViewById(R.id.profilePictureViewMyIcon);
		textViewMyName = (TextView) view.findViewById(R.id.textViewMyName);
		imageButtonMyAvailability = (ImageButton) view
				.findViewById(R.id.imageButtonMyAvailability);
		textViewStatus = (TextView) view.findViewById(R.id.textViewStatus);
		buttonOutgoingBroadcasts = (Button) view
				.findViewById(R.id.buttonOutgoingBroadcasts);
		buttonIncomingBroadcasts = (Button) view
				.findViewById(R.id.buttonIncomingBroadcasts);

		Typeface champagneLimousinesFont = Typeface.createFromAsset(
				getActivity().getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES);
		Typeface coolveticaFont = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(), Fonts.COOLVETICA);

		textViewMyName.setTypeface(coolveticaFont);
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
		onMyProposalUpdate(myProposal);
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
			Log.e("YouFragment.onMyAvailabilityUpdate",
					"Expiration date was null");
			return;
		}

		Log.i("YouFragment.onMyAvailabilityUpdate", "onMyAvailabilityUpdate: "
				+ newAvailability.getExpirationDate().toString());

		myCurrentAvailability = newAvailability;
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
		myProposal = proposal;

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		Fragment fragment = null;

		if (myProposal == null) {
			fragment = new CreateProposalFragment();
		} else {
			fragment = new MyProposalFragment();
		}

		fragmentTransaction.replace(R.id.frameLayoutYouFragment, fragment);
		fragmentTransaction.commit();
	}
}
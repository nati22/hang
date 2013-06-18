package com.hangapp.android.activity.fragment;

import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.IncomingBroadcastsActivity;
import com.hangapp.android.activity.OutgoingBroadcastsActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.model.callback.MyAvailabilityListener;
import com.hangapp.android.model.callback.MyUserDataListener;
import com.hangapp.android.model.callback.OutgoingBroadcastsListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;

public final class MyAvailabilityFragment extends SherlockFragment implements
		MyAvailabilityListener, MyUserDataListener, IncomingBroadcastsListener,
		OutgoingBroadcastsListener {

	private ProfilePictureView profilePictureView;
	private TextView textViewMyName;
	private Button buttonOutgoingBroadcasts;
	private Button buttonIncomingBroadcasts;

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
		View view = inflater.inflate(R.layout.fragment_me, container,
				false);

		// Reference views
		profilePictureView = (ProfilePictureView) view
				.findViewById(R.id.profilePictureViewMyIcon);
		textViewMyName = (TextView) view.findViewById(R.id.textViewMyName);
		buttonOutgoingBroadcasts = (Button) view
				.findViewById(R.id.buttonOutgoingBroadcasts);
		buttonIncomingBroadcasts = (Button) view
				.findViewById(R.id.buttonIncomingBroadcasts);

		Typeface tf = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/champagne_limousines_bold.ttf");

		textViewMyName.setTypeface(tf);
		buttonIncomingBroadcasts.setTypeface(tf);
		buttonOutgoingBroadcasts.setTypeface(tf);

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

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Set this Fragment as an AvailabilityListener
		database.addMyAvailabilityListener(this);
		database.addMyUserDataListener(this);
		database.addIncomingBroadcastsListener(this);
		database.addOutgoingBroadcastsListener(this);

		// Retrieve my current Availability from the database.
		myCurrentAvailability = database.getMyAvailability();
	}

	@Override
	public void onPause() {
		super.onPause();

		database.removeMyAvailabilityListener(this);
		database.removeMyUserDataListener(this);
		database.removeIncomingBroadcastsListener(this);
		database.removeOutgoingBroadcastsListener(this);
	}

	@Override
	public void onMyAvailabilityUpdate(Availability newAvailability) {
		Log.d("MyAvailabilityFragment.onMyAvailabilityUpdate",
				"onMyAvailabilityUpdate: "
						+ newAvailability.getExpirationDate().toString());

		myCurrentAvailability = newAvailability;
	}

	@Override
	public void onOutgoingBroadcastsUpdate(List<User> outgoingBroadcasts) {
		if (outgoingBroadcasts == null) {
			buttonOutgoingBroadcasts.setText("0 Outgoing Broadcasts");
		} else if (outgoingBroadcasts.size() == 1) {
			buttonOutgoingBroadcasts.setText("1 Outgoing Broadcast");
		} else {
			buttonOutgoingBroadcasts.setText(outgoingBroadcasts.size()
					+ " Outgoing Broadcasts");
		}
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		if (incomingBroadcasts == null) {
			buttonOutgoingBroadcasts.setText("0 Incoming Broadcasts");
		} else if (incomingBroadcasts.size() == 1) {
			buttonIncomingBroadcasts.setText("1 Incoming Broadcast");
		} else {
			buttonIncomingBroadcasts.setText(incomingBroadcasts.size()
					+ " Incoming Broadcasts");
		}
	}

	@Override
	public void onMyUserDataUpdate(User me) {
		profilePictureView.setProfileId(me.getJid());
		textViewMyName.setText(me.getFullName().toLowerCase());
	}
}

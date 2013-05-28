package com.hangapp.newandroid.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.UserDatabase;
import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.util.HangLog;

public class AvailabilityFragment extends SherlockFragment {

	private UserDatabase database;

	private Availability myAvailability;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		database = UserDatabase.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_availability, container,
				false);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		HangLog.toastD(getActivity(), "AvailabilityFragment.onResume",
				"My Jid: " + database.getMyJid());

		// Pull the user's current Availability.
		myAvailability = database.getMyAvailability();
	}

}

package com.hangapp.android.activity.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.callback.MyAvailabilityListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;

public final class MyAvailabilityFragment extends SherlockFragment implements
		MyAvailabilityListener {

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
		View view = inflater.inflate(R.layout.fragment_availability, container,
				false);

		Typeface tf = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/champagne_limousines_bold.ttf");
		TextView tv = (TextView) view.findViewById(R.id.textViewMyName);
		tv.setTypeface(tf);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Set this Fragment as an AvailabilityListener
		database.addMyAvailabilityListener(this);

		// Retrieve my current Availability from the database.
		myCurrentAvailability = database.getMyAvailability();
	}

	@Override
	public void onPause() {
		super.onPause();
		database.removeMyAvailabilityListener(this);
	}

	@Override
	public void onMyAvailabilityUpdate(Availability newAvailability) {
		Log.d("MyAvailabilityFragment.onMyAvailabilityUpdate",
				"onMyAvailabilityUpdate: "
						+ newAvailability.getExpirationDate().toString());

		myCurrentAvailability = newAvailability;
	}
}

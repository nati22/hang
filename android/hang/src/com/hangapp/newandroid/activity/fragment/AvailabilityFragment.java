package com.hangapp.newandroid.activity.fragment;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.model.Availability.Status;
import com.hangapp.newandroid.model.callback.MyAvailabilityListener;
import com.hangapp.newandroid.network.rest.RestClient;
import com.hangapp.newandroid.network.rest.RestClientImpl;
import com.hangapp.newandroid.util.AvailabilityButton;
import com.hangapp.newandroid.util.HangLog;

public final class AvailabilityFragment extends SherlockFragment implements
		MyAvailabilityListener {

	private AvailabilityButton[] buttonsAvailability;
	private Button buttonDone;
	private Button buttonCancel;

	private Database database;
	private RestClient restClient;

	private Availability myCurrentAvailability;
	private Availability myNewAvailability;

	private AvailabilityButtonOnClickListener availabilityOnClickListener = new AvailabilityButtonOnClickListener();

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

		// Reference Views.
		buttonsAvailability = new AvailabilityButton[] {
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability00),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability01),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability02),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability03),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability04),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability05),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability06),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability07),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability08),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability09),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability10),
				(AvailabilityButton) view
						.findViewById(R.id.buttonAvailability11) };
		buttonDone = (Button) view.findViewById(R.id.buttonDone);
		buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

		// Initialize the Done and Cancel buttons.
		buttonDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmNewAvailability();
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelNewAvailability();
			}
		});

		// Initialize the AvailabilityButtons.
		for (int i = 0; i < buttonsAvailability.length; i++) {
			buttonsAvailability[i].setId(i);
			buttonsAvailability[i]
					.setOnClickListener(availabilityOnClickListener);
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		database.addMyStatusListener(this);

		// Retrieve my current Availability from the database.
		myCurrentAvailability = database.getMyAvailability();

		initializeAvailabilityButtons();
	}

	@Override
	public void onPause() {
		super.onPause();

		database.removeMyStatusListener(this);
	}

	class AvailabilityButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(View button) {
			// Cast the button that was clicked to an AvailabilityButton.
			AvailabilityButton availabilityButton = (AvailabilityButton) button;

			// None -> Free.
			if (availabilityButton.getState() == null) {
				changeAvailabilityButtonStates(availabilityButton, Status.FREE);
			}
			// Free -> Busy.
			else if (availabilityButton.getState() == Status.FREE) {
				changeAvailabilityButtonStates(availabilityButton, Status.BUSY);
			}
			// Busy -> None.
			else if (availabilityButton.getState() == Status.BUSY) {
				changeAvailabilityButtonStates(availabilityButton, null);
			}
			// Error case.
			else {
				Log.e("AvailabilityButtonOnClickListener", "Unknown state: "
						+ availabilityButton.getState().toString());
			}
		}
	}

	private void initializeAvailabilityButtons() {
		// Find the instant of time RIGHT NOW.
		DateTime rightNow = new DateTime();

		// Construct a new DateTime that is only as accurate as the current
		// hour.
		DateTime rightNowTruncated = new DateTime(rightNow.getYear(),
				rightNow.getMonthOfYear(), rightNow.getDayOfMonth(),
				rightNow.getHourOfDay(), 0);

		// Set the time of each button.
		for (AvailabilityButton buttonAvailability : buttonsAvailability) {
			buttonAvailability.setTime(rightNowTruncated);
			rightNowTruncated = rightNowTruncated.plusHours(1);
		}

		// TODO: Update the colors of the Availability strip.
		updateAvailabilityStripColors(myCurrentAvailability);
	}

	private void changeAvailabilityButtonStates(AvailabilityButton buttton,
			Availability.Status newState) {
		// Initialize the potential new Availability.
		myNewAvailability = new Availability(newState, buttton.getTime());

		// Update the colors of the Availability strip.
		updateAvailabilityStripColors(buttton.getId(), newState);

		// Enable the "Edit" buttons in case the user changes his mind.
		buttonDone.setVisibility(View.VISIBLE);
		buttonCancel.setVisibility(View.VISIBLE);
	}

	private void confirmNewAvailability() {
		// Disable the "Edit" buttons.
		buttonDone.setVisibility(View.INVISIBLE);
		buttonCancel.setVisibility(View.INVISIBLE);

		// Upload this new Availability to the server.
		database.setMyAvailability(myNewAvailability);
		restClient.updateMyAvailability(myNewAvailability);

		// Set the new Availability and clear out the old one.
		myCurrentAvailability = myNewAvailability;
		myNewAvailability = null;
	}

	private void cancelNewAvailability() {
		// Disable the "Edit" buttons.
		buttonDone.setVisibility(View.INVISIBLE);
		buttonCancel.setVisibility(View.INVISIBLE);

		// Clear the potential new Availability.
		myNewAvailability = null;

		// Update the UI to reflect the old Availability again.
		updateAvailabilityStripColors(myCurrentAvailability);
	}

	private boolean updateAvailabilityStripColors(Availability availability) {
		if (availability == null) {
			HangLog.toastE(getActivity(),
					"AvailabilityFragment.updateAvailabilityStripColors",
					"Couldn't udpateAvailabilityStripColors: Availability given was null");
			return false;
		}

		// Search for the AvailabilityButton that corresponds to the
		// Availability's expiration date.
		for (AvailabilityButton button : buttonsAvailability) {
			if (button.getTime().isEqual(availability.getExpirationDate())) {
				updateAvailabilityStripColors(button.getId(),
						availability.getColor());
				return true;
			}
		}

		// If this method gets here, then the for loop failed to find the
		// AvailabilityButton that we wanted. Show an error message.
		HangLog.toastE(getActivity(),
				"AvailabilityFragment.updateAvailabilityStripColors",
				"Couldn't find Availability button for expiration date: "
						+ availability.getExpirationDate());
		return false;
	}

	private void updateAvailabilityStripColors(int middleButtonId,
			Availability.Status newState) {

		// This button and all buttons to the left of this button should be set
		// to the new state.
		for (int i = 0; i <= middleButtonId; i++) {
			buttonsAvailability[i].setState(newState);
		}

		// Every button to the right of this button should be set to null state.
		for (int i = middleButtonId + 1; i < buttonsAvailability.length; i++) {
			buttonsAvailability[i].setState(null);
		}
	}

	@Override
	public void onMyAvailabilityUpdate(Availability newAvailability) {
		myCurrentAvailability = newAvailability;
		initializeAvailabilityButtons();
	}
}

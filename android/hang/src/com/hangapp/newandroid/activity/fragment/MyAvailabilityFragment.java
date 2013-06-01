package com.hangapp.newandroid.activity.fragment;

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
import com.hangapp.newandroid.util.Utils;

public final class MyAvailabilityFragment extends SherlockFragment implements
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

		Utils.initializeAvailabilityButtons(buttonsAvailability);
		Utils.updateAvailabilityStripColors(buttonsAvailability,
				myCurrentAvailability, getActivity());
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

			if (myCurrentAvailability == null && myNewAvailability == null) {
				changeAvailabilityButtonStates(availabilityButton, Status.FREE);
				return;
			}

			if (myNewAvailability == null) {
				myNewAvailability = myCurrentAvailability;
			}

			// Assume that the new Status color is the same as the
			// current Status. The bar should only toggle if you click the
			// rightmost button.
			if (availabilityButton.getTime().isEqual(
					myNewAvailability.getExpirationDate())) {
				// None -> Free.
				if (availabilityButton.getState() == null) {
					myNewAvailability.setStatus(Status.FREE);
				}
				// Free -> Busy.
				else if (availabilityButton.getState() == Status.FREE) {
					myNewAvailability.setStatus(Status.BUSY);
				}
				// Busy -> None.
				else if (availabilityButton.getState() == Status.BUSY) {
					myNewAvailability = null;
				} else {
					Log.e("AvailabilityButtonOnClickListener",
							"Unknown state: "
									+ availabilityButton.getState().toString());
				}
			}

			changeAvailabilityButtonStates(availabilityButton,
					myNewAvailability != null ? myNewAvailability.getStatus()
							: null);
		}
	}

	private void changeAvailabilityButtonStates(AvailabilityButton buttton,
			Availability.Status newState) {
		// Initialize the potential new Availability
		if (newState != null) {
			myNewAvailability = new Availability(newState, buttton.getTime());
		} else {
			myNewAvailability = null;
		}

		// Update the colors of the Availability strip.
		Utils.updateAvailabilityStripColors(buttonsAvailability,
				buttton.getId(), newState);

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
		Utils.updateAvailabilityStripColors(buttonsAvailability,
				myCurrentAvailability, getActivity());
	}

	@Override
	public void onMyAvailabilityUpdate(Availability newAvailability) {
		myCurrentAvailability = newAvailability;
		Utils.initializeAvailabilityButtons(buttonsAvailability);
		Utils.updateAvailabilityStripColors(buttonsAvailability,
				newAvailability, getActivity());
	}
}

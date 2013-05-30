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
import com.hangapp.newandroid.util.AvailabilityButton;

public final class AvailabilityFragment extends SherlockFragment {

	private AvailabilityButton[] buttonsAvailability;
	private Button buttonDone;
	private Button buttonCancel;

	private Database database;

	private Availability myAvailability;
	private AvailabilityButtonOnClickListener availabilityOnClickListener = new AvailabilityButtonOnClickListener();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		database = Database.getInstance();

		myAvailability = new Availability();
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

		buttonDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeAvailabilityClean();
			}
		});

		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeAvailabilityClean();
			}
		});

		// Set the OnClickListeners.
		for (Button button : buttonsAvailability) {
			button.setOnClickListener(availabilityOnClickListener);
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		initializeAvailabilityButtons();
	}

	class AvailabilityButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(View button) {
			AvailabilityButton availabilityButton = (AvailabilityButton) button;

			// None -> Free.
			if (availabilityButton.getState() == null) {
				availabilityButton.setState(Status.FREE);
				myAvailability.putStatus(availabilityButton.getTime(),
						Status.FREE);
				makeAvailabilityDirty();
				return;
			}

			switch (availabilityButton.getState()) {
			// Free -> Busy.
			case FREE:
				availabilityButton.setState(Status.BUSY);
				myAvailability.putStatus(availabilityButton.getTime(),
						Status.BUSY);
				makeAvailabilityDirty();
				return;
				// Busy -> None.
			case BUSY:
				availabilityButton.setState(null);
				myAvailability.removeStatus(availabilityButton.getTime());
				makeAvailabilityDirty();
				return;
			default:
				Log.e("AvailabilityButtonOnClickListener", "Unknown state: "
						+ availabilityButton.getState().toString());
				return;
			}
		}
	}

	private void initializeAvailabilityButtons() {
		// Find the instant of time RIGHT NOW.
		DateTime rightNow = new DateTime();

		// Construct a new DateTime that is only as accurate as the current
		// hour.
		DateTime rightNowNoMinutes = new DateTime(rightNow.getYear(),
				rightNow.getMonthOfYear(), rightNow.getDayOfMonth(),
				rightNow.getHourOfDay(), 0);

		// Set the time of each button.
		for (AvailabilityButton buttonAvailability : buttonsAvailability) {
			buttonAvailability.setTime(rightNowNoMinutes);
			rightNowNoMinutes = rightNowNoMinutes.plusHours(1);
		}
	}

	private void makeAvailabilityDirty() {
		buttonDone.setVisibility(View.VISIBLE);
		buttonCancel.setVisibility(View.VISIBLE);
	}

	private void makeAvailabilityClean() {
		buttonDone.setVisibility(View.INVISIBLE);
		buttonCancel.setVisibility(View.INVISIBLE);
	}
}

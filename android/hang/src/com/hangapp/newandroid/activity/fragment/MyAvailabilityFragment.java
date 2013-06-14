package com.hangapp.newandroid.activity.fragment;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;
import android.widget.Toast;

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

	private AvailabilityButton[] availabilityStrip;
	private CheckBox checkBoxFree;
	private CheckBox checkBoxBusy;
	private Button buttonPost;
	private TimePicker timePickerAvailability;

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
		View view = inflater.inflate(
				R.layout.fragment_availability2_timepicker, container, false);

		// Reference Views.
		availabilityStrip = new AvailabilityButton[] {
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
		buttonPost = (Button) view.findViewById(R.id.buttonUpdateMyAvailability);
		checkBoxFree = (CheckBox) view.findViewById(R.id.checkBoxFree);
		checkBoxBusy = (CheckBox) view.findViewById(R.id.checkBoxBusy);
		timePickerAvailability = (TimePicker) view
				.findViewById(R.id.timePickerAvailability);
		timePickerAvailability
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		timePickerAvailability.setCurrentHour(new DateTime().getHourOfDay());
		timePickerAvailability.setCurrentMinute(new DateTime()
				.getMinuteOfHour());

		// Initialize the Done and Cancel buttons.
		checkBoxBusy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					checkBoxFree.setChecked(false);
					timePickerAvailability.setEnabled(true);
				} else {
					if (!checkBoxFree.isChecked()) {
						timePickerAvailability.setEnabled(false);
					}
				}
			}
		});
		checkBoxFree.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					checkBoxBusy.setChecked(false);
					timePickerAvailability.setEnabled(true);
				} else {
					if (!checkBoxBusy.isChecked()) {
						timePickerAvailability.setEnabled(false);
					}
				}
			}
		});
		buttonPost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!checkBoxBusy.isChecked() && !checkBoxFree.isChecked()) {
					Toast.makeText(getActivity(),
							"Please pick an availability: Free or Busy?",
							Toast.LENGTH_SHORT).show();
					return;
				}

				// Instantiate a new DateTime with the hour parsed from the
				// TimePicker.
				DateTime rightNow = new DateTime();
				DateTime expirationDate = new DateTime(rightNow.getYear(),
						rightNow.getMonthOfYear(), rightNow.getDayOfMonth(),
						timePickerAvailability.getCurrentHour(), 0);

				// If the DateTime is pointing to a time that has already
				// passed, then the user really meant for an expiration date
				// tomorrow. Add one day.
				if (expirationDate.isBefore(rightNow)) {
					expirationDate = expirationDate.plusDays(1);
				}

				Status status = checkBoxFree.isChecked() ? Status.FREE
						: Status.BUSY;
				Availability availability = new Availability(status,
						expirationDate);

				saveNewAvailability(availability);
			}
		});

		// Initialize the AvailabilityButtons.
		for (int i = 0; i < availabilityStrip.length; i++) {
			availabilityStrip[i].setId(i);
		}
		Utils.initializeAvailabilityButtons(availabilityStrip);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Set this Fragment as an AvailabilityListener
		database.addMyAvailabilityListener(this);

		// Retrieve my current Availability from the database.
		myCurrentAvailability = database.getMyAvailability();

		// Update the Availability strip.
		Utils.updateAvailabilityStripColors(availabilityStrip,
				myCurrentAvailability, getActivity());
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

		Utils.updateAvailabilityStripColors(availabilityStrip, newAvailability,
				getActivity());
	}

	private void saveNewAvailability(Availability newAvailability) {
		Log.i("myNewAvailability", newAvailability.toString());

		// Upload this new Availability to the server.
		database.setMyAvailability(newAvailability);
		restClient.updateMyAvailability(newAvailability);

		// Set the new Availability and clear out the old one.
		myCurrentAvailability = newAvailability;
	}

}

package com.hangapp.newandroid.activity.fragment;

import org.joda.time.DateTime;

import android.content.Context;
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

	private AvailabilityButton[] buttonsAvailability;
	private CheckBox checkBoxFree;
	private CheckBox checkBoxBusy;
	private Button buttonPost;
	private Button buttonCancel;
	// private SeekBar seekBarAvailability;
	private TimePicker timePickerAvailability;

	private Database database;
	private RestClient restClient;

	private Availability myCurrentAvailability;
	private Availability myNewAvailability;

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
		buttonPost = (Button) view.findViewById(R.id.buttonPostAvailability);
		buttonCancel = (Button) view.findViewById(R.id.buttonCancel);
		checkBoxFree = (CheckBox) view.findViewById(R.id.checkBoxFree);
		checkBoxBusy = (CheckBox) view.findViewById(R.id.checkBoxBusy);
		/*
		 * seekBarAvailability = (SeekBar) view
		 * .findViewById(R.id.seekBarAvailability);
		 * seekBarAvailability.setEnabled(false);
		 */
		timePickerAvailability = (TimePicker) view
				.findViewById(R.id.timePickerAvailability);
		timePickerAvailability
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		// timePickerAvailability.setEnabled(false);
		timePickerAvailability.setCurrentHour(new DateTime().getHourOfDay());
		timePickerAvailability.setCurrentMinute(new DateTime()
				.getMinuteOfHour());

		// Initialize the Done and Cancel buttons.
		/*
		 * buttonPost.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { confirmNewAvailability(); }
		 * }); buttonCancel.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { cancelNewAvailability(); }
		 * });
		 */
		checkBoxBusy.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					checkBoxFree.setChecked(false);
					// seekBarAvailability.setEnabled(true);
					timePickerAvailability.setEnabled(true);
				} else {
					if (!checkBoxFree.isChecked()) {
						// seekBarAvailability.setEnabled(false);
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
					// seekBarAvailability.setEnabled(true);
					timePickerAvailability.setEnabled(true);
				} else {
					if (!checkBoxBusy.isChecked()) {
						// seekBarAvailability.setEnabled(false);
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

				// Get DateTime out of TimePicker
				DateTime dt = new DateTime();
				dt = dt.withHourOfDay(timePickerAvailability.getCurrentHour());
				dt = dt.withMinuteOfHour(timePickerAvailability
						.getCurrentMinute());
				
				if (areSameTime(getActivity(), dt, currentTime())) {
					Toast.makeText(getActivity(),
							"Status time is for right now", Toast.LENGTH_SHORT)
							.show();
					return;
				} else if (dt.isBefore(new DateTime())) {
					Toast.makeText(getActivity(), "Status time is before NOW.",
							Toast.LENGTH_SHORT).show();
//					Log.d(MyAvailabilityFragment.class.getSimpleName(),
//							"TimePicker time: " + dt.toString());
//					Log.d(MyAvailabilityFragment.class.getSimpleName(),
//							"Current time: " + new DateTime().toString());
//					Log.d(MyAvailabilityFragment.class.getSimpleName(),
//							"Status time is in the past.");
					return;
				} else {
					Toast.makeText(getActivity(), "Status time is valid.",
							Toast.LENGTH_SHORT).show();
//					Log.d(MyAvailabilityFragment.class.getSimpleName(),
//							"TimePicker time: " + dt.toString());
//					Log.d(MyAvailabilityFragment.class.getSimpleName(),
//							"Current time: " + new DateTime().toString());
				}

				Status st = checkBoxFree.isChecked() ? Status.FREE
						: Status.BUSY;
				Log.i("st status is", st.toString());
				Log.i("dt time is ", dt.toString());

				Availability avail = new Availability(st, dt);

//				Log.w("so avail.status should be of type", avail.getStatus()
//						+ "");
//				Log.w("so avail.status should be", avail.getStatus().toString());
//				Log.w("so avail.getExpHOUR is ", avail.getExpirationDate()
//						.getHourOfDay() + "");
//				Log.w("so avail.getExpMin is", avail.getExpirationDate()
//						.getMinuteOfHour() + "");
//
//				Log.i("avail exp", avail.getExpirationDate().toString());
//				Log.i("avail status", avail.getStatus() + "");

				// Toast.makeText(
				// getActivity(),
				// "setting availability "
				// + avail.getExpirationDate().toString(),
				// Toast.LENGTH_SHORT).show();

				confirmNewAvailability(avail);
			}
		});

		// Initialize the AvailabilityButtons.
		for (int i = 0; i < buttonsAvailability.length; i++) {
			buttonsAvailability[i].setId(i);
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Set this Fragment as an AvailabilityListener
		database.addMyAvailabilityListener(this);

		// Retrieve my current Availability from the database.
		myCurrentAvailability = database.getMyAvailability();

		// Initialize Availability buttons and update colors
		Utils.initializeAvailabilityButtons(buttonsAvailability);
		Utils.updateAvailabilityStripColors(buttonsAvailability,
				myCurrentAvailability, getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		database.removeMyAvailabilityListener(this);
	}

	@Override
	public void onMyAvailabilityUpdate(Availability newAvailability) {
		myCurrentAvailability = newAvailability;

	}

	/*
	 * private void changeAvailabilityButtonStates(AvailabilityButton buttton,
	 * Availability.Status newState) { // Initialize the potential new
	 * Availability if (newState != null) { myNewAvailability = new
	 * Availability(newState, buttton.getTime()); } else { myNewAvailability =
	 * null; }
	 * 
	 * // Update the colors of the Availability strip.
	 * Utils.updateAvailabilityStripColors(buttonsAvailability, buttton.getId(),
	 * newState);
	 * 
	 * // Enable the "Edit" buttons in case the user changes his mind.
	 * buttonPost.setVisibility(View.VISIBLE);
	 * buttonCancel.setVisibility(View.VISIBLE); }
	 */
	private void confirmNewAvailability(Availability newAvailability) {
		// Disable the "Edit" buttons.
		// buttonPost.setVisibility(View.INVISIBLE);
		// buttonCancel.setVisibility(View.INVISIBLE);

		Log.i("myNewAvailability", newAvailability.toString());

		// Upload this new Availability to the server.
		database.setMyAvailability(newAvailability);
		restClient.updateMyAvailability(newAvailability);

		// Set the new Availability and clear out the old one.
		myCurrentAvailability = newAvailability;
		myNewAvailability = null;

		Log.d("db.getMyAvailability", database.getMyAvailability().toString());
	}

	/*
	 * private void cancelNewAvailability() { // Disable the "Edit" buttons.
	 * buttonPost.setVisibility(View.INVISIBLE);
	 * buttonCancel.setVisibility(View.INVISIBLE);
	 * 
	 * // Clear the potential new Availability. myNewAvailability = null;
	 * 
	 * // Update the UI to reflect the old Availability again.
	 * Utils.updateAvailabilityStripColors(buttonsAvailability,
	 * myCurrentAvailability, getActivity()); }
	 * 
	 * @Override public void onMyAvailabilityUpdate(Availability
	 * newAvailability) { myCurrentAvailability = newAvailability;
	 * Utils.initializeAvailabilityButtons(buttonsAvailability);
	 * Utils.updateAvailabilityStripColors(buttonsAvailability, newAvailability,
	 * getActivity()); }
	 */

	public static boolean areSameTime(Context ctxt, DateTime dt1, DateTime dt2) {

		if (dt1.getDayOfYear() == dt2.getDayOfYear()) {
			if (dt1.getHourOfDay() == dt2.getHourOfDay()) {
				if (dt1.getMinuteOfHour() == dt2.getMinuteOfHour()) {
					Log.d("areSameTime()", "different minute");
					return true;
				} else {
					return false;
				}
			} else {
				Log.d("areSameTime()", "different hour");
				return false;
			}
		} else {
			Log.d("areSameTime()", "different day");
			return false;
		}

	}

	public static DateTime currentTime() {
		return new DateTime();
	}

}

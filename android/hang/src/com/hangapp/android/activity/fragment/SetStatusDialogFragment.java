package com.hangapp.android.activity.fragment;

import org.joda.time.DateTime;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hangapp.android.R;
import com.hangapp.android.activity.HomeActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.Availability.Status;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.Fonts;

/**
 * The dialog that shows up when you click on your own status in
 * {@link YouFragment}.
 */
public class SetStatusDialogFragment extends DialogFragment {

	private RadioGroup radioGroupFreeBusy;
	private TextView textViewAvailabilityDuration;
	private SeekBar seekBarAvailabilityDuration;
	private Button buttonSetAvailability;
	private EditText editTextStatus;

	private Database database;
	private RestClient restClient;

	public SetStatusDialogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, 0); // remove title from dialogfragment
		// Instantiate dependencies
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getActivity()
				.getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_set_status, container);

		// Reference views.
		radioGroupFreeBusy = (RadioGroup) view
				.findViewById(R.id.radioGroupFreeBusy);
		// radioButtonFree = (RadioButton)
		// view.findViewById(R.id.radioButtonFree);
		// radioButtonBusy = (RadioButton)
		// view.findViewById(R.id.radioButtonBusy);
		textViewAvailabilityDuration = (TextView) view
				.findViewById(R.id.textViewAvailabilityDuration);
		seekBarAvailabilityDuration = (SeekBar) view
				.findViewById(R.id.seekBarAvailabilityDuration);
		buttonSetAvailability = (Button) view
				.findViewById(R.id.buttonSetAvailability);
		editTextStatus = (EditText) view.findViewById(R.id.availabilityText);

		// Set fonts.
		Typeface tf = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
		textViewAvailabilityDuration.setTypeface(tf);
		buttonSetAvailability.setTypeface(tf);

		// Set OnClickListeners.
		buttonSetAvailability.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setStatus();
				HomeActivity.closeSoftKeyboard(getActivity(), editTextStatus);
			}
		});

		// Initialize the SeekBar.
		seekBarAvailabilityDuration
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// Do nothing.
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// Do nothing.
					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						DateTime expirationDate = getExpirationDate();

						textViewAvailabilityDuration.setText("until: "
								+ expirationDate.toString("h:mm aa"));
					}
				});

		return view;
	}

	protected void setStatus() {
		Availability.Status status = null;

		switch (radioGroupFreeBusy.getCheckedRadioButtonId()) {

		case R.id.radioButtonFree:
			status = Status.FREE;
			Log.i("setStatus", "picked green");
			Log.i("status", "status = " + status.toString());
			break;
		case R.id.radioButtonBusy:
			status = Status.BUSY;
			Log.i("setStatus", "picked red");

			break;
		default:
			Toast.makeText(getActivity(), "Choose if you're Free or Busy",
					Toast.LENGTH_LONG).show();
			Log.i("setStatus", "Toast displayed.");
			return;
		}

		Log.i("status", "status = " + status.toString());

		DateTime expirationDate = getExpirationDate();

		Availability newAvailability = new Availability(status, expirationDate, editTextStatus.getText().toString());
		database.setMyAvailability(newAvailability);
		Log.i("*******", "avail status = " + newAvailability.getStatus());
		Log.i("(((((((", "avail status tostring" + newAvailability.getStatus().toString());
		restClient.updateMyAvailability(newAvailability);


		dismiss();
	}

	private DateTime getExpirationDate() {
		DateTime rightNow = new DateTime();
		DateTime expirationDate = new DateTime(rightNow.getYear(),
				rightNow.getMonthOfYear(), rightNow.getDayOfMonth(),
				rightNow.getHourOfDay(), 0);
		expirationDate = expirationDate.plusHours(seekBarAvailabilityDuration
				.getProgress() + 1);

		return expirationDate;
	}
}

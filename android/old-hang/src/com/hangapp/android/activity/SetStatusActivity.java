package com.hangapp.android.activity;

import java.util.Calendar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hangapp.android.R;
import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.Status;
import com.hangapp.android.model.Status.Color;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.HangLog;

//@ContentView(R.layout.activity_set_status)
public final class SetStatusActivity extends BaseActivity {

	// @InjectView(R.id.radioGroupFreeBusy)
	private RadioGroup radioGroup;
	// @InjectView(R.id.timePickerStatusDuration)
	private TimePicker timePickerStatusDuration;
	// @InjectView(R.id.buttonSetStatus)
	private Button buttonSetStatus;
	// @InjectView(R.id.textViewSetStatusPrompt)
	private TextView textViewSetStatusPrompt;

	// @InjectResource(R.string.how_long_will_you_be_free)
	// private String stringHowLongWillYouBeFree;
	// @InjectResource(R.string.when_will_you_be_free)
	// private String stringWhenWillYouBeFree;

	// @Inject
	private DefaultUser defaultUser;
	// @Inject
	private RestClient restClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_status);

		setTitle("Set my Status");

		// Reference GUI views.
		radioGroup = (RadioGroup) findViewById(R.id.radioGroupFreeBusy);
		timePickerStatusDuration = (TimePicker) findViewById(R.id.timePickerStatusDuration);
		buttonSetStatus = (Button) findViewById(R.id.buttonSetStatus);
		textViewSetStatusPrompt = (TextView) findViewById(R.id.textViewSetStatusPrompt);

		// Instantiate dependencies.
		defaultUser = DefaultUser.getInstance();
		restClient = new RestClientImpl(getApplicationContext());

		// Turn on the "Up" button
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set the TimePicker to have the default expirationDate.
		Calendar defaultExpirationDate = Calendar.getInstance();
		defaultExpirationDate.add(Calendar.HOUR_OF_DAY,
				Status.DEFAULT_STATUS_DURATION);
		timePickerStatusDuration.setCurrentHour(defaultExpirationDate
				.get(Calendar.HOUR_OF_DAY));

		// Can't inject the RadioButtons since they're inside the RadioGroup.
		RadioButton radioFree = (RadioButton) radioGroup
				.findViewById(R.id.radioButtonFree);
		RadioButton radioBusy = (RadioButton) radioGroup
				.findViewById(R.id.radioButtonBusy);

		// Handle
		radioFree.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					textViewSetStatusPrompt
							.setText(getString(R.string.how_long_will_you_be_free));
					buttonSetStatus
							.setBackgroundResource(R.drawable.button_green);
				}
			}
		});

		radioBusy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					textViewSetStatusPrompt
							.setText(getString(R.string.when_will_you_be_free));
					buttonSetStatus
							.setBackgroundResource(R.drawable.button_red);
				}
			}
		});
	}

	public void setStatus(View v) {

		Calendar expirationDate = Calendar.getInstance();
		expirationDate.set(Calendar.HOUR_OF_DAY,
				timePickerStatusDuration.getCurrentHour());
		expirationDate.set(Calendar.MINUTE,
				timePickerStatusDuration.getCurrentMinute());

		// If the time they set has already passed, then increment it to the
		// next day.
		if (expirationDate.before(Calendar.getInstance())) {
			expirationDate.add(Calendar.DATE, 1);
		}

		// // Set the Date
		// Date expirationDate = new Date();
		// expirationDate.setHours(timePickerStatusDuration.getCurrentHour());
		// expirationDate.setMinutes(timePickerStatusDuration.getCurrentMinute());

		// Set the Color
		Status.Color color = null;
		switch (radioGroup.getCheckedRadioButtonId()) {
		case R.id.radioButtonFree:
			color = Color.GREEN;
			break;
		case R.id.radioButtonBusy:
			color = Color.RED;
			break;
		default:
			HangLog.toastE(getApplicationContext(),
					"SetStatusActivity.setStatus",
					"Unknown radio button checked");
			return;
		}

		// Construct the status
		Status status = new Status(color, expirationDate.getTime());

		// Set it as the DefaultUser's status
		defaultUser.setStatus(status);

		// Upload it to the server
		restClient.updateMyStatus(status);

		// ArrayList<Integer> recipientJIDs = new ArrayList<Integer>();
		// for (User user : defaultUser.getOutgoingBroadcastsList())
		// recipientJIDs.add(user.getJid());
		// ParsePushSender.sendTickle(recipientJIDs);

		// Quit this activity.
		finish();
	}
}

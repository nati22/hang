package com.hangapp.android.activity;

import java.util.Calendar;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.hangapp.android.R;
import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.HangLog;

public final class CreateProposalActivity extends BaseActivity {

	// @InjectView(R.id.editTextProposalDescription)
	private EditText editTextDesc;
	// @InjectView(R.id.imageViewDescriptionInvalid)
	private ImageView imageViewDescriptionInvalid;
	// @InjectView(R.id.editTextProposalLocation)
	private EditText editTextLoc;
	// @InjectView(R.id.imageViewLocationInvalid)
	private ImageView imageViewLocationInvalid;
	// @InjectView(R.id.timePickerAt)
	private TimePicker timePicker;
	// @InjectView(R.id.textViewTodayAt)
	private TextView textViewTodayAt;
	// @InjectView(R.id.buttonCreateProposal)
	private Button buttonCreateProposal;

	// @InjectResource(R.string.char_limit)
	// private String stringCharLimit;
	// @InjectResource(R.string.today_at)
	// private String stringTodayAt;
	// @InjectResource(R.string.tomorrow_at)
	// private String stringTomorrowAt;

	// @Inject
	private DefaultUser defaultUser;
	// @Inject
	private RestClient restClient;
	// @Inject
	// private XMPP xmpp;
	// @Inject
	// private SharedPreferences prefs;

	// private MultiUserChat muc;
	private Handler mHandler = new Handler();

	/**
	 * The number of milliseconds to wait before conducting a sanity check on
	 * the fields.
	 */
	private static final int SANITY_CHECK_DELAY = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_proposal);

		setTitle("Create New Proposal");

		// Set GUI references.
		editTextDesc = (EditText) findViewById(R.id.editTextProposalDescription);
		imageViewDescriptionInvalid = (ImageView) findViewById(R.id.imageViewDescriptionInvalid);
		editTextLoc = (EditText) findViewById(R.id.editTextProposalLocation);
		imageViewLocationInvalid = (ImageView) findViewById(R.id.imageViewLocationInvalid);
		timePicker = (TimePicker) findViewById(R.id.timePickerAt);
		textViewTodayAt = (TextView) findViewById(R.id.textViewTodayAt);
		buttonCreateProposal = (Button) findViewById(R.id.buttonCreateProposal);

		// Instantiate dependencies.
		defaultUser = DefaultUser.getInstance();
		restClient = new RestClientImpl(getApplicationContext());
		// prefs = PreferenceManager
		// .getDefaultSharedPreferences(getApplicationContext());

		// Turn on the "Up" button
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Fill Strings for char limit hints
		String proposalDescriptionCharLimit = String.format(
				getString(R.string.char_limit), Proposal.DESCRIPTION_MAX_CHARS);
		String proposalLocationCharLimit = String.format(
				getString(R.string.char_limit), Proposal.LOCATION_MAX_CHARS);

		// Inject the hints into the EditTexts
		editTextDesc.setHint(editTextDesc.getHint() + " "
				+ proposalDescriptionCharLimit);
		editTextLoc.setHint(editTextLoc.getHint() + " "
				+ proposalLocationCharLimit);

		// Sanity checks for Proposal Description
		editTextDesc.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mHandler.removeCallbacks(mSanityCheckTask);
				mHandler.postDelayed(mSanityCheckTask, SANITY_CHECK_DELAY);
			}
		});

		// Sanity checks for Proposal Location
		editTextLoc.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mHandler.removeCallbacks(mSanityCheckTask);
				mHandler.postDelayed(mSanityCheckTask, SANITY_CHECK_DELAY);
			}
		});

		// Sanity checks for Proposal Time
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// Set the Proposal Time text to say "Today" or "Tomorrow" based
				// on whether not the time they picked already passed.
				Calendar proposalTime = getTimePickerTime();

				if (proposalTime.before(Calendar.getInstance())) {
					textViewTodayAt.setText(getString(R.string.tomorrow_at));
				} else {
					textViewTodayAt.setText(getString(R.string.today_at));
				}
			}
		});
	}

	Runnable mSanityCheckTask = new Runnable() {
		@Override
		public void run() {

			// Red exclamation mark check for Proposal Description
			if (Proposal.descriptionIsValid(editTextDesc.getText().toString())) {
				imageViewDescriptionInvalid.setVisibility(View.INVISIBLE);
			} else {
				imageViewDescriptionInvalid.setVisibility(View.VISIBLE);
			}

			// Red exclamation mark check for Proposal Location
			if (Proposal.locationIsValid(editTextLoc.getText().toString())) {
				imageViewLocationInvalid.setVisibility(View.INVISIBLE);
			} else {
				imageViewLocationInvalid.setVisibility(View.VISIBLE);
			}

			// If both the Description and Location are valid, then enable the
			// CreateProposal button.
			buttonCreateProposal.setEnabled(Proposal
					.descriptionIsValid(editTextDesc.getText().toString())
					&& Proposal.locationIsValid(editTextLoc.getText()
							.toString()));
		}
	};

	public void broadcastProposal(View v) {
		// Pull out the edit texts.
		String description = editTextDesc.getText().toString();
		String location = editTextLoc.getText().toString();
		Calendar proposalTime = getTimePickerTime();

		// Perform sanity checks on the GUI thread.
		if (!Proposal.descriptionIsValid(description)) {
			HangLog.toastE(getApplicationContext(),
					"CreateProposalActivity.broadcastProposal",
					"Invalid proposal description");
			return;
		} else if (!Proposal.locationIsValid(location)) {
			HangLog.toastE(this, "CreateProposalActivity.broadcastProposal",
					"Invalid proposal location");
			return;
		}

		// TODO: Move this business logic into Proposal.
		if (proposalTime.before(Calendar.getInstance())) {
			proposalTime.add(Calendar.DATE, 1);
		}

		// Construct the proposal
		Proposal prop = new Proposal(description, location,
				proposalTime.getTime(), null, null);

		// Save it as the DefaultUser's proposal
		defaultUser.setProposal(prop);

		// Upload to server
		restClient.updateMyProposal(prop);

		// // Send Proposal to recipients (via Parse)'
		// Integer myJID = defaultUser.getUserCopy(getApplicationContext())
		// .getJid();
		// ArrayList<Integer> recipientJIDs = new ArrayList<Integer>();
		// for (User user : defaultUser.getOutgoingBroadcastsList()) {
		// recipientJIDs.add(user.getJid());
		// }
		// parsePushSender.sendProposal(myJID, recipientJIDs);

		// muc = new MultiUserChat(XMPP.getXMPPConnection(),
		// Utils.getDefaultUserJID(getApplicationContext())
		// + "@conference." + XMPP.JABBER_SERVER_URL);
		// try {
		// muc.destroy("Creating new muc...", "Creating new muc");
		// } catch (XMPPException e) {
		// Log.d("Couldn't destroy MUC: " + e.getMessage());
		// } finally {
		// try {
		// String defaultUserName = prefs.getString(Keys.FIRST_NAME, null);
		// muc.join(defaultUserName);
		// } catch (XMPPException e) {
		// Log.e("Couldn't join MUC: " + e.getMessage());
		// }
		// }

		// Close this window
		finish();

	}

	private Calendar getTimePickerTime() {
		// Set the Calendar
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
		calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

		return calendar;
	}
}

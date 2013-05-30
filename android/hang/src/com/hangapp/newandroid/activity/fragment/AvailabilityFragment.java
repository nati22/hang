package com.hangapp.newandroid.activity.fragment;

import org.joda.time.DateTime;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.NewAvailability;
import com.hangapp.newandroid.model.NewAvailability.Status;
import com.hangapp.newandroid.util.AvailabilityButton;
import com.hangapp.newandroid.util.HangLog;

public class AvailabilityFragment extends SherlockFragment {

	private AvailabilityButton[] buttonsAvailability;

	private Database database;

	private NewAvailability myAvailability;

	private Drawable greyDrawable;
	private Drawable greenDrawable;
	private Drawable redDrawable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		database = Database.getInstance();
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

		AvailabilityOnClickListener availabilityOnClickListener = new AvailabilityOnClickListener();
		for (Button button : buttonsAvailability) {
			button.setOnClickListener(availabilityOnClickListener);
		}

		// Grab ConstantState objects for each of the possible color buttons
		// (used for comparison later).
		greyDrawable = getResources().getDrawable(R.drawable.button_grey);
		greenDrawable = getResources().getDrawable(R.drawable.button_green);
		redDrawable = getResources().getDrawable(R.drawable.button_red);

		myAvailability = new NewAvailability();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		HangLog.toastD(getActivity(), "AvailabilityFragment.onResume",
				"My Jid: " + database.getMyJid());

		populateAvailabilityButtons();
	}

	class AvailabilityOnClickListener implements OnClickListener {
		@Override
		public void onClick(View button) {
			AvailabilityButton availabilityButton = (AvailabilityButton) button;

			// None -> Free.
			if (availabilityButton.getState() == null) {
				availabilityButton.setState(Status.FREE);
				return;
			}

			switch (availabilityButton.getState()) {
			// Free -> Busy.
			case FREE:
				availabilityButton.setState(Status.BUSY);
				return;
				// Busy -> Free.
			case BUSY:
				availabilityButton.setState(null);
				return;
			default:
				Log.e("AvailabilityOnClickListener", "Unknown state: "
						+ availabilityButton.getState().toString());
			}
		}
	}

	private void populateAvailabilityButtons() {
		// Populate the Views based on the current time.
		DateTime currentTime = new DateTime();

		Toast.makeText(getActivity(), currentTime.toString(), Toast.LENGTH_LONG)
				.show();

		for (Button buttonAvailability : buttonsAvailability) {
			buttonAvailability.setText(currentTime.toString("h aa"));
			currentTime = currentTime.plusHours(1);
		}
	}

}

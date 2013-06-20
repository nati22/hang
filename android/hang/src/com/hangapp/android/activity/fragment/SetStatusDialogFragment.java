package com.hangapp.android.activity.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.Fonts;

public class SetStatusDialogFragment extends DialogFragment {

	private ImageButton imageButtonFree;
	private ImageButton imageButtonBusy;
	private TextView textViewAvailabilityDuration;
	private SeekBar seekBarAvailabilityDuration;
	private Button buttonSetAvailability;

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
		imageButtonFree = (ImageButton) view.findViewById(R.id.imageButtonFree);
		imageButtonFree = (ImageButton) view.findViewById(R.id.imageButtonBusy);
		textViewAvailabilityDuration = (TextView) view
				.findViewById(R.id.textViewAvailabilityDuration);
		seekBarAvailabilityDuration = (SeekBar) view
				.findViewById(R.id.seekBarAvailabilityDuration);
		buttonSetAvailability = (Button) view
				.findViewById(R.id.buttonSetAvailability);

		// Set fonts.
		Typeface tf = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
		textViewAvailabilityDuration.setTypeface(tf);
		buttonSetAvailability.setTypeface(tf);

		buttonSetAvailability.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setStatus();
			}
		});

		return view;
	}

	private void setStatus() {
		Toast.makeText(getActivity(), "Not yet implemented.",
				Toast.LENGTH_SHORT).show();

		dismiss();
	}
}

package com.hangapp.android.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hangapp.android.R;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.Availability.Status;
import com.hangapp.android.model.User;

public class StatusIcon extends ImageButton /* implements OnClickListener */{

	public StatusIcon(Context context) {
		super(context);
	}

	public StatusIcon(Context context, AttributeSet set) {
		super(context, set);
	}

	private User user;
	private TextView textViewTimeRemaining;
	private boolean initialized = false;

	public void initialize(Context context, User user, View parentView) {
		this.user = user;

		this.setBackgroundResource(R.drawable.imagebutton_status_grey);
		// this.setBackgroundDrawable(context.getResources().getDrawable(
		// R.drawable.imagebutton_status_grey));

		// Reference TextView
		textViewTimeRemaining = (TextView) parentView
				.findViewById(R.id.textViewAvailabilityExpirationDate);

		// Create and assign font
		textViewTimeRemaining.setTypeface(Typeface.createFromAsset(
				context.getAssets(), Fonts.COOLVETICA));
		initialized = true;
	}

	/**
	 * This method handles the status icon color and the text displaying the
	 * remaining time. Sanity checks built in.
	 * 
	 * @param availability
	 */
	public void setAvailabilityColor(Availability availability) {
		// Do sanity checks
		if (!initialized) {
			Log.e("StatusIcon.update()", "The StatusIcon was not initialized!");
			return;
		}

		if (availability == null || availability.getStatus() == null
				|| availability.getExpirationDate() == null) {
			this.setImageDrawable(getResources().getDrawable(
					!isPressed ? R.drawable.status_grey
							: R.drawable.status_grey_pushed));
			textViewTimeRemaining.setText("0h");
//			Log.i("StatusIcon.setAvailabilityColor",
//					"either availability, its Status, its ExpDate or all are null");
			return;
		}

		// Check if status has expired
		if (!availability.isActive()) {
			this.setImageDrawable(getResources().getDrawable(
					R.drawable.status_grey));
			textViewTimeRemaining.setText("0h");
			Log.i("StatusIcon.setAvailabilityColor()", user.getFirstName()
					+ "'s Status is inactive");
			return;
		}

		// All seems good, so let's set the status
		if (availability.getStatus().equals(Status.BUSY)) {
			this.setImageDrawable(getResources().getDrawable(
					!isPressed ? R.drawable.status_red
							: R.drawable.status_red_pushed));
			String remainingTime = Utils
					.getAbbvRemainingTimeString(availability
							.getExpirationDate());
			textViewTimeRemaining.setText(remainingTime);
		} else if (availability.getStatus().equals(Status.FREE)) {
			this.setImageDrawable(getResources().getDrawable(
					!isPressed ? R.drawable.status_green
							: R.drawable.status_green_pushed));
			String remainingTime = Utils
					.getAbbvRemainingTimeString(availability
							.getExpirationDate());
			textViewTimeRemaining.setText(remainingTime);
		} else {
			Log.e("StatusIcon.setAvailabilityColor()",
					"Invalid value: method was passed in \""
							+ availability.toString() + "\"");
		}
	}

	private boolean isPressed = false;

	/**
	 * This allows the user to set the availability with an initially pressed
	 * state.
	 * 
	 * @param availability
	 * @param isPressed
	 */
	public void setAvailabilityColor(Availability availability,
			boolean isPressed) {
		this.isPressed = isPressed;
		setAvailabilityColor(availability);
	}

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
		setAvailabilityColor(user.getAvailability());

	}
}

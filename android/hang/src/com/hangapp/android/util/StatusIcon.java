package com.hangapp.android.util;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.User;
import com.hangapp.android.model.Availability.Status;
import com.hangapp.android.network.rest.RestClient;

public class StatusIcon extends ImageButton /*implements OnClickListener */{

	public StatusIcon(Context context) {
		super(context);
		this.context = context;
	}

	public StatusIcon(Context context, AttributeSet set) {
		super(context, set);
		this.context = context;
	}

	private Context context;
	private Database db;
	private RestClient restClient;
	private User user;
	private View parentView;
	private TextView textViewTimeRemaining;
	private boolean initialized = false;

	
/*	@Override
	public void onClick(View v) {
		if (initialized) {
			if (!user.getAvailability().isActive()) {
				restClient.sendNudge(user.getJid());
				Toast.makeText(context, "Sending a nudge to " + user.getFirstName(),
						Toast.LENGTH_SHORT).show();
			} else {
				// Determine hrs and min left
				DateTime expDateTime = user.getAvailability().getExpirationDate();
				DateTime currentDateTime = new DateTime();
				int min = Minutes.minutesBetween(currentDateTime, expDateTime)
						.getMinutes();
				int hrs = 0;
				while (min >= 60) {
					hrs++;
					min = min - 60;
				}

				// Display remaining time to user
				Toast.makeText(context,
						hrs + " hr" + ((hrs > 1) ? "s " : " ") + min + " min remaining",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context,
					"There was a problem processing your request",
					Toast.LENGTH_SHORT).show();
		}
	}*/
	
	private boolean isInitialized() {
		return initialized;
	}

	public void initialize(Context context, Database db, RestClient restClient,
			User user, View parentView) {
		this.context = context;
		this.restClient = restClient;
		this.db = db;
		this.user = user;
		this.parentView = parentView;

		this.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.imagebutton_status_grey));

		// Reference TextView
		textViewTimeRemaining = (TextView) parentView
				.findViewById(R.id.textViewAvailabilityExpirationDate);

		// Create and assign font
		textViewTimeRemaining.setTypeface(Typeface.createFromAsset(
				context.getAssets(), Fonts.COOLVETICA));
		initialized = true;
	}

	/*
	 * Here I'm passing in a fresh availability because that new one is
	 * guaranteed to be accurate with what we have locally. It may be the case
	 * that what we have locally is just as good but I really don't have the time
	 * to track everything and see if that's the case so this is a temporary
	 * spur-of-the-moment call.
	 */
	public void update(Availability availability) {
		if (!initialized) {
			Log.e("StatusIcon.update()", "The StatusIcon was not initialized!");
			return;
		}

		// Check if the user has an Availability..
		if (user.getAvailability() != null) {

			// Check if their status is active
			if (user.getAvailability().isActive()) {

				// Set availability button
				if (user.getAvailability().getStatus() == Status.FREE) {
					this.setImageDrawable(getResources().getDrawable(
							R.drawable.status_green));

					String remainingTime = Utils.getAbbvRemainingTimeString(user
							.getAvailability().getExpirationDate());
					textViewTimeRemaining.setText(remainingTime);
				} else if (user.getAvailability().getStatus() == Status.BUSY) {
					this.setImageDrawable(getResources().getDrawable(
							R.drawable.status_red));

					String remainingTime = Utils.getAbbvRemainingTimeString(user
							.getAvailability().getExpirationDate());
					textViewTimeRemaining.setText(remainingTime);
				} else {
					// Then Status is neither FREE or BUSY
					Log.e("FeedFragment.getView",
							"Unknown user availability status: "
									+ user.getAvailability().getStatus());
				}

			} else {
				// Then status has expired
				this.setImageDrawable(getResources().getDrawable(
						R.drawable.status_grey));
			}

		} else {
			// Then the user has a null availability
			this.setImageDrawable(getResources().getDrawable(
					R.drawable.status_grey));

			Log.e("FeedFragment", user.getFirstName()
					+ "'s getAvailability == null");
		}
	}

	public void setAvailabilityColor(Availability.Status color) {

	}

}

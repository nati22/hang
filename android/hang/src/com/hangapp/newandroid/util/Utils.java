package com.hangapp.newandroid.util;

import org.joda.time.DateTime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hangapp.newandroid.model.Availability;

public final class Utils {
	public static final String BASE_URL = "http://therealhangapp.appspot.com/rest";
	public static final String USERS_URL = BASE_URL + "/users";

	/**
	 * Returns true IFF this device is connected to the internet, either through
	 * WiFi, 3G or 4G.
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		return activeNetworkInfo != null;
	}

	public static void initializeAvailabilityButtons(
			AvailabilityButton[] availabilityButtons) {
		// Find the instant of time RIGHT NOW.
		DateTime rightNow = new DateTime();

		// Construct a new DateTime that is only as accurate as the current
		// hour.
		DateTime rightNowTruncated = new DateTime(rightNow.getYear(),
				rightNow.getMonthOfYear(), rightNow.getDayOfMonth(),
				rightNow.getHourOfDay(), 0);

		// Set the time of each button.
		for (AvailabilityButton buttonAvailability : availabilityButtons) {
			buttonAvailability.setTime(rightNowTruncated);
			rightNowTruncated = rightNowTruncated.plusHours(1);
		}
	}

	public static boolean updateAvailabilityStripColors(
			AvailabilityButton[] availabilityButtons,
			Availability availability, Context context) {
		if (availability == null) {
			for (AvailabilityButton button : availabilityButtons) {
				button.setState(null);
			}
			return true;
		}

		if (availability.getExpirationDate() == null) {
			Log.e("Utils.updateAvailabilityStripColors",
					"Couldn't udpateAvailabilityStripColors: Expiration date given was null");
			return false;
		}

		// Search for the AvailabilityButton that corresponds to the
		// Availability's expiration date.
		for (AvailabilityButton button : availabilityButtons) {
			if (button.getTime().isEqual(availability.getExpirationDate())) {
				Utils.updateAvailabilityStripColors(availabilityButtons,
						button.getId(), availability.getStatus());
				return true;
			}
		}

		// If this method gets here, then the for loop failed to find the
		// AvailabilityButton that we wanted. Show an error message.
		HangLog.toastE(context,
				"MyAvailabilityFragment.updateAvailabilityStripColors",
				"Couldn't find Availability button for expiration date: "
						+ availability.getExpirationDate());
		return false;
	}

	public static void updateAvailabilityStripColors(
			AvailabilityButton[] availabilityButtons, int middleButtonId,
			Availability.Status newState) {

		// This button and all buttons to the left of this button should be set
		// to the new state.
		for (int i = 0; i <= middleButtonId; i++) {
			availabilityButtons[i].setState(newState);
		}

		// Every button to the right of this button should be set to null state.
		for (int i = middleButtonId + 1; i < availabilityButtons.length; i++) {
			availabilityButtons[i].setState(null);
		}
	}

}

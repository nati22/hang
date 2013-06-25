package com.hangapp.android.model;

import org.joda.time.DateTime;

import android.util.Log;

public final class Availability implements Comparable<Availability> {

	/**
	 * How many hours a "Free" status should last.
	 */
	public static final Integer DEFAULT_STATUS_DURATION = 2;

	private Status status;
	private DateTime expirationDate;

	/**
	 * Convenience constructor that takes a String for the Availability Status
	 * instead of a proper Availability.Status enum
	 * 
	 * @param Context
	 *            context
	 * @param String
	 *            description
	 * @param String
	 *            colorString
	 * @param Date
	 *            expirationDate
	 */
	public Availability(String statusString, DateTime expirationDate) {
		this.status = parseStatus(statusString);
		this.expirationDate = expirationDate;
	}

	/**
	 * 
	 * @param Context
	 *            context
	 * @param String
	 *            description
	 * @param Status
	 *            status
	 * @param Date
	 *            expirationDate
	 */
	public Availability(Status color, DateTime expirationDate) {
		this.status = color;
		this.expirationDate = expirationDate;
	}

	public String getDescription() {
		// if (!isActive()) {
		// return "No Availability.";
		// }

		String description = "(availability description goes here)";

		// switch (status) {
		// case FREE:
		// description += "Free";
		//
		// int currentHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		//
		// Calendar expirationDateCalendar = Calendar.getInstance();
		// expirationDateCalendar.setTime(expirationDate);
		//
		// int expirationDateHours = expirationDateCalendar
		// .get(Calendar.HOUR_OF_DAY);
		// int difference = expirationDateHours - currentHours;
		//
		// if (difference == 1) {
		// description += " for " + difference + " hour.";
		// } else {
		// description += " for " + difference + " hours.";
		// }
		//
		// break;
		// case BUSY:
		// description += "Busy";
		// description += " until ";
		// description += DateFormat.format("h:mm aa", expirationDate);
		// break;
		// default:
		// Log.e("Availability.getDescription", "Unknown status status: "
		// + status.toString());
		// }

		return description;
	}

	public Status getStatus() {
		if (status == null || !isActive()) {
			if (!isActive()) {
				Log.d("Availability", "Availability.isActive() =" + isActive());
			}
			return null;
		}
		return status;
	}

	public DateTime getExpirationDate() {
		return expirationDate;
	}

	public void setStatus(Status color) {
		this.status = color;
	}

	/**
	 * Convenience setter for {@link Availability.Status}s.
	 */

	public void setColor(String colorString) {
		this.status = parseStatus(colorString);
	}

	public void setExpirationDate(DateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * Helper function that parses a {@link Availability.Status} from a String.
	 * 
	 * @param colorString
	 * @return
	 */
	public static Status parseStatus(String colorString) {
		// Convert the StatusColor string back to its enum
		Status parsedColor = null;
		for (Status color : Status.values()) {
			if (color.toString().equals(colorString)) {
				parsedColor = color;
			}
		}

		return parsedColor;
	}

	public String toString() {
		return getDescription();
	}

	/**
	 * @return True if this status is active.
	 */
	public boolean isActive() {
		return expirationDate != null /* && expirationDate.isAfter(new DateTime()) */;
	}

	/**
	 * Inner class enum describing each of the availabilities, as opposed to
	 * having a separate file for this.
	 */
	public enum Status {
		FREE(0), BUSY(1);

		private Status(int value) {
			this.value = value;
		}

		int value;
	}

	@Override
	public int compareTo(Availability another) {
		if (this.status == null && another.status != null) {
			return 1;
		} else if (this.status != null && another.status == null) {
			return -1;
		} else if (this.status == null && another.status == null) {
			return 0;
		} else {
			if (this.status.value < another.status.value) {
				return -1;
			} else if (this.status.value > another.status.value) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}

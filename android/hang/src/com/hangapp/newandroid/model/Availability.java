package com.hangapp.newandroid.model;

import java.util.Calendar;
import java.util.Date;

import android.text.format.DateFormat;
import android.util.Log;

public final class Availability implements Comparable<Availability> {

	/**
	 * How many hours a "Free" status should last.
	 */
	public static final Integer DEFAULT_STATUS_DURATION = 2;

	private Color color;
	private Date expirationDate;

	/**
	 * Convenience constructor that takes a String for the Availability Color
	 * instead of a proper Availability.Color enum
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
	public Availability(String colorString, Date expirationDate) {
		this.color = parseColor(colorString);
		this.expirationDate = expirationDate;
	}

	/**
	 * 
	 * @param Context
	 *            context
	 * @param String
	 *            description
	 * @param Color
	 *            color
	 * @param Date
	 *            expirationDate
	 */
	public Availability(Color color, Date expirationDate) {
		this.color = color;
		this.expirationDate = expirationDate;
	}

	public String getDescription() {
		if (!isActive()) {
			return "No Availability.";
		}

		String description = "";

		switch (color) {
		case GREEN:
			description += "Free";

			int currentHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

			Calendar expirationDateCalendar = Calendar.getInstance();
			expirationDateCalendar.setTime(expirationDate);

			int expirationDateHours = expirationDateCalendar
					.get(Calendar.HOUR_OF_DAY);
			int difference = expirationDateHours - currentHours;

			if (difference == 1) {
				description += " for " + difference + " hour.";
			} else {
				description += " for " + difference + " hours.";
			}

			break;
		case RED:
			description += "Busy";
			description += " until ";
			description += DateFormat.format("h:mm aa", expirationDate);
			break;
		default:
			Log.e("Availability.getDescription", "Unknown status color: "
					+ color.toString());
		}

		return description;
	}

	public Color getColor() {
		if (color == null || !isActive()) {
			return null;
		}

		return color;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Convenience setter for {@link Availability.Color}s.
	 */
	public void setColor(String colorString) {
		this.color = parseColor(colorString);
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * Helper function that parses a {@link Availability.Color} from a String.
	 * 
	 * @param colorString
	 * @return
	 */
	public static Color parseColor(String colorString) {
		// Convert the StatusColor string back to its enum
		Color parsedColor = null;
		for (Color color : Color.values()) {
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
		return expirationDate != null && expirationDate.after(new Date());
	}

	/**
	 * Inner class enum describing each of the colors, as opposed to having a
	 * separate file for this.
	 */
	public enum Color {
		GREEN(0), RED(1);

		private Color(int value) {
			this.value = value;
		}

		int value;
	}

	@Override
	public int compareTo(Availability another) {
		if (this.color == null && another.color != null) {
			return -1;
		} else if (this.color != null && another.color == null) {
			return 1;
		} else {
			if (this.color.value < another.color.value) {
				return -1;
			} else if (this.color.value > another.color.value) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}

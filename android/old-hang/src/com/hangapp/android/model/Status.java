package com.hangapp.android.model;

import java.util.Date;

import android.text.format.DateFormat;
import android.util.Log;

import com.hangapp.android.R;

public final class Status {

	/**
	 * How many hours a "Free" status should last.
	 */
	public static final Integer DEFAULT_STATUS_DURATION = 2;

	private Color color;
	private Date expirationDate;

	/**
	 * Convenience constructor that takes a String for the Status Color instead
	 * of a proper Status.Color enum
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
	public Status(String colorString, Date expirationDate) {
		super();
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
	public Status(Color color, Date expirationDate) {
		super();
		this.color = color;
		this.expirationDate = expirationDate;
	}

	public String getDescription() {
		if (!isActive()) {
			return "No Status.";
		}

		String description = "";

		switch (color) {
		case GREEN:
			description += "Free";
			break;
		case RED:
			description += "Busy";
			break;
		default:
			Log.e("Status.getDescription",
					"Unknown status color: " + color.toString());
		}

		description += " until ";
		description += DateFormat.format("h:mm aa", expirationDate);

		return description;
	}

	public Color getColor() {
		if (color == null || !isActive()) {
			return Color.GREY;
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
	 * Convenience setter for {@link Status.Color}s.
	 */
	public void setColor(String colorString) {
		this.color = parseColor(colorString);
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * Helper function that parses a {@link Status.Color} from a String.
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
	private boolean isActive() {
		if (expirationDate != null) {
			return expirationDate.after(new Date());
		} else {
			return false;
		}
	}

	/**
	 * Inner class enum describing each of the colors, as opposed to having a
	 * separate file for this.
	 */
	public enum Color {
		GREEN(R.drawable.button_green), RED(R.drawable.button_red), GREY(
				R.drawable.button_black);

		private Color(int icon) {
			this.icon = icon;
		}

		public int getIcon() {
			return icon;
		}

		int icon;
	}

}

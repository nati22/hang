package com.hangapp.newandroid.model;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

public final class Availability {

	private Map<DateTime, Status> statuses = new HashMap<DateTime, Availability.Status>();

	public void putStatus(DateTime dateTime, Status status) {
		statuses.put(dateTime, status);
	}

	public Status getStatus(DateTime dateTime) {
		return statuses.get(dateTime);
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

}

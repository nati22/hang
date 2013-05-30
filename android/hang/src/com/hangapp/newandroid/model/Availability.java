package com.hangapp.newandroid.model;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.hangapp.newandroid.util.Keys;

public final class Availability {

	/**
	 * Inner enum describing each of Availability block.
	 */
	public enum Status {
		FREE(0), BUSY(1);

		private Status(int value) {
			this.value = value;
		}

		int value;
	}

	private Map<DateTime, Status> statuses = new HashMap<DateTime, Availability.Status>();

	public void putStatus(DateTime dateTime, Status status) {
		statuses.put(dateTime, status);
	}

	public Status getStatus(DateTime dateTime) {
		return statuses.get(dateTime);
	}

	public void removeStatus(DateTime dateTime) {
		statuses.remove(dateTime);
	}

	public String toJson() {
		// Before serializing this Availability, clear out any expired Statuses.
		clearExpiredStatuses();

		// Get ready to serialize.
		JSONObject statusesJsonObject = new JSONObject();
		JSONArray freeJsonArray = new JSONArray();
		JSONArray busyJsonObject = new JSONArray();

		// Attempt to serialize.
		try {
			for (DateTime time : statuses.keySet()) {
				Status status = statuses.get(time);

				switch (status) {
				case FREE:
					freeJsonArray.put(time.toString());
					break;
				case BUSY:
					busyJsonObject.put(time.toString());
					break;
				default:
					throw new Exception("Unknown status: " + status.toString());
				}
			}

			statusesJsonObject.put(Keys.FREE, freeJsonArray);
			statusesJsonObject.put(Keys.BUSY, busyJsonObject);

			return statusesJsonObject.toString();
		} catch (Exception e) {
			Log.e("Availability.toJson()", e.getMessage());
			return null;
		}
	}

	private void clearExpiredStatuses() {
		for (DateTime time : statuses.keySet()) {
			if (time.isBefore(new DateTime())) {
				statuses.remove(time);
			}
		}
	}

}

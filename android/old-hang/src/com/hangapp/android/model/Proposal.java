package com.hangapp.android.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class Proposal {
	private String description;
	private String location;
	private Date startTime;
	private List<User> interested;
	private List<User> confirmed;
	// private boolean expired;

	public static final int DESCRIPTION_MAX_CHARS = 100;
	public static final int LOCATION_MAX_CHARS = 50;

	/**
	 * A proposal's default duration, in hours.
	 */
	public static final Integer PROPOSAL_DURATION = 2;

	public Proposal(String description, String location, Date time,
			List<User> interested, List<User> confirmed) {
		this.description = description;
		this.location = location;
		this.startTime = time;
		this.interested = interested != null ? interested
				: new ArrayList<User>();
		this.confirmed = confirmed != null ? confirmed : new ArrayList<User>();
	}

	public Proposal(String description, String location, Date time) {
		this.description = description;
		this.location = location;
		this.startTime = time;
		this.interested = new ArrayList<User>();
		this.confirmed = new ArrayList<User>();
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public Date getStartTime() {
		return startTime;
	}

	public List<User> getInterested() {
		return interested;
	}

	public List<User> getConfirmed() {
		return confirmed;
	}

	// public void setExpired(boolean isExpired) {
	// this.expired = isExpired;
	// }

	public boolean isActive() {
		if (startTime == null) {
			return false;
		}
		Date expirationDate = (Date) startTime.clone();
		expirationDate.setHours(expirationDate.getHours() + PROPOSAL_DURATION);

		return new Date().before(expirationDate);
	}

	public static boolean descriptionIsValid(String proposalDescription) {
		return proposalDescription != null
				&& !proposalDescription.trim().equals("")
				&& proposalDescription.length() <= Proposal.DESCRIPTION_MAX_CHARS;
	}

	public static boolean locationIsValid(String proposalLocation) {
		if (proposalLocation != null) {
			return proposalLocation.length() <= Proposal.LOCATION_MAX_CHARS;
		}

		return true;
	}

	public static boolean timeIsValid(Calendar proposalTime) {
		return !proposalTime.before(Calendar.getInstance());
	}

}

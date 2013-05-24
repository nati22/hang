package com.hangapp.newandroid.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class Proposal {
	private String description;
	private String location;
	private Date startTime;
	private List<User> interestedUsers;
	private List<User> confirmedUsers;

	public static final int DESCRIPTION_MAX_CHARS = 100;
	public static final int LOCATION_MAX_CHARS = 50;

	/**
	 * A proposal's default duration, in hours.
	 */
	public static final Integer PROPOSAL_DURATION = 2;

	public Proposal(String description, String location, Date time) {
		this.description = description;
		this.location = location;
		this.startTime = time;
		
		// We need at least an empty list to check for Users
		this.interestedUsers = new ArrayList<User>();
		this.confirmedUsers = new ArrayList<User>();
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
		return interestedUsers;
	}

	public void setInterested(List<User> interestedUsers) {
		this.interestedUsers = interestedUsers;
	}

	public List<User> getConfirmed() {
		return confirmedUsers;
	}

	public void setConfirmed(List<User> confirmedUsers) {
		this.confirmedUsers = confirmedUsers;
	}

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

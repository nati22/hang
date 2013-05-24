package com.hangapp.android.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hangapp.android.util.Keys;

public final class User implements Comparable<User> {
	/**
	 * A {@link User}'s JID is his phone number.
	 */
	private String jid;
	private String firstName;
	private String lastName;
	private Status status;
	private Proposal proposal;
	private List<User> incomingBroadcasts;
	private List<User> outgoingBroadcasts;

	public User(String jid, String firstName, String lastName, Status status,
			Proposal proposal) {
		super();
		this.jid = jid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.status = status;
		this.proposal = proposal;
	}

	public String getJid() {
		return jid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public Status getStatus() {
		return status;
	}

	public Proposal getProposal() {
		return proposal;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
	}

	public List<User> getIncomingBroadcasts() {
		return incomingBroadcasts;
	}

	public void setIncomingBroadcasts(List<User> incomingBroadcasts) {
		this.incomingBroadcasts = incomingBroadcasts;
	}

	public List<User> getOutgoingBroadcasts() {
		return outgoingBroadcasts;
	}

	public void setOutgoingBroadcasts(List<User> outgoingBroadcasts) {
		this.outgoingBroadcasts = outgoingBroadcasts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jid == null) ? 0 : jid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (jid == null) {
			if (other.jid != null)
				return false;
		} else if (!jid.equals(other.jid))
			return false;
		return true;
	}

	@Override
	public int compareTo(User another) {
		if (!this.firstName.equals(another.firstName)) {
			return this.firstName.compareTo(another.firstName);
		} else {
			return this.lastName.compareTo(another.lastName);
		}
	}

	// public static List<User> parseParseListOfUsers(Context context,
	// List<ParseObject> list) {
	// if (list == null) {
	// final String message = "No incoming broadcasts.";
	// Log.d(message);
	// Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	// return new ArrayList<User>();
	// }
	//
	// // Log.d( "list length = " + list.size());
	// List<User> parsedUsers = new ArrayList<User>();
	// for (ParseObject userObject : list) {
	//
	// // Parse the user
	// User user = parseParseUser(context, userObject);
	//
	// // Add User to parsed list
	// if (!parsedUsers.contains(user))
	// parsedUsers.add(user);
	// }
	//
	// return parsedUsers;
	// }

	// public static User parseParseUser(Context context, ParseObject
	// userObject) {
	// Integer jid = userObject.getInt(Keys.JID);
	// String firstName = userObject.getString(Keys.FIRST_NAME);
	// String lastName = userObject.getString(Keys.LAST_NAME);
	// String statusText = userObject.getString(Keys.STATUS_TEXT);
	// String statusColorString = userObject.getString(Keys.STATUS_COLOR);
	// Date statusExpDate = userObject.getDate(Keys.STATUS_EXPIRATION_DATE);
	// String propDesc = userObject.getString(Keys.PROPOSAL_DESCRIPTION);
	// String propLoc = userObject.getString(Keys.PROPOSAL_LOCATION);
	// Date propTime = userObject.getDate(Keys.PROPOSAL_TIME);
	//
	// // Parse the list of Interested users
	// List<ParseObject> propInterestedObjects = userObject
	// .getList(Keys.PROPOSAL_INTERESTED);
	// List<User> propInterested = new ArrayList<User>();
	//
	// if (propInterestedObjects != null) {
	// for (ParseObject interestedUserObject : propInterestedObjects) {
	// Integer interestedJID = interestedUserObject.getInt(Keys.JID);
	// String interestedFirstName = interestedUserObject
	// .getString(Keys.FIRST_NAME);
	// String interestedLastName = interestedUserObject
	// .getString(Keys.LAST_NAME);
	//
	// User interestedUser = new User(interestedJID,
	// interestedFirstName, interestedLastName, null, null);
	//
	// if (!propInterested.contains(interestedUser)) {
	// propInterested.add(interestedUser);
	// }
	// }
	// }
	//
	// // Construct models
	// Status status = new Status(statusColorString, statusExpDate);
	// Proposal prop = new Proposal(propDesc, propLoc, propTime,
	// propInterested, null);
	// User user = new User(jid, firstName, lastName, status, prop);
	//
	// return user;
	// }

	public static User parseUser(String userJsonString) throws JSONException {
		User user = null;
		Status status = null;
		Proposal proposal = null;

		JSONObject userJsonObject = new JSONObject(userJsonString);

		String jid = userJsonObject.getString(Keys.JID);
		String firstName = userJsonObject.getString(Keys.FIRST_NAME);
		String lastName = userJsonObject.getString(Keys.LAST_NAME);

		String statusColor = userJsonObject.getString(Keys.STATUS_COLOR);
		String statusExpirationDateString = userJsonObject
				.getString(Keys.STATUS_EXPIRATION_DATE);

		Date date;
		if (statusExpirationDateString != null
				&& !statusExpirationDateString.equals("null")) {
			date = new Date(Date.parse(statusExpirationDateString));
		} else {
			date = null;
		}

		status = new Status(statusColor, date);

		String proposalDescription = userJsonObject
				.getString(Keys.PROPOSAL_DESCRIPTION);
		String proposalLocation = userJsonObject
				.getString(Keys.PROPOSAL_LOCATION);
		String proposalTime = userJsonObject.getString(Keys.PROPOSAL_TIME);
		List<User> interestedUsers = parseUsersJsonArray(userJsonObject
				.getJSONArray(Keys.PROPOSAL_INTERESTED));
		List<User> confirmedUsers = parseUsersJsonArray(userJsonObject
				.getJSONArray(Keys.PROPOSAL_CONFIRMED));

		// FIXME: Use the real Date.
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		if (!proposalDescription.equals("null")) {
			proposal = new Proposal(proposalDescription, proposalLocation,
					calendar.getTime(), interestedUsers, confirmedUsers);
		}

		List<User> incomingBroadcasts = parseUsersJsonArray(userJsonObject
				.getJSONArray(Keys.INCOMING));
		List<User> outgoingBroadcasts = parseUsersJsonArray(userJsonObject
				.getJSONArray(Keys.OUTGOING));

		user = new User(jid, firstName, lastName, status, proposal);

		user.setIncomingBroadcasts(incomingBroadcasts);
		user.setOutgoingBroadcasts(outgoingBroadcasts);

		return user;
	}

	private static List<User> parseUsersJsonArray(JSONArray usersJsonArray)
			throws JSONException {
		List<User> users = new ArrayList<User>();

		for (int i = 0; i < usersJsonArray.length(); i++) {
			JSONObject userJsonObject = usersJsonArray.getJSONObject(i);

			// 'color': self.status_color,
			// 'exp': self.status_expiration_date,
			// 'desc': self.proposal_description,
			// 'loc': self.proposal_location,
			// 'time': self.proposal_time

			String jid = userJsonObject.getString(Keys.JID);
			String firstName = userJsonObject.getString(Keys.FIRST_NAME);
			String lastName = userJsonObject.getString(Keys.LAST_NAME);

			String statusColor = userJsonObject.getString(Keys.STATUS_COLOR);
			String statusExpirationDateString = userJsonObject
					.getString(Keys.STATUS_EXPIRATION_DATE);

			// TODO: Use the real Date.
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, 1);
			Status status = new Status(statusColor, calendar.getTime());

			String proposalDescription = userJsonObject
					.getString(Keys.PROPOSAL_DESCRIPTION);
			String proposalLocation = userJsonObject
					.getString(Keys.PROPOSAL_LOCATION);
			String proposalTime = userJsonObject.getString(Keys.PROPOSAL_TIME);
			// TODO: Use the real Date.
			Proposal proposal = new Proposal(proposalDescription,
					proposalLocation, calendar.getTime());

			User user = new User(jid, firstName, lastName, status, proposal);
			users.add(user);
		}

		return users;
	}
}

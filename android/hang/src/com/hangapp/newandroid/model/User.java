package com.hangapp.newandroid.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.hangapp.newandroid.util.Keys;

public final class User implements Comparable<User>, Parcelable {
	/**
	 * A {@link User}'s JID is his Facebook ID.
	 */
	private String jid;
	private String firstName;
	private String lastName;
	private Availability availability;
	private Proposal proposal;
	private List<User> incomingBroadcasts;
	private List<User> outgoingBroadcasts;

	public User(String jid, String firstName, String lastName) {
		super();
		this.jid = jid;
		this.firstName = firstName;
		this.lastName = lastName;
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

	public Availability getAvailability() {
		return availability;
	}

	public Proposal getProposal() {
		return proposal;
	}

	public void setAvailability(Availability status) {
		this.availability = status;
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
	public String toString() {
		return "User {jid=" + jid + ", firstName=" + firstName + ", lastName="
				+ lastName + "}";
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
		if (this.availability == null && another.availability != null) {
			return 1;
		} else if (this.availability != null && another.availability == null) {
			return -1;
		} else if (this.availability == null && another.availability == null) {
			return 0;
		} else {
			return this.availability.compareTo(another.availability);
		}
	}

	public static User parseUser(String userJsonString) throws JSONException {
		User user = null;
		Availability status = null;
		Proposal proposal = null;

		JSONObject userJsonObject = new JSONObject(userJsonString);

		String jid = userJsonObject.getString(Keys.JID);
		String firstName = userJsonObject.getString(Keys.FIRST_NAME);
		String lastName = userJsonObject.getString(Keys.LAST_NAME);

		String statusColor = userJsonObject.getString(Keys.AVAILABILITY_COLOR);
		String statusExpirationDateString = userJsonObject
				.getString(Keys.AVAILABILITY_EXPIRATION_DATE);

		Date date;
		if (statusExpirationDateString != null
				&& !statusExpirationDateString.equals("null")) {
			date = new Date(Date.parse(statusExpirationDateString));
		} else {
			date = null;
		}

		status = new Availability(statusColor, date);

		String proposalDescription = userJsonObject
				.getString(Keys.PROPOSAL_DESCRIPTION);
		String proposalLocation = userJsonObject
				.getString(Keys.PROPOSAL_LOCATION);
		String proposalTime = userJsonObject.getString(Keys.PROPOSAL_TIME);

		// FIXME: Use the real Date.
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		if (!proposalDescription.equals("null")) {
			proposal = new Proposal(proposalDescription, proposalLocation,
					calendar.getTime());
		}

		user = new User(jid, firstName, lastName);
		user.setAvailability(status);
		user.setProposal(proposal);

		JSONArray incomingBroadcastsJsonArray = userJsonObject
				.getJSONArray(Keys.INCOMING);
		JSONArray outgoingBroadcastsJsonArray = userJsonObject
				.getJSONArray(Keys.OUTGOING);

		// Populate IncomingBroadcast strings
		List<String> incomingBroadcastsStrings = new ArrayList<String>();
		for (int i = 0; i < incomingBroadcastsJsonArray.length(); i++) {
			incomingBroadcastsStrings
					.add(incomingBroadcastsJsonArray.getString(i));
		}

		// Populate OutgoingBroadcast strings
		List<String> outgoingBroadcastsStrings = new ArrayList<String>();
		for (int i = 0; i < outgoingBroadcastsJsonArray.length(); i++) {
			outgoingBroadcastsStrings
					.add(outgoingBroadcastsJsonArray.getString(i));
		}

		// Parse the Library.
		Map<String, User> library = parseLibrary(userJsonObject
				.getJSONObject(Keys.LIBRARY));

		// Use the library to retrieve the Incoming and Outgoing Broadcasts.
		List<User> incomingBroadcasts = searchLibraryForJids(library,
				incomingBroadcastsStrings);
		List<User> outgoingBroadcasts = searchLibraryForJids(library,
				outgoingBroadcastsStrings);

		user.setIncomingBroadcasts(incomingBroadcasts);
		user.setOutgoingBroadcasts(outgoingBroadcasts);

		return user;
	}

	public static User parseUserName(String userJsonString) throws JSONException {
		User user = null;

		JSONObject userJsonObject = new JSONObject(userJsonString);

		String jid = userJsonObject.getString(Keys.JID);
		String firstName = userJsonObject.getString(Keys.FIRST_NAME);
		String lastName = userJsonObject.getString(Keys.LAST_NAME);

		user = new User(jid, firstName, lastName);

		return user;
	}

	private static Map<String, User> parseLibrary(JSONObject library)
			throws JSONException, ClassCastException {
		Map<String, User> users = new HashMap<String, User>();

		Iterator<?> keys = library.keys();

		while (keys.hasNext()) {
			JSONObject userJsonObject = library
					.getJSONObject((String) keys.next());

			String jid = userJsonObject.getString(Keys.JID);
			String firstName = userJsonObject.getString(Keys.FIRST_NAME);
			String lastName = userJsonObject.getString(Keys.LAST_NAME);

			String statusColor = userJsonObject.getString(Keys.AVAILABILITY_COLOR);
			String statusExpirationDateString = userJsonObject
					.getString(Keys.AVAILABILITY_EXPIRATION_DATE);

			Date expirationDate = null;

			if (!statusExpirationDateString.equals("null")) {
				expirationDate = new Date(Date.parse(statusExpirationDateString));
			}

			Availability availability = new Availability(statusColor,
					expirationDate);

			User user = new User(jid, firstName, lastName);

			// TODO: Use new Availability model.
			user.setAvailability(availability);

			try {
				// Parsing proposal detail fields
				String proposalDescription = userJsonObject
						.getString(Keys.PROPOSAL_DESCRIPTION);
				String proposalLocation = userJsonObject
						.getString(Keys.PROPOSAL_LOCATION);
				String proposalStartTimeString = userJsonObject
						.getString(Keys.PROPOSAL_TIME);

				// Sanity checks on the Strings we just parsed
				if (proposalDescription.equals("null")) {
					throw new JSONException("proposalDescription is \"null\"");
				} else if (proposalLocation.equals("null")) {
					throw new JSONException("proposalLocation is \"null\"");
				} else if (proposalStartTimeString.equals("null")) {
					throw new JSONException("proposalStartTimeString is \"null\"");
				}

				// TODO: Switch to JodaTime (IN YO FACE)
				Date proposalStartTime = new Date(
						Date.parse(proposalStartTimeString));

				Proposal proposal = new Proposal(proposalDescription,
						proposalLocation, proposalStartTime);
				user.setProposal(proposal);
				
			} catch (JSONException e) {
				Log.e("User.parseLibrary", "User " + user.firstName
						+ " had no proposal: " + e.getMessage());
			}

			users.put(jid, user);
		}

		return users;
	}

	private static List<User> searchLibraryForJids(Map<String, User> library,
			List<String> jids) {
		List<User> users = new ArrayList<User>();

		for (String jid : jids) {
			users.add(library.get(jid));
		}

		return users;
	}

	/*
	 * Parcelable.
	 */
	public User(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(jid);
		out.writeString(firstName);
		out.writeString(lastName);
	}

	private void readFromParcel(Parcel in) {
		jid = in.readString();
		firstName = in.readString();
		lastName = in.readString();
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
}

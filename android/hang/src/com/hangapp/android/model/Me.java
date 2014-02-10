package com.hangapp.android.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.util.Log;

import com.hangapp.android.util.Keys;

final public class Me extends User {

	private List<String> incomingBroadcasts;
	private List<String> outgoingBroadcasts;
	private List<String> seenProposals;

	public Me(String jid, String firstName, String lastName) {
		super(jid, firstName, lastName);
	}

	public Me(Parcel in) {
		super(in);
	}

	public List<String> getIncomingBroadcasts() {
		return incomingBroadcasts;
	}

	public void setIncomingBroadcasts(List<String> incomingBroadcasts) {
		this.incomingBroadcasts = incomingBroadcasts;
	}

	public List<String> getOutgoingBroadcasts() {
		return outgoingBroadcasts;
	}

	public void setOutgoingBroadcasts(List<String> outgoingBroadcasts) {
		this.outgoingBroadcasts = outgoingBroadcasts;
	}

	public List<String> getSeenProposals() {
		return seenProposals;
	}

	public void setSeenProposals(List<String> seenProposals) {
		this.seenProposals = seenProposals;
	}

	public static Me parseMyUserData(String userJsonString)
			throws JSONException {
		Me me = null;
		Availability availability = null;
		Proposal proposal = null;

		JSONObject userJsonObject = new JSONObject(userJsonString);

		String jid = userJsonObject.getString(Keys.JID);
		String firstName = userJsonObject.getString(Keys.FIRST_NAME);
		String lastName = userJsonObject.getString(Keys.LAST_NAME);

		me = new Me(jid, firstName, lastName);

		String statusColor = userJsonObject.getString(Keys.AVAILABILITY_COLOR);
		String statusExpirationDateString = userJsonObject
				.getString(Keys.AVAILABILITY_EXPIRATION_DATE);
		String statusText = userJsonObject.getString(Keys.STATUS_TEXT);
		if (statusText == null || statusText.equals("null")) {
			Log.i("User.parseMyUserData", "No status text received for "
					+ firstName);
			statusText = "";
		} else {
			Log.i("User.parseMyUserData",
					"statusText != null && statusText != \"null\"");
		}

		DateTime date;
		if (statusExpirationDateString != null
				&& !statusExpirationDateString.equals("null")) {
			date = DateTime.parse(statusExpirationDateString);
		} else {
			date = null;
		}

		availability = new Availability(statusColor, date, statusText);
		me.setAvailability(availability);

		// Grab the Proposal String fields from JSON
		String proposalDescription = userJsonObject
				.getString(Keys.PROPOSAL_DESCRIPTION);
		String proposalLocation = userJsonObject
				.getString(Keys.PROPOSAL_LOCATION);
		String proposalStartTimeString = userJsonObject
				.getString(Keys.PROPOSAL_START_TIME);

		JSONArray proposalInterestedJidsArray = userJsonObject
				.getJSONArray(Keys.PROPOSAL_INTERESTED);
		JSONArray proposalConfirmedJidsArray = userJsonObject
				.getJSONArray(Keys.PROPOSAL_CONFIRMED);

		List<String> proposalInterestedJidStrings = new ArrayList<String>();
		List<String> proposalConfirmedJidStrings = new ArrayList<String>();

		for (int i = 0; i < proposalInterestedJidsArray.length(); i++) {
			proposalInterestedJidStrings.add(proposalInterestedJidsArray
					.getString(i));
		}
		for (int i = 0; i < proposalConfirmedJidsArray.length(); i++) {
			proposalConfirmedJidStrings.add(proposalConfirmedJidsArray
					.getString(i));
		}

		if (proposalLocation.equals("null")) {
			Log.e("User.parseUser", "Proposal location was \"null\"");
			proposalLocation = null;
		}

		// Sanity checks on string fields.
		if (proposalDescription.equals("null")) {
			Log.e("User.parseUser", "Proposal description was \"null\"");
		} else if (proposalStartTimeString.equals("null")) {
			Log.e("User.parseUser", "Proposal start time was \"null\"");
		} else {
			// If all the sanity checks passed, then parse and create the
			// Proposal object.
			DateTime proposalStartTime = DateTime.parse(userJsonObject
					.getString(Keys.PROPOSAL_START_TIME));
			proposal = new Proposal(proposalDescription, proposalLocation,
					proposalStartTime);
			proposal.setInterested(proposalInterestedJidStrings);
			proposal.setConfirmed(proposalConfirmedJidStrings);
			me.setProposal(proposal);
		}

		JSONArray incomingBroadcastsJsonArray = userJsonObject
				.getJSONArray(Keys.INCOMING);
		JSONArray outgoingBroadcastsJsonArray = userJsonObject
				.getJSONArray(Keys.OUTGOING);

		// Populate IncomingBroadcast strings
		List<String> incomingBroadcastsStrings = new ArrayList<String>();
		for (int i = 0; i < incomingBroadcastsJsonArray.length(); i++) {
			incomingBroadcastsStrings.add(incomingBroadcastsJsonArray
					.getString(i));
		}

		// Populate OutgoingBroadcast strings
		List<String> outgoingBroadcastsStrings = new ArrayList<String>();
		for (int i = 0; i < outgoingBroadcastsJsonArray.length(); i++) {
			outgoingBroadcastsStrings.add(outgoingBroadcastsJsonArray
					.getString(i));
		}

		me.setIncomingBroadcasts(incomingBroadcastsStrings);
		me.setOutgoingBroadcasts(outgoingBroadcastsStrings);

		// Get Proposals that this User has seen
		JSONArray seenProposalsArray = userJsonObject
				.getJSONArray(Keys.PROPOSAL_SEEN);

		List<String> seenProposalsStrings = new ArrayList<String>();
		for (int i = 0; i < seenProposalsArray.length(); i++)
			seenProposalsStrings.add(seenProposalsArray.getString(i));

		me.setSeenProposals(seenProposalsStrings);

		return me;
	}

	public static List<User> parseLibrary(String myUserDataJsonString)
			throws JSONException {
		JSONObject userJsonObject = new JSONObject(myUserDataJsonString);
		return parseLibrary(userJsonObject.getJSONObject(Keys.LIBRARY));
	}

	private static List<User> parseLibrary(JSONObject library)
			throws JSONException, ClassCastException {
		List<User> users = new ArrayList<User>();

		Iterator<?> keys = library.keys();

		while (keys.hasNext()) {
			JSONObject userJsonObject = library.getJSONObject((String) keys
					.next());

			Log.i(User.class.getSimpleName(),
					"Parsing user JSON object from library: "
							+ userJsonObject.toString());

			String jid = userJsonObject.getString(Keys.JID);
			String firstName = userJsonObject.getString(Keys.FIRST_NAME);
			String lastName = userJsonObject.getString(Keys.LAST_NAME);

			User user = new User(jid, firstName, lastName);

			// Try to parse this user's Availability.
			try {

				String statusColor = userJsonObject
						.getString(Keys.AVAILABILITY_COLOR);
				String statusExpirationDateString = userJsonObject
						.getString(Keys.AVAILABILITY_EXPIRATION_DATE);
				String statusText = userJsonObject.getString(Keys.STATUS_TEXT);

				DateTime expirationDate = null;

				if (!statusExpirationDateString.equals("null")) {
					expirationDate = DateTime.parse(statusExpirationDateString);
				}

				Availability availability = new Availability(statusColor,
						expirationDate, statusText);

				// TODO: Use new Availability model.
				user.setAvailability(availability);
			} catch (JSONException e) {
//				Crashlytics.logException("User.parseLibrary", "User " + user.firstName
//						+ " had no proposal: " + e.getMessage());
				
				Log.e("User.parseLibrary", "User " + user.firstName
						+ " had no proposal: " + e.getMessage());
				
			}

			// Try to parse this user's Proposal.
			try {
				// Parsing proposal detail fields
				String proposalDescription = userJsonObject
						.getString(Keys.PROPOSAL_DESCRIPTION);
				String proposalLocation = userJsonObject
						.getString(Keys.PROPOSAL_LOCATION);
				String proposalStartTimeString = userJsonObject
						.getString(Keys.PROPOSAL_START_TIME);

				// Sanity checks on the Strings we just parsed
				if (proposalDescription.equals("null")) {
					throw new JSONException("proposalDescription is \"null\"");
				} else if (proposalLocation.equals("null")) {
					throw new JSONException("proposalLocation is \"null\"");
				} else if (proposalStartTimeString.equals("null")) {
					throw new JSONException(
							"proposalStartTimeString is \"null\"");
				}

				// TODO: Switch to JodaTime (IN YO FACE)
				DateTime proposalStartTime = DateTime
						.parse(proposalStartTimeString);

				Proposal proposal = new Proposal(proposalDescription,
						proposalLocation, proposalStartTime);

				// Get interested and confirmed for User
				JSONArray interestedJIDs = userJsonObject
						.getJSONArray(Keys.PROPOSAL_INTERESTED);
				for (int i = 0; i < interestedJIDs.length(); i++) {
					proposal.addInterested(interestedJIDs.getString(i));
				}

				JSONArray confirmedJIDs = userJsonObject
						.getJSONArray(Keys.PROPOSAL_CONFIRMED);
				for (int i = 0; i < confirmedJIDs.length(); i++) {
					proposal.addConfirmed(confirmedJIDs.getString(i));
				}

				user.setProposal(proposal);

			} catch (JSONException e) {
				Log.e("User.parseLibrary", "User " + user.firstName
						+ " had no proposal: " + e.getMessage());
			}

			users.add(user);
		}

		return users;
	}

	@SuppressWarnings("unused")
	private static List<User> searchLibraryForJids(Map<String, User> library,
			List<String> jids) {
		List<User> users = new ArrayList<User>();

		for (String jid : jids) {
			users.add(library.get(jid));
		}

		return users;
	}

}

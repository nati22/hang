package com.hangapp.android.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hangapp.android.model.Availability;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.model.callback.MyAvailabilityListener;
import com.hangapp.android.model.callback.MyProposalListener;
import com.hangapp.android.model.callback.MyUserDataListener;
import com.hangapp.android.model.callback.OutgoingBroadcastsListener;
import com.hangapp.android.model.callback.SeenProposalsListener;
import com.hangapp.android.util.BaseApplication;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.Utils;

/**
 * This class persists client-side user data. It is a Singleton that maintains
 * several Lists of Listeners to it, where a Listener is an object that
 * "observes" this Singleton.
 */
public final class Database {

	private static Database instance = new Database();

	private SharedPreferences prefs;

	private Map<String, User> library = new HashMap<String, User>();

	private List<MyUserDataListener> myUserDataListeners = new ArrayList<MyUserDataListener>();
	private List<IncomingBroadcastsListener> incomingBroadcastsListeners = new ArrayList<IncomingBroadcastsListener>();
	private List<OutgoingBroadcastsListener> outgoingBroadcastsListeners = new ArrayList<OutgoingBroadcastsListener>();
	private List<MyAvailabilityListener> myStatusListeners = new ArrayList<MyAvailabilityListener>();
	private List<MyProposalListener> myProposalListeners = new ArrayList<MyProposalListener>();
	private List<SeenProposalsListener> seenProposalsListeners = new ArrayList<SeenProposalsListener>();

	/** Private constructor */
	private Database() {
	}

	public static final synchronized Database getInstance() {
		return instance;
	}

	/**
	 * This method should be called exactly once, from {@link BaseApplication}.
	 */
	public void initialize(Context context) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public boolean addIncomingBroadcastsListener(
			IncomingBroadcastsListener listener) {
		return incomingBroadcastsListeners.add(listener);
	}

	public boolean removeIncomingBroadcastsListener(
			IncomingBroadcastsListener listener) {
		return incomingBroadcastsListeners.remove(listener);
	}

	public boolean addOutgoingBroadcastsListener(
			OutgoingBroadcastsListener listener) {
		return outgoingBroadcastsListeners.add(listener);
	}

	public boolean removeOutgoingBroadcastsListener(
			OutgoingBroadcastsListener listener) {
		return outgoingBroadcastsListeners.remove(listener);
	}

	public boolean addMyProposalListener(MyProposalListener listener) {
		return myProposalListeners.add(listener);
	}

	public boolean removeMyProposalListener(MyProposalListener listener) {
		return myProposalListeners.remove(listener);
	}

	public boolean addMyAvailabilityListener(MyAvailabilityListener listener) {
		return myStatusListeners.add(listener);
	}

	public boolean removeMyAvailabilityListener(MyAvailabilityListener listener) {
		return myStatusListeners.remove(listener);
	}

	public boolean addMyUserDataListener(MyUserDataListener listener) {
		return myUserDataListeners.add(listener);
	}

	public boolean removeMyUserDataListener(MyUserDataListener listener) {
		return myUserDataListeners.remove(listener);
	}

	public boolean addSeenProposalListener(SeenProposalsListener listener) {
		return seenProposalsListeners.add(listener);
	}

	public boolean removeSeenProposalListener(SeenProposalsListener listener) {
		return seenProposalsListeners.remove(listener);
	}

	public void setMyAvailability(Availability availability) {
		if (availability == null || availability.getExpirationDate() == null
				|| availability.getExpirationDate().isBefore(new DateTime())) {
			Log.v("Database.setStatus",
					"Called setStatus on null status or null expiration date");

			// Notify listeners
			for (MyAvailabilityListener myStatusListener : myStatusListeners) {
				myStatusListener.onMyAvailabilityUpdate(availability);
			}

			return;
		}

		if (availability.getStatus() == null
				|| availability.getExpirationDate() == null) {
			deleteMyAvailability();
			return;
		}

		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(Keys.AVAILABILITY_COLOR, availability.getStatus()
				.toString());
		prefsEditor.putString(Keys.AVAILABILITY_EXPIRATION_DATE, availability
				.getExpirationDate().toString());
		prefsEditor.putString(Keys.STATUS_TEXT, availability.getDescription());

		prefsEditor.commit();

		// Notify listeners
		for (MyAvailabilityListener myStatusListener : myStatusListeners) {
			myStatusListener.onMyAvailabilityUpdate(availability);
		}
	}

	/**
	 * @return User's current {@link Availability}. Returns null if there is no
	 *         Availability yet, or if the Availability has expired.
	 */
	public Availability getMyAvailability() {

		Availability.Status statusColor = Availability.Status.fromString(prefs
				.getString(Keys.AVAILABILITY_COLOR, null));
		String dateString = prefs.getString(Keys.AVAILABILITY_EXPIRATION_DATE,
				null);

		if (statusColor == null) {
			Log.e("Database.getMyAvailability",
					"Couldn't getMyAvailability: statusColor in SharedPrefs was null");
			return null;
		}
		if (dateString == null) {
			Log.e("Database.getMyAvailability",
					"Couldn't getMyAvailability: dateString in SharedPrefs was null");
			return null;
		}

		DateTime expirationDate = DateTime.parse(dateString);

		Availability myAvailability = new Availability(statusColor,
				expirationDate, prefs.getString(Keys.STATUS_TEXT, null));

		if (!myAvailability.isActive()) {
			return null;
		}

		return myAvailability;
	}

	public void deleteMyAvailability() {
		SharedPreferences.Editor prefsEditor = prefs.edit();

		prefsEditor.remove(Keys.AVAILABILITY_COLOR);
		prefsEditor.remove(Keys.AVAILABILITY_EXPIRATION_DATE);

		prefsEditor.commit();
	}

	public void setMyProposal(Proposal proposal) {
		if (proposal == null) {
			Log.v("Database.setProposal",
					"Called setProposal() with null proposal");

			// Notify listeners
			for (MyProposalListener myProposalListener : myProposalListeners) {
				myProposalListener.onMyProposalUpdate(proposal);
			}

			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Convert the each of the String arrays into a comma-separated String.
		String interestedString = Utils.convertArrayToString(proposal
				.getInterested());
		String confirmedString = Utils.convertArrayToString(proposal
				.getConfirmed());

		editor.putString(Keys.PROPOSAL_DESCRIPTION, proposal.getDescription());
		editor.putString(Keys.PROPOSAL_LOCATION, proposal.getLocation());
		editor.putString(Keys.PROPOSAL_START_TIME, proposal.getStartTime()
				.toString());
		editor.putString(Keys.PROPOSAL_INTERESTED, interestedString);
		editor.putString(Keys.PROPOSAL_CONFIRMED, confirmedString);

		editor.commit();

		// Notify listeners
		for (MyProposalListener myProposalListener : myProposalListeners) {
			myProposalListener.onMyProposalUpdate(proposal);
		}
	}

	public Proposal getMyProposal() {
		Proposal myProposal = null;

		String proposalDescription = prefs.getString(Keys.PROPOSAL_DESCRIPTION,
				null);
		String proposalLocation = prefs.getString(Keys.PROPOSAL_LOCATION, null);
		String proposalStartTimeString = prefs.getString(
				Keys.PROPOSAL_START_TIME, null);
		String proposalInterestedString = prefs.getString(
				Keys.PROPOSAL_INTERESTED, null);

		String proposalConfirmedString = prefs.getString(
				Keys.PROPOSAL_CONFIRMED, null);

		if (proposalDescription == null) {
			Log.e("Database.getMyProposal", "proposalDescription was null");
			return null;
		}

		if (proposalLocation == null) {
			Log.e("Database.getMyProposal", "proposalLocation was null");
			return null;
		}

		if (proposalStartTimeString == null) {
			Log.e("Database.getMyProposal", "proposalStartTimeString was null");
			return null;
		}

		if (proposalInterestedString == null) {
			Log.e("Database.getMyProposal", "proposalInterestedString was null");
		}

		if (proposalConfirmedString == null) {
			Log.e("Database.getMyProposal", "proposalConfirmedString was null");
		}

		// Convert the Start Time back into a Date Time.
		DateTime proposalStartTime = DateTime.parse(proposalStartTimeString);

		// Convert the Interested and Confirmed strings back into List<String>'s
		List<String> interested = Utils
				.convertStringToArray(proposalInterestedString);
		List<String> confirmed = Utils
				.convertStringToArray(proposalConfirmedString);

		myProposal = new Proposal(proposalDescription, proposalLocation,
				proposalStartTime);
		myProposal.setInterested(interested);
		myProposal.setConfirmed(confirmed);

		return myProposal;
	}

	public void deleteMyProposal() {
		SharedPreferences.Editor editor = prefs.edit();

		editor.remove(Keys.PROPOSAL_DESCRIPTION);
		editor.remove(Keys.PROPOSAL_LOCATION);
		editor.remove(Keys.PROPOSAL_START_TIME);
		editor.remove(Keys.PROPOSAL_INTERESTED);
		editor.remove(Keys.PROPOSAL_INTERESTED);

		editor.commit();

		// Notify listeners
		for (MyProposalListener listener : myProposalListeners) {
			listener.onMyProposalUpdate(null);
		}
	}

	/**
	 * Returns a deep copy of the user's current incoming broadcasts.
	 * 
	 * @return
	 */
	public List<User> getMyIncomingBroadcasts() {
		String incomingBroadcastJidsStringArray = prefs.getString(
				Keys.INCOMING, null);

		if (incomingBroadcastJidsStringArray == null) {
			return null;
		}

		List<String> incomingBroadcastJids = Utils
				.convertStringToArray(incomingBroadcastJidsStringArray);

		// usersDataSource.open();
		// List<User> myIncomingBroadcasts = usersDataSource
		// .getMyIncomingBroadcastsFromSQLite(incomingBroadcastJids);
		// usersDataSource.close();

		List<User> myIncomingBroadcasts = new ArrayList<User>(
				incomingBroadcastJids.size());

		for (String incomingBroadcastJid : incomingBroadcastJids) {
			User incomingBroadcast = this.library.get(incomingBroadcastJid);

			if (incomingBroadcast != null) {
				myIncomingBroadcasts.add(incomingBroadcast);
			} else {
				Log.e("Database.getMyIncomingBroadcasts",
						"No incoming broadcast for jid: "
								+ incomingBroadcastJid);
			}
		}

		return myIncomingBroadcasts;
	}

	/**
	 * Returns a deep copy of the user's current outgoing broadcasts.
	 * 
	 * @return
	 */
	public List<User> getMyOutgoingBroadcasts() {
		String outgoingBroadcastJidsStringArray = prefs.getString(
				Keys.OUTGOING, null);

		if (outgoingBroadcastJidsStringArray == null) {
			return null;
		}

		List<String> outgoingBroadcastJids = Utils
				.convertStringToArray(outgoingBroadcastJidsStringArray);

		List<User> myOutgoingBroadcasts = new ArrayList<User>(
				outgoingBroadcastJids.size());

		for (String outgoingBroadcastJid : outgoingBroadcastJids) {
			User outgoingBroadcast = this.library.get(outgoingBroadcastJid);

			if (outgoingBroadcast != null) {
				myOutgoingBroadcasts.add(outgoingBroadcast);
			} else {
				Log.e("Database.getMyOutgoingBroadcasts",
						"No outgoing broadcast for jid: "
								+ outgoingBroadcastJid);
			}
		}

		return myOutgoingBroadcasts;
	}

	public User getIncomingUser(String jid) {
		String incomingBroadcastJidsStringArray = prefs.getString(
				Keys.INCOMING, null);

		if (incomingBroadcastJidsStringArray == null) {
			Log.e("Database.getIncomingUser",
					"incomingBroadcastJidsStringArray was null");
			return null;
		}

		List<String> incomingBroadcastJids = Utils
				.convertStringToArray(incomingBroadcastJidsStringArray);

		if (!jid.equals(getMyJid()) && !incomingBroadcastJids.contains(jid)) {
			Log.e("Database.getIncomingUser", "Incoming user " + jid
					+ " not found in incomingBroadcasts");
			return null;
		}

		return library.get(jid);
	}

	public User getOutgoingUser(String jid) {
		String outgoingBroadcastJidsStringArray = prefs.getString(
				Keys.OUTGOING, null);

		if (outgoingBroadcastJidsStringArray == null) {
			return null;
		}

		List<String> outgoingBroadcastJids = Utils
				.convertStringToArray(outgoingBroadcastJidsStringArray);

		if (!outgoingBroadcastJids.contains(jid)) {
			Log.e("Database.getOutgoingUser", "Outgoing user " + jid
					+ " not found in outgoingBroadcasts");
			return null;
		}

		return library.get(jid);
	}

	public void addFilteredUser(String jid) {

	}

	public void removeFilteredUser(String jid) {

	}

	public void setMyUserData(String jid, String firstName, String lastName) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.JID, jid);
		editor.putString(Keys.FIRST_NAME, firstName);
		editor.putString(Keys.LAST_NAME, lastName);
		editor.commit();

		User me = new User(jid, firstName, lastName);

		for (MyUserDataListener listener : myUserDataListeners) {
			listener.onMyUserDataUpdate(me);
		}
	}

	public void setMyIncomingBroadcasts(List<String> incomingBroadcasts) {
		// Convert the List<String>'s to single comma separated Strings.
		String incomingStringList = Utils
				.convertArrayToString(incomingBroadcasts);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.INCOMING, incomingStringList);
		editor.commit();

		// Notify observers of incoming broadcasts. Need to query
		// SQLite to return actual User objects instead of Strings of JIDs.
		List<User> incomingBroadcastUserObjects = getMyIncomingBroadcasts();

		if (incomingBroadcastUserObjects == null) {
			incomingBroadcastUserObjects = new ArrayList<User>();
		}

		for (IncomingBroadcastsListener listener : incomingBroadcastsListeners) {
			listener.onIncomingBroadcastsUpdate(incomingBroadcastUserObjects);
		}
	}

	public void setMyOutgoingBroadcasts(List<String> outgoingBroadcasts) {
		// Convert the List<String>'s to single comma separated Strings.
		String outgoingStringList = Utils
				.convertArrayToString(outgoingBroadcasts);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.OUTGOING, outgoingStringList);
		editor.commit();

		// Notify observers of outgoing broadcasts. Need to query
		// SQLite to return actual User objects instead of Strings of JIDs.
		List<User> outgoingBroadcastUserObjects = getMyOutgoingBroadcasts();

		if (outgoingBroadcastUserObjects == null) {
			outgoingBroadcastUserObjects = new ArrayList<User>();
		}

		for (OutgoingBroadcastsListener listener : outgoingBroadcastsListeners) {
			listener.onOutgoingBroadcastsUpdate(outgoingBroadcastUserObjects);
		}
	}

	public void setMySeenProposals(List<String> seenProposals) {
		// Convert the List<String>'s to single comma separated Strings.
		String seenProposalsStringList = Utils
				.convertArrayToString(seenProposals);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.PROPOSAL_SEEN, seenProposalsStringList);
		editor.commit();

		for (SeenProposalsListener listener : seenProposalsListeners) {
			listener.onMySeenProposalsUpdate(seenProposals);
		}

	}

	public void addSeenProposal(String broadcasterJid) {
		String seenProposalsListString = prefs.getString(Keys.PROPOSAL_SEEN,
				null);

		List<String> seenProposals = Utils
				.convertStringToArray(seenProposalsListString);

		if (!seenProposals.contains(broadcasterJid)) {
			seenProposals.add(broadcasterJid);
		} else {
			Log.i("Database.addSeenProposal()", "Proposal has been seen before");
		}

		seenProposalsListString = Utils.convertArrayToString(seenProposals);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.PROPOSAL_SEEN, seenProposalsListString);
		editor.commit();

		for (SeenProposalsListener listener : seenProposalsListeners) {
			listener.onMySeenProposalsUpdate(seenProposals);
		}
	}

	public List<String> getMySeenProposals() {
		String seenProposalsListString = prefs.getString(Keys.PROPOSAL_SEEN,
				null);

		if (seenProposalsListString == null)
			return null;

		return Utils.convertStringToArray(seenProposalsListString);
	}

	public void deleteMySeenProposal(String proposalJid) {
		String seenProposalsListString = prefs.getString(Keys.PROPOSAL_SEEN,
				null);

		List<String> seenProposals = Utils
				.convertStringToArray(seenProposalsListString);

		boolean removed = seenProposals.remove(proposalJid);

		if (removed)
			Log.i("Database.deleteMySeenProposal", "seenProposals.remove("
					+ proposalJid + ") returned false");

		seenProposalsListString = Utils.convertArrayToString(seenProposals);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.PROPOSAL_SEEN, seenProposalsListString);
		editor.commit();

		for (SeenProposalsListener listener : seenProposalsListeners) {
			listener.onMySeenProposalsUpdate(seenProposals);
		}

	}

	public String getMyJid() {
		return prefs.getString(Keys.JID, null);
	}

	public String getMyFirstName() {
		return prefs.getString(Keys.FIRST_NAME, null);
	}

	public String getMyLastName() {
		return prefs.getString(Keys.LAST_NAME, null);
	}

	public String getMyFullName() {
		return getMyFirstName() + " " + getMyLastName();
	}

	/**
	 * Returns a list of the JIDs of the users whose Proposals I'm currently
	 * interested in.
	 * 
	 * @return
	 */
	public List<String> getJidsImInterestedIn() {
		String interestedJidsString = prefs.getString(
				Keys.JIDS_IM_INTERESTED_IN, null);

		if (interestedJidsString != null) {
			return Utils.convertStringToArray(interestedJidsString);
		} else {
			return null;
		}
	}

	public void setJidsImInterestedIn(List<String> interestedJids) {
		String interestedJidsString = Utils
				.convertArrayToString(interestedJids);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.JIDS_IM_INTERESTED_IN, interestedJidsString);
		editor.commit();
	}

	/**
	 * Helper method only to be used by the REST calls, as they finish parsing
	 * in the JSON for the library. ======= Helper method only to be used by the
	 * REST calls, as they finish parsing in the JSON for the library. >>>>>>>
	 * 177b45476535132f4f2c242136f66e89d3e2680b
	 */
	public void saveLibrary(List<User> newLibrary) {
		// usersDataSource.open();
		//
		// // Clear out the existing Users table from SQLite.
		// usersDataSource.clearUsersTable();
		//
		// for (User userToSave : library) {
		// usersDataSource.saveUserInDatabase(userToSave);
		// }
		//
		// usersDataSource.close();

		this.library.clear();

		for (User user : newLibrary) {
			this.library.put(user.getJid(), user);
		}
	}

	public void clear() {
		// Clear out SQLite as well as SharedPrefs
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();

		// usersDataSource.open();
		// usersDataSource.clearUsersTable();
		// usersDataSource.close();

		library.clear();
	}

}

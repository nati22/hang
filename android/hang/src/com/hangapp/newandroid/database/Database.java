package com.hangapp.newandroid.database;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.model.Proposal;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.model.callback.IncomingBroadcastsListener;
import com.hangapp.newandroid.model.callback.MyAvailabilityListener;
import com.hangapp.newandroid.model.callback.MyProposalListener;
import com.hangapp.newandroid.model.callback.MyUserDataListener;
import com.hangapp.newandroid.model.callback.OutgoingBroadcastsListener;
import com.hangapp.newandroid.util.BaseApplication;
import com.hangapp.newandroid.util.Keys;
import com.hangapp.newandroid.util.Utils;

/**
 * This class persists client-side user data. It is a Singleton that maintains
 * several Lists of Listeners to it, where a Listener is an object that
 * "observes" this Singleton.
 */
public final class Database {

	private static Database instance = new Database();

	private SharedPreferences prefs;
	private UsersDataSource usersDataSource;

	private List<IncomingBroadcastsListener> incomingBroadcastsListeners = new ArrayList<IncomingBroadcastsListener>();
	private List<MyProposalListener> myProposalListeners = new ArrayList<MyProposalListener>();
	private List<MyAvailabilityListener> myStatusListeners = new ArrayList<MyAvailabilityListener>();
	private List<OutgoingBroadcastsListener> outgoingBroadcastsListeners = new ArrayList<OutgoingBroadcastsListener>();
	private List<MyUserDataListener> myUserDataListeners = new ArrayList<MyUserDataListener>();

	/*
	 * private Map<String, List<IncomingBroadcastListListener>>
	 * incomingBroadcastListListeners = new HashMap<String,
	 * List<IncomingBroadcastListListener>>();
	 */
	/** Private constructor */
	private Database() {
	}

	public static final synchronized Database getInstance() {
		return instance;
	}

	/**
	 * This method should be called exactly once, from {@link BaseApplication}.
	 * 
	 * @param context
	 */
	public void initialize(Context context) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.usersDataSource = new UsersDataSource(context);
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

	/*
	 * public boolean addIncomingBroadcastListListener(String jid,
	 * IncomingBroadcastListListener listener) { return
	 * incomingBroadcastListListeners.get(jid).add(listener); }
	 * 
	 * public boolean removeIncomingBroadcastListListener(String jid,
	 * IncomingBroadcastListListener listener) { return
	 * incomingBroadcastListListeners.get(jid).remove(listener); }
	 */

	public boolean removeMyAvailabilityListener(MyAvailabilityListener listener) {
		return myStatusListeners.remove(listener);
	}

	public boolean addMyUserDataListener(MyUserDataListener listener) {
		return myUserDataListeners.add(listener);
	}

	public boolean removeMyUserDataListener(MyUserDataListener listener) {
		return myUserDataListeners.remove(listener);
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

		Availability.Status statusColor = Availability.parseStatus(prefs
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
				expirationDate);

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
		String interestedString = Utils.convertStringArrayToString(proposal
				.getInterested());
		String confirmedString = Utils.convertStringArrayToString(proposal
				.getConfirmed());

		editor.putString(Keys.PROPOSAL_DESCRIPTION, proposal.getDescription());
		editor.putString(Keys.PROPOSAL_LOCATION, proposal.getLocation());
		editor.putString(Keys.PROPOSAL_START_TIME, proposal.getStartTime()
				.toString());
		editor.putString(Keys.PROPOSAL_INTERESTED, interestedString);
		editor.putString(Keys.PROPOSAL_INTERESTED, confirmedString);

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

		usersDataSource.open();
		List<User> myIncomingBroadcasts = usersDataSource
				.getMyIncomingBroadcastsFromSQLite(incomingBroadcastJids);
		usersDataSource.close();

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

		usersDataSource.open();
		List<User> myOutgoingBroadcasts = usersDataSource
				.getMyOutgoingBroadcastsFromSQLite(outgoingBroadcastJids);
		usersDataSource.close();

		return myOutgoingBroadcasts;
	}

	public User getIncomingUser(String jid) {
		return null;
	}

	public User getOutgoingUser(String jid) {
		return null;
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
				.convertStringArrayToString(incomingBroadcasts);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.INCOMING, incomingStringList);
		editor.commit();

		// Notify observers of incoming broadcasts. Need to query
		// SQLite to return actual User objects instead of Strings of JIDs.
		List<User> incomingBroadcastUserObjects = getMyIncomingBroadcasts();
		for (IncomingBroadcastsListener listener : incomingBroadcastsListeners) {
			listener.onIncomingBroadcastsUpdate(incomingBroadcastUserObjects);
		}
	}

	public void setMyOutgoingBroadcasts(List<String> outgoingBroadcasts) {
		// Convert the List<String>'s to single comma separated Strings.
		String outgoingStringList = Utils
				.convertStringArrayToString(outgoingBroadcasts);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.OUTGOING, outgoingStringList);
		editor.commit();

		// Notify observers of outgoing broadcasts. Need to query
		// SQLite to return actual User objects instead of Strings of JIDs.
		List<User> outgoingBroadcastUserObjects = getMyOutgoingBroadcasts();
		for (OutgoingBroadcastsListener listener : outgoingBroadcastsListeners) {
			listener.onOutgoingBroadcastsUpdate(outgoingBroadcastUserObjects);
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
	 * Helper method only to be used by the REST calls, as they finish parsing
	 * in the JSON for the library.
	 */
	public void saveLibrary(List<User> library) {
		usersDataSource.open();

		// Clear out the existing Users table from SQLite.
		usersDataSource.clearUsersTable();

		for (User userToSave : library) {
			usersDataSource.saveUserInDatabase(userToSave);
		}

		usersDataSource.close();
	}

	public void clear() {
		// Clear out SQLite as well as SharedPrefs
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();

		usersDataSource.open();
		usersDataSource.clearUsersTable();
		usersDataSource.close();

		// FIXME: Clear out XMPP messages here as well.
	}

}

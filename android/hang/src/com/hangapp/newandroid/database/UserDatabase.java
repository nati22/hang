package com.hangapp.newandroid.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.model.Proposal;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.model.callback.IncomingBroadcastsListener;
import com.hangapp.newandroid.model.callback.MyProposalListener;
import com.hangapp.newandroid.model.callback.MyStatusListener;
import com.hangapp.newandroid.model.callback.MyUserDataListener;
import com.hangapp.newandroid.model.callback.OutgoingBroadcastsListener;
import com.hangapp.newandroid.util.BaseApplication;
import com.hangapp.newandroid.util.Keys;

/**
 * Singleton that maintains several Lists of Listeners to it. This class
 * persists client-side user data.
 */
public final class UserDatabase {

	private static UserDatabase instance = new UserDatabase();

	private SharedPreferences prefs;

	private Proposal proposal;
	private Map<String, User> incomingMap = new HashMap<String, User>();
	private Map<String, User> outgoingMap = new HashMap<String, User>();

	private List<User> incomingList = new ArrayList<User>();
	private List<User> outgoingList = new ArrayList<User>();

	private List<IncomingBroadcastsListener> incomingBroadcastsListeners = new ArrayList<IncomingBroadcastsListener>();
	private List<MyProposalListener> myProposalListeners = new ArrayList<MyProposalListener>();
	private List<MyStatusListener> myStatusListeners = new ArrayList<MyStatusListener>();
	private List<OutgoingBroadcastsListener> outgoingBroadcastsListeners = new ArrayList<OutgoingBroadcastsListener>();
	private List<MyUserDataListener> myUserDataListeners = new ArrayList<MyUserDataListener>();

	/** Private constructor */
	private UserDatabase() {
	}

	public static final synchronized UserDatabase getInstance() {
		return instance;
	}

	/**
	 * This method should be called exactly once, from {@link BaseApplication}.
	 * 
	 * @param context
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

	public boolean addMyStatusListener(MyStatusListener listener) {
		return myStatusListeners.add(listener);
	}

	public boolean removeMyStatusListener(MyStatusListener listener) {
		return myStatusListeners.remove(listener);
	}

	public boolean addMyUserDataListener(MyUserDataListener listener) {
		return myUserDataListeners.add(listener);
	}

	public boolean removeMyUserDataListener(MyUserDataListener listener) {
		return myUserDataListeners.remove(listener);
	}

	public void setMyAvailability(Availability status) {
		if (status == null || status.getExpirationDate() == null
				|| status.getExpirationDate().before(new Date())) {
			Log.v("UserDatabase.setStatus",
					"Called setStatus on null status / null status expiration date");

			// Notify listeners
			for (MyStatusListener myStatusListener : myStatusListeners) {
				myStatusListener.onMyStatusUpdate(status);
			}

			return;
		}

		SharedPreferences.Editor prefsEditor = prefs.edit();

		// Store serialized object instead?
		prefsEditor.putString(Keys.AVAILABILITY_COLOR, status.getColor()
				.toString());
		prefsEditor.putString(Keys.AVAILABILITY_EXPIRATION_DATE, status
				.getExpirationDate().toGMTString());
		prefsEditor.commit();

		// Notify listeners
		for (MyStatusListener myStatusListener : myStatusListeners) {
			myStatusListener.onMyStatusUpdate(status);
		}
	}

	/**
	 * @return User's current {@link Availability}. Returns null if there is no
	 *         Availability yet, or if the Availability has expired.
	 */
	public Availability getMyAvailability() {

		Availability.Color statusColor = Availability.parseColor(prefs
				.getString(Keys.AVAILABILITY_COLOR, null));
		String dateString = prefs.getString(Keys.AVAILABILITY_EXPIRATION_DATE,
				null);
		Date expirationDate = dateString != null ? new Date(Date.parse(prefs
				.getString(Keys.AVAILABILITY_EXPIRATION_DATE, null))) : null;

		Availability myAvailability = new Availability(statusColor,
				expirationDate);

		if (!myAvailability.isActive()) {
			return null;
		}

		return myAvailability;
	}

// TODO: We need a way to store time, interested users, confirmed users
	public void setMyProposal(Proposal proposal) {
		if (proposal == null) {
			Log.v("UserDatabase.setProposal",
					"Called setProposal() with null proposal");

			// Notify listeners
			for (MyProposalListener myProposalListener : myProposalListeners) {
				myProposalListener.onMyProposalUpdate(proposal);
			}

			return;
		}

		this.proposal = proposal;

		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(Keys.PROPOSAL_DESCRIPTION, proposal.getDescription());
		editor.putString(Keys.PROPOSAL_LOCATION, proposal.getLocation());
	/*	editor.putString(Keys.PROPOSAL_TIME, proposal.getStartTime()
				.toGMTString()); 
*/
		editor.commit();

		// Notify listeners
		for (MyProposalListener myProposalListener : myProposalListeners) {
			myProposalListener.onMyProposalUpdate(proposal);
		}
	}

	public Proposal getMyProposal() {
		return proposal;
	}

	public void deleteMyProposal() {
		this.proposal = null;

		// Notify listeners
		for (MyProposalListener listener : myProposalListeners) {
			listener.onMyProposalUpdate(null);
		}
	}

	public void setIncomingBroadcasts(List<User> incoming) {
		// Clear out the current list
		this.incomingList.clear();
		this.incomingMap.clear();

		// Put the whole list in.
		this.incomingList.addAll(incoming);
		for (User user : incoming) {
			this.incomingMap.put(user.getJid(), user);
		}

		// Notify listeners
		for (IncomingBroadcastsListener listener : incomingBroadcastsListeners) {
			listener.onIncomingBroadcastsUpdate(incoming);
		}
	}

	public void setMyOutgoingBroadcasts(List<User> outgoing) {
		// Clear out the current list.
		this.outgoingMap.clear();
		this.outgoingList.clear();

		// Add everything
		this.outgoingList.addAll(outgoing);
		for (User user : outgoing) {
			this.outgoingMap.put(user.getJid(), user);
		}

		// Notify listeners
		for (OutgoingBroadcastsListener listener : outgoingBroadcastsListeners) {
			listener.onOutgoingBroadcastsUpdate(outgoing);
		}
	}

	/**
	 * Returns a deep copy of the user's current outgoing broadcasts.
	 * 
	 * @return
	 */
	public List<User> getMyOutgoingBroadcasts() {
		List<User> outgoingBroadcasts = new ArrayList<User>(this.outgoingList);
		return outgoingBroadcasts;
	}

	/**
	 * Returns a deep copy of the user's current incoming broadcasts.
	 * 
	 * @return
	 */
	public List<User> getMyIncomingBroadcasts() {
		List<User> incomingBroadcasts = new ArrayList<User>(this.incomingList);
		return incomingBroadcasts;
	}

	public User getIncomingUser(String jid) {
		// (nati) If the other user stops broadcasting would this become an
		// issue?
		return incomingMap.get(jid);
	}

	public User getOutgoingUser(String jid) {
		// (nati) If defaultuser stops broadcasting could this be a problem?
		return outgoingMap.get(jid);
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

	public void clear() {
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();

		proposal = null;
		incomingList.clear();
		incomingMap.clear();
		outgoingList.clear();
		outgoingMap.clear();
	}

}

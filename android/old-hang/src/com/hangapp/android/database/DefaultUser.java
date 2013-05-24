package com.hangapp.android.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.model.GraphUser;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.Status;
import com.hangapp.android.model.User;
import com.hangapp.android.model.listener.IncomingBroadcastsListener;
import com.hangapp.android.model.listener.MyProposalListener;
import com.hangapp.android.model.listener.MyStatusListener;
import com.hangapp.android.model.listener.OutgoingBroadcastsListener;
import com.hangapp.android.util.Keys;

/**
 * Observable singleton that contains the controlling user's User model, as well
 * as his Incoming and Outgoing broadcasts Lists
 * 
 * @author girum
 */
public final class DefaultUser {

	private static DefaultUser instance = new DefaultUser();

	// @Inject
	private SharedPreferences prefs;

	private Proposal proposal;
	private Map<String, User> incomingMap = new HashMap<String, User>();
	private Map<String, User> outgoingMap = new HashMap<String, User>();

	// @Inject
	private ArrayList<User> incomingList = new ArrayList<User>();
	// @Inject
	private ArrayList<User> outgoingList = new ArrayList<User>();

	// @Inject
	private ArrayList<IncomingBroadcastsListener> incomingBroadcastsListeners = new ArrayList<IncomingBroadcastsListener>();
	// @Inject
	private ArrayList<MyProposalListener> myProposalListeners = new ArrayList<MyProposalListener>();
	// @Inject
	private ArrayList<MyStatusListener> myStatusListeners = new ArrayList<MyStatusListener>();
	// @Inject
	private ArrayList<OutgoingBroadcastsListener> outgoingBroadcastsListeners = new ArrayList<OutgoingBroadcastsListener>();

//	private List<GraphUser> selectedUsers;

	public static final synchronized DefaultUser getInstance() {
		return instance;
	}

	public void init(Context context) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public User getUserCopy(Context context) {
		String jid = prefs.getString(Keys.JID, null);
		String firstName = prefs.getString(Keys.FIRST_NAME, null);
		String lastName = prefs.getString(Keys.LAST_NAME, null);

		User userCopy = new User(jid, firstName, lastName, getMyStatus(),
				getMyProposal());
		return userCopy;
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

	public boolean removeMyProposalListner(MyProposalListener listener) {
		return myProposalListeners.remove(listener);
	}

	public boolean addMyStatusListener(MyStatusListener listener) {
		return myStatusListeners.add(listener);
	}

	public boolean removeMyStatusListener(MyStatusListener listener) {
		return myStatusListeners.remove(listener);
	}

	public void setStatus(Status status) {
		if (status == null || status.getExpirationDate() == null) {
			Log.v("DefaultUser.setStatus",
					"Called setStatus on null status / null status expiration date");

			// Notify listeners
			for (MyStatusListener myStatusListener : myStatusListeners) {
				myStatusListener.onMyStatusUpdate(status);
			}

			return;
		}

		SharedPreferences.Editor prefsEditor = prefs.edit();

		// Store serialized object instead?
		prefsEditor.putString(Keys.STATUS_COLOR, status.getColor().toString());
		prefsEditor.putString(Keys.STATUS_EXPIRATION_DATE, status
				.getExpirationDate().toGMTString());
		prefsEditor.commit();

		// Notify listeners
		for (MyStatusListener myStatusListener : myStatusListeners) {
			myStatusListener.onMyStatusUpdate(status);
		}
	}

	public Status getMyStatus() {
		Status.Color statusColor = Status.parseColor(prefs.getString(
				Keys.STATUS_COLOR, null));
		String dateString = prefs.getString(Keys.STATUS_EXPIRATION_DATE, null);
		Date expirationDate = dateString != null ? new Date(Date.parse(prefs
				.getString(Keys.STATUS_EXPIRATION_DATE, null))) : null;

		return new Status(statusColor, expirationDate);
	}

	public void setProposal(Proposal proposal) {
		if (proposal == null) {
			Log.v("DefaultUser.setProposal",
					"Called setProposal() with null proposal");

			// Notify listeners
			for (MyProposalListener myProposalListener : myProposalListeners) {
				myProposalListener.onMyProposalUpdate(proposal);
			}

			return;
		}

		this.proposal = proposal;

		// SharedPreferences.Editor editor = prefs.edit();
		//
		// editor.putString(Keys.PROPOSAL_DESC, proposal.getDescription());
		// editor.putString(Keys.PROPOSAL_LOC, proposal.getLocation());
		// editor.putString(Keys.PROPOSAL_TIME,
		// proposal.getStartTime().toGMTString());

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

	// TODO: Make this DRY against the outgoing method below
	public void setIncomingBroadcasts(List<User> incoming) {

		if (incoming == null) {
			Log.e("DefaultUser.setIncomingBroadcasts",
					"Incoming Broadcasts array is null");
			return;
		}

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

	// TODO: We shouldn't expose this ArrayList directly. Clients should only
	// be able to add to this ArrayList via methods that we expose.
	// Those methods notify observers as they add/remove elements.
	//
	// Also, what if this list needs to be asynchronously updated
	// before it can return?
	//
	//
	// The solution: each Activity should maintain its own List<Whatever> in
	// it. Methods like this should use the callback pattern to populate the
	// Activity's ArrayList.
	public ArrayList<User> getIncomingBroadcastsList() {
		return incomingList;
	}

	public List<User> getOutgoingBroadcastsList() {
		return outgoingList;
	}

	public void setOutgoingBroadcasts(List<User> outgoing) {
		// Clear and update HashMap
		this.outgoingMap.clear();
		this.outgoingList.clear();

		if (outgoing == null) {
			Log.e("DefaultUser.setOutgoingBroadcasts",
					"Outgoing Broadcasts array is null");
			return;
		}

		// Add everything
		this.outgoingList.addAll(outgoing);
		for (User user : outgoing) {
			this.outgoingMap.put(user.getJid(), user);
		}

		Collections.sort(outgoingList);

		// Notify listeners
		for (OutgoingBroadcastsListener listener : outgoingBroadcastsListeners) {
			listener.onOutgoingBroadcastsUpdate(outgoing);
		}
	}

	public User getIncomingUser(String jid) {
		// (nati) If the other user stops broadcasting would this become an
		// issue?
		return incomingMap.get(jid);
	}

	// TODO: This method is a hack. Remove it eventually.
	public void notifyAllListeners() {
		for (IncomingBroadcastsListener listener : incomingBroadcastsListeners) {
			listener.onIncomingBroadcastsUpdate(incomingList);
		}
		for (OutgoingBroadcastsListener listener : outgoingBroadcastsListeners) {
			listener.onOutgoingBroadcastsUpdate(outgoingList);
		}
		for (MyStatusListener listener : myStatusListeners) {
			listener.onMyStatusUpdate(getMyStatus());
		}
		for (MyProposalListener listener : myProposalListeners) {
			listener.onMyProposalUpdate(getMyProposal());
		}
	}

	public User getOutgoingUser(String jid) {
		// (nati) If defaultuser stops broadcasting could this be a problem?
		return outgoingMap.get(jid);
	}

//	public List<GraphUser> getSelectedUsers() {
//		return selectedUsers;
//	}
//
//	public void setSelectedUsers(List<GraphUser> selectedUsers) {
//		this.selectedUsers = selectedUsers;
//	}
}

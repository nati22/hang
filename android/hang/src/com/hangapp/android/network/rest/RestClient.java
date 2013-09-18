package com.hangapp.android.network.rest;

import java.util.List;

import com.hangapp.android.model.Availability;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;
import com.hangapp.android.network.xmpp.XMPP;

public interface RestClient {
	public void registerNewUser(XMPP xmpp, User newUser);

	public void getMyData(XMPP xmpp);

	public void updateMyAvailability(Availability status);

	public void updateMyProposal(Proposal proposal);

	public void deleteMyProposal();

	// TODO: Remove unnecessary dependency on XMPP (due to call of
	// GetUserData).
	public void addBroadcastee(XMPP xmpp, String broadcasteeJid);

	// TODO: Remove unnecessary dependency on XMPP (due to call of
	// GetUserData).
	public void addBroadcastees(XMPP xmpp, List<String> broadcasteeJids);

	// TODO: Remove unnecessary dependency on XMPP (due to call of
	// GetUserData).
	public void deleteBroadcastee(XMPP xmpp, String broadcasteeJid);

	public void sendNudge(String targetJid);

	public void setInterested(String broadcasterJid);

	public void setConfirmed(String broadcasterJid);

	// TODO: Remove unnecessary dependency on XMPP (due to call of
	// GetUserData).
	public void deleteInterested(XMPP xmpp, String broadcasterJid);

	// TODO: Remove unnecessary dependency on XMPP (due to call of
	// GetUserData).
	public void deleteConfirmed(XMPP xmpp, String broadcasterJid);

	public void setSeenProposal(String broadcasterJid);

	public void deleteSeenProposal(String broadcasterJid);
}
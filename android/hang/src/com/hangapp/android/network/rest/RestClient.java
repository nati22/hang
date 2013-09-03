package com.hangapp.android.network.rest;

import java.util.List;

import com.hangapp.android.model.Availability;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;

public interface RestClient {
	public void registerNewUser(User newUser);

	public void getMyData();

	public void updateMyAvailability(Availability status);

	public void updateMyProposal(Proposal proposal);

	public void deleteMyProposal();

	public void addBroadcastee(String broadcasteeJid);

	public void addBroadcastees(List<String> broadcasteeJids);

	public void deleteBroadcastee(String broadcasteeJid);

	public void sendNudge(String targetJid);

	public void setInterested(String broadcasterJid);

	public void setConfirmed(String broadcasterJid);

	public void deleteInterested(String broadcasterJid);

	public void deleteConfirmed(String broadcasterJid);

	public void setSeenProposal(String broadcasterJid);

	public void deleteSeenProposal(String broadcasterJid);
}
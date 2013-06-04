package com.hangapp.newandroid.network.rest;

import com.hangapp.newandroid.model.Proposal;
import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.model.User;

public interface RestClient {
	public void registerNewUser(User newUser);

	public void getMyData();

	public void updateMyAvailability(Availability status);

	public void updateMyProposal(Proposal proposal);

	public void deleteMyProposal();

	public void addBroadcastee(String broadcasteeJid);

	public void deleteBroadcastee(String broadcasteeJid);

	public void sendNudge(String targetJid);

	public void setInterested(String broadcasteeJid);

	public void setConfirmed(String broadcasteeJid);

	public void deleteInterested(String broadcasteeJid);

	public void deleteConfirmed(String broadcasteeJid);
}
package com.hangapp.newandroid.network.rest;

import com.hangapp.newandroid.model.Proposal;
import com.hangapp.newandroid.model.OldAvailability;
import com.hangapp.newandroid.model.User;

public interface RestClient {
	public void registerNewUser(User newUser);

	public void getMyData();
	
	public void updateMyAvailability(OldAvailability status);

	public void updateMyProposal(Proposal proposal);

	public void deleteMyProposal();

	public void addBroadcastee(String broadcasteeJID);
}
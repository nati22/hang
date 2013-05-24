package com.hangapp.android.network.rest;

import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.Status;
import com.hangapp.android.model.User;

public interface RestClient {
	public void registerUser(User newUser);

	public void updateMyStatus(Status status);

	public void updateMyProposal(Proposal proposal);

	public void deleteMyProposal();

	public void updateSomeoneElsesProposal(Integer hostUserJID,
			Proposal proposal);

	public void getUserData();

	public void addBroadcastee(Integer broadcasteeJID);
}

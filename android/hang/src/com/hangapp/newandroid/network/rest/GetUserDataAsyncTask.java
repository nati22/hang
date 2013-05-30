package com.hangapp.newandroid.network.rest;

import android.content.Context;

import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.User;

public final class GetUserDataAsyncTask extends BaseGetRequestAsyncTask<User> {

	private static final String URL_SUFFIX = "/users/";

	private Database database;

	protected GetUserDataAsyncTask(Database database, Context context,
			String jid) {
		super(context, URL_SUFFIX + jid);

		// Set dependencies.
		this.database = database;
	}

	@Override
	public User call() throws Exception {
		// Execute the GET request
		super.call();
		
		// Try to parse the resulting JSON.
		User user = User.parseUser(responseString);

		// If it worked, return the User.
		return user;
	}

	@Override
	protected void onSuccess(User me) throws Exception {
		// Save the user data in the database.
		database.setMyAvailability(me.getAvailability());
		database.setMyProposal(me.getProposal());
		database.setIncomingBroadcasts(me.getIncomingBroadcasts());
		database.setMyOutgoingBroadcasts(me.getOutgoingBroadcasts());
	}

}

package com.hangapp.newandroid.network.rest;

import java.util.List;

import android.content.Context;

import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.User;

final class GetUserDataAsyncTask extends BaseGetRequestAsyncTask<User> {

	private static final String URL_SUFFIX = "/users/";

	private Database database;
	private List<User> library;

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

		// Try to parse the resulting JSON for my user data.
		User me = User.parseMyUserData(responseString);

		// Try to parse the resulting JSON for the library.
		library = User.parseLibrary(responseString);

		// If it worked, return the User.
		return me;
	}

	@Override
	protected void onSuccess(User me) throws Exception {
		// Save the user data in the database.
		database.setMyUserData(me.getJid(), me.getFirstName(), me.getLastName());
		database.setMyIncomingBroadcasts(me.getIncomingBroadcasts());
		database.setMyOutgoingBroadcasts(me.getOutgoingBroadcasts());
		database.setMyAvailability(me.getAvailability());
		database.setMyProposal(me.getProposal());

		// Save the library of other users' data into the database.
		database.saveLibrary(library);
	}
}

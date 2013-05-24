package com.hangapp.android.network.rest;

import android.content.Context;

import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.User;

public class GetUserDataAsyncTask extends BaseGetRequestAsyncTask<User> {

	private static final String URL_SUFFIX = "/users/";

	// @Inject
	private DefaultUser defaultUser;

	protected GetUserDataAsyncTask(Context context, String jid) {
		super(context, URL_SUFFIX + jid);

		// Instantiate dependencies.
		defaultUser = DefaultUser.getInstance();

		// // Inject the fields of this POJO. RoboGuice field injection doesn't
		// // work on POJOs without this.
		// RoboGuice.getInjector(context).injectMembers(this);
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
	protected void onSuccess(User user) throws Exception {
		// Save the user data in the database.
		defaultUser.setStatus(user.getStatus());
		defaultUser.setProposal(user.getProposal());
		defaultUser.setIncomingBroadcasts(user.getIncomingBroadcasts());
		defaultUser.setOutgoingBroadcasts(user.getOutgoingBroadcasts());
	}

}

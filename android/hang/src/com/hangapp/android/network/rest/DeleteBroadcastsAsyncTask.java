package com.hangapp.android.network.rest;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.Database;
import com.hangapp.android.model.User;

class DeleteBroadcastsAsyncTask extends BaseDeleteRequestAsyncTask<User> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String BROADCAST_URI_SUFFIX = "/broadcasts";

	private RestClient restClient;

	protected DeleteBroadcastsAsyncTask(Database database,
			RestClient restClient, Context context, String myJid,
			String formattedTargetJids) {
		super(context, USERS_URI_SUFFIX + myJid + BROADCAST_URI_SUFFIX
				+ formattedTargetJids);

		// Inject dependencies.
		this.restClient = restClient;

	}

	public User call() throws Exception {
		// Execute the DELETE request
		super.call();

		Log.i("DeleteBroadcasteeAsyncTask", responseString);

		return null;
	}

	protected void onSuccess(User me) throws Exception {
		restClient.getMyData();
	}

}

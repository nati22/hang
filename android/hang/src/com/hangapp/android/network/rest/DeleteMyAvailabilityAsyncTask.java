package com.hangapp.android.network.rest;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.Database;

final class DeleteMyAvailabilityAsyncTask extends
		BaseDeleteRequestAsyncTask<String> {
	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String STATUS_URI_SUFFIX = "/status";

	protected DeleteMyAvailabilityAsyncTask(Database database, Context context,
			String jid) {
		super(context, USERS_URI_SUFFIX + jid + STATUS_URI_SUFFIX);
	}

	@Override
	public String call() throws Exception {
		// Execute the PUT request
		super.call();

		// TODO: Try to parse the resulting JSON
		Log.e("DeleteMyProposalAsyncTask.call", "Should parse "
				+ responseString);

		return null;
	}
}

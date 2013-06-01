package com.hangapp.newandroid.network.rest;

import android.content.Context;
import android.util.Log;

import com.hangapp.newandroid.database.Database;

public final class DeleteMyAvailabilityAsyncTask extends BaseDeleteRequestAsyncTask {
	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String STATUS_URI_SUFFIX = "/status";

	private Database database;

	protected DeleteMyAvailabilityAsyncTask(Database database, Context context,
			String jid) {
		super(context, USERS_URI_SUFFIX + jid + STATUS_URI_SUFFIX);

		// Set dependencies
		this.database = database;
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

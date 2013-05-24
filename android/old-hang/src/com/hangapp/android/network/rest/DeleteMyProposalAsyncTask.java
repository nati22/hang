package com.hangapp.android.network.rest;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.DefaultUser;

public class DeleteMyProposalAsyncTask extends
		BaseDeleteRequestAsyncTask<String> {
	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String STATUS_URI_SUFFIX = "/proposal";

	// @Inject
	private DefaultUser defaultUser;

	protected DeleteMyProposalAsyncTask(Context context, String jid) {
		super(context, USERS_URI_SUFFIX + jid + STATUS_URI_SUFFIX);

		// Instantiate dependencies
		defaultUser = DefaultUser.getInstance();

		// // Inject the fields of this POJO. RoboGuice field injection doesn't
		// // work on POJOs without this.
		// RoboGuice.getInjector(context).injectMembers(this);
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

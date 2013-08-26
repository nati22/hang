package com.hangapp.android.network.rest;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.Database;

public class DeleteProposalSeenAsyncTask extends
		BaseDeleteRequestAsyncTask<String> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String PROPOSAL_SEEN_URI_SUFFIX = "/proposal/seen";

	private RestClient restClient;

	protected DeleteProposalSeenAsyncTask(Database database, Context context,
			String myJid, String targetJid) {
		super(context, USERS_URI_SUFFIX + myJid + PROPOSAL_SEEN_URI_SUFFIX
				+ "?target=" + targetJid);

		this.restClient = new RestClientImpl(database, context);
	}

	@Override
	public String call() throws Exception {
		// Execute the DELETE request
		super.call();

		Log.i("DeleteProposalSeenAsyncTask.call()", "Response string: \""
				+ responseString + ".\"");

		return null;
	}

}

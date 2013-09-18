package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

class PutProposalSeenAsyncTask extends BasePutRequestAsyncTask<String> {
	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String PROPOSAL_SEEN_URI_SUFFIX = "/proposal/seen";

	protected PutProposalSeenAsyncTask(Context context, String jid,
			List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + jid + PROPOSAL_SEEN_URI_SUFFIX,
				parameters);

	}

	@Override
	public String call() throws Exception {
		// Execute the PUT Request
		super.call();

		Log.i("PutProposalSeenAsyncTask.call()", "Response string: \""
				+ responseString + ".\"");
		return null;
	}

}

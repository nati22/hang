package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.model.Proposal;

class PutProposalAsyncTask extends BasePutRequestAsyncTask<Proposal> {
	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String PROPOSAL_URI_SUFFIX = "/proposal";

	protected PutProposalAsyncTask(Context context, String jid,
			List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + jid + PROPOSAL_URI_SUFFIX, parameters);
	}

	@Override
	public Proposal call() throws Exception {
		// Execute the PUT request
		super.call();

		// TODO: Try to parse the resulting JSON
		Log.e("PutProposalAsyncTask.call", "Should parse " + responseString);

		return null;
	}
}

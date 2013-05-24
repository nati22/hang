package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.Proposal;

public class SetProposalAsyncTask extends BasePutRequestAsyncTask<Proposal> {
	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String STATUS_URI_SUFFIX = "/proposal";

	// @Inject
	private DefaultUser defaultUser;

	protected SetProposalAsyncTask(Context context, String jid,
			List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + jid + STATUS_URI_SUFFIX, parameters);

		// Instantiate dependencies.
		defaultUser = DefaultUser.getInstance();

		// // Inject the fields of this POJO. RoboGuice field injection doesn't
		// // work on POJOs without this.
		// RoboGuice.getInjector(context).injectMembers(this);
	}

	@Override
	public Proposal call() throws Exception {
		// Execute the PUT request
		super.call();

		// TODO: Try to parse the resulting JSON
		Log.e("SetProposalAsyncTask.call", "Should parse " + responseString);

		return null;
	}
}

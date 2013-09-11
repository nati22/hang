package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.model.User;

class AddBroadcastAsyncTask extends BasePutRequestAsyncTask<User> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String BROADCAST_URI_SUFFIX = "/broadcast";

	private RestClient restClient;

	protected AddBroadcastAsyncTask(RestClient restClient, Context context,
			String jid, List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + jid + BROADCAST_URI_SUFFIX,
				parameters);

		// Inject dependencies.
		this.restClient = restClient;
	}

	@Override
	public User call() throws Exception {
		// Execute the PUT request
		super.call();

		Log.i("AddBroadcasteeAsyncTask", responseString);

		return null;
	}

	@Override
	protected void onSuccess(User me) throws Exception {
		restClient.getMyData();
	}
}

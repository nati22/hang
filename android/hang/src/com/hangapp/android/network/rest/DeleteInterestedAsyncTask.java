package com.hangapp.android.network.rest;

import android.content.Context;
import android.util.Log;

class DeleteInterestedAsyncTask extends BaseDeleteRequestAsyncTask<String> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String INTERESTED_URI_SUFFIX = "/proposal/interested";

	private RestClient restClient;

	protected DeleteInterestedAsyncTask(RestClient restClient,
			Context context, String myJid, String targetJid) {
		super(context, USERS_URI_SUFFIX + myJid + INTERESTED_URI_SUFFIX
				+ "?target=" + targetJid);

		this.restClient = restClient;
	}

	@Override
	public String call() throws Exception {
		// Execute the DELETE request
		super.call();

		Log.i("DeleteInterestedAsyncTask", "Response string: " + responseString);

		return null;
	}

	protected void onSuccess(String x) throws Exception {
		restClient.getMyData();
	}
}

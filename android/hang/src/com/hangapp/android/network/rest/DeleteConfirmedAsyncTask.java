package com.hangapp.android.network.rest;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.Database;
import com.hangapp.android.network.xmpp.XMPP;

class DeleteConfirmedAsyncTask extends BaseDeleteRequestAsyncTask<String> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String CONFIRMED_URI_SUFFIX = "/proposal/confirmed";

	private RestClient restClient;
	private XMPP xmpp;

	protected DeleteConfirmedAsyncTask(Database database,
			RestClient restClient, XMPP xmpp, Context context, String myJid,
			String targetJid) {
		super(context, USERS_URI_SUFFIX + myJid + CONFIRMED_URI_SUFFIX
				+ "?target=" + targetJid);

		this.restClient = restClient;
	}

	@Override
	public String call() throws Exception {
		// Execute the DELETE request
		super.call();

		Log.i("DeleteConfirmedAsyncTask", "Response string: " + responseString);

		return null;
	}

	protected void onSuccess(String x) throws Exception {
		restClient.getMyData(xmpp);
	}

}

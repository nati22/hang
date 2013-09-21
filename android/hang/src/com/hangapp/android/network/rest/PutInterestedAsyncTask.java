package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.Database;

class PutInterestedAsyncTask extends BasePutRequestAsyncTask<String> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String INTERESTED_URI_SUFFIX = "/proposal/interested";

	protected PutInterestedAsyncTask(Database database, Context context,
			String jid, List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + jid + INTERESTED_URI_SUFFIX, parameters);

	}

	@Override
	public String call() throws Exception {
		// Execute the PUT request
		super.call();

		Log.e("PutInterestedAsyncTask", "Response string: " + responseString);

		return null;
	}

	protected void onSuccess(String x) throws Exception {
		Database db = Database.getInstance();
		Log.i("REMOVE THIS CODE", "friend interested list = "
				+ db.getIncomingUser("592674933").getProposal().getInterested()
						.toString());
	}

}

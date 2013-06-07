package com.hangapp.newandroid.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

import com.hangapp.newandroid.database.Database;

public class SetInterestedAsyncTask extends BasePutRequestAsyncTask<String> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String INTERESTED_URI_SUFFIX = "/proposal/interested";

	protected SetInterestedAsyncTask(Database database, Context context,
			String jid, List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + jid + INTERESTED_URI_SUFFIX, parameters);

	}

	@Override
	public String call() throws Exception {
		// Execute the PUT request
		super.call();

		Log.e("SetInterestedAsyncTask", "Response string: " + responseString);

		return null;
	}

}

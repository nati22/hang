package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.Database;

class PutConfirmedAsyncTask extends BasePutRequestAsyncTask<String> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String CONFIRMED_URI_SUFFIX = "/proposal/confirmed";

	protected PutConfirmedAsyncTask(Database database, Context context,
			String jid, List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + jid + CONFIRMED_URI_SUFFIX,
				parameters);

	}

	@Override
	public String call() throws Exception {
		// Execute the PUT request
		super.call();

		Log.e("PutConfirmedAsyncTask", "Response string: " + responseString);

		return null;
	}

}

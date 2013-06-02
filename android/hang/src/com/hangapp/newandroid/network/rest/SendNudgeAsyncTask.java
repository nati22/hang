package com.hangapp.newandroid.network.rest;

import java.util.List;

import android.content.Context;
import android.util.Log;

public class SendNudgeAsyncTask extends
		BasePutRequestAsyncTask<String> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String NUDGE_URI_SUFFIX = "/nudge";

	public SendNudgeAsyncTask(Context context, String jid, List parameters) {
		super(context, USERS_URI_SUFFIX + jid + NUDGE_URI_SUFFIX, parameters);
	}

	public String call() throws Exception {
		// Execute the PUT request
		super.call();

		Log.d("SendNudgeAsyncTask.call", "Response string: " + responseString);

		return null;
	}

}

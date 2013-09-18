package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;


import android.content.Context;
import android.util.Log;

class PostNudgeAsyncTask extends BasePostRequestAsyncTask<String> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String NUDGE_URI_SUFFIX = "/nudge";

	/**
	 * 
	 * @param context 
	 * @param jid The sender's jid
	 * @param parameters Mapping from Keys.NUDGEE_JID to the recipient's jid
	 */
	public PostNudgeAsyncTask(Context context, String jid,
			List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + jid + NUDGE_URI_SUFFIX, parameters);
	}

	public String call() throws Exception {
		// Execute the POST request
		super.call();

		Log.d("PostNudgeAsyncTask.call", "Response string: " + responseString);

		return null;
	}

}

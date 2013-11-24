package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import com.hangapp.android.database.Database;

import android.content.Context;
import android.util.Log;

public class PutChatNotificationAsyncTask extends
		BasePutRequestAsyncTask<String> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String CHAT_URI_SUFFIX = "/proposal/chat";

	protected PutChatNotificationAsyncTask(Context context,
			List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + Database.getInstance().getMyJid()
				+ CHAT_URI_SUFFIX, parameters);
	}

	@Override
	public String call() throws Exception {
		// Execute the PUT request
		super.call();

		Log.e("PutChatNotificationAsyncTask", "Response string: "
				+ responseString);

		return null;
	}

}

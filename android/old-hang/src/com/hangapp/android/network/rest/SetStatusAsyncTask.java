package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.Status;

public class SetStatusAsyncTask extends BasePutRequestAsyncTask<Status> {

	private static final String USERS_URI_SUFFIX = "/users/";
	private static final String STATUS_URI_SUFFIX = "/status";

	// @Inject
	private DefaultUser defaultUser;

	protected SetStatusAsyncTask(Context context, String jid,
			List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + jid + STATUS_URI_SUFFIX, parameters);

		// Instantiate dependencies.
		defaultUser = DefaultUser.getInstance();

		// // Inject the fields of this POJO. RoboGuice field injection doesn't
		// // work on POJOs without this.
		// RoboGuice.getInjector(context).injectMembers(this);
	}

	@Override
	public Status call() throws Exception {
		// Execute the PUT request
		super.call();

		// TODO: Try to parse the resulting JSON
		Log.e("SetStatusAsyncTask", "Should parse " + responseString);

		return null;
	}

}

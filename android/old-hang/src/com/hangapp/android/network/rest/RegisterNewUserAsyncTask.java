package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.User;

public class RegisterNewUserAsyncTask extends BasePutRequestAsyncTask<User> {
	private static final String USERS_URI_SUFFIX = "/users/";

	// @Inject
	private DefaultUser defaultUser;

	protected RegisterNewUserAsyncTask(Context context, User newUser,
			List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + newUser.getJid(), parameters);

		defaultUser = DefaultUser.getInstance();

		// // Inject the fields of this POJO. RoboGuice field injection doesn't
		// // work on POJOs without this.
		// RoboGuice.getInjector(context).injectMembers(this);
	}

	@Override
	public User call() throws Exception {
		// Execute the PUT request
		super.call();

		// TODO: Try to parse the resulting JSON
		Log.e("RegisterNewUserAsyncTask", "Should parse " + responseString);

		return null;
	}
}

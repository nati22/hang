package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.User;
import com.hangapp.android.util.Keys;

final class PutUserAsyncTask extends BasePutRequestAsyncTask<User> {
	private static final String USERS_URI_SUFFIX = "/users/";

	private Database database;
	private GoogleCloudMessaging gcm;
	private SharedPreferences prefs;
	private String newUserJid;

	protected PutUserAsyncTask(Database database,
			GoogleCloudMessaging gcm, SharedPreferences prefs, Context context,
			User newUser, List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + newUser.getJid(), parameters);

		// Reference dependencies.
		this.database = database;
		this.gcm = gcm;
		this.prefs = prefs;

		this.newUserJid = newUser.getJid();
	}

	@Override
	public User call() throws Exception {
		if (prefs.getString(Keys.REGISTRATION_ID, null) == null) {
			// Query GCM for a registration id.
			String registrationId = gcm.register(RestClientImpl.GCM_SENDER_ID);
/*			Log.i("PutUserAsyncTask", "GCM Registration ID: " + registrationId);
*/
			parameters.add(new BasicNameValuePair(Keys.REGISTRATION_ID,
					registrationId));
		}

		// Execute the PUT request
		super.call();

		// Parse the response from the PUT request.
/*		Log.d("PutUserAsyncTask.call()", responseString);
*/		User me = User.parseUser(responseString);

		return me;
	}

	@Override
	protected void onSuccess(User me) throws Exception {
		super.onSuccess(me);

		database.setMyUserData(me.getJid(), me.getFirstName(), me.getLastName());

		// If the user was successfully saved into the database, directly
		// execute a GetMyDataAsyncTask call.
		new GetMyDataAsyncTask(database, context, newUserJid).execute();
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		Log.e("NewUserAsynctask", e.getMessage());
	}
}

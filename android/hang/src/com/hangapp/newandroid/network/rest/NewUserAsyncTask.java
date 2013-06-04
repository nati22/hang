package com.hangapp.newandroid.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.util.Keys;

final class NewUserAsyncTask extends BasePutRequestAsyncTask<User> {
	private static final String USERS_URI_SUFFIX = "/users/";

	private Database database;
	private GoogleCloudMessaging gcm;
	private SharedPreferences prefs;
	private String newUserJid;

	protected NewUserAsyncTask(Database database, GoogleCloudMessaging gcm,
			SharedPreferences prefs, Context context, User newUser,
			List<NameValuePair> parameters) {
		super(context, USERS_URI_SUFFIX + newUser.getJid(), parameters);

		// Reference dependencies.
		this.database = database;
		this.gcm = gcm;
		this.prefs = prefs;

		// Save this "new" user's Jid to start the XMPP service later.
		this.newUserJid = newUser.getJid();
	}

	@Override
	public User call() throws Exception {
		if (prefs.getString(Keys.REGISTRATION_ID, null) == null) {
			// Query GCM for a registration id.
			String registrationId = gcm.register(RestClientImpl.GCM_SENDER_ID);
			Log.i("NewUserAsyncTask", "GCM Registration ID: " + registrationId);

			parameters.add(new BasicNameValuePair(Keys.REGISTRATION_ID,
					registrationId));
		}

		// TODO: Start the XMPP service, regardless of whether or not you already
		// exist on our App Engine server.
		// TODO: This is an "unbound service". That is to say, the service that
		// is created this way exists forever, until the application is stopped.
		// If a user logs out and then logs back in, there will be two XMPP
		// services in existence. Fix this.
//		XMPP.getInstance().attemptToConnectAndLogin(newUserJid);
		// Intent xmppServiceIntent = new Intent(context, XMPP.class);
		// xmppServiceIntent.putExtra(Keys.JID, newUserJid);
		// context.startService(xmppServiceIntent);

		// Execute the PUT request
		super.call();

		// Parse the response from the PUT request.
		Log.d("NewUserAsyncTask.call()", responseString);
		User myUserObject = User.parseUserName(responseString);

		return myUserObject;
	}

	@Override
	protected void onSuccess(User myUserObject) throws Exception {
		super.onSuccess(myUserObject);

		database.setMyUserData(myUserObject.getJid(),
				myUserObject.getFirstName(), myUserObject.getLastName());
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		Log.e("NewUserAsynctask", e.getMessage());
	}
}

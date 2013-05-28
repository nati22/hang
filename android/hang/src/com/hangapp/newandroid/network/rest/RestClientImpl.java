package com.hangapp.newandroid.network.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hangapp.newandroid.database.UserDatabase;
import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.model.Proposal;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.Keys;

public final class RestClientImpl implements RestClient {

	static final String GCM_SENDER_ID = "369775641911";
	static final String GCM_API_KEY = "AIzaSyAJtklyMjzyHNfRC2Ratkoh3ziFodaZWZU";

	private UserDatabase database;
	private Context context;
	private SharedPreferences prefs;
	private GoogleCloudMessaging gcm;

	public RestClientImpl(UserDatabase database, Context context) {
		this.context = context;

		// Set dependencies.
		this.database = database;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		gcm = GoogleCloudMessaging.getInstance(context);
	}

	@Override
	public void registerNewUser(User newUser) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.FIRST_NAME, newUser
				.getFirstName()));
		parameters.add(new BasicNameValuePair(Keys.LAST_NAME, newUser
				.getLastName()));

		new NewUserAsyncTask(database, gcm, prefs, context, newUser, parameters)
				.execute();

		
		// TODO: Send a tickle to my recipients.
	}

	@Override
	public void updateMyAvailability(Availability status) {

		String jid = database.getMyJid();

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.AVAILABILITY_COLOR, status
				.getColor().toString()));
		parameters.add(new BasicNameValuePair(
				Keys.AVAILABILITY_EXPIRATION_DATE, status.getExpirationDate()
						.toGMTString()));

		new SetStatusAsyncTask(database, context, jid, parameters).execute();

		// TODO: Send a tickle to my recipients
	}

	@Override
	public void updateMyProposal(Proposal proposal) {

		String jid = database.getMyJid();

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.PROPOSAL_DESCRIPTION,
				proposal.getDescription()));
		parameters.add(new BasicNameValuePair(Keys.PROPOSAL_LOCATION, proposal
				.getLocation()));
		parameters.add(new BasicNameValuePair(Keys.PROPOSAL_TIME, proposal
				.getStartTime().toGMTString()));

		if (!proposal.getInterested().isEmpty()) {
			for (User interestedUser : proposal.getInterested()) {
				parameters.add(new BasicNameValuePair(Keys.PROPOSAL_INTERESTED,
						interestedUser.getJid().toString()));
			}
		}

		if (!proposal.getConfirmed().isEmpty()) {
			for (User confirmedUser : proposal.getConfirmed()) {
				parameters.add(new BasicNameValuePair(Keys.PROPOSAL_CONFIRMED,
						confirmedUser.getJid().toString()));
			}
		}

		new SetProposalAsyncTask(database, context, jid, parameters).execute();

		// TODO: Send a tickle to my recipients
	}

	@Override
	public void deleteMyProposal() {
		String jid = database.getMyJid();

		new DeleteMyProposalAsyncTask(database, context, jid).execute();

		// TODO: Send a tickle to my recipients
	}

	@Override
	public void getMyData() {

		String jid = database.getMyJid();

		if (jid == null) {
			HangLog.toastE(context, "RestClientImpl",
					"Cannot refresh; haven't retrieved Facebook ID yet");
		} else {
			HangLog.toastD(context, "RestClientImpl", "Refreshing...");
		}

		// TODO: GetUserData is currently hard-coded to use "123" as the JID.
		new GetUserDataAsyncTask(database, context, jid).execute();
	}

	@Override
	public void addBroadcastee(String broadcasteeJID) {

		String myJid = database.getMyJid();

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.TARGET, broadcasteeJID));

		new AddBroadcastAsyncTask(database, this, context, myJid, parameters)
				.execute();

	}
}

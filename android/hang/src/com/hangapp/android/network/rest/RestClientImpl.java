package com.hangapp.android.network.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;
import com.hangapp.android.util.Keys;

public final class RestClientImpl implements RestClient {

	static final String GCM_SENDER_ID = "369775641911";
	static final String GCM_API_KEY = "AIzaSyAJtklyMjzyHNfRC2Ratkoh3ziFodaZWZU";

	private Database database;
	private Context context;
	private SharedPreferences prefs;
	private GoogleCloudMessaging gcm;

	public RestClientImpl(Database database, Context context) {
		this.context = context;

		// Set dependencies.
		this.database = database;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// TODO: Inject this on a method-by-method basis.
		gcm = GoogleCloudMessaging.getInstance(context);
	}

	@Override
	public void registerNewUser(User newUser) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.FIRST_NAME, newUser
				.getFirstName()));
		parameters.add(new BasicNameValuePair(Keys.LAST_NAME, newUser
				.getLastName()));

		new PutUserAsyncTask(database, gcm, prefs, context, newUser,
				parameters).execute();

		// TODO: Send a tickle to my recipients.
	}

	@Override
	public void updateMyAvailability(Availability status) {
		String jid = database.getMyJid();

		if (status != null) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();

			parameters.add(new BasicNameValuePair(Keys.AVAILABILITY_COLOR,
					status.getStatus().toString()));
			parameters.add(new BasicNameValuePair(
					Keys.AVAILABILITY_EXPIRATION_DATE, status
							.getExpirationDate().toString()));
			parameters.add(new BasicNameValuePair(Keys.STATUS_TEXT, status
					.getDescription()));
			new PutAvailabilityAsyncTask(context, jid, parameters).execute();
		} else {
			new DeleteMyAvailabilityAsyncTask(database, context, jid).execute();
		}

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
		parameters.add(new BasicNameValuePair(Keys.PROPOSAL_START_TIME,
				proposal.getStartTime().toString()));

		if (!proposal.getInterested().isEmpty()) {
			for (String interestedUserJid : proposal.getInterested()) {
				parameters.add(new BasicNameValuePair(Keys.PROPOSAL_INTERESTED,
						interestedUserJid));
			}
		}

		if (!proposal.getConfirmed().isEmpty()) {
			for (String confirmedUserJid : proposal.getConfirmed()) {
				parameters.add(new BasicNameValuePair(Keys.PROPOSAL_CONFIRMED,
						confirmedUserJid));
			}
			
		}

		new PutProposalAsyncTask(context, jid, parameters).execute();

		// TODO: Send a tickle to my recipients
	}

	@Override
	public void deleteMyProposal() {
		String jid = database.getMyJid();

		new DeleteMyProposalAsyncTask(database, context, jid).execute();
		
		// Delete User info on Firebase
		String chatFirebaseUrl = Keys.CHATS_URL + jid;
		Firebase chatFirebase = new Firebase(chatFirebaseUrl);
		chatFirebase.setValue(null);
	}

	@Override
	public void getMyData() {
		String jid = database.getMyJid();

		if (jid == null || jid.equals("") || jid.equals("null")) {
			Log.e("RestClientImpl",
					"Cannot refresh; haven't retrieved Facebook ID yet");
			return;
		} else {
			Log.i("RestClientImpl", "Refreshing...");
		}

		new GetMyDataAsyncTask(database, context, jid).execute();
	}

	@Override
	public void addBroadcastees(List<String> broadcasteeJIDs) {
		String myJid = database.getMyJid();

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();

		for (String broadcasteeJID : broadcasteeJIDs) {
			parameters.add(new BasicNameValuePair(Keys.TARGET, broadcasteeJID));
		}

		new PutBroadcastsAsyncTask(context, this, myJid,
				parameters).execute();
	}

	@Override
	public void sendNudge(String targetJid) {
		String myJid = database.getMyJid();

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.TARGET, targetJid));

		new PostNudgeAsyncTask(context, myJid, parameters).execute();
	}

	@Override
	public void deleteBroadcastees(List<String> broadcasteeJIDs) {
		
		// This StringBuilder will put all the jids into a HTTP ready format
		StringBuilder builder = new StringBuilder();
		for (String broadcastee : broadcasteeJIDs) {
			builder.append("?target=" + broadcastee);
		}
		
		new DeleteBroadcastsAsyncTask(database, this, context,
				database.getMyJid(), builder.toString()).execute();
	}

	@Override
	public void setInterested(String broadcasterJid) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.TARGET, broadcasterJid));

		new PutInterestedAsyncTask(database, context, database.getMyJid(),
				parameters).execute();
	}

	@Override
	public void setConfirmed(String broadcasterJid) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.TARGET, broadcasterJid));

		new PutConfirmedAsyncTask(database, context, database.getMyJid(),
				parameters).execute();
	}
	
	@Override
	public void sendChatNotification(List<String> targets, String hostJid) {
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();

		for (String target : targets) {
			parameters.add(new BasicNameValuePair(Keys.TARGET, target));
		}
		parameters.add(new BasicNameValuePair(Keys.HOST_JID, hostJid));
		
		new PutChatNotificationAsyncTask(context, parameters).execute();
	}

	@Override
	public void deleteInterested(String broadcasterJid) {
		new DeleteInterestedAsyncTask(this, context, database.getMyJid(),
				broadcasterJid).execute();
	}

	@Override
	public void deleteConfirmed(String broadcasterJid) {

		new DeleteConfirmedAsyncTask(database, this, context,
				database.getMyJid(), broadcasterJid).execute();
	}

	@Override
	public void setSeenProposal(String broadcasterJid) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.TARGET, broadcasterJid));

		new PutProposalSeenAsyncTask(context, database.getMyJid(), parameters)
				.execute();
	}

	@Override
	public void deleteSeenProposal(String broadcasterJid) {

		new DeleteProposalSeenAsyncTask(this, context, database.getMyJid(),
				broadcasterJid);
	}

}

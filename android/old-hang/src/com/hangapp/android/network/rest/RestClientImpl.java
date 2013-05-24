package com.hangapp.android.network.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.Status;
import com.hangapp.android.model.User;
import com.hangapp.android.util.HangLog;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.Utils;

//@ContextSingleton
public class RestClientImpl implements RestClient {

	// @Inject
	private DefaultUser defaultUser;
	// @Inject
	private Context context;
	// @Inject
	private SharedPreferences prefs;

	public RestClientImpl(Context context) {
		this.context = context;

		// Instantiate dependencies.
		defaultUser = DefaultUser.getInstance();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	public void registerUser(User newUser) {
		// String jid = Utils.getDefaultUserJID(context);

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.FIRST_NAME, newUser
				.getFirstName()));
		parameters.add(new BasicNameValuePair(Keys.LAST_NAME, newUser
				.getLastName()));

		new RegisterNewUserAsyncTask(context, newUser, parameters).execute();

		// TODO: Send a tickle to my recipients.
	}

	@Override
	public void updateMyStatus(Status status) {

		String jid = Utils.getDefaultUserJID(context);

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.STATUS_COLOR, status
				.getColor().toString()));
		parameters.add(new BasicNameValuePair(Keys.STATUS_EXPIRATION_DATE,
				status.getExpirationDate().toGMTString()));

		new SetStatusAsyncTask(context, jid, parameters).execute();

		// TODO: Send a tickle to my recipients
	}

	@Override
	public void updateMyProposal(Proposal proposal) {

		String jid = Utils.getDefaultUserJID(context);

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Keys.PROPOSAL_DESCRIPTION,
				proposal.getDescription()));
		parameters.add(new BasicNameValuePair(Keys.PROPOSAL_LOCATION, proposal
				.getLocation()));
		parameters.add(new BasicNameValuePair(Keys.PROPOSAL_TIME, proposal
				.getStartTime().toGMTString()));

		for (User interestedUser : proposal.getInterested()) {
			parameters.add(new BasicNameValuePair(Keys.PROPOSAL_INTERESTED,
					interestedUser.getJid().toString()));
		}

		for (User confirmedUser : proposal.getConfirmed()) {
			parameters.add(new BasicNameValuePair(Keys.PROPOSAL_CONFIRMED,
					confirmedUser.getJid().toString()));
		}

		new SetProposalAsyncTask(context, jid, parameters).execute();

		// TODO: Send a tickle to my recipients
	}

	@Override
	public void deleteMyProposal() {
		String jid = Utils.getDefaultUserJID(context);

		new DeleteMyProposalAsyncTask(context, jid).execute();

		// TODO: Send a tickle to my recipients
	}

	@Override
	public void updateSomeoneElsesProposal(Integer hostUserJID,
			Proposal proposal) {
		HangLog.toastE(context, "RestClientImpl.updateSomeoneElsesProposal",
				"updateSomeoneElsesProposal() not yet implemented");
	}

	@Override
	public void getUserData() {

		String jid = Utils.getDefaultUserJID(context);

		// TODO: GetUserData is currently hard-coded to use "123" as the JID.
		new GetUserDataAsyncTask(context, jid).execute();
	}

	@Override
	public void addBroadcastee(Integer broadcasteeJID) {
		HangLog.toastE(context, "RestClientImpl.addBroadcastee",
				"addBroadcastee() not yet implemented");
	}
}

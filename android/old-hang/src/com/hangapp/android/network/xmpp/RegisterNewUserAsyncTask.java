//package com.hangapp.android.network.xmpp;
//
//import org.jivesoftware.smack.AccountManager;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//
//import android.content.Context;
//
//import com.hangapp.android.network.rest.parse.BaseParseAsyncTask;
//import com.hangapp.android.util.Log;
//
//public class RegisterNewUserAsyncTask extends BaseParseAsyncTask<String> {
//
//	Integer jid;
//	boolean successfullyCreatedAccount = false;
//	XMPPConnection xmppConnection;
//
//	protected RegisterNewUserAsyncTask(Context context, Integer jid,
//			XMPPConnection xmppConnection) {
//		super(context);
//		this.jid = jid;
//		this.xmppConnection = xmppConnection;
//	}
//
//	@Override
//	public String call() throws Exception {
//		if (!xmppConnection.isConnected()) {
//			Log.e("Can't register user:  Jabber isn't connected");
//			return null;
//		}
//
//		// Create the account
//		AccountManager accountManager = new AccountManager(xmppConnection);
//		try {
//			Log.d("Attempting to register user: " + jid);
//			// Every user's Password is THE SAME THING as his JID.
//			accountManager.createAccount(jid.toString(), jid.toString());
//			successfullyCreatedAccount = true;
//		} catch (XMPPException e) {
//			Log.e("XMPP Error: " + e.getMessage());
//		}
//
//		return null;
//	}
//
//
//	@Override
//	protected void onSuccess(String result) {
//		if (successfullyCreatedAccount) {
//			Log.d("Successfully created a new jabber account");
//		} else {
//			Log.e("Did NOT successfully create a new Jabber account, "
//					+ "won't create GAE user");
//		}
//
//		Log.i("Attempting to login...");
////		new XMPP.LoginTask(getContext(), jid.toString()).execute();
//		new LoginAsyncTask(context, jid.toString(), xmppConnection).execute();
//	}
//
//}

package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

public class RegisterJabberUserAsyncTask extends
		BaseXmppAsyncTask<XMPPConnection> {

	protected RegisterJabberUserAsyncTask(String myJid,
			XMPPConnection xmppConnection, Context context) {
		super(myJid, xmppConnection, context);
	}

	@Override
	public XMPPConnection call() throws Exception {
		super.call();

		AccountManager accountManager = xmppConnection.getAccountManager();
		accountManager.createAccount(myJid, myJid);

		return xmppConnection;
	}

	@Override
	protected void onSuccess(XMPPConnection t) throws Exception {
		super.onSuccess(t);

		new LoginAsyncTask(myJid, xmppConnection, context).execute();
	}
}

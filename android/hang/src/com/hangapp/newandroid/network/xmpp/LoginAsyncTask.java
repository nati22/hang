package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

import com.hangapp.newandroid.util.HangLog;

public class LoginAsyncTask extends BaseXmppAsyncTask<XMPPConnection> {

	protected LoginAsyncTask(String myJid, XMPPConnection xmppConnection,
			Context context) {
		super(myJid, xmppConnection, context);
	}

	@Override
	public XMPPConnection call() throws Exception {
		xmppConnection.login(myJid, myJid);

		if (xmppConnection.isAuthenticated()) {
			return xmppConnection;
		} else {
			throw new Exception("XMPPConnection did not authenticate");
		}
	}

	@Override
	protected void onSuccess(XMPPConnection t) throws Exception {
		super.onSuccess(t);

		HangLog.toastD(context, "LoginAsyncTask.onSuccess",
				"XMPPConnection is authenticated");
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		HangLog.toastE(context, "LoginAsyncTask.onException",
				"XMPPConnection did not authenticate: attempting to register");
		
		new RegisterJabberUserAsyncTask(myJid, xmppConnection, context)
				.execute();
	}
}

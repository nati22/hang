package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

import com.hangapp.newandroid.util.HangLog;

public class LoginAsyncTask extends BaseXmppAsyncTask<XMPPConnection> {

	private String username;
	private String password;

	protected LoginAsyncTask(String username, String password,
			XMPPConnection xmppConnection, Context context) {
		super(xmppConnection, context);
		this.username = username;
		this.password = password;
	}

	@Override
	public XMPPConnection call() throws Exception {
		xmppConnection.login(username, password);

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
}

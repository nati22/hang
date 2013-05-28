package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

import com.hangapp.newandroid.util.HangLog;

public class LogoutAsyncTask extends BaseXmppAsyncTask<Boolean> {

	protected LogoutAsyncTask(String myJid, XMPPConnection xmppConnection,
			Context context) {
		super(myJid, xmppConnection, context);
	}

	@Override
	public Boolean call() throws Exception {
		xmppConnection.disconnect();

		if (!xmppConnection.isConnected()) {
			return true;
		} else {
			throw new Exception("XMPPConnection did not disconnect");
		}
	}

	@Override
	protected void onSuccess(Boolean t) throws Exception {
		super.onSuccess(t);

		HangLog.toastD(context, "LogoutAsyncTask.onSuccess",
				"XMPPConnection disconnected");
	}
}

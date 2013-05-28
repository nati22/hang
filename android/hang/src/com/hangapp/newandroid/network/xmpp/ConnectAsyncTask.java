package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

import com.hangapp.newandroid.util.HangLog;

public class ConnectAsyncTask extends BaseXmppAsyncTask<XMPPConnection> {

	private static MyConnectionListener mConnectionListener = new MyConnectionListener();

	protected ConnectAsyncTask(String myJid, XMPPConnection xmppConnection,
			Context context) {
		super(myJid, xmppConnection, context);
	}

	@Override
	public XMPPConnection call() throws Exception {
		if (xmppConnection.isConnected()) {
			throw new Exception(
					"Will not connect to XMPPManager: Already connected");
		}

		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		xmppConnection.connect();

		if (xmppConnection.isConnected()) {
			return xmppConnection;
		} else {
			throw new Exception("XMPPConnection did NOT connect to the server.");
		}
	}

	@Override
	protected void onSuccess(XMPPConnection result) {
		HangLog.toastD(context, "ConnectAsyncTask.onSuccess",
				"Connected to XMPP server.");

		// If the Connection succeeded, then add your ConnectionListener.
		result.removeConnectionListener(mConnectionListener);
		result.addConnectionListener(mConnectionListener);

		new LoginAsyncTask(myJid, xmppConnection, context).execute();
	}

}

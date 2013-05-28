package com.hangapp.android.network.xmpp;

import android.util.Log;

public class MyConnectionListener implements
		org.jivesoftware.smack.ConnectionListener {
	public void reconnectionSuccessful() {
		Log.d("ConnectAsyncTask.call", "XMPPConnection: reconnectionSuccessful");
	}

	public void reconnectionFailed(Exception e) {
		Log.e("ConnectAsyncTask.call", "XMPPConnection: reconnectionFailed: "
				+ e.getMessage());
	}

	public void reconnectingIn(int seconds) {
		Log.d("ConnectAsyncTask.call", "XMPPConnection: reconnectingIn "
				+ seconds + " seconds");
	}

	public void connectionClosedOnError(Exception e) {
		Log.e("ConnectAsyncTask.call",
				"XMPPConnection: connectionClosedOnError: " + e.getMessage());
	}

	public void connectionClosed() {
		Log.d("ConnectAsyncTask.call", "XMPPConnection: connectionClosed");
	}
}

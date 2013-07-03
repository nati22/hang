package com.hangapp.android.network.xmpp;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import android.util.Log;

/**
 * Our implementation of {@link XMPPConnection}'s {@link ConnectionListener}.
 * This is the class that handles the {@code XMPPConnection} closing,
 * reconnecting, etc.
 */
class MyConnectionListener implements ConnectionListener {

	@Override
	public void connectionClosed() {
		Log.i("MyConnectionListener", "XMPPConnection closed");
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		Log.e("MyConnectionListener",
				"XMPPConnection closed on error: " + e.getMessage());
	}

	@Override
	public void reconnectingIn(int seconds) {
		Log.i("MyConnectionListener", "Reconnecting in " + seconds + " seconds");
	}

	@Override
	public void reconnectionFailed(Exception e) {
		Log.e("MyConnectionListener", "Reconnection failed: " + e.getMessage());
	}

	@Override
	public void reconnectionSuccessful() {
		Log.i("MyConnectionListener", "Reconnection successful");
	}

}

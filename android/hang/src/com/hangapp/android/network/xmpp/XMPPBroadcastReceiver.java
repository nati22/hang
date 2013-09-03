package com.hangapp.android.network.xmpp;

import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.common.collect.Iterables;
import com.hangapp.android.util.Keys;

public class XMPPBroadcastReceiver extends BroadcastReceiver {

	public static final String XMPP_BROADCAST_RECEIVER = "com.hangapp";
	private Context context;
	private Intent intent;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		this.intent = intent;

		Log.i("XMPPBroadcastReceiver.onReceive()",
				"XMPP Broadcast Receiver started.");

		Toast.makeText(context,
				"Connected to chat. Joining Proposal Chatrooms...",
				Toast.LENGTH_SHORT).show();

		String myJid = intent.getStringExtra(Keys.JID);

		joinMucs(myJid);
	}

	protected void joinMucs(String myJid) {
		XMPP xmpp = XMPP.getInstance();

		// Cycle forever through the Mucs that you have to join.
		// For each Muc in our List<String> unjoined Mucs, attempt to
		// join it and remove it from the queue of unjoined Mucs.
		Iterable<String> infinite = Iterables.cycle(xmpp.mucsToJoin);

		for (Iterator<String> mucsToJoinIterator = infinite.iterator(); mucsToJoinIterator
				.hasNext();) {
			// If you try to join the muc without being connected, then
			// try to
			// connect again.
			if (!xmpp.xmppConnection.isConnected()) {
				Log.e("XMPPIntentService.login()", "Can't login: not connected");

				// Not connected, so attempt to connect.
				xmpp.connect(myJid, context);
				return;
			}

			// If you try to join Mucs without being logged in, then try
			// to
			// login again.
			if (!xmpp.xmppConnection.isAuthenticated()) {
				xmpp.login(myJid, context);
				return;
			}

			String mucToJoin = mucsToJoinIterator.next();
			boolean didJoinMuc = xmpp.joinMuc(mucToJoin, myJid);

			if (didJoinMuc) {
				Log.i("XMPPBroadcastReceiver#joinMucs()", "XMPP joined muc: "
						+ mucToJoin);
				mucsToJoinIterator.remove();
			}
		}

		Toast.makeText(context, "Joined all Proposal chatrooms",
				Toast.LENGTH_SHORT).show();
	}

}

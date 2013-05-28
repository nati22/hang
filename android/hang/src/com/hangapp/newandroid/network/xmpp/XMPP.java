package com.hangapp.newandroid.network.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.ConfigureProviderManager;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Context;
import android.util.Log;

import com.hangapp.newandroid.util.HangLog;

public class XMPP {
	private static XMPP instance = new XMPP();

	private static final String JABBER_SERVER_URL = "ec2-184-72-81-86.compute-1.amazonaws.com";
	private XMPPConnection xmppConnection;
	private Context context;

	private Map<String, MultiUserChat> mucs = new HashMap<String, MultiUserChat>();

	private XMPP() {

	}

	public static XMPP getInstance() {
		return instance;
	}

	public void initialize(Context context) {
		this.context = context;

		// Setup aSmack.
		SmackAndroid.init(context);
		ConfigureProviderManager.configureProviderManager();

		// Initialize the XMPPConnection itself. Point it to our EC2 server.
		xmppConnection = new XMPPConnection(JABBER_SERVER_URL);
	}

	public void attemptToConnectAndLogin(String myJid) {
		Log.i("XMPP.onStartCommand", "Connecting to XMPPManager...");

		if (xmppConnection == null) {
			HangLog.toastE(context, "XMPP.onStartCommand",
					"Fatal error: XMPPConnection was null");
			return;
		}

		if (!xmppConnection.isConnected()) {
			// Attempt to connect to the server.
			new ConnectAsyncTask(myJid, xmppConnection, context).execute();
		} else if (!xmppConnection.isAuthenticated()) {
			new LoginAsyncTask(myJid, xmppConnection, context).execute();
		} else {
			Log.e("XMPP.onStartCommand",
					"Requested a start of XMPP service, but user is already authenticated");
		}
	}

	public void joinMuc(String mucName, String myJid,
			PacketListener messageListener) {
		if (xmppConnection == null) {
			HangLog.toastE(context, "XMPP.joinMuc", "XMPPConnection was null, "
					+ "not joining MUC");
			return;
		} else if (!xmppConnection.isConnected()) {
			HangLog.toastE(context, "XMPP.joinMuc", "XMPPConnection was not "
					+ "connected, not joining MUC");
			return;
		} else if (!xmppConnection.isAuthenticated()) {
			HangLog.toastE(context, "XMPP.joinMuc",
					"XMPPConnection was not authenticated, "
							+ "not joining MUC");
			return;
		}

		MultiUserChat muc = mucs.get(mucName);

		if (muc == null) {
			muc = new MultiUserChat(xmppConnection, myJid + "@conference."
					+ XMPP.JABBER_SERVER_URL);
			mucs.put(mucName, muc);
		}

		new JoinMucAsyncTask(muc, mucName, myJid, messageListener,
				xmppConnection, context).execute();
	}

	public void sendMessage(String mucName, String message) {
		MultiUserChat muc = mucs.get(mucName);

		new SendMucMessageAsyncTask(muc, mucName, message, context).execute();
	}

	public void logout() {
		new LogoutAsyncTask(null, xmppConnection, context).execute();
	}
}

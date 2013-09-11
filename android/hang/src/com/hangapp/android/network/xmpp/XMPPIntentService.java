package com.hangapp.android.network.xmpp;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ConfigureProviderManager;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.hangapp.android.util.BaseApplication;
import com.hangapp.android.util.Keys;

/**
 * This helper class is an {@link IntentService}, and it handles all of our
 * background XMPP logic for us. You should interface with this class via the
 * {@link XMPP} class. <br />
 * <br />
 * It is driven by a constant queue of Intent messages sent from either
 * <ol>
 * the app (e.g. {@link BaseApplication}) or from
 * </ol>
 * <ol>
 * itself (e.g. once you connect, immediately send a new Intent to attempt to
 * login).
 * </ol>
 */
public final class XMPPIntentService extends IntentService {
	static final String XMPP_SERVER_URL = "ec2-184-72-81-86.compute-1.amazonaws.com";

	/**
	 * Hold a reference to the front-facing XMPP object.
	 */
	private static XMPP xmpp;

	/**
	 * Share a single {@link MyConnectionListener} object for the
	 * {@link XMPPConnection}, instead of instantiating a new one every time you
	 * connect to XMPP.
	 */
	private static MyConnectionListener mConnectionListener = new MyConnectionListener();

	public XMPPIntentService() {
		super("XMPPIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Instantiate your dependency on the front-facing XMPP object
		xmpp = XMPP.getInstance();

		// Figure out which message the Intent is sending.
		int message = intent.getIntExtra(Keys.MESSAGE, 0);

		// Pull out all extras. Sanity check them later, when you need them.
		String myJid = intent.getStringExtra(Keys.JID);

		// Start the correct method based on which message was passed into the
		// Intent.
		switch (message) {
		case Keys.XMPP_CONNECT:
			// Sanity check.
			if (myJid == null) {
				Log.e("XMPPIntentService", "Can't connect to XMPP: Null myJid");
				return;
			}

			connect(myJid);
			return;
		case Keys.XMPP_REGISTER:
			// Sanity check.
			if (myJid == null) {
				Log.e("XMPPIntentService", "Can't register to XMPP: Null myJid");
				return;
			}

			register(myJid);
			return;
		case Keys.XMPP_LOGIN:

			// Sanity check.
			if (myJid == null) {
				Log.e("XMPPIntentService", "Can't login to XMPP: Null myJid");
				return;
			}

			login(myJid);
			return;
		case Keys.XMPP_LOGOUT:
			logout();
			return;
		default:
			Log.e("XMPPPIntentService.onHandleIntent",
					"Unknown intent message code: " + message);
			return;
		}
	}

	protected void connect(String myJid) {
		Log.v("XMPPIntentService", "XMPPIntentService: connect()");

		// If this is the first time running, then initialize the XMPP
		// connection.
		if (xmpp.xmppConnection == null) {
			// Setup aSmack.
			SmackAndroid.init(getApplicationContext());
			ConfigureProviderManager.configureProviderManager();

			// Initialize the XMPPConnection itself. Point it to our EC2 server.
			xmpp.xmppConnection = new XMPPConnection(XMPP_SERVER_URL);
		}

		// If you're already connected, then just attempt to login.
		if (xmpp.xmppConnection.isConnected()) {
			Log.e("XMPPIntentService.connect()", "Already connected");

			// Already connected, so attempt to login.
			xmpp.login(myJid, getApplicationContext());
			return;
		}

		// Otherwise, attempt to connect to XMPP.
		try {
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			xmpp.xmppConnection.connect();
		} catch (XMPPException e) {
			Log.e("XMPPIntentService.connect()", e.getMessage());

			// Attempt to connect again?
			xmpp.connect(myJid, getApplicationContext());
			return;
		}

		if (xmpp.xmppConnection.isConnected()) {
			Log.d("XMPPIntentService.connect()", "Connected to XMPP");
			// If the Connection succeeded, then add your
			// ConnectionListener.
			xmpp.xmppConnection.removeConnectionListener(mConnectionListener);
			xmpp.xmppConnection.addConnectionListener(mConnectionListener);

			// Attempt to login.
			xmpp.login(myJid, getApplicationContext());
			return;
		} else {
			// If XMPPConnection isn't connected even after connect() passed,
			// then attempt to connect again.
			xmpp.connect(myJid, getApplicationContext());
			return;
		}
	}

	protected void register(String myJid) {
		Log.v("XMPPIntentService", "XMPPIntentService: register()");

		// If you try to login without being connected, then try to connect
		// again.
		if (!xmpp.xmppConnection.isConnected()) {
			Log.e("XMPPIntentService.register()", "Can't login: not connected");

			// Not connected, so attempt to connect.
			xmpp.connect(myJid, getApplicationContext());
			return;
		}

		// If you try to register when you're already authenticated, then you're
		// done -- you don't have to register because you're already logged in.
		if (xmpp.xmppConnection.isAuthenticated()) {
			Log.e("XMPPIntentService.register()",
					"Can't register: already authenticated");
			// Do nothing.
			return;
		}

		try {
			AccountManager accountManager = xmpp.xmppConnection
					.getAccountManager();
			accountManager.createAccount(myJid, myJid);
		} catch (XMPPException e) {
			Log.e("XMPPIntentService.register()",
					"Can't register new XMPP user: " + e.getMessage());
			xmpp.connect(myJid, getApplicationContext());
			return;
		}

	}

	protected void login(String myJid) {
		Log.v("XMPPIntentService", "XMPPIntentService: login()");

		// If you try to login without being connected, then try to connect
		// again.
		if (!xmpp.xmppConnection.isConnected()) {
			Log.e("XMPPIntentService.login()", "Can't login: not connected");

			// Not connected, so attempt to connect.
			xmpp.connect(myJid, getApplicationContext());
			return;
		}

		if (xmpp.xmppConnection.isAuthenticated()) {
			Log.e("XMPPIntentService.login()", "XMPP already authenticated");
			return;
		}

		try {
			Log.i("XMPPIntentService.login()", "XMPP: attempting to login.");
			xmpp.xmppConnection.login(myJid, myJid);
			Log.i("XMPPIntentService.login()", "XMPP: login attempt finished.");
		} catch (XMPPException e) {
			// Show the error. Attempt to register.
			Log.e("XMPPIntentService.login()",
					"XMPP: can't login: " + e.getMessage());
			register(myJid);
			return;
		}

		if (xmpp.xmppConnection.isAuthenticated()) {
			Log.d("XMPPIntentService.login()", "Logged into XMPP");
			// If login succeeded, then move onto attempting to join all of the
			// Mucs.
			// Send off a Broadcast to do this on the UI thread.
			if (xmpp.mucsToJoin.isEmpty()) {
				Log.v("XMPPIntentService.login()",
						"Won't join MUCs: All MUCs already joined");
				return;
			}

			Intent joinMucsBroadcastIntent = new Intent();
			joinMucsBroadcastIntent
					.setAction(XMPPBroadcastReceiver.XMPP_BROADCAST_RECEIVER);
			joinMucsBroadcastIntent.putExtra(Keys.JID, myJid);
			sendBroadcast(joinMucsBroadcastIntent);

			// joinMucs(myJid);
		} else {
			// If login failed, then start over from connect().
			Log.e("XMPPIntentService.login()", "XMPP login failed. connect");
			xmpp.connect(myJid, getApplicationContext());
		}
	}

	protected void logout() {
		Log.v("XMPPIntentService", "XMPPIntentService: logout()");

		// TODO
	}

}

package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ConfigureProviderManager;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.hangapp.newandroid.database.MessagesDataSource;
import com.hangapp.newandroid.util.BaseApplication;
import com.hangapp.newandroid.util.Keys;
import com.hangapp.newandroid.util.Utils;

/**
 * This {@link IntentService} handles all of our XMPP logic for us. Send
 * messages to it via the {@link startService} method call. <br />
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
	private static final String XMPP_SERVER_URL = "ec2-184-72-81-86.compute-1.amazonaws.com";

	/**
	 * Maintain a single, static {@link XMPPConnection} through the lifecycle of
	 * the whole app. <br />
	 * <br />
	 * We don't have to worry about concurrent access to the same variable
	 * because {@link IntentService} guarantees that onHandleIntent() handles a
	 * single {@link Intent} at a time, in a queue.
	 */
	private static XMPPConnection xmppConnection;

	/**
	 * Share a single {@link MyConnectionListener} object for the
	 * {@link XMPPConnection}, instead of instantiating a new one every time you
	 * connect to XMPP.
	 */
	private static MyConnectionListener mConnectionListener = new MyConnectionListener();

	/**
	 * The <a href="http://en.wikipedia.org/wiki/Data_access_object">Data Access
	 * Object</a> to our SQLite database for XMPP messages.
	 */
	private static MessagesDataSource messagesDataSource;

	public XMPPIntentService() {
		super("XMPPIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Figure out which message the Intent is sending.
		int message = intent.getIntExtra(Keys.MESSAGE, 0);
		Log.d("TAG", "XMPPIntentService started with intent message: "
				+ message);

		// Pull out myJid from the Intent.
		String myJid = intent.getStringExtra(Keys.JID);
		if (myJid == null) {
			Log.e("XMPPIntentService",
					"Can't login: Intent was passed a null JID");
			return;
		}

		// Start the correct method based on which message was passed into the
		// Intent.
		switch (message) {
		case Keys.XMPP_CONNECT:
			connect(myJid);
			return;
		case Keys.XMPP_REGISTER:
			register();
			return;
		case Keys.XMPP_LOGIN:
			login(myJid);
			return;
		case Keys.XMPP_LOGOUT:
			logout();
			return;
		case Keys.XMPP_JOIN_MUC:
			joinMuc();
			return;
		case Keys.XMPP_SEND_MUC_MESSAGE:
			sendMucMessage();
			return;
		default:
			Log.e("XMPPPIntentService.onHandleIntent",
					"Unknown intent message code: " + message);
			return;
		}
	}

	protected void connect(String myJid) {
		// If this is the first time running, then initialize the XMPP
		// connection.
		if (xmppConnection == null) {
			// Setup aSmack.
			SmackAndroid.init(getApplicationContext());
			ConfigureProviderManager.configureProviderManager();

			// Initialize the XMPPConnection itself. Point it to our EC2 server.
			xmppConnection = new XMPPConnection(XMPP_SERVER_URL);
		}

		// If you're already connected, then just attempt to login.
		if (xmppConnection.isConnected()) {
			Log.e("XMPPIntentService.connect()", "Already connected");

			// Already connected, so attempt to login.
			Utils.startLoginIntent(myJid, getApplicationContext());
			return;
		}

		// Otherwise, attempt to connect to XMPP.
		try {
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			xmppConnection.connect();
		} catch (XMPPException e) {
			Log.e("XMPPIntentService.connect()", e.getMessage());

			// Attempt to connect again?
			Utils.startConnectIntent(myJid, getApplicationContext());
			return;
		}

		if (xmppConnection.isConnected()) {
			Log.d("XMPPIntentService.connect()", "Connected to XMPP");
			// If the Connection succeeded, then add your
			// ConnectionListener.
			xmppConnection.removeConnectionListener(mConnectionListener);
			xmppConnection.addConnectionListener(mConnectionListener);

			// Attempt to login.
			Utils.startLoginIntent(myJid, getApplicationContext());
			return;
		} else {
			// If XMPPConnection isn't connected even after connect() passed,
			// then attempt to connect again.
			Utils.startConnectIntent(myJid, getApplicationContext());
			return;
		}
	}

	protected void register() {

	}

	protected void login(String myJid) {
		// If you try to login without being connected, then try to connect
		// again.
		if (!xmppConnection.isConnected()) {
			Log.e("XMPPIntentService.login()", "Can't login: not connected");

			// Already connected, so attempt to login.
			Utils.startConnectIntent(myJid, getApplicationContext());
			return;
		}

		if (xmppConnection.isAuthenticated()) {
			Log.e("XMPPIntentService.login()", "XMPP already authenticated");
			return;
		}

		try {
			xmppConnection.login(myJid, myJid);
		} catch (XMPPException e) {
			// Show the error, but don't do anything about it yet.
			Log.e("XMPPIntentService.login()", "Can't login: " + e.getMessage());
			return;
		}

		if (xmppConnection.isAuthenticated()) {
			Log.d("XMPPIntentService.login()", "Logged into XMPP");
			// If login succeeded, then we're done. Let this IntentService die.
			// Do nothing.
		} else {
			// If login failed, then start over from connect().
			Utils.startConnectIntent(myJid, getApplicationContext());
		}
	}

	protected void logout() {

	}

	protected void joinMuc() {

	}

	protected void sendMucMessage() {

	}

}

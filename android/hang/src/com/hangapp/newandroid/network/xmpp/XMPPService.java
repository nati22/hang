package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.ConfigureProviderManager;

import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.Keys;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class XMPPService extends Service {

	private static final String JABBER_SERVER_URL = "ec2-184-72-81-86.compute-1.amazonaws.com";
	private XMPPConnection xmppConnection;

	@Override
	public void onCreate() {
		super.onCreate();

		// Setup aSmack.
		SmackAndroid.init(getApplicationContext());
		ConfigureProviderManager.configureProviderManager();

		// Initialize the XMPPConnection itself. Point it to our EC2 server.
		xmppConnection = new XMPPConnection(JABBER_SERVER_URL);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("XMPPService.onStartCommand", "Connecting to XMPPManager...");

		String myJid = intent.getStringExtra(Keys.JID);

		if (xmppConnection == null) {
			HangLog.toastE(this, "XMPPService.onStartCommand",
					"Fatal error: XMPPConnection was null");
			return Service.START_NOT_STICKY;
		}

		if (!xmppConnection.isConnected()) {
			// Attempt to connect to the server.
			new ConnectAsyncTask(myJid, xmppConnection, this).execute();
		} else if (!xmppConnection.isAuthenticated()) {
			new LoginAsyncTask("girum", "password", xmppConnection, this);
		} else {
			Log.e("XMPPService.onStartCommand",
					"Requested a start of XMPP service, but user is already authenticated");
		}

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		Log.i("XMPPService.onBind", "XMPPService onBind called.");

		// String myJid = intent.getStringExtra(Keys.JID);

		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		new LogoutAsyncTask(xmppConnection, getApplicationContext()).execute();
	}

}

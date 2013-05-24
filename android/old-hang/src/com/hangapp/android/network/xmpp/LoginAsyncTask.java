//package com.hangapp.android.network.xmpp;
//
//import org.jivesoftware.smack.SASLAuthentication;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//
//import android.content.Context;
//
//import com.hangapp.android.network.rest.parse.BaseParseAsyncTask;
//import com.hangapp.android.util.Log;
//
//public class LoginAsyncTask extends BaseParseAsyncTask<String> {
//
//	private String username;
//	XMPPConnection xmppConnection;
//
//	protected LoginAsyncTask(Context context, String username,
//			XMPPConnection xmppConnection) {
//		super(context);
//		this.username = username;
//		this.xmppConnection = xmppConnection;
//	}
//
//	@Override
//	public String call() throws Exception {
//
//		// XMPPConnection connected() sanity check
//		if (!xmppConnection.isConnected()) {
//			final String errorMessage = "Will not login to server: XMPP is not connected";
//			Log.e(errorMessage);
//			// new XMPP.ConnectTask(getContext());
//			new ConnectAsyncTask(xmppConnection, context).execute();
//			return null;
//		}
//
//		// XMPPConnection already authenticated sanity check
//		if (xmppConnection.isAuthenticated()) {
//			final String errorMessage = "Will not login to server: XMPP is already logged in";
//			Log.e(errorMessage);
//			return null;
//		}
//
//		// You have to put this code before you login
//		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
//
//		// You have to specify your Jabber ID address
//		// WITHOUT @jabber.org at the end
//		try {
//			xmppConnection.login(username, username);
//		} catch (XMPPException e) {
//			Log.e("XMPP Error: " + e.getMessage());
//			return null;
//		}
//
//		Log.d("Login appears to be successful");
//
//		return null;
//	}
//
//	@Override
//	protected void onSuccess(String result) {
//
//		// GCMUtils.registerPhoneInGCM(context);
//	}
//
//}

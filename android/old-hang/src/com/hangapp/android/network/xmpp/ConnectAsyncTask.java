//package com.hangapp.android.network.xmpp;
//
//import org.jivesoftware.smack.ConnectionListener;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//
//import android.content.Context;
//
//import com.hangapp.android.network.rest.parse.BaseParseAsyncTask;
//import com.hangapp.android.util.Log;
//import com.hangapp.android.util.Utils;
//
//public class ConnectAsyncTask extends BaseParseAsyncTask<String> {
//
//	private XMPPConnection xmppConnection;
//
//	protected ConnectAsyncTask(XMPPConnection xmppConnection, Context context) {
//		super(context);
//		this.xmppConnection = xmppConnection;
//	}
//
//	@Override
//	public String call() throws Exception {
//		try {
//			if (xmppConnection.isConnected()) {
//				Log.e("Will not connect to XMPP: Already connected");
//			}
//			xmppConnection.connect();
//
//			// Add a MyConnectionListener here, so that you know immediately
//			// when
//			// you lose your connection to the XMPP server
//			// TODO: The MyConnectionListener here needs to be its own class.
//			xmppConnection.addConnectionListener(new MyConnectionListener() {
//				public void reconnectionSuccessful() {
//					Log.d("XMPPConnection: reconnectionSuccessful");
//				}
//
//				public void reconnectionFailed(Exception e) {
//					Log.e("XMPPConnection: reconnectionFailed: "
//							+ e.getMessage());
//				}
//
//				public void reconnectingIn(int seconds) {
//					Log.d("XMPPConnection: reconnectingIn " + seconds
//							+ " seconds");
//				}
//
//				public void connectionClosedOnError(Exception e) {
//					Log.e("XMPPConnection: connectionClosedOnError: "
//							+ e.getMessage());
//					new ConnectAsyncTask(xmppConnection, context).execute();
//				}
//
//				public void connectionClosed() {
//					Log.d("XMPPConnection: connectionClosed");
//					new ConnectAsyncTask(xmppConnection, context).execute();
//				}
//			});
//
//		} catch (XMPPException e) {
//			Log.e("XMPPException occurred: " + e.getMessage());
//			return null;
//		}
//		return null;
//	}
//
//	@Override
//	protected void onSuccess(String result) {
//
//		if (xmppConnection.isConnected()) {
//			Log.d("Connected to XMPP server.");
//
//			// Quietly attempt to register/login
//			Log.i("Attempting to register/login...");
//			// new XMPP.RegisterNewUserTask(getContext(),
//			// Utils.getDefaultUserJID(getContext())).execute();
//			new RegisterNewUserAsyncTask(context,
//					Utils.getDefaultUserJID(context), xmppConnection).execute();
//
//		} else {
//			Log.e("XMPPConnection did NOT connect to the server.");
//		}
//	}
//
//}

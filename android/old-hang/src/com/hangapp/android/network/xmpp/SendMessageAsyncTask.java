//package com.hangapp.android.network.xmpp;
//
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smackx.muc.MultiUserChat;
//
//import android.content.Context;
//
//import com.hangapp.android.network.rest.parse.BaseParseAsyncTask;
//import com.hangapp.android.util.Log;
//
//public class SendMessageAsyncTask extends BaseParseAsyncTask<String> {
//
//	private MultiUserChat muc;
//	private String message;
//	private XMPPConnection xmppConnection;
//
//	public SendMessageAsyncTask(Context context, MultiUserChat muc,
//			String message, XMPPConnection xmppConnection) {
//		super(context);
//		this.muc = muc;
//		this.message = message;
//	}
//
//	@Override
//	public String call() throws Exception {
//
//		if (!xmppConnection.isAuthenticated()) {
//			final String errorMessage = "Won't send message to MUC: Not authenticated/connected";
//			Log.e(errorMessage);
//			return null;
//		}
//
//		if (muc == null) {
//			final String errorMessage = "Won't send message to MUC: muc was null;";
//			Log.e(errorMessage);
//			return null;
//		}
//
//		// Invite the inviteeJID
//		try {
//			Log.d("Sending message: " + message);
//			muc.sendMessage(message);
//		} catch (XMPPException e) {
//			Log.e(e.getMessage());
//			return null;
//		}
//
//		return null;
//	}
//
//}

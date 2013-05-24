//package com.hangapp.android.network.xmpp;
//
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smackx.muc.MultiUserChat;
//
//import android.content.Context;
//
//import com.hangapp.android.network.rest.parse.BaseParseAsyncTask;
//import com.hangapp.android.util.Log;
//
//public class InviteUserToMUCAsyncTask extends BaseParseAsyncTask<String> {
//	private MultiUserChat muc;
//	private Integer inviteeJID, hostJID;
//	private XMPPConnection xmppConnection;
//
//	public InviteUserToMUCAsyncTask(Context context, MultiUserChat muc,
//			Integer inviteeJID, Integer hostJID, XMPPConnection xmppConnection) {
//		super(context);
//		this.muc = muc;
//		this.inviteeJID = inviteeJID;
//		this.hostJID = hostJID;
//		this.xmppConnection = xmppConnection;
//	}
//
//	@Override
//	public String call() throws Exception {
//		if (!xmppConnection.isAuthenticated()) {
//			final String errorMessage = "Won't invite user to MUC: Not authenticated/connected";
//			Log.e(errorMessage);
//			return null;
//		}
//
//		if (muc == null) {
//			final String errorMessage = "Won't invite user to MUC: muc was null";
//			Log.e(errorMessage);
//			return null;
//		}
//
//		// Invite the inviteeJID
//		muc.invite(inviteeJID + "@" + XMPP.JABBER_SERVER_URL + "/Smack",
//				"Please join my chatroom, from Android");
//		final String message = "Invited " + inviteeJID + " into " + hostJID;
//		Log.d(message);
//
//		return null;
//	}
//
//}

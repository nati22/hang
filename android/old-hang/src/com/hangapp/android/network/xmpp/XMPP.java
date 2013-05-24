package com.hangapp.android.network.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Application;
import android.content.Context;

import com.hangapp.android.model.User;

public class XMPP {
	public static final String JABBER_SERVER_URL = "ec2-54-242-9-67.compute-1.amazonaws.com";

	private static XMPPConnection xmppConnection;

//	@Inject
	private Application context;

	static {
		// You muse configure the provider manager before doing any XMPP logic:
		// <https://github.com/Flowdalic/asmack>
		// ConfigureProviderManager.configureProviderManager();
		// xmppConnection = new XMPPConnection(JABBER_SERVER_URL);
	}

	// TODO: Remove this dependency from our code (make this private).
	public static XMPPConnection getXMPPConnection() {
		return xmppConnection;
	}

	public void connect() {
		// new ConnectAsyncTask(xmppConnection, context).execute();
	}

	public void register(Integer jid) {
		// new RegisterNewUserAsyncTask(context, jid, xmppConnection).execute();
	}

	public void login(String username) {
		// new LoginAsyncTask(context, username, xmppConnection).execute();
	}

	public void joinMuc(MultiUserChat muc, User joinee, Integer hostJID) {
		// new JoinMUCAsyncTask(context, muc, joinee, hostJID, xmppConnection)
		// .execute();
	}

	public void inviteUserToMuc(MultiUserChat muc, Integer inviteeJID,
			Integer hostJID) {
		// new InviteUserToMUCAsyncTask(context, muc, inviteeJID, hostJID,
		// xmppConnection).execute();
	}

	public void sendMessage(MultiUserChat muc, String text) {
		// new SendMessageAsyncTask(context, muc, text,
		// xmppConnection).execute();
	}

	/**
	 * Disconnects from Jabber and unregisters from GCM.
	 * 
	 * @param Context
	 */
	public static void logout(Context context) {

		// Disconnect from Jabber
		// Presence offlinePresence = new Presence(Presence.Type.unavailable,
		// "",
		// 1, Presence.Mode.away);
		// xmppConnection.sendPacket(offlinePresence);

		// // Unregister from GCM
		// GCMRegistrar.unregister(context);
	}
}

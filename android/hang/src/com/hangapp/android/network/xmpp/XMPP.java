package com.hangapp.android.network.xmpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hangapp.android.database.MessagesDataSource;
import com.hangapp.android.model.callback.MucListener;
import com.hangapp.android.util.Keys;

/**
 * Front-facing XMPP interface. This class uses {@link XMPPIntentService} in the
 * background. If you want to use XMPP from a client class, inject this class as
 * your dependency.
 */
public class XMPP {

	private static XMPP instance = new XMPP();
	private static Map<String, MultiUserChat> mucs = new HashMap<String, MultiUserChat>();

	private XMPP() {
	}

	public void initialize(Context context) {
		this.messagesDataSource = new MessagesDataSource(context);
	}

	public static synchronized XMPP getInstance() {
		return instance;
	}

	/**
	 * The <a href="http://en.wikipedia.org/wiki/Data_access_object">Data Access
	 * Object</a> to our SQLite database for XMPP messages.
	 */
	private MessagesDataSource messagesDataSource;

	private static Map<String, ArrayList<MucListener>> mucMessageListeners = new HashMap<String, ArrayList<MucListener>>();

	/**
	 * The only front-facing XMPPConnection method in this class. You should
	 * call this method once from front-facing code, and this package should
	 * handle the rest. That is, it should handle connecting, logging in,
	 * reconnections on failure, etc.
	 */
	public void connect(String myJid, Context context) {
		Intent connectIntent = new Intent(context, XMPPIntentService.class);
		connectIntent.putExtra(Keys.MESSAGE, Keys.XMPP_CONNECT);
		connectIntent.putExtra(Keys.JID, myJid);
		context.startService(connectIntent);
	}

	/**
	 * Package-private helper method that attempts to login in the background.
	 */
	void login(String myJid, Context context) {
		Intent loginIntent = new Intent(context, XMPPIntentService.class);
		loginIntent.putExtra(Keys.MESSAGE, Keys.XMPP_LOGIN);
		loginIntent.putExtra(Keys.JID, myJid);
		context.startService(loginIntent);
	}

	/**
	 * Don't forget to add listeners to each MUC by name. Should be called in
	 * onResume() of client classes.
	 */
	public boolean addMucListener(String mucName, MucListener listener) {
		ArrayList<MucListener> listenersForThisMuc = mucMessageListeners
				.get(mucName);

		// If there isn't already an ArrayList<MucListeners> for this MUC, then
		// make it and put it into the Map.
		if (listenersForThisMuc == null) {
			Log.i("XMPP",
					"addMucListener: There was no List<MucListeners> for mucName: "
							+ mucName + ", so one was created.");
			listenersForThisMuc = new ArrayList<MucListener>();
			mucMessageListeners.put(mucName, listenersForThisMuc);
		}

		return listenersForThisMuc.add(listener);
	}

	/**
	 * The counterpart to removeMucListener. Should be called in onPause() of
	 * client classes.
	 */
	public boolean removeMucListener(String mucName, MucListener listener) {
		ArrayList<MucListener> listenersForThisMuc = mucMessageListeners
				.get(mucName);

		// If there isn't already an ArrayList<MucListeners> for this MUC, then
		// make it and put it into the Map.
		if (listenersForThisMuc == null) {
			Log.i("XMPP",
					"addMucListener: There was no List<MucListeners> for mucName: "
							+ mucName + ", so one was created.");
			listenersForThisMuc = new ArrayList<MucListener>();
			mucMessageListeners.put(mucName, listenersForThisMuc);
		}

		return listenersForThisMuc.remove(listener);
	}

	/**
	 * Helper method for MyPacketListeners to add Messages to the SQLite
	 * database.
	 */
	void addMucMessage(String mucName, Message message) {
		Log.d("XMPP", "Opening SQLite datasource");
		messagesDataSource.open();
		messagesDataSource.createMessage(mucName, message);
		List<Message> messages = getAllMessages(mucName);

		for (MucListener listener : mucMessageListeners.get(mucName)) {
			Log.d("XMPP", "Added muc message: " + message.getBody()
					+ ", notifying listener: " + listener.toString());
			listener.onMucMessageUpdate(mucName, messages);
		}

		messagesDataSource.close();
	}

	/**
	 * Retrieves all Messages for an MUC from the database.
	 */
	public List<Message> getAllMessages(String mucName) {
		List<Message> messages = null;

		messagesDataSource.open();
		messages = messagesDataSource.getAllMessages(mucName);
		messagesDataSource.close();

		return messages;
	}

	/**
	 * Join an MUC on the UI thread, so that the MessageListener for the MUC is
	 * also added in the UI thread.
	 */
	public void joinMuc(final String mucName, String myJid) {
		XMPPConnection xmppConnection = XMPPIntentService.xmppConnection;
		MultiUserChat muc = mucs.get(mucName);

		if (!xmppConnection.isConnected()) {
			Log.e("XMPPPIntentService.joinMuc()",
					"Failed to join muc: not connected");
			return;
		}

		if (!xmppConnection.isAuthenticated()) {
			Log.e("XMPPPIntentService.joinMuc()",
					"Failed to join muc: not authenticated");
			return;
		}

		if (muc == null) {
			muc = new MultiUserChat(xmppConnection, mucName + "@conference."
					+ XMPPIntentService.XMPP_SERVER_URL);
			mucs.put(mucName, muc);
		}

		try {
			if (muc.isJoined()) {
				muc.leave();
			}
			muc.join(myJid);
		} catch (XMPPException e) {
			Log.e("XMPPPIntentService.joinMuc()",
					"Failed to join muc: " + e.getMessage());
			return;
		}

		Log.i("XMPPIntentService.joinMuc()", "Joined muc: " + mucName);

		muc.addMessageListener(new PacketListener() {
			@Override
			public void processPacket(final Packet packet) {
				if (packet instanceof Message) {
					Message message = (Message) packet;

					addMucMessage(mucName, message);
				}
			}
		});

		Log.i("XMPP", "Muc " + mucName + " message listeners: ");
	}

	/**
	 * Leave the MUC in the UI thread.
	 */
	public void leaveMuc(String mucName) {
		MultiUserChat muc = mucs.get(mucName);

		if (muc == null) {
			muc = new MultiUserChat(XMPPIntentService.xmppConnection, mucName
					+ "@conference." + XMPPIntentService.XMPP_SERVER_URL);
			mucs.put(mucName, muc);
		}

		muc.leave();
	}

	/**
	 * Send an MUC message in the UI thread.
	 */
	public void sendMucMessage(String myJid, String mucName, String message) {
		MultiUserChat muc = mucs.get(mucName);

		if (muc == null) {
			joinMuc(mucName, myJid);
		}

		muc = mucs.get(mucName);

		try {
			muc.sendMessage(message);
		} catch (XMPPException e) {
			Log.e("XMPPIntentService.sendMucMessage()",
					"Couldn't send XMPP muc message: " + e.getMessage());
		}
	}
}

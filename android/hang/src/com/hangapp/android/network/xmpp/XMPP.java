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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.hangapp.android.database.Database;
import com.hangapp.android.database.MessagesDataSource;
import com.hangapp.android.model.callback.MucListener;
import com.hangapp.android.util.BaseApplication;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.Utils;

/**
 * Front-facing XMPP interface. This class uses {@link XMPPIntentService} in the
 * background. If you want to use XMPP from a client class, inject this class as
 * your dependency.
 */
final public class XMPP {

	private static XMPP instance = new XMPP();

	/**
	 * Maintain a single, static {@link XMPPConnection} through the lifecycle of
	 * the whole app. <br />
	 * <br />
	 * We don't have to worry about concurrent access to the same variable
	 * because {@link IntentService} guarantees that onHandleIntent() handles a
	 * single {@link Intent} at a time, in a queue.'
	 * 
	 * TODO: How long does this field persist? Is that why XMPP keeps
	 * disconnecting on us?
	 */
	XMPPConnection xmppConnection;

	private Map<String, MultiUserChat> mucMap = new HashMap<String, MultiUserChat>();
	private Database database;
	private Context context;

	/**
	 * The <a href="http://en.wikipedia.org/wiki/Data_access_object">Data Access
	 * Object</a> to our SQLite database for XMPP messages.
	 */
	private MessagesDataSource messagesDataSource;
	private Map<String, ArrayList<MucListener>> mucMessageListeners = new HashMap<String, ArrayList<MucListener>>();
	List<String> mucsToJoin = new ArrayList<String>();

	private XMPP() {
	}

	/**
	 * To be called exactly one time: from {@link BaseApplication}
	 * 
	 * @param context
	 */
	public void initialize(Database database, Context context) {
		this.database = database;
		this.context = context;
		this.messagesDataSource = new MessagesDataSource(context);
	}

	public static synchronized XMPP getInstance() {
		return instance;
	}

	private boolean isDoneJoiningAllChatrooms() {
		return xmppConnection != null && xmppConnection.isConnected()
				&& xmppConnection.isAuthenticated() && mucsToJoin.isEmpty();
	}

	/**
	 * The only front-facing XMPPConnection method in this class. You should
	 * call this method once from front-facing code, and this package should
	 * handle the rest. That is, it should handle connecting, logging in,
	 * reconnections on failure, etc.
	 */
	public void connect(String myJid, Context context) {
		if (isDoneJoiningAllChatrooms()) {
			Log.v("XMPP.connect()",
					"Won't connect to XMPP: Already authenticated");
			return;
		}

		Toast.makeText(context, "Connecting to chat...", Toast.LENGTH_SHORT)
				.show();

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
	boolean addMucMessage(String mucName, Message message) {
		boolean mucMessageAdded = false;

		messagesDataSource.open();

		// TODO: Move these SQLite calls into an AsyncTask of some sort
		// (i.e: move them OFF of the UI thread).
		mucMessageAdded = messagesDataSource.createMessage(mucName, message);
		List<Message> messages = getAllMessages(mucName);

		if (mucMessageListeners.get(mucName) != null) {
			for (MucListener listener : mucMessageListeners.get(mucName)) {
				Log.i("XMPP", "Added muc message: " + message.getBody()
						+ ", notifying listener: " + listener.toString());
				listener.onMucMessageUpdate(mucName, messages);
			}
		}

		messagesDataSource.close();

		return mucMessageAdded;
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
	 * 
	 * TODO: Queue up MUCs who failed to join and attempt to join them later,
	 * using exponential backoff.
	 */
	public boolean joinMuc(final String mucName, String myJid) {
		Log.v("XMPP.joinMuc", "Attempting to join muc: " + mucName);

		// Grab a reference to the single XMPPConnection that we use
		// from the utility XMPPIntentService class.
		// XMPPConnection xmppConnection = XMPPIntentService.xmppConnection;

		if (!xmppConnection.isConnected()) {
			Log.e("XMPPPIntentService.joinMuc()",
					"Failed to join muc: not connected");
			return false;
		}

		if (!xmppConnection.isAuthenticated()) {
			Log.e("XMPPPIntentService.joinMuc()",
					"Failed to join muc: not authenticated");
			return false;
		}

		// Pull the MultiUserChatObject from our map of Muc's Jid -> Muc Object
		// map.
		MultiUserChat muc = mucMap.get(mucName);

		// If the MUC Object from the map hasn't been instantiated yet,
		// instantiate one and throw it into the Map.
		// This technique is called "lazy instantiation".
		if (muc == null) {
			muc = new MultiUserChat(xmppConnection, mucName + "@conference."
					+ XMPPIntentService.XMPP_SERVER_URL);
			mucMap.put(mucName, muc);
		}

		try {
			// // Leave the MUC and rejoin unconditionally: this is so that we
			// get
			// // the server to re-send us all of the Messages in that MUC.
			// if (muc.isJoined()) {
			// muc.leave();
			// }
			muc.join(myJid);
		} catch (XMPPException e) {
			Log.e("XMPPPIntentService.joinMuc()",
					"Failed to join muc: " + e.getMessage());
			return false;
		}

		Log.v("XMPPIntentService.joinMuc()", "Joined muc: " + mucName);

		// aSmack defines that you must add a Message Listener to the MUC
		// *after* you've joined the MUC. It will throw an exception otherwise.
		muc.addMessageListener(new PacketListener() {
			// TODO: Don't just create a new PacketListener instance for every
			// MUC. Instead, make them all reference the same object.
			@Override
			public void processPacket(final Packet packet) {
				boolean gotNewMessage = false;

				// Smack upcasts all Messages to the "Packet" superclass,
				// regardless of whether or not you actually have a Message
				// (alternatives include IQs, Presences, etc).
				if (packet instanceof Message) {
					final Message message = (Message) packet;

					gotNewMessage = addMucMessage(mucName, message);

					if (gotNewMessage) {
						// TODO: Parse the message that you got for who actually
						// sent you the message. Once you have his JID, join
						// ChatActivity for his JID.
						final String fromJid = Utils
								.parseJidFromMessage(message);

						// If I sent this message, then don't make a
						// notification for it.
						final String myJid = database.getMyJid();
						if (myJid != null && myJid.equals(fromJid)) {
							return;
						}

						String from = Utils.convertJidToName(fromJid, database);

						Utils.showChatNotification(context, "Message from "
								+ from, message.getBody(), fromJid);
					}
				}
			}
		});

		Log.v("XMPP", "Muc " + mucName + " message listeners: ");
		return true;
	}

	/**
	 * Leave the MUC in the UI thread.
	 */
	public void leaveMuc(String mucName) {
		MultiUserChat muc = mucMap.get(mucName);

		// If the MUC Object from the map hasn't been instantiated yet,
		// instantiate one and throw it into the Map.
		// This technique is called "lazy instantiation".
		if (muc == null) {
			muc = new MultiUserChat(xmppConnection, mucName + "@conference."
					+ XMPPIntentService.XMPP_SERVER_URL);
			mucMap.put(mucName, muc);
		}

		muc.leave();
	}

	/**
	 * Send an MUC message in the UI thread.
	 */
	public void sendMucMessage(String myJid, String mucName, String message) {
		MultiUserChat muc = mucMap.get(mucName);

		if (muc == null) {
			joinMuc(mucName, myJid);
		}

		muc = mucMap.get(mucName);

		try {
			muc.sendMessage(message);
		} catch (XMPPException e) {
			Log.e("XMPPIntentService.sendMucMessage()",
					"Couldn't send XMPP muc message: " + e.getMessage());
		}
	}

	/**
	 * Front-facing method used to clear out the queue of Mucs to join and
	 * populate it with a new set of Mucs to join. Also tells XMPP to start the
	 * "join" process on all of them.
	 * 
	 * @param myJid
	 * @param mucsToJoin
	 */
	public synchronized void setListOfMucsToJoinAndConnect(String myJid,
			List<String> mucsToJoin) {
		this.mucsToJoin.clear();
		this.mucsToJoin.addAll(mucsToJoin);

		// Once you modify the existing internal list of Mucs to join, just
		// start the regular Connect process to ensure that XMPPConnection
		// and authentication is all good before attempting to join each
		// of those Mucs.
		connect(myJid, context);
	}
}

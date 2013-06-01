package com.hangapp.newandroid.network.xmpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.content.Intent;

import com.hangapp.newandroid.database.MessagesDataSource;
import com.hangapp.newandroid.model.callback.MucMessageListener;
import com.hangapp.newandroid.util.Keys;

/**
 * Client-facing XMPP interface. This class uses {@link XMPPIntentService} to
 * work.
 */
public class XMPP {

	private static XMPP instance = new XMPP();

	private XMPP() {

	}

	public void initialize(Context context) {
		this.messagesDataSource = new MessagesDataSource(context);
	}

	public static XMPP getInstance() {
		return instance;
	}

	/**
	 * The <a href="http://en.wikipedia.org/wiki/Data_access_object">Data Access
	 * Object</a> to our SQLite database for XMPP messages.
	 */
	private MessagesDataSource messagesDataSource;

	private static Map<String, ArrayList<MucMessageListener>> mucMessageListeners = new HashMap<String, ArrayList<MucMessageListener>>();

	public void connect(String myJid, Context context) {
		Intent connectIntent = new Intent(context, XMPPIntentService.class);
		connectIntent.putExtra(Keys.MESSAGE, Keys.XMPP_CONNECT);
		connectIntent.putExtra(Keys.JID, myJid);
		context.startService(connectIntent);
	}

	public void login(String myJid, Context context) {
		Intent loginIntent = new Intent(context, XMPPIntentService.class);
		loginIntent.putExtra(Keys.MESSAGE, Keys.XMPP_LOGIN);
		loginIntent.putExtra(Keys.JID, myJid);
		context.startService(loginIntent);
	}

	public void joinMuc(String mucName, String myJid, Context context) {
		Intent joinMucIntent = new Intent(context, XMPPIntentService.class);
		joinMucIntent.putExtra(Keys.MESSAGE, Keys.XMPP_JOIN_MUC);
		joinMucIntent.putExtra(Keys.MUC_NAME, mucName);
		joinMucIntent.putExtra(Keys.JID, myJid);
		context.startService(joinMucIntent);
	}

	public void leaveMuc(String mucName, Context context) {
		Intent joinMucIntent = new Intent(context, XMPPIntentService.class);
		joinMucIntent.putExtra(Keys.MESSAGE, Keys.XMPP_JOIN_MUC);
		joinMucIntent.putExtra(Keys.MUC_NAME, mucName);
		context.startService(joinMucIntent);
	}

	public void sendMessage(String mucName, String message, Context context) {
		Intent sendMucMessageIntent = new Intent(context,
				XMPPIntentService.class);
		sendMucMessageIntent.putExtra(Keys.MESSAGE, Keys.XMPP_SEND_MUC_MESSAGE);
		sendMucMessageIntent.putExtra(Keys.MUC_NAME, mucName);
		sendMucMessageIntent.putExtra(Keys.MUC_MESSAGE, message);
		context.startService(sendMucMessageIntent);
	}

	public boolean addMucMessageListener(String mucName,
			MucMessageListener listener) {
		ArrayList<MucMessageListener> listenersForThisMuc = mucMessageListeners
				.get(mucName);

		if (listenersForThisMuc == null) {
			listenersForThisMuc = new ArrayList<MucMessageListener>();

			mucMessageListeners.put(mucName, listenersForThisMuc);
		}

		return listenersForThisMuc.add(listener);
	}

	public boolean removeMucMessageListener(String mucName,
			MucMessageListener listener) {
		ArrayList<MucMessageListener> listenersForThisMuc = mucMessageListeners
				.get(mucName);

		if (listenersForThisMuc == null) {
			listenersForThisMuc = new ArrayList<MucMessageListener>();

			mucMessageListeners.put(mucName, listenersForThisMuc);
		}

		return listenersForThisMuc.remove(listener);
	}

	void addMucMessage(String mucName, Message message) {
		messagesDataSource.open();
		messagesDataSource.createMessage(mucName, message);
		List<Message> messages = getAllMessages(mucName);

		for (MucMessageListener listener : mucMessageListeners.get(mucName)) {
			listener.onMucMessageUpdate(mucName, messages);
		}

		messagesDataSource.close();
	}

	public List<Message> getAllMessages(String mucName) {
		List<Message> messages = null;

		messagesDataSource.open();

		messages = messagesDataSource.getAllMessages(mucName);

		messagesDataSource.close();

		return messages;
	}
}

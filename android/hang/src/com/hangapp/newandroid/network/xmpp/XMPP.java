package com.hangapp.newandroid.network.xmpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.hangapp.newandroid.database.MessagesDataSource;
import com.hangapp.newandroid.model.callback.MucListener;
import com.hangapp.newandroid.util.Keys;

/**
 * Front-facing XMPP interface. This class uses {@link XMPPIntentService} in the
 * background.
 */
public class XMPP {

	private static XMPP instance = new XMPP();
	private static MucBroadcastReceiver mucBroadcastReceiver = new MucBroadcastReceiver();

	private XMPP() {

	}

	public void initialize(Context context) {
		this.messagesDataSource = new MessagesDataSource(context);

		IntentFilter filter = new IntentFilter(
				MucBroadcastReceiver.PROCESS_RESPONSE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		context.registerReceiver(mucBroadcastReceiver, filter);
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
		Intent leaveMucIntent = new Intent(context, XMPPIntentService.class);
		leaveMucIntent.putExtra(Keys.MESSAGE, Keys.XMPP_JOIN_MUC);
		leaveMucIntent.putExtra(Keys.MUC_NAME, mucName);
		context.startService(leaveMucIntent);
	}

	public void sendMessage(String myJid, String mucName, String messageToSend,
			Context context) {
		Intent sendMucMessageIntent = new Intent(context,
				XMPPIntentService.class);
		sendMucMessageIntent.putExtra(Keys.MESSAGE, Keys.XMPP_SEND_MUC_MESSAGE);
		sendMucMessageIntent.putExtra(Keys.JID, myJid);
		sendMucMessageIntent.putExtra(Keys.MUC_NAME, mucName);
		sendMucMessageIntent.putExtra(Keys.MUC_MESSAGE, messageToSend);
		context.startService(sendMucMessageIntent);
	}

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

	void addMucMessage(String mucName, Message message) {
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

	public List<Message> getAllMessages(String mucName) {
		List<Message> messages = null;

		messagesDataSource.open();

		messages = messagesDataSource.getAllMessages(mucName);

		messagesDataSource.close();

		return messages;
	}
}

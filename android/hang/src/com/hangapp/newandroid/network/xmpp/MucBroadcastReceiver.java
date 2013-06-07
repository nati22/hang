package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.packet.Message;

import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.Keys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MucBroadcastReceiver extends BroadcastReceiver {

	public static final String ACTION_RESP = "com.hangapp.android.intent.action.MESSAGE_PROCESSED";

	@Override
	public void onReceive(Context context, Intent intent) {
		XMPP xmpp = XMPP.getInstance();

		String mucName = intent.getStringExtra(Keys.MUC_NAME);
		String messagePacketId = intent.getStringExtra(Keys.MESSAGE_PACKET_ID);
		String messageFrom = intent.getStringExtra(Keys.MESSAGE_FROM);
		String messageBody = intent.getStringExtra(Keys.MESSAGE_BODY);

		Message message = new Message();
		message.setPacketID(messagePacketId);
		message.setFrom(messageFrom);
		message.setBody(messageBody);

		HangLog.toastD(context, "MucBroadcastReceiver",
				"MucBroadcastReceiver about to addMucMessage: " + messageBody);
		xmpp.addMucMessage(mucName, message);
	}

}

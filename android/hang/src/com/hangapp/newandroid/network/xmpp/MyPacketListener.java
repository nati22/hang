package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.content.Context;

import com.hangapp.newandroid.util.HangLog;

public class MyPacketListener implements PacketListener {

	private XMPP xmpp;
	private String mucName;
	private Context context;

	public MyPacketListener(String mucName, Context context) {
		this.mucName = mucName;
		xmpp = XMPP.getInstance();
		this.context = context;
	}

	@Override
	public void processPacket(Packet packet) {
		if (packet instanceof Message) {
			Message message = (Message) packet;

			HangLog.toastD(context, "MyPacketListener", "Processed message: "
					+ message.getBody());

			// Add the Message to the internal database.
			xmpp.addMucMessage(mucName, message);
		}
	}
}

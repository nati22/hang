package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

public class MessageListener implements PacketListener {

//	private XMPP xmpp;
	private String mucName;

	public MessageListener(String mucName) {
		this.mucName = mucName;
//		xmpp = XMPP.getInstance();
	}

	@Override
	public void processPacket(Packet packet) {
		if (packet instanceof Message) {
			Message message = (Message) packet;

			// Add the Message to the internal database.
//			xmpp.addMucMessage(mucName, message);
		}
	}

}

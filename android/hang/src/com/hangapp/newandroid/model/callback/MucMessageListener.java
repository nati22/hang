package com.hangapp.newandroid.model.callback;

import java.util.List;

import org.jivesoftware.smack.packet.Message;

public interface MucMessageListener {

	public void onMucMessageUpdate(String mucName, List<Message> messages);
}

package com.hangapp.newandroid.activity;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.network.xmpp.XMPP;
import com.hangapp.newandroid.util.BaseFragmentActivity;
import com.hangapp.newandroid.util.Keys;

public class ChatActivity extends BaseFragmentActivity implements
		PacketListener {

	private AutoCompleteTextView editTextChatMessage;

	private String mucName;

	private Database database;
	private XMPP xmpp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Instantiate dependencies
		database = Database.getInstance();
		xmpp = XMPP.getInstance();

		mucName = getIntent().getStringExtra(Keys.HOST_JID);

		// Join the Muc.
		String myJid = database.getMyJid();
		xmpp.joinMuc(mucName, myJid, this);

		// Reference Views.
		editTextChatMessage = (AutoCompleteTextView) findViewById(R.id.editTextChatMessage);
	}

	@Override
	public void processPacket(Packet packet) {
		if (packet instanceof Message) {
			Message message = (Message) packet;
			Log.i("ChatActivity.processPacket", "Got message: " + message.getBody());
		}
	}

	public void sendMessage(View v) {
		xmpp.sendMessage(mucName, editTextChatMessage.getText().toString());
		editTextChatMessage.setText("");
	}
}

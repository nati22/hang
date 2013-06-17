package com.hangapp.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.callback.MucListener;
import com.hangapp.android.network.xmpp.XMPP;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.HangLog;
import com.hangapp.android.util.Keys;

public final class ChatActivity extends BaseActivity implements
		MucListener {

	private EditText editTextChatMessage;
	private ListView listViewChatCells;
	private MessageAdapter adapter;

	private String mucName;
	private List<Message> messages = new ArrayList<Message>();

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
		xmpp.joinMuc(mucName, myJid);

		// Reference Views.
		editTextChatMessage = (EditText) findViewById(R.id.editTextChatMessage);
		listViewChatCells = (ListView) findViewById(R.id.listViewChatCells);

		// Setup adapter.
		adapter = new MessageAdapter(this, R.id.listViewChatCells);
		listViewChatCells.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		xmpp.addMucListener(mucName, this);

		messages.clear();
		messages.addAll(xmpp.getAllMessages(mucName));
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();

		xmpp.leaveMuc(mucName);
		xmpp.removeMucListener(mucName, this);
	}

	public void sendMessage(View v) {
		String message = editTextChatMessage.getText().toString().trim();

		if (message.equals("")) {
			HangLog.toastE(this, "ChatActivity.sendMessage",
					"Can't send empty message");
			return;
		}

		String myJid = database.getMyJid();

		xmpp.sendMucMessage(myJid, mucName, message);
		editTextChatMessage.setText("");
	}

	class MessageAdapter extends ArrayAdapter<Message> {

		public MessageAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId, ChatActivity.this.messages);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message = getItem(position);

			String myJid = database.getMyJid();

			// Grab the real name of the "from" from the database.
			String userJid = message.getFrom().split("@")[0];

			String from = "Unknown user";
			// if (database.getIncomingUser(userJid) != null) {
			// from = database.getIncomingUser(userJid).getFullName();
			// } else if (database.getOutgoingUser(userJid) != null) {
			// from = database.getOutgoingUser(userJid).getFullName();
			// }

			// Inflate the cell if necessary.
			// TODO: The cell Type could be different, based on if it's an
			// incoming or outgoing cell.
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.cell_incoming_message, null);
			}

			// Reference Views.
			TextView textViewMessageBody = (TextView) convertView
					.findViewById(R.id.textViewMessageBody);
			TextView textViewMessageFrom = (TextView) convertView
					.findViewById(R.id.textViewMessageFrom);

			// Populate Views.
			textViewMessageBody.setText(message.getBody());
			textViewMessageFrom.setText(from);

			return convertView;
		}
	}

	@Override
	public void onMucMessageUpdate(String mucName, final List<Message> messages) {
		for (Message message : messages) {
			Log.i("ChatActivity.onMucMessageUpdate", "Got muc message: "
					+ message.getBody());
		}

		this.messages.clear();
		this.messages.addAll(messages);
		adapter.notifyDataSetChanged();

		listViewChatCells.post(new Runnable() {
			public void run() {
				listViewChatCells.setSelection(listViewChatCells.getCount() - 1);
			}
		});
	}

}

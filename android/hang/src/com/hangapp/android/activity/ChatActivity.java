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
import android.widget.Toast;

import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.YouFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.callback.MucListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.xmpp.XMPP;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.Keys;

/**
 * Get to this Activity via the "Chat" icon in {@link YouFragment} and
 * {@link ProfileActivity}. <br />
 * <br />
 * All XMPP messages are sent and received in this Activity. Most of our client
 * classes (where a "client class" is either an Activity or a Fragment)
 * instantiate and inject two dependencies: {@link Database} and
 * {@link RestClient}. This class instantiates and injects a third: {@link XMPP}
 * . {@code XMPP} is the client-facing interface to *all* of our XMPP logic.
 * Don't use any other XMPP classes directly. <br />
 * <br />
 * This activity requires that a String MUC name was passed into it at creation.
 * The JID of an MUC (the MUC's name) is always the JID of the user who owns
 * that Proposal.
 */
public final class ChatActivity extends BaseActivity implements MucListener {

	// UI widgets.
	private EditText editTextChatMessage;
	private ListView listViewChatCells;
	private MessageAdapter adapter;

	// Member datum.
	private String mucName;
	private List<Message> messages = new ArrayList<Message>();

	// Dependencies.
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

		// Pull the Muc name from the Intent.
		mucName = getIntent().getStringExtra(Keys.HOST_JID);

		// Sanity check on the Muc name pulled from the Intent.
		if (mucName == null) {
			Toast.makeText(getApplicationContext(), "Muc name was null",
					Toast.LENGTH_SHORT).show();
			finish();
		}

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

		// "Subscribe" this activity to new MUC messages.
		xmpp.addMucListener(mucName, this);

		// Clear out the member datum list of messages for this Activity.
		messages.clear();

		// Then, grab the cached messages for this MUC from SQLite.
		List<Message> mucMessages = xmpp.getAllMessages(mucName);
		messages.addAll(mucMessages);

		// Notify data set changed.
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();

//		// Leave the MUC.
//		xmpp.leaveMuc(mucName);

		// "Unsubscribe" this activity from new MUC messages.
		xmpp.removeMucListener(mucName, this);
	}

	/**
	 * XML OnClickListener for the "sendMessage" button.
	 */
	public void sendMessage(View v) {
		String message = editTextChatMessage.getText().toString().trim();

		if (message.equals("")) {
			Toast.makeText(this, "Can't send empty message", Toast.LENGTH_SHORT)
					.show();
			Log.e("ChatActivity.sendMessage", "Can't send empty message");
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

			String fromJid = message.getFrom().substring(message.getFrom().indexOf(".com/") + 5);
			String from;
			if (database.getOutgoingUser(fromJid) != null) {
				from = database.getOutgoingUser(fromJid).getFullName();
			} else if (database.getMyJid().equals(fromJid)) {
				from = "Me";
			} else from = "User#" + fromJid;
			
			// TODO: Grab the real name of the "from" from the internal
			// database.
			// String userJid = from.split("@")[0];

			// Inflate the cell if necessary.
			// TODO: The cell Type could be different, based on if it's an
			// incoming or outgoing cell.
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.cell_incoming_message, null);
			}

			// Reference Views.
			TextView textViewMessageFrom = (TextView) convertView
					.findViewById(R.id.textViewMessageFrom2);
			TextView textViewMessageBody = (TextView) convertView
					.findViewById(R.id.textViewMessageBody2);

			// Populate Views.
			textViewMessageBody.setText(message.getBody());
			textViewMessageFrom.setText(from + ":  ");

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
				
		scrollMyListViewToBottom();
	}
	
	private void scrollMyListViewToBottom() {
	    listViewChatCells.post(new Runnable() {
	        @Override
	        public void run() {
	            // Select the last row so it will scroll into view...
	            listViewChatCells.setSelection(adapter.getCount() - 1);
	        }
	    });
	}

}

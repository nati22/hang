package com.hangapp.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.YouFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
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
public final class ChatActivity extends BaseActivity implements MucListener,
		IncomingBroadcastsListener {

	// UI widgets.
	private EditText editTextChatMessage;
	private ListView listViewChatCells;
	private MessageAdapter adapter;

	// Member datum.
	private String mucName;
	private List<Message> messages = new ArrayList<Message>();
	private String myJid;
	private LinearLayout linLayoutInterested;
	private List<String> listInterestedJids = new ArrayList<String>();

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
		// TODO: Remove me. Don't directly join the MUC from here; instead just
		// add this MUC to the list of MUCs to join and have XMPPIntentService
		// do it for you.
		myJid = database.getMyJid();
		xmpp.joinMuc(mucName, myJid);

		// Reference Views.
		editTextChatMessage = (EditText) findViewById(R.id.editTextChatMessage);
		listViewChatCells = (ListView) findViewById(R.id.listViewChatCells);
		linLayoutInterested = (LinearLayout) findViewById(R.id.linearLayoutInterestedChat);

		// Setup adapter.
		adapter = new MessageAdapter(this, R.id.listViewChatCells);
		listViewChatCells.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// "Subscribe" this activity to new MUC messages.
		xmpp.addMucListener(mucName, this);

		// Setup listener
		database.addIncomingBroadcastsListener(this);

		// Clear out the member datum list of messages for this Activity.
		messages.clear();

		// Then, grab the cached messages for this MUC from SQLite.
		List<Message> mucMessages = xmpp.getAllMessages(mucName);
		messages.addAll(mucMessages);

		// Refresh list
		onIncomingBroadcastsUpdate(database.getMyIncomingBroadcasts());

		// Notify data set changed.
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Remove listener
		database.removeIncomingBroadcastsListener(this);

		// // Leave the MUC.
		// xmpp.leaveMuc(mucName);

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

			// TODO: Put this into a Utility method when you duplicate the logic
			// for Chat notifications.
			String fromJid = message.getFrom().substring(
					message.getFrom().indexOf(".com/") + 5);
			String from;
			if (database.getMyJid().equals(fromJid)) {
				from = "Me";
			} else if (database.getOutgoingUser(fromJid) != null) {
				from = database.getOutgoingUser(fromJid).getFullName();
			} else
				from = "User#" + fromJid;

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

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {

		// If this is the user's own chat
		if (myJid.equals(mucName)) {

			// Check if my interested list has been updated
			if (!database.getMyProposal().getInterested()
					.equals(listInterestedJids)) {
				Log.w("ChatActivity.onIncomingBroadcastsUpdate",
						"this is my chat");
				Log.w("ChatActivity.onIncomingBroadcastsUpdate",
						"replacing a list of "
								+ listInterestedJids.size()
								+ " with a list of "
								+ database.getMyProposal().getInterested()
										.size()
								+ " which is \""
								+ database.getMyProposal().getInterested()
										.get(0) + "\"");
				listInterestedJids.clear();
				listInterestedJids.addAll(database.getMyProposal()
						.getInterested());

				Log.w("ChatActivity.onIncomingBroadcastsUpdate",
						"listInterestedJids has size "
								+ listInterestedJids.size());
				Log.w("ChatActivity.onIncomingBroadcastsUpdate",
						"listInterestedJids = " + listInterestedJids.toString());

				// Update horizontal list
				updateHorizontalList(listInterestedJids, linLayoutInterested);
			}

		} else if (incomingBroadcasts.contains(database
				.getIncomingUser(mucName))) {
			// Then this is one of the User's broadcasters chats
			Log.i("ChatActivity.onIncomingBroadcastsUpdate()", "this is "
					+ database.getIncomingUser(mucName).getFirstName()
					+ "'s chat");

			// Check if their interested list has been updated
			if (!database.getIncomingUser(mucName).getProposal()
					.getInterested().equals(listInterestedJids)) {
				Log.i("ChatActivity.onIncomingBroadcastsUpdate()",
						"their original interested list read: "
								+ listInterestedJids.toString());

				Log.i("ChatActivity.onIncomingBroadcastsUpdate()",
						"that list is being replaced with: "
								+ database.getIncomingUser(mucName)
										.getProposal().getInterested()
										.toString());
				listInterestedJids.clear();
				listInterestedJids.addAll(database.getIncomingUser(mucName)
						.getProposal().getInterested());

				// Update horizontal list
				updateHorizontalList(listInterestedJids, linLayoutInterested);
			} else {
				Log.i("ChatActivity.onIncomingBroadcastsUpdate()",
						"their list hasn't changed: "
								+ listInterestedJids.toString());
			}

		} else {
			Log.e("ChatActivity.onIncomingBroadcastsUpdate()", "myJid = "
					+ myJid);
			Log.e("ChatActivity.onIncomingBroadcastsUpdate()", "mucName = "
					+ mucName);
			Log.e("ChatActivity.onIncomingBroadcastsUpdate()",
					"incomingBroadcasts.toString() = "
							+ incomingBroadcasts.toString());
			Toast.makeText(getApplicationContext(),
					"There was an error opening this chat", Toast.LENGTH_SHORT)
					.show();
			this.finish();
		}
	}

	public void updateHorizontalList(List<String> jids, LinearLayout linLayout) {
		linLayout.removeAllViews();

		for (int i = 0; i < jids.size(); i++) {
			final String jid = jids.get(i);

			// Get the cell
			View view = LayoutInflater.from(this).inflate(
					R.layout.cell_profile_icon_mini, null);

			// Set the FB Profile pic
			ProfilePictureView icon = (ProfilePictureView) view
					.findViewById(R.id.profilePictureIcon);
			icon.setProfileId(jid);
			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Check if it's me
					if (myJid.equals(jid)) {
						// TODO: This is 100% pointless and should be removed :)
						switch (new Random().nextInt(2)) {
						case 0:
							Toast.makeText(getApplicationContext(),
									"Look familiar?", Toast.LENGTH_SHORT)
									.show();
							return;
						default:
							Toast.makeText(getApplicationContext(),
									"Guess who?", Toast.LENGTH_SHORT).show();
						}

					} else if (database.getIncomingUser(jid) != null) {
						// Then this is someone broadcasting to me
						Toast.makeText(
								getApplicationContext(),
								"It's "
										+ database.getIncomingUser(jid)
												.getFirstName(),
								Toast.LENGTH_SHORT).show();
					} else if (database.getOutgoingUser(jid) != null) {
						// Then this is someone I'm broadcasting to
						Toast.makeText(
								getApplicationContext(),
								"It's "
										+ database.getOutgoingUser(jid)
												.getFirstName(),
								Toast.LENGTH_SHORT).show();
					} else {
						// This is a stranger to me
						Toast.makeText(
								getApplicationContext(),
								database.getIncomingUser(mucName)
										.getFirstName() + "'s friend",
								Toast.LENGTH_SHORT).show();
					}

				}
			});

			linLayout.addView(view);

		}

	}
}

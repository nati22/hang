package com.hangapp.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.common.base.CharMatcher;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.YouFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.model.callback.MucListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.xmpp.XMPP;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.SntpClient;
import com.hangapp.android.util.Utils;

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
public final class FirebaseChatActivity extends BaseActivity implements
/* MucListener, */IncomingBroadcastsListener {

	// UI widgets.
	private EditText editTextChatMessage;
	private ListView listViewChatCells;
	private MessageAdapter adapter;

	// Member datum.
	private String mucName;

	private List<Message> messages = new ArrayList<Message>();
	private List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
	private ChatMessageAdapter chatAdapter;

	private String myJid;
	private LinearLayout linLayoutInterested;
	private List<String> listInterestedJids = new ArrayList<String>();

	/* Stuff added for Firebase */
	private boolean isHost = false;
	private static final String TAG = "FirebaseChatActivity";
	private static final String FIREBASE_URL = "https://hangapp.firebaseio.com/";
	private static final String CHATS_URL = FIREBASE_URL + "chats/";
	private Firebase chatFirebase;
	private Firebase chatMembersFirebase;
	private Firebase chatMemberMyselfFirebase;
	private Firebase chatMembersPresentFirebase;
	private Firebase chatMemberPresentMyselfFirebase;
	private Firebase chatHostFirebase;
	private Firebase chatMessagesFirebase;

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
		// Find out whether the user is the host or not
		isHost = getIntent().getBooleanExtra(Keys.IS_HOST, false);

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

		setupChatFirebase();

		// Reference Views.
		editTextChatMessage = (EditText) findViewById(R.id.editTextChatMessage);
		listViewChatCells = (ListView) findViewById(R.id.listViewChatCells);
		linLayoutInterested = (LinearLayout) findViewById(R.id.linearLayoutInterestedChat);

		// Setup adapter.
		/*
		 * adapter = new MessageAdapter(this, R.id.listViewChatCells);
		 * listViewChatCells.setAdapter(adapter); listViewChatCells
		 * .setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		 * listViewChatCells.setStackFromBottom(true);
		 */

		chatAdapter = new ChatMessageAdapter(this, R.id.listViewChatCells);
		listViewChatCells.setAdapter(chatAdapter);
		listViewChatCells
				.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listViewChatCells.setStackFromBottom(true);

	}

	private void setupChatFirebase() {
		String chatFirebaseUrl = CHATS_URL + mucName;
		chatFirebase = new Firebase(chatFirebaseUrl);
		chatHostFirebase = new Firebase(chatFirebaseUrl + "/"
				+ Keys.FIREBASE_HOST_ID);
		chatMembersFirebase = new Firebase(chatFirebaseUrl + "/"
				+ Keys.FIREBASE_MEMBERS);
		chatMessagesFirebase = new Firebase(chatFirebaseUrl + "/"
				+ Keys.FIREBASE_MESSAGES);
		chatMembersPresentFirebase = new Firebase(chatFirebaseUrl + "/"
				+ Keys.FIREBASE_MEMBERS_PRESENT);

		// If this is the host, we'll create the session on Firebase
		if (isHost) {
			chatFirebase.child(Keys.FIREBASE_HOST_ID).setValue(myJid);
		} else {
			// make sure chat still exists before continuing
		}

		// add yourself to members //TODO THIS SHOULD HAPPEN WHEN USER SAYS THEYRE
		// INTERESTED
		chatMemberMyselfFirebase = chatMembersFirebase.child(myJid);
		chatMemberMyselfFirebase.setValue(myJid);

		// add yourself to currently present
		chatMemberPresentMyselfFirebase = chatMembersPresentFirebase.child(myJid);
		chatMemberPresentMyselfFirebase.setValue(myJid);
		chatMemberPresentMyselfFirebase.onDisconnect().removeValue();

	}

	@Override
	protected void onResume() {
		super.onResume();

		// "Subscribe" this activity to new MUC messages.
		/* xmpp.addMucListener(mucName, this); */

		// Setup listener
		database.addIncomingBroadcastsListener(this);

		// Clear out the member datum list of messages for this Activity.
	//	messages.clear();

	//	final List<Message> msgs = new ArrayList<Message>();
		final List<ChatMessage> chatMsgs = new ArrayList<ChatMessage>();

		/** Get the previous messages from Firebase */
		chatMessagesFirebase.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildRemoved(DataSnapshot arg0) { }

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) { }

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) { }

			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {

				String text = arg0.child("text").getValue().toString();
				String jid = arg0.child("jid").getValue().toString();

				// messages.add(new ChatMessage("", ""));
	//			Message msg = new Message();
	//			msg.setBody(text);
	//			msg.setFrom(jid);
	//			msgs.add(msg);

				ChatMessage chatMsg = new ChatMessage(text, jid);
				chatMsgs.add(chatMsg);
	//			Log.i(TAG, "msgs size = " + msgs.size());
				chatMessages.add(chatMsg); 
				Log.i(TAG, "chatMsgs size = " + chatMsgs.size());
				chatAdapter.notifyDataSetChanged();
			}

			@Override
			public void onCancelled(FirebaseError arg0) { }
		});

		// messages.addAll(chatMsgs);

		// Then, grab the cached messages for this MUC from SQLite.
		// final List<Message> mucMessages = xmpp.getAllMessages(mucName);

		// Log.d(TAG, "chatMsgs size " + msgs.size());

		messages.addAll(chatMsgs);
		Log.e(TAG, "messages size " + messages.size());
		Log.e(TAG, "chatMsgs size " + chatMsgs.size());

		// Refresh list
		onIncomingBroadcastsUpdate(database.getMyIncomingBroadcasts());

		// Notify data set changed.
		chatAdapter.notifyDataSetChanged();
		Log.e(TAG, "chatAdapter.notified");
	}

	@Override
	protected void onPause() {
		super.onPause();

		// remove user from currently present members in firebase chat
		chatMembersPresentFirebase.child(myJid).removeValue();

		// Remove listener
		database.removeIncomingBroadcastsListener(this);

		// "Unsubscribe" this activity from new MUC messages.
		/* xmpp.removeMucListener(mucName, this); */
	}

	/**
	 * XML OnClickListener for the "sendMessage" button.
	 */
	public void sendMessage(View v) {
		final String message = editTextChatMessage.getText().toString().trim();

		if (message.equals("")) {
			Log.e("FirebaseChatActivity.sendMessage", "Can't send empty message");
			Toast.makeText(getApplicationContext(), "Write something first!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		final String myJid = database.getMyJid();

		Toast.makeText(getApplicationContext(), "" + getNTPtime(),
				Toast.LENGTH_LONG).show();

		chatMessagesFirebase.child("" + getNTPtime()).setValue(
				new ChatMessage(message, myJid));

		// xmpp.sendMucMessage(myJid, mucName, message);
		editTextChatMessage.setText("");
	}

	class ChatMessage extends Message {

		public ChatMessage(String text, String jid) {
			this.text = text;
			this.jid = jid;
		}

		private String text;
		private String jid;

		public String getText() {
			return text;
		}

		public String getJid() {
			return jid;
		}
	}

	class MessageAdapter extends ArrayAdapter<Message> {

		public MessageAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId, FirebaseChatActivity.this.messages);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message = getItem(position);

			String fromJid = Utils.parseJidFromMessage(message);
			String from = Utils.convertJidToName(fromJid, database);

			Log.e(TAG, "getView called for msg " + message.getBody());

			// Inflate the cell if necessary.
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

	class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

		public ChatMessageAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId,
					FirebaseChatActivity.this.chatMessages);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.e(TAG, "getView called");

			ChatMessage message = getItem(position);

			String fromJid = message.getJid();
			/** This will mess up if we don't know who it is. */
			String fromName = Database.getInstance().getIncomingUser(fromJid) != null ? Database
					.getInstance().getIncomingUser(fromJid).getFullName()
					: "Stranger";

			// Inflate the cell if necessary.
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
			textViewMessageBody.setText(message.getText());
			textViewMessageFrom.setText(fromName + ":  ");

			return convertView;
		}
	}

	/*
	 * @Override public void onMucMessageUpdate(String mucName, final
	 * List<Message> messages) { // Must explicitly run on UI thread because the
	 * Smack packet listener // runs on a daemon (background) thread.
	 * runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { FirebaseChatActivity.this.messages.clear();
	 * FirebaseChatActivity.this.messages.addAll(messages);
	 * adapter.notifyDataSetChanged();
	 * 
	 * listViewChatCells.smoothScrollToPosition(adapter.getCount() - 1); } }); }
	 */

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		// If this is the user's own chat.
		if (myJid.equals(mucName)) {

			// Check if my interested list has been updated
			if (!database.getMyProposal().getInterested()
					.equals(listInterestedJids)) {
				listInterestedJids.clear();
				listInterestedJids.addAll(database.getMyProposal().getInterested());

				// Update horizontal list
				updateHorizontalList(listInterestedJids, linLayoutInterested);
			}
		}
		// If this is someone else's chat, and they are broadcasting to you.
		else if (incomingBroadcasts.contains(database.getIncomingUser(mucName))) {
			final User host = database.getIncomingUser(mucName);

			Proposal hostProposal = host.getProposal();
			List<String> hostInterested = hostProposal.getInterested();

			// If the host has a Proposal
			if (hostProposal != null) {

				// Check if their interested list has been updated
				if (!hostInterested.equals(listInterestedJids)) {

					listInterestedJids.clear();
					listInterestedJids.addAll(host.getProposal().getInterested());

					// Update horizontal list
					updateHorizontalList(listInterestedJids, linLayoutInterested);

					// TODO: If you're not Interested, get lost
					if (!hostInterested.contains(database.getMyJid())) {
						onBackPressed();
					}

				} else {
					Log.i("FirebaseChatActivity.onIncomingBroadcastsUpdate()",
							"their list hasn't changed: "
									+ listInterestedJids.toString());
				}
			}

		}

		// If this is someone else's Chat, but the user isn't broadcasting to you.
		else {
			Log.e("FirebaseChatActivity.onIncomingBroadcastsUpdate()",
					"Attempted to join MUC for a user who is not broadcasting to you.");
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
							Toast.makeText(getApplicationContext(), "Look familiar?",
									Toast.LENGTH_SHORT).show();
							return;
						default:
							Toast.makeText(getApplicationContext(), "Guess who?",
									Toast.LENGTH_SHORT).show();
						}

					} else if (database.getIncomingUser(jid) != null) {
						// Then this is someone broadcasting to me
						Toast.makeText(getApplicationContext(),
								"It's " + database.getIncomingUser(jid).getFirstName(),
								Toast.LENGTH_SHORT).show();
					} else if (database.getOutgoingUser(jid) != null) {
						// Then this is someone I'm broadcasting to
						Toast.makeText(getApplicationContext(),
								"It's " + database.getOutgoingUser(jid).getFirstName(),
								Toast.LENGTH_SHORT).show();
					} else {
						// This is a stranger to me
						Toast.makeText(
								getApplicationContext(),
								database.getIncomingUser(mucName).getFirstName()
										+ "'s friend", Toast.LENGTH_SHORT).show();
					}

				}
			});
			linLayout.addView(view);
		}
	}

	private long getNTPtime() {
		SntpClient client = new SntpClient();
		if (client.requestTime(Utils.someCaliNtpServers[0], 10000)) {

			return client.getNtpTime() + SystemClock.elapsedRealtime()
					- client.getNtpTimeReference();
		} else {
			Toast.makeText(getApplicationContext(), "NTP error",
					Toast.LENGTH_SHORT).show();
			return 0;
		}
	}
}

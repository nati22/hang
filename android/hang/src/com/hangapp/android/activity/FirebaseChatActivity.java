package com.hangapp.android.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.YouFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.network.xmpp.XMPP;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.SntpClient;
import com.hangapp.android.util.Utils;

// TODO Need to be careful of lost conenction to the internet/Firebase

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
		IncomingBroadcastsListener {

	private static final String LOG_ID = "R2D2:  ";

	// UI widgets.
	private EditText editTextChatMessage;
	private ListView listViewChatCells;
	private Button buttonSendMessage;

	// Member datum.
	private String mucName;

	private List<ChatMessageGroup> chatMessageGroups = new ArrayList<ChatMessageGroup>();
	private ChatMessageAdapter chatAdapter;

	private List<String> otherPresentUsers = new ArrayList<String>();

	private String myJid;
	private LinearLayout linLayoutInterested;
	private List<String> listInterestedJids = new ArrayList<String>();

	/* Stuff added for Firebase */
	private boolean isHost = false;
	private static final String TAG = "FirebaseChatActivity";

	private Firebase chatFirebase;
	private Firebase chatMembersFirebase;
	private Firebase chatMemberMyselfFirebase;
	private Firebase chatMembersPresentFirebase;
	private Firebase chatMemberPresentMyselfFirebase;
	private Firebase chatHostFirebase;
	private Firebase chatMessagesFirebase;

	// Dependencies.
	private Database database;
	private RestClient restClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Instantiate dependencies
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getApplicationContext());

		// Pull the Muc name from the Intent.
		mucName = getIntent().getStringExtra(Keys.HOST_JID);
		// Find out whether the user is the host or not
		isHost = getIntent().getBooleanExtra(Keys.IS_HOST, false);

		// Sanity check on the Muc name pulled from the Intent.
		if (mucName == null) {
			Toast.makeText(getApplicationContext(), "Muc name was null",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		// Join the Muc.
		// TODO: Remove me. Don't directly join the MUC from here; instead just
		// add this MUC to the list of MUCs to join and have XMPPIntentService
		// do it for you.
		myJid = database.getMyJid();
		// xmpp.joinMuc(mucName, myJid);

		setupChatFirebase();

		// Reference Views.
		editTextChatMessage = (EditText) findViewById(R.id.editTextChatMessage);
		listViewChatCells = (ListView) findViewById(R.id.listViewChatCells);
		linLayoutInterested = (LinearLayout) findViewById(R.id.linearLayoutInterestedChat);
		buttonSendMessage = (Button) findViewById(R.id.buttonSendMessage);

		// Setup adapter.
		chatAdapter = new ChatMessageAdapter(this, R.id.listViewChatCells);
		listViewChatCells.setAdapter(chatAdapter);
		listViewChatCells
				.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listViewChatCells.setStackFromBottom(true);

	}

	private void setupChatFirebase() {
		String chatFirebaseUrl = Keys.CHATS_URL + mucName;
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

		// add yourself to members
		// TODO THIS SHOULD HAPPEN WHEN USER SAYS THEY'RE INTERESTED
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

		// Setup listener
		database.addIncomingBroadcastsListener(this);

		final List<ChatMessage> chatMsgs = new ArrayList<ChatMessage>();

		/** Get the previous messages from Firebase */
		chatMessagesFirebase.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {

				String text = arg0.child("text").getValue().toString();
				String jid = arg0.child("jid").getValue().toString();
				String time = arg0.getName().toString();

				receiveNewMessage(new ChatMessage(text, jid, time));
				chatAdapter.notifyDataSetChanged();
				Log.d(TAG, "added msg " + text);
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});

		chatMembersPresentFirebase
				.addChildEventListener(new ChildEventListener() {

					@Override
					public void onChildRemoved(DataSnapshot arg0) {
						String removed_jid = arg0.getValue().toString();
						Log.d(TAG, "user " + removed_jid + " left.");
						otherPresentUsers.remove(removed_jid);

					}

					@Override
					public void onChildMoved(DataSnapshot arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onChildChanged(DataSnapshot arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onChildAdded(DataSnapshot arg0, String arg1) {
						if (arg0 != null) {
							String added_jid = arg0.getValue().toString();
							if (!added_jid.equals(myJid)) {
								Log.d(TAG, "user " + added_jid
										+ " just entered the chat");
								otherPresentUsers.add(added_jid);
							}
						}
					}

					@Override
					public void onCancelled(FirebaseError arg0) {
						// TODO Auto-generated method stub

					}
				});

		chatFirebase.addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot arg0) {

				if (arg0.hasChildren() == false) {

					if (!mucName.equals(database.getMyJid())) {

						User guy = database.getIncomingUser(mucName);

						if (guy != null) {
							Toast.makeText(getApplicationContext(),
									guy.getFirstName() + " deleted his proposal!",
									Toast.LENGTH_SHORT).show();
						}

						finish();
					}
				}
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});

		// chatFirebase.onDisconnect()ChatMessage

		// Refresh list
		onIncomingBroadcastsUpdate(database.getMyIncomingBroadcasts());

		// Notify data set changed.
		chatAdapter.notifyDataSetChanged();
	}

	private void receiveNewMessage(ChatMessage newMsg) {
		// add to ChatMessage groups
		if (!chatMessageGroups.isEmpty()) {
			// get last message group
			ChatMessageGroup lastGroup = chatMessageGroups.get(chatMessageGroups
					.size() - 1);
			// if it's same sender, add to last group
			if (lastGroup.getSenderJid().equals(newMsg.jid)) {
				lastGroup.addChatMessage(newMsg);
			} else {

				// else create new message group
				chatMessageGroups.add(new ChatMessageGroup(newMsg));
			}
		} else {

			// add new msg to first group
			chatMessageGroups.add(new ChatMessageGroup(newMsg));
		}
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

	public void sendMessage(View v) {
		final String message = editTextChatMessage.getText().toString().trim();

		if (message.equals("")) {
			Log.e("FirebaseChatActivity.sendMessage", LOG_ID
					+ "Can't send empty message");
			Toast.makeText(getApplicationContext(), "Write something first!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		buttonSendMessage.setEnabled(false);
		// This AsyncTask gets the NTP server time
		// and handles pushing data to Firebase
		new SendMessageAsyncTask().execute(Utils.someCaliNtpServers[0]);

		// Get IDs of Interested users WHO ARE NOT PRESENT in the chat
		List<String> usersToNotify = new ArrayList<String>();
		List<String> interestedList = isHost ? database.getMyProposal()
				.getInterested() : database.getIncomingUser(mucName).getProposal()
				.getInterested();

		// Let's only send notifications if there are actually users to send them
		// to
		if (interestedList.size() > 0) {
			for (String userJid : interestedList) {
				if (!otherPresentUsers.contains(userJid) && !userJid.equals(myJid)) {
					Log.d(TAG, "will send to " + userJid);
					usersToNotify.add(userJid);
				}
			}

			// Send a notification to the other host as well, if applicable
			if (!isHost && !otherPresentUsers.contains(mucName)) {
				usersToNotify.add(mucName);
			}

			restClient.sendChatNotification(usersToNotify, mucName);
		}

	}

	private class SendMessageAsyncTask extends AsyncTask<String, Void, Void> {

		final String message = editTextChatMessage.getText().toString().trim();
		private long ntpTime = 0;

		@Override
		protected Void doInBackground(String... params) {
			ntpTime = getNTPtime();
			return null;
		}

		protected void onPostExecute(Void result) {
			if (ntpTime != 0) {
				String time = "" + ntpTime;
				chatMessagesFirebase.child("" + time).setValue(
						new ChatMessage(message, myJid, time));

				editTextChatMessage.setText("");

				Log.d(TAG, LOG_ID + "Sent message \"" + message + "\"");
			} else {
				Log.e(TAG, LOG_ID + "Failed to send message.");
				Toast.makeText(getApplicationContext(), "Error sending message",
						Toast.LENGTH_SHORT).show();
			}

			buttonSendMessage.setEnabled(true);

		}

	}

	class ChatMessage {

		public ChatMessage(String text, String jid, String time) {
			this.text = text;
			this.jid = jid;
			this.time = time;
		}

		private String text;
		private String jid;
		private String time;

		public String getText() {
			return text;
		}

		public String getJid() {
			return jid;
		}

		public String getTime() {
			return time;
		}
	}

	class ChatMessageGroup {

		public ChatMessageGroup(ArrayList<ChatMessage> msgs) {
			messages = msgs;
		}

		public ChatMessageGroup(ChatMessage msg) {
			messages = new ArrayList<ChatMessage>();
			addChatMessage(msg);
		}

		private String sender = "";
		private ArrayList<ChatMessage> messages;

		public ArrayList<ChatMessage> getChatMessages() {
			return messages;
		}

		public ChatMessage getChatMessageAt(int pos) {
			return messages.get(pos);
		}

		public boolean addChatMessage(ChatMessage msg) {
			if (sender.equals("")) {
				sender = msg.jid;
			}
			return messages.add(msg);
		}

		public ChatMessage getLastChatMessage() {
			if (!messages.isEmpty()) {
				return messages.get(messages.size() - 1);
			} else {
				return null;
			}
		}

		public String getSenderJid() {
			if (!sender.equals("")) {
				return sender;
			} else {
				return null;
			}
		}

		public int size() {
			return messages.size();
		}
	}

	/* class ChatMessageAdapter extends ArrayAdapter<ChatMessage> { */
	class ChatMessageAdapter extends ArrayAdapter<ChatMessageGroup> {

		Database db;
		Context context;

		public ChatMessageAdapter(Context context, int textViewResourceId) {
			/*
			 * super(context, textViewResourceId,
			 * FirebaseChatActivity.this.chatMessages);
			 */
			super(context, textViewResourceId,
					FirebaseChatActivity.this.chatMessageGroups);
			this.context = context;
			db = Database.getInstance();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			// ChatMessage message = getItem(position);
			ChatMessageGroup messageGroup = getItem(position);
			String fromJid = messageGroup.getSenderJid();

			// Determine if the message is from 'Me'
			boolean isMyMessage = fromJid.equals(db.getMyJid());
			// Inflate the cell if necessary.
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.cell_chat_message, null);

				// Reference views
/*				holder.relLayoutBg = (RelativeLayout) convertView
						.findViewById(R.id.chat_message_background);*/
				holder.profilePictureView = (ProfilePictureView) convertView
						.findViewById(R.id.profilePictureViewMessageFrom);
				holder.textViewMsgFrom = (TextView) convertView
						.findViewById(R.id.textViewMessageFrom2);
				holder.linLayoutProfilePicHolder = (LinearLayout) convertView
						.findViewById(R.id.profilePictureViewHolder);
				holder.linLayoutMessageList = (LinearLayout) convertView
						.findViewById(R.id.linLayoutMsgList);
				holder.viewBottomDivider = (View) convertView
						.findViewById(R.id.bottom_divider);

				convertView.setTag(holder);

			} else {
				// convertview already exists
				holder = (ViewHolder) convertView.getTag();
			}

			// clear linLayout
			holder.linLayoutMessageList.removeAllViews();

			// add messages to linlayout
			for (int i = 0; i < messageGroup.size(); i++) {
				// get msg
				ChatMessage msg = messageGroup.getChatMessageAt(i);

				// get time
				long secs = Long.parseLong(msg.getTime());
				SimpleDateFormat sdf = new SimpleDateFormat("M/d h:mm:ss a");

				// if same day, don't show day (just time)
				/* need extra logic because joda DateTime week goes 
				 * from M-Su while Calendar goes from Su-Sat */
				int day_of_week = (new DateTime(new Date(secs)).getDayOfWeek() + 1) % 7;
				int msg_day_of_week = Calendar.getInstance().get(
						Calendar.DAY_OF_WEEK);

				if (day_of_week == msg_day_of_week)
					sdf = new SimpleDateFormat("h:mm:ss a");

				String time = sdf.format(new Date(secs));

				// create TextView
				TextView tView = new TextView(context);
				tView.setText(msg.text + "  (" + time + ")");
				tView.setTextSize(getResources().getDimensionPixelSize(
						R.dimen.chat_text_size));
				tView.setFocusable(false);

				LinearLayout.LayoutParams tParams = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				int scale = (int) getResources().getDisplayMetrics().density;
				if (isMyMessage) {
					tParams.setMargins(10 * scale, 2 * scale, 0, 0);
				} else {
					tParams.setMargins(0, 2 * scale, 10 * scale, 0);
				}
				tView.setLayoutParams(tParams);

				// add TextView to LinearLayout
				holder.linLayoutMessageList.addView(tView);
			}

			// Align xml assets according to sender
			{
				int alignRight = RelativeLayout.ALIGN_PARENT_RIGHT;
				int alignLeft = RelativeLayout.ALIGN_PARENT_LEFT;

				// move profile pic holder to other side
				RelativeLayout.LayoutParams paramsProfilePicHolder = new RelativeLayout.LayoutParams(
						(int) getResources().getDimension(
								R.dimen.chat_user_name_width),
						LayoutParams.WRAP_CONTENT);
				paramsProfilePicHolder
						.addRule(isMyMessage ? alignRight : alignLeft);
				holder.linLayoutProfilePicHolder
						.setLayoutParams(paramsProfilePicHolder);

				// move message list to other side
				RelativeLayout.LayoutParams paramsLinLayoutMsgList = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				paramsLinLayoutMsgList
						.addRule(isMyMessage ? alignLeft : alignRight);
				int scale = (int) getResources().getDisplayMetrics().density;
				paramsLinLayoutMsgList.setMargins(10 * scale, 2 * scale,
						10 * scale, 0);
				holder.linLayoutMessageList.setPadding(0, 2, 0, 2);
				holder.linLayoutMessageList.setLayoutParams(paramsLinLayoutMsgList);

			}

			// If isMyMessage, this will become null
			User fromUser = db.getIncomingUser(fromJid);

			// Set the name
			String fromName = "Stranger";
			if (isMyMessage) {
				fromName = "Me";
			} else if (fromUser != null) {
				fromName = fromUser.getFirstName();
			} else {
				if (fromJid != null && !(fromJid.length() == 0)) {
					Log.e(TAG, "Unable to recognize jid " + fromJid);
				} else {
					Log.e(TAG, "Empty or null message.getJid() at pos " + position);
				}
			}

			// Populate Views.
			holder.textViewMsgFrom.setText(fromName);
			holder.profilePictureView.setProfileId(fromJid);
			return convertView;
		}

		class ViewHolder {
//			RelativeLayout relLayoutBg;
			LinearLayout linLayoutProfilePicHolder;
			ProfilePictureView profilePictureView;
			TextView textViewMsgFrom;
			LinearLayout linLayoutMessageList;
			View viewBottomDivider;
		}
	}

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
							LOG_ID + "their list hasn't changed: "
									+ listInterestedJids.toString());
				}
			}

		}

		// If this is someone else's Chat, but the user isn't broadcasting to
		// you.
		else {
			Log.e("FirebaseChatActivity.onIncomingBroadcastsUpdate()",
					LOG_ID
							+ "Attempted to join MUC for a user who is not broadcasting to you.");
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

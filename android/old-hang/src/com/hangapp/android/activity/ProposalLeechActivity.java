package com.hangapp.android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.ActionBar;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.ProposalChatFragment;
import com.hangapp.android.activity.fragment.ProposalLeechFragment;
import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.User;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.BaseFragmentActivity;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.MyTabsListener;

//@ContentView(R.layout.activity_proposal_leech)
public final class ProposalLeechActivity extends BaseFragmentActivity {

	// @InjectView(R.id.host_text) private TextView hostName;
	// @InjectView(R.id.textViewLocation) private TextView location;
	// @InjectView(R.id.textViewTime) private TextView timeView;
	// @InjectView(R.id.interested_list) private ListView interestedList;
	// @InjectView(R.id.chat_listview) private ListView chatList;
	// @InjectView(R.id.chatTextField) private EditText chatField;
	// @InjectView(R.id.submitChatButton) private Button submitChatText;
	// @InjectView(R.id.toggleButton_interested) private ToggleButton
	// toggleButtonInterested;

//	@Inject
	private DefaultUser defaultUser;
//	@Inject
	private RestClient parseAreSomeClowns;
//	@Inject
//	private XMPP xmpp;

	private User host = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proposal_leech);
		
		// Instantiate dependencies.
		defaultUser = DefaultUser.getInstance();
		parseAreSomeClowns = new RestClientImpl(getApplicationContext());

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Turn on the "Up" button
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String jid = getIntent().getStringExtra(Keys.HOST_JID_KEY);
		host = defaultUser.getIncomingUser(jid);

		setTitle(host.getFirstName() + "'s proposal");

		// TODO: Fix tab text spacing issue.
		ActionBar.Tab tabA = actionBar.newTab().setText("    Details")
				.setIcon(R.drawable.roster);
		ActionBar.Tab tabB = actionBar.newTab().setText("    Chat")
				.setIcon(R.drawable.chat);

		Fragment rosterFragment = new ProposalLeechFragment();
		Fragment chatFragment = new ProposalChatFragment();

		tabA.setTabListener(new MyTabsListener(rosterFragment));
		tabB.setTabListener(new MyTabsListener(chatFragment));

		actionBar.addTab(tabA);
		actionBar.addTab(tabB);
	}
	// private InterestedArrayAdapter interestedAdapter;
	// private MUCArrayAdapter mucAdapter;
	// private MultiUserChat muc;
	// @Inject private ArrayList<Message> msgs;
	// private User host = null;

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// Integer jid = getIntent().getIntExtra(Keys.HOST_JID_KEY, 0);
	// if (getIntent().getIntExtra(Keys.HOST_JID_KEY, 0) != 0) {
	// host = defaultUser.getIncomingUser(jid);
	// Log.d("getIncomingUser(" + jid + ") = " + host.toString());
	// } else {
	// Log.e("Invalid User Host sent with Intent to ProposalLeechActivity");
	// finish();
	// return;
	// }
	//
	// // Set Activity Title
	// try {
	// getSupportActionBar().setTitle(host.getProposal().getDescription());
	// } catch (Exception e) {
	// Log.e("host proposal has no description. title not set");
	// }
	//
	// setupGUI();
	// joinMUC();
	// }
	//
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getSupportMenuInflater();
	// inflater.inflate(R.menu.activity_proposal_leech, menu);
	//
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	// @Override
	// protected void onResume() {
	// super.onResume();
	//
	// // Subscribe this Activity to the DefaultUser
	// defaultUser.addIncomingBroadcastsListener(this);
	//
	// List<User> interested = host.getProposal().getInterested();
	// User userCopy = defaultUser.getUserCopy(getApplicationContext());
	//
	// // Check off the Interested toggle button based on if the user already
	// // exists in the Interested list.
	// toggleButtonInterested.setChecked(interested.contains(userCopy));
	// }
	//
	// @SuppressWarnings("deprecation")
	// private void setupGUI() {
	// // Set host name textview
	// hostName = (TextView) findViewById(R.id.host_text);
	// hostName.setText(host.getFullName());
	//
	// // Set location textview
	// location = (TextView) findViewById(R.id.textViewLocation);
	// String locText = host.getProposal().getLocation();
	// if (locText != null && locText.length() > 0)
	// location.setText(locText);
	//
	// // Set time textview
	// timeView = (TextView) findViewById(R.id.textViewTime);
	// Date date = host.getProposal().getStartTime();
	// if (date != null)
	// timeView.setText(date.getHours() + ":" + date.getMinutes());
	//
	// // Inflate major GUI from XML
	// interestedList = (ListView) findViewById(R.id.interested_list);
	// chatList = (ListView) findViewById(R.id.chat_listview);
	// toggleButtonInterested = (ToggleButton)
	// findViewById(R.id.toggleButton_interested);
	//
	// interestedAdapter = new InterestedArrayAdapter(getApplicationContext(),
	// R.layout.cell_interested, host.getProposal().getInterested());
	// mucAdapter = new MUCArrayAdapter(getApplicationContext(),
	// R.layout.cell_chat_message, msgs);
	//
	// interestedList.setDivider(null);
	// interestedList.setAdapter(interestedAdapter);
	// chatList.setDivider(null);
	// chatList.setAdapter(mucAdapter);
	//
	// // Set up chat input GUI
	// chatField = (EditText) findViewById(R.id.chatTextField);
	// submitChatText = (Button) findViewById(R.id.submitChatButton);
	// submitChatText.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// String text = chatField.getText().toString();
	// if (text != null && text != "") {
	// Toast.makeText(getApplicationContext(),
	// "Sending message: " + text, Toast.LENGTH_SHORT)
	// .show();
	//
	// // Send the message to XMPP
	// xmpp.sendMessage(muc, text);
	// // Clear out the chat field
	// chatField.setText("");
	// }
	// }
	// });
	//
	// }
	//
	// private void joinMUC() {
	// muc = new MultiUserChat(XMPP.getXMPPConnection(), host.getJid()
	// + "@conference." + XMPP.JABBER_SERVER_URL);
	//
	// xmpp.joinMuc(muc, defaultUser.getUserCopy(getApplicationContext()),
	// host.getJid());
	//
	// muc.addMessageListener(new PacketListener() {
	// public void processPacket(Packet arg0) {
	// Message castedMessage = null;
	//
	// // Cast the Packet into a Message
	// try {
	// castedMessage = (Message) arg0;
	// } catch (Exception e) {
	// Log.e("Received packet that wasn't a message: "
	// + e.getMessage());
	// return;
	// }
	//
	// if (!msgs.contains(castedMessage)) {
	// Log.d("Receieved new message: " + castedMessage.getBody()
	// + ", from: " + castedMessage.getFrom());
	// Log.i("Message id: " + castedMessage.getPacketID());
	// runOnUiThread(new AddNewMessageRunnable(castedMessage));
	// }
	// }
	// });
	// }
	//
	// // TODO: Temporary test, remove this
	// public void inviteToMUC(View v) {
	// // new XMPP.InviteUserToMUCTask(getApplicationContext(), muc,
	// // Utils.getDefaultUserJID(getApplicationContext()), host.getJid())
	// // .execute();
	// xmpp.inviteUserToMuc(muc, host.getJid(),
	// Utils.getDefaultUserJID(getApplicationContext()));
	// }
	//
	// /**
	// * Helper class that simply extends runnable: the purpose of this helper
	// * class is to both add the message to the message array AND
	// * notifyDataSetChanged() from the GUI thread.
	// */
	// private class AddNewMessageRunnable implements Runnable {
	// private Message message;
	//
	// public AddNewMessageRunnable(Message message) {
	// this.message = message;
	// }
	//
	// public void run() {
	// if (!msgs.contains(message)) {
	// msgs.add(message);
	// mucAdapter.notifyDataSetChanged();
	// }
	// }
	// }
	//
	// // Refactor to be static inner class.
	// // Need InterestedArrayAdapter that tracks interested list in Proposal
	// static class InterestedArrayAdapter extends BaseArrayAdapter<User> {
	//
	// public InterestedArrayAdapter(Context context, int textViewResourceId,
	// List<User> interestedUsers) {
	// super(context, textViewResourceId, interestedUsers);
	// }
	//
	// // @Override
	// // public View getView(int position, View convertView, ViewGroup parent)
	// // {
	// //
	// // // Retrieve corresponding User for this specific cell
	// // User user = host.getProposal().getInterested().get(position);
	// //
	// // // If it's the first time loading this cell
	// // // if (convertView == null) {
	// //
	// // // Get an inflater object
	// // LayoutInflater inflater = (LayoutInflater) getApplicationContext()
	// // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// // // Get xml layout for this cell
	// // convertView = inflater.inflate(R.layout.cell_interested, null);
	// //
	// // TextView interestedName = (TextView) convertView
	// // .findViewById(R.id.interested_name);
	// //
	// // interestedName.setText(user.getFullName());
	// // interestedName.setOnClickListener(new OnClickListener() {
	// // @Override
	// // public void onClick(View v) {
	// // Toast.makeText(getApplicationContext(),
	// // "Pressuring user to join", Toast.LENGTH_SHORT)
	// // .show();
	// // }
	// // });
	// // // }
	// // return convertView;
	// // }
	//
	// @Override
	// public View getViewEnhanced(User object, View convertedView) {
	// TextView interestedName = (TextView) convertedView
	// .findViewById(R.id.interested_name);
	//
	// interestedName.setText(object.getFullName());
	// interestedName.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// Toast.makeText(getContext(), "Pressuring user to join",
	// Toast.LENGTH_SHORT).show();
	// }
	// });
	// return convertedView;
	// }
	//
	// }
	//
	// static class MUCArrayAdapter extends BaseArrayAdapter<Message> {
	//
	// String lastSender = "";
	//
	// public MUCArrayAdapter(Context context, int textViewResourceId,
	// List<Message> messages) {
	// super(context, textViewResourceId, messages);
	// }
	//
	// // @Override
	// // public View getView(int position, View convertView, ViewGroup parent)
	// // {
	// // Message msg = msgs.get(position);
	// // TextView msgTextView;
	// // // if (convertView == null) {
	// // LayoutInflater inflater = (LayoutInflater) getApplicationContext()
	// // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// // convertView = inflater.inflate(R.layout.cell_chat_message, null);
	// // msgTextView = (TextView) convertView
	// // .findViewById(R.id.message_text);
	// //
	// // // Strip From for actual sender JID
	// // String senderTextRaw = msg.getFrom().split("/")[1];
	// // String messageText = msg.getBody();
	// //
	// // if (lastSender != senderTextRaw) {
	// // msgTextView.setText(Html.fromHtml("<b>" + senderTextRaw
	// // + ": </b>" + "<small>" + messageText + "</small>"));
	// // lastSender = senderTextRaw;
	// // } else {
	// // msgTextView.setText(Html.fromHtml("<small>" + messageText
	// // + "</small>"));
	// // }
	// // // }
	// //
	// // return convertView;
	// // }
	//
	// @Override
	// public View getViewEnhanced(Message object, View convertedView) {
	// TextView msgTextView = (TextView) convertedView
	// .findViewById(R.id.message_text);
	//
	// // Strip From for actual sender JID
	// String senderTextRaw = object.getFrom().split("/")[1];
	// String messageText = object.getBody();
	//
	// // TODO: Remove manual HTML formatting
	// if (lastSender != senderTextRaw) {
	// msgTextView.setText(Html.fromHtml("<b>" + senderTextRaw
	// + ": </b>" + "<small>" + messageText + "</small>"));
	// lastSender = senderTextRaw;
	// } else {
	// msgTextView.setText(Html.fromHtml("<small>" + messageText
	// + "</small>"));
	// }
	//
	// return convertedView;
	// }
	//
	// }
	//
	// public void setInterested(View v) {
	// List<User> interested = host.getProposal().getInterested();
	// User userCopy = defaultUser.getUserCopy(getApplicationContext());
	//
	// if (toggleButtonInterested.isChecked()) {
	// if (!interested.contains(userCopy)) {
	// interested.add(userCopy);
	// Collections.sort(interested);
	// defaultUser.notifyAllListeners();
	// }
	// } else {
	// if (interested.contains(userCopy)) {
	// for (int i = 0; i < interested.size(); i++) {
	// interested.remove(userCopy);
	// }
	// defaultUser.notifyAllListeners();
	// }
	// }
	//
	// parseAreSomeClowns.updateSomeoneElsesProposal(host.getJid(),
	// host.getProposal());
	// }
	//
	// public void setConfirmed(View v) {
	// Toast.makeText(getApplicationContext(), "Not implemented yet.",
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	//
	// // Unsubscribe this Activity from the DefaultUser
	// defaultUser.removeIncomingBroadcastsListener(this);
	//
	// Log.i("Leaving MUC");
	// muc.leave();
	// }
	//
	// @Override
	// public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
	// interestedAdapter.notifyDataSetChanged();
	// }

}

package com.hangapp.android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.ProposalChatFragment;
import com.hangapp.android.activity.fragment.ProposalHostFragment;
import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.BaseFragmentActivity;
import com.hangapp.android.util.MyTabsListener;

//@ContentView(R.layout.activity_proposal_host)
public final class ProposalHostActivity extends BaseFragmentActivity {

	// @InjectView(R.id.host_text) private TextView hostName;
	// @InjectView(R.id.textViewLocation) private TextView location;
	// @InjectView(R.id.textViewTime) private TextView timeView;
	// @InjectView(R.id.interested_list) private ListView interestedList;
	// @InjectView(R.id.confirmed_list) private ListView confirmedList;
	// @InjectView(R.id.chat_listview) private ListView chatList;
	// @InjectView(R.id.chatTextField) private EditText chatField;
	// @InjectView(R.id.submitChatButton) private Button submitChatText;

	// @Inject
	private DefaultUser defaultUser;
	// @Inject
	private RestClient parseAreSomeClowns;

	// @Inject
	// private XMPP xmpp;

	// private InterestedArrayAdapter interestedAdapter;
	// private ConfirmedArrayAdapter confirmedAdapter;
	// private MUCArrayAdapter mucAdapter;
	// private MultiUserChat muc;
	// private User host;
	//
	// @Inject private ArrayList<Message> msgs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proposal_host);

		// Instantiate dependencies.
		defaultUser = DefaultUser.getInstance();
		parseAreSomeClowns = new RestClientImpl(getApplicationContext());

		ActionBar actionBar = getSupportActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		setTitle("My proposal");

		// Turn on the "Up" button
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// TODO: Fix tab text spacing issue.
		ActionBar.Tab tabA = actionBar.newTab().setText("    Details")
				.setIcon(R.drawable.roster);
		ActionBar.Tab tabB = actionBar.newTab().setText("    Chat")
				.setIcon(R.drawable.chat);

		Fragment rosterFragment = new ProposalHostFragment();
		Fragment chatFragment = new ProposalChatFragment();

		tabA.setTabListener(new MyTabsListener(rosterFragment));
		tabB.setTabListener(new MyTabsListener(chatFragment));

		actionBar.addTab(tabA);
		actionBar.addTab(tabB);

	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// // host = defaultUser.getUserCopy(getApplicationContext());
	//
	// // Set Activity Title
	// try {
	// // getSupportActionBar().setTitle(host.getProposal().getDescription());
	// } catch (Exception e) {
	// Log.e("host proposal has no description. title not set");
	// }
	//
	// setupGUI();
	// joinMUC();
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_proposal_host, menu);

		return super.onCreateOptionsMenu(menu);
	}

	// private void setupGUI() {
	// // // Set host name textview
	// // hostName.setText("You! (" + host.getFirstName() + ")");
	// //
	// // // Set location textview
	// // String locText = host.getProposal().getLocation();
	// // if (locText != null && locText.length() > 0)
	// // location.setText(locText);
	// //
	// // // Set time textview
	// // Date date = host.getProposal().getStartTime();
	// // if (date != null)
	// // timeView.setText(date.getHours() + ":" + date.getMinutes());
	// //
	// // interestedAdapter = new
	// // InterestedArrayAdapter(getApplicationContext(),
	// // R.layout.cell_interested, host.getProposal().getInterested());
	// // confirmedAdapter = new ConfirmedArrayAdapter(getApplicationContext(),
	// // R.layout.cell_confirmed, host.getProposal().getConfirmed());
	// // mucAdapter = new MUCArrayAdapter(getApplicationContext(),
	// // R.layout.cell_chat_message, msgs);
	// //
	// // interestedList.setDivider(null);
	// // interestedList.setAdapter(interestedAdapter);
	// // confirmedList.setDivider(null);
	// // confirmedList.setAdapter(confirmedAdapter);
	// // chatList.setDivider(null);
	// // chatList.setAdapter(mucAdapter);
	// //
	// // // Set up chat input GUI
	// // submitChatText.setOnClickListener(new OnClickListener() {
	// // @Override
	// // public void onClick(View v) {
	// // String text = chatField.getText().toString();
	// // if (text != null && text != "") {
	// // Toast.makeText(getApplicationContext(),
	// // "Sending message: " + text, Toast.LENGTH_SHORT)
	// // .show();
	// //
	// // // Send the message to XMPP
	// // xmpp.sendMessage(muc, text);
	// //
	// // // Clear out the chat field
	// // chatField.setText("");
	// // }
	// // }
	// // });
	//
	// }
	//
	// private void joinMUC() {
	// // muc = new MultiUserChat(XMPP.getXMPPConnection(), host.getJid()
	// // + "@conference." + XMPP.JABBER_SERVER_URL);
	// //
	// // xmpp.joinMuc(muc, defaultUser.getUserCopy(getApplicationContext()),
	// // host.getJid());
	// //
	// // muc.addMessageListener(new PacketListener() {
	// // public void processPacket(Packet arg0) {
	// // Message castedMessage = null;
	// //
	// // // Cast the Packet into a Message
	// // try {
	// // castedMessage = (Message) arg0;
	// // } catch (Exception e) {
	// // Log.e("Received packet that wasn't a message: "
	// // + e.getMessage());
	// // return;
	// // }
	// //
	// // if (!msgs.contains(castedMessage)) {
	// // runOnUiThread(new AddNewMessageRunnable(castedMessage));
	// // }
	// // }
	// // });
	// }
	//
	// // TODO: Temporary, remove this
	// public void inviteToMUC(View v) {
	// // xmpp.inviteUserToMuc(muc, host.getJid(),
	// // Utils.getDefaultUserJID(getApplicationContext()));
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
	// // do nothing
	// // if (!msgs.contains(message)) {
	// // msgs.add(message);
	// // mucAdapter.notifyDataSetChanged();
	// // }
	// }
	// }

	// // TODO: Refactor to be static inner class.
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
	// // // TODO: Only inflate a new cell if it's the first time loading this
	// // // cell.
	// // // TODO: Don't make a new Inflater for every cell.
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
	// }
	//
	// static class ConfirmedArrayAdapter extends BaseArrayAdapter<User> {
	//
	// public ConfirmedArrayAdapter(Context context, int textViewResourceId,
	// List<User> confirmedUsers) {
	// super(context, textViewResourceId, confirmedUsers);
	// }
	//
	// // @Override
	// // public View getView(int position, View convertView, ViewGroup parent)
	// // {
	// // User user = host.getProposal().getConfirmed().get(position);
	// //
	// // // if (convertView == null) {
	// //
	// // LayoutInflater inflater = (LayoutInflater) getApplicationContext()
	// // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// // convertView = inflater.inflate(R.layout.cell_confirmed, null);
	// //
	// // TextView confirmedName = (TextView) convertView
	// // .findViewById(R.id.confirmed_name);
	// //
	// // confirmedName.setText(user.getFullName());
	// // // }
	// // return convertView;
	// // }
	//
	// @Override
	// public View getViewEnhanced(User object, View convertedView) {
	// TextView confirmedName = (TextView) convertedView
	// .findViewById(R.id.confirmed_name);
	// confirmedName.setText(object.getFullName());
	//
	// return convertedView;
	// }
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
	// // String messageTime = "[" + new Date().getHours() + ":"
	// // + new Date().getMinutes() + "]";
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
	// // TODO: Remove manual HTML formatting.
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
	// }
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	//
	// // Log.i("Leaving MUC");
	// // muc.leave();
	// }
	//
	public boolean deleteMyProposal(MenuItem item) {
		Log.d("ProposalHostActivity.deleteMyProposal", "Deleting proposal: "
				+ defaultUser.getMyProposal().getDescription());

		defaultUser.deleteMyProposal();
		parseAreSomeClowns.deleteMyProposal();
		finish();

		return true;
	}
	//
	// @Override
	// public void onMyProposalUpdate(Proposal proposal) {
	// // interestedAdapter.notifyDataSetChanged();
	// // confirmedAdapter.notifyDataSetChanged();
	// }

}

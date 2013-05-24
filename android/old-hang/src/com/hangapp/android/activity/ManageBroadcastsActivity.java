package com.hangapp.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.ManageIncomingBroadcastsFragment;
import com.hangapp.android.activity.fragment.ManageOutgoingBroadcastsFragment;
import com.hangapp.android.util.BaseFragmentActivity;
import com.hangapp.android.util.MyTabsListener;

public final class ManageBroadcastsActivity extends BaseFragmentActivity {

	// @InjectView(R.id.outgoingLinearLayout) private LinearLayout outgoing;
	// @InjectView(R.id.incomingLinearLayout) private LinearLayout incoming;
	// @InjectView(R.id.incoming_listview) private ListView listViewIncoming;
	// @InjectView(R.id.outgoing_listview) private ListView listViewOutgoing;
	// @InjectView(R.id.radioButtonOutgoing) private RadioButton radioOutgoing;
	// @InjectView(R.id.radioButtonIncoming) private RadioButton radioIncoming;

	// // TODO: Remove the temporary "add one outgoing broadcast" GUI stuff.
	// @InjectView(R.id.editTextAddOutgoingBroadcast) private EditText
	// editTextAddOutgoingBroadcast;

	// @Inject private DefaultUser defaultUser;
	// @Inject private RestClient parseAreSomeClowns;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_broadcasts);

		ActionBar actionBar = getSupportActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		setTitle("Manage Broadcasts");

		// Turn on the "Up" button
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// TODO: Fix tab text spacing issue.
		ActionBar.Tab tabA = actionBar.newTab().setText("    Outgoing")
				.setIcon(R.drawable.outgoing);
		ActionBar.Tab tabB = actionBar.newTab().setText("    Incoming")
				.setIcon(R.drawable.incoming);

		Fragment outgoingBroadcastsFragment = new ManageOutgoingBroadcastsFragment();
		Fragment incomingBroadcastsFragment = new ManageIncomingBroadcastsFragment();

		tabA.setTabListener(new MyTabsListener(outgoingBroadcastsFragment));
		tabB.setTabListener(new MyTabsListener(incomingBroadcastsFragment));

		actionBar.addTab(tabA);
		actionBar.addTab(tabB);

		// // add Button click Listeners
		// radioOutgoing.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// if (isChecked) {
		// outgoing.setVisibility(View.VISIBLE);
		// incoming.setVisibility(View.GONE);
		// }
		// }
		// });
		//
		// radioIncoming.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// if (isChecked) {
		// outgoing.setVisibility(View.GONE);
		// incoming.setVisibility(View.VISIBLE);
		// Log.d("There are " + listViewIncoming.getChildCount()
		// + " elements");
		// }
		// }
		// });

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getSupportMenuInflater();
	// inflater.inflate(R.menu.activity_manage_broadcasts, menu);
	//
	// return super.onCreateOptionsMenu(menu);
	// }

	@Override
	protected void onResume() {
		super.onResume();

		// // Subscribe this Activity to changes it cares about
		// defaultUser.addIncomingBroadcastsListener(this);
		//
		//
		// defaultUser.notifyAllListeners();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Unsbscribe this Activity from changes it cares about
		// defaultUser.removeIncomingBroadcastsListener(this);
		// defaultUser.removeOutgoingBroadcastsListener(this);
	}

	@SuppressLint("NewApi")
	public void addNewOutgoingBroadcast(View v) {

		// String stringJID = editTextAddOutgoingBroadcast.getText().toString();
		//
		// if (!stringJID.trim().equals("")) {
		//
		// Toast.makeText(getApplicationContext(),
		// "Adding outgoing broadcast", Toast.LENGTH_LONG).show();
		//
		// // Parse the Integer form of JID that you want to broadcast to
		// Integer broadcasteeJID = Integer
		// .parseInt(editTextAddOutgoingBroadcast.getText().toString());
		//
		// // Upload the "New Outgoing Broadcast" request to the server
		// parseAreSomeClowns.addBroadcastee(broadcasteeJID);
		// } else {
		// Toast.makeText(getApplicationContext(),
		// "Please enter a valid phone number", Toast.LENGTH_LONG)
		// .show();
		//
		// Log.d("No phone number entered. Cannot add outgoing broadcast.");
		// }
	}

	/**
	 * This method subscribes/unsubscribes the user from the universal channel.
	 * It's necessary because of the OnClickListener's lack of access to a live
	 * Context (I'm still not convinced that an "old" [final] copy of the app
	 * Context will work properly.)
	 * 
	 * @param channel
	 *            The channel for which to modify subscription status
	 * @param subscribed
	 *            "true" subscribes, "false" unsubscribes
	 */
	public static void manageChannelSubscription(Context context,
			boolean subscribed) {
		// if (subscribed) {
		// /*
		// * From Parse API: "The provided Activity class specifies which
		// * Activity will be run when a user responds to notifications on
		// * this channel. This lets you handle push notifications on
		// * different channels in different ways."
		// */
		// PushService.subscribe(context, Keys.UNIVERSAL_CHANNEL,
		// HomeActivity.class);
		// } else {
		// PushService.unsubscribe(context, Keys.UNIVERSAL_CHANNEL);
		// }
	}

}

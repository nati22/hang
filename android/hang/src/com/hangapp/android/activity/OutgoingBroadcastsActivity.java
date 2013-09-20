package com.hangapp.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.YouFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.OutgoingBroadcastsListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.network.xmpp.XMPP;
import com.hangapp.android.util.BaseActivity;

/**
 * Get to this Activity via {@link YouFragment} and clicking the number of
 * Outgoing Broadcasts you have. <br />
 * <br />
 * This is a simple Activity that shows a ListView of your Outgoing broadcasts.
 * It also listens for changes in Outgoing Broadcasts so that it doesn't have to
 * refresh manually. <br />
 * <br />
 * For some reason, this Activity uses the Facebook {@link UiLifecycleHelper} to
 * "listen" to change in Facebook session state (whether or not you're logged
 * in). I'm not really sure what it needs it for, so TODO: remove Facebook
 * session state listener from this class.
 */
public final class OutgoingBroadcastsActivity extends BaseActivity implements
		OutgoingBroadcastsListener {

	// Member datum.
	private List<User> outgoingBroadcasts = new ArrayList<User>();

	// Dependencies.
	private Database database;
	private RestClient restClient;
	private XMPP xmpp;

	// UI stuff.
	private ListView listViewOutgoingBroadcasts;
	private OutgoingBroadcastsArrayAdapter adapter;

	// Facebook session state listener stuff.
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state,
				final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outgoing_broadcasts);

		// Instantiate dependencies.
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getApplicationContext());
		xmpp = XMPP.getInstance();

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Reference Views.
		listViewOutgoingBroadcasts = (ListView) findViewById(R.id.listViewOutgoingBroadcasts);

		// Setup ListView
		adapter = new OutgoingBroadcastsArrayAdapter(this,
				R.id.listViewOutgoingBroadcasts, outgoingBroadcasts);
		listViewOutgoingBroadcasts.setAdapter(adapter);
		listViewOutgoingBroadcasts
				.setEmptyView(findViewById(android.R.id.empty));

		// Facebook SDK stuff (lifted from Facebook SDK Android tutorial).
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		database.addOutgoingBroadcastsListener(this);
		onOutgoingBroadcastsUpdate(database.getMyOutgoingBroadcasts());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_outgoing_broadcasts,
				menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		database.removeOutgoingBroadcastsListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_edit_outgoing_broadcasts:
			// TODO
			return true;
		case R.id.menu_add_outgoing_broadcast:
			// TODO
			return true;
		default:
			Log.e("OutgoingBroadcastsActivity.onOptionsItemSelected",
					"Unknown MenuItem " + item.getTitle());
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean toggleDeleteButtons(MenuItem item) {
		adapter.showDeleteButtons = !adapter.showDeleteButtons;
		adapter.notifyDataSetChanged();

		return true;
	}

	public boolean addNewOutgoingBroadcasts(MenuItem item) {
		startAddOutgoingBroadcastsActivity(
				AddOutgoingBroadcastActivity.FRIEND_PICKER, RESULT_OK);

		return true;
	}

	class OutgoingBroadcastsArrayAdapter extends ArrayAdapter<User> {
		private boolean showDeleteButtons;

		public OutgoingBroadcastsArrayAdapter(Context context,
				int textViewResourceId, List<User> outgoingBroadcasts) {
			super(context, textViewResourceId, outgoingBroadcasts);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final User outgoingBroadcast = getItem(position);
			Availability hisStatus = outgoingBroadcast.getAvailability();

			// Inflate if necessary.
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.cell_outgoing_broadcast, null);
			}

			// Reference Views.
			ProfilePictureView profilePictureView = (ProfilePictureView) convertView
					.findViewById(R.id.profilePictureView);
			TextView textViewOutgoingBroadcastName = (TextView) convertView
					.findViewById(R.id.textViewOutgoingBroadcastName);
			TextView textViewOutgoingBroadcastStatus = (TextView) convertView
					.findViewById(R.id.textViewOutgoingBroadcastStatus);
			Button buttonDeleteOutgoingBroadcast = (Button) convertView
					.findViewById(R.id.buttonDeleteOutgoingBroadcast);

			// Set Views to have correct data for this User.
			profilePictureView.setProfileId(outgoingBroadcast.getJid());
			textViewOutgoingBroadcastName.setText(outgoingBroadcast
					.getFullName());
			textViewOutgoingBroadcastStatus
					.setText((hisStatus != null && hisStatus.isActive()) ? hisStatus
							.getDescription() : "");

			// Set OnClickListeners.
			buttonDeleteOutgoingBroadcast
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// Tell the user that you're deleting a broadcast.
							Log.i("OutgoingBroadcastsActivity.deleteOutgoingBroadcast",
									"Deleting "
											+ outgoingBroadcast.getFirstName()
											+ " from my Outgoing Broadcasts");
							Toast.makeText(
									getApplicationContext(),
									"Deleting "
											+ outgoingBroadcast.getFirstName()
											+ " from my Outgoing Broadcasts",
									Toast.LENGTH_SHORT).show();

							// Actually delete the outgoing Broadcast.
							List<String> broadcasts = new ArrayList<String>();
							broadcasts.add(outgoingBroadcast.getJid());
							restClient.deleteBroadcastees(xmpp, broadcasts);
						}
					});

			// Show/hide the "Delete" button if we need to.
			if (showDeleteButtons) {
				buttonDeleteOutgoingBroadcast.setVisibility(View.VISIBLE);
			} else {
				buttonDeleteOutgoingBroadcast.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	private void startAddOutgoingBroadcastsActivity(Uri data, int requestCode) {
		Intent intent = new Intent();
		intent.setData(data);
		intent.setClass(this, AddOutgoingBroadcastActivity.class);
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		} else if (resultCode == Activity.RESULT_OK) {
			// Do nothing for now
		}
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// Do nothing.
	}

	@Override
	public void onOutgoingBroadcastsUpdate(List<User> outgoingBroadcasts) {
		this.outgoingBroadcasts.clear();

		if (outgoingBroadcasts != null) {
			this.outgoingBroadcasts.addAll(outgoingBroadcasts);
		}

		adapter.notifyDataSetChanged();
	}

}

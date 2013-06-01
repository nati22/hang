package com.hangapp.newandroid.activity;

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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.model.callback.OutgoingBroadcastsListener;
import com.hangapp.newandroid.util.BaseFragmentActivity;

public final class OutgoingBroadcastsActivity extends BaseFragmentActivity
		implements OutgoingBroadcastsListener {

	private static final int REAUTH_ACTIVITY_CODE = 100;
	private List<User> outgoingBroadcasts = new ArrayList<User>();

	private Database database;

	private ListView listViewOutgoingBroadcasts;
	private OutgoingBroadcastsArrayAdapter adapter;

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
		startPickerActivity(AddOutgoingBroadcastActivity.FRIEND_PICKER,
				RESULT_OK);

		return true;
	}

	static class OutgoingBroadcastsArrayAdapter extends ArrayAdapter<User> {
		private boolean showDeleteButtons;

		public OutgoingBroadcastsArrayAdapter(Context context,
				int textViewResourceId, List<User> outgoingBroadcasts) {
			super(context, textViewResourceId, outgoingBroadcasts);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			User outgoingBroadcast = getItem(position);
			Availability hisStatus = outgoingBroadcast.getAvailability();

			// Inflate if necessary.
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.cell_outgoing_broadcast, null);
			}

			// Reference Views.
			ProfilePictureView profilePictureView = (ProfilePictureView) convertView
					.findViewById(R.id.profilePictureViewIcon);
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
					.setText(hisStatus != null ? hisStatus.getDescription()
							: "Unknown Availability");

			// Show/hide the "Delete" button if we need to.
			if (showDeleteButtons) {
				buttonDeleteOutgoingBroadcast.setVisibility(View.VISIBLE);
			} else {
				buttonDeleteOutgoingBroadcast.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	private void startPickerActivity(Uri data, int requestCode) {
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

	}

	@Override
	public void onOutgoingBroadcastsUpdate(List<User> outgoingBroadcasts) {
		this.outgoingBroadcasts.clear();
		this.outgoingBroadcasts.addAll(outgoingBroadcasts);

		adapter.notifyDataSetChanged();
	}

}

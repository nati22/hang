package com.hangapp.newandroid.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.UserDatabase;
import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.model.callback.IncomingBroadcastsListener;
import com.hangapp.newandroid.util.BaseFragmentActivity;

public class IncomingBroadcastsActivity extends BaseFragmentActivity implements
		IncomingBroadcastsListener {

	private List<User> incomingBroadcasts = new ArrayList<User>();

	private ListView listViewIncomingBroadcasts;
	private IncomingBroadcastsArrayAdapter adapter;

	private UserDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incoming_broadcasts);

		// Instantiate dependencies
		database = UserDatabase.getInstance();

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Reference Views.
		listViewIncomingBroadcasts = (ListView) findViewById(R.id.listViewIncomingBroadcasts);

		// Setup ListView
		adapter = new IncomingBroadcastsArrayAdapter(this,
				R.id.listViewIncomingBroadcasts, incomingBroadcasts);
		listViewIncomingBroadcasts.setAdapter(adapter);
		listViewIncomingBroadcasts
				.setEmptyView(findViewById(android.R.id.empty));
	}

	@Override
	protected void onResume() {
		super.onResume();

		database.addIncomingBroadcastsListener(this);
		onIncomingBroadcastsUpdate(database.getMyIncomingBroadcasts());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		database.removeIncomingBroadcastsListener(this);
	}

	static class IncomingBroadcastsArrayAdapter extends ArrayAdapter<User> {

		public IncomingBroadcastsArrayAdapter(Context context,
				int textViewResourceId, List<User> incomingBroadcasts) {
			super(context, textViewResourceId, incomingBroadcasts);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			User incomingBroadcast = getItem(position);
			Availability hisStatus = incomingBroadcast.getAvailability();

			// Inflate if necessary.
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.cell_incoming_broadcast, null);

			}

			// Reference Views.
			ProfilePictureView profilePictureView = (ProfilePictureView) convertView
					.findViewById(R.id.profilePictureViewIcon);
			TextView textViewIncomingBroadcastName = (TextView) convertView
					.findViewById(R.id.textViewIncomingBroadcastName);
			TextView textViewIncomingBroadcastStatus = (TextView) convertView
					.findViewById(R.id.textViewIncomingBroadcastStatus);

			// Set Views to have correct data for this User.
			profilePictureView.setProfileId(incomingBroadcast.getJid());
			textViewIncomingBroadcastName.setText(incomingBroadcast
					.getFullName());
			textViewIncomingBroadcastStatus
					.setText(hisStatus != null ? hisStatus.getDescription()
							: "Unknown Availability");

			return convertView;
		}
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		this.incomingBroadcasts.clear();
		this.incomingBroadcasts.addAll(incomingBroadcasts);

		adapter.notifyDataSetChanged();
	}

}

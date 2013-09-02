package com.hangapp.android.activity;

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
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.YouFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.util.BaseActivity;

/**
 * Get to this Activity via {@link YouFragment} and clicking on the number of
 * Incoming Broadcasts you have. <br />
 * <br />
 * This is a simple Activity that shows a ListView of your Incoming broadcasts.
 * It also listens for changes in IncomingBroadcasts so that it doesn't have to
 * refresh manually.
 */
public final class IncomingBroadcastsActivity extends BaseActivity implements
		IncomingBroadcastsListener {

	// Member datum.
	private List<User> incomingBroadcasts = new ArrayList<User>();

	// UI stuff.
	private ListView listViewIncomingBroadcasts;
	private IncomingBroadcastsArrayAdapter adapter;

	// Dependencies.
	private Database database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incoming_broadcasts);

		// Instantiate dependencies
		database = Database.getInstance();

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

		// "Subscribe" to changes in Incoming Broadcasts.
		database.addIncomingBroadcastsListener(this);

		// Refresh Incoming Broadcasts from the internal database (our cache).
		onIncomingBroadcastsUpdate(database.getMyIncomingBroadcasts());
	}

	@Override
	protected void onPause() {
		super.onPause();

		// "Unsubscribe" from changes in Incoming Broadcasts.
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
					.findViewById(R.id.profilePictureView);
			TextView textViewIncomingBroadcastName = (TextView) convertView
					.findViewById(R.id.textViewIncomingBroadcastName);
			TextView textViewIncomingBroadcastStatus = (TextView) convertView
					.findViewById(R.id.textViewIncomingBroadcastStatus);

			// Set Views to have correct data for this User.
			profilePictureView.setProfileId(incomingBroadcast.getJid());
			textViewIncomingBroadcastName.setText(incomingBroadcast
					.getFullName());
			textViewIncomingBroadcastStatus
					.setText((hisStatus != null && hisStatus.isActive()) ? hisStatus.getDescription()
							: "");

			return convertView;
		}
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		this.incomingBroadcasts.clear();
		
		if (incomingBroadcasts != null) {
			this.incomingBroadcasts.addAll(incomingBroadcasts);
		}

		adapter.notifyDataSetChanged();
	}

}

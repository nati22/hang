package com.hangapp.android.activity.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.ProfileActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability.Status;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.Keys;

public final class FeedFragment extends SherlockFragment implements
		IncomingBroadcastsListener {

	private ListView listViewFriends;

	private ArrayList<User> incomingBroadcasts = new ArrayList<User>();
	private FriendsAdapter adapter;

	private Database database;
	private RestClient restClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate dependencies.
		database = Database.getInstance();

		// Setup listener.
		database.addIncomingBroadcastsListener(this);

		restClient = new RestClientImpl(database, getActivity()
				.getApplicationContext());

		// Reload the incoming broadcasts from savedInstanceState.
		if (savedInstanceState != null) {
			ArrayList<User> savedIncomingBroadcasts = savedInstanceState
					.getParcelableArrayList(Keys.FRIENDS);
			incomingBroadcasts.clear();
			incomingBroadcasts.addAll(savedIncomingBroadcasts);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		// Inflate the View for this Fragment.
		View view = inflater.inflate(R.layout.fragment_feed, container, false);

		// Reference Views.
		listViewFriends = (ListView) view
				.findViewById(R.id.listViewFriendsFragment);

		// Set up the Adapter.
		adapter = new FriendsAdapter(getActivity(), incomingBroadcasts);
		listViewFriends.setAdapter(adapter);
		listViewFriends.setEmptyView(view.findViewById(android.R.id.empty));

		// Set the ListView's OnItemClickListener to open ProfileActivity.
		listViewFriends.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Context context = FeedFragment.this.getActivity();
				User user = incomingBroadcasts.get(position);

				Intent proposalLeechIntent = new Intent(context,
						ProfileActivity.class);
				proposalLeechIntent.putExtra(Keys.HOST_JID, user.getJid());
				context.startActivity(proposalLeechIntent);
			}
		});

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelableArrayList(Keys.FRIENDS, incomingBroadcasts);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		database.removeIncomingBroadcastsListener(this);
	}

	private class FriendsAdapter extends BaseAdapter {
		private List<User> friends;
		private LayoutInflater inflater;
		private Context context;

		public FriendsAdapter(Context context, List<User> friends) {
			inflater = LayoutInflater.from(context);
			this.context = context;
			this.friends = friends;
		}

		@Override
		public int getCount() {
			return friends.size();
		}

		@Override
		public Object getItem(int position) {
			return friends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			final User user = friends.get(position);

			// Inflate the View if necessary.
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.cell_friend_fragment,
						null);

				// Reference views
				holder.profilePictureView = (ProfilePictureView) convertView
						.findViewById(R.id.profilePictureView);
				holder.textViewFriendName = (TextView) convertView
						.findViewById(R.id.textViewFriendName);
				holder.imageViewProposalIcon = (ImageView) convertView
						.findViewById(R.id.buttonFriendProposal);
				holder.imageViewAvailability = (ImageView) convertView
						.findViewById(R.id.imageViewAvailability);

				Typeface tf = Typeface.createFromAsset(getActivity()
						.getApplicationContext().getAssets(),
						"fonts/champagne_limousines_bold.ttf");
				holder.textViewFriendName.setTypeface(tf);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Populate the Views.
			holder.profilePictureView.setProfileId(user.getJid());
			holder.textViewFriendName.setText(user.getFullName());

			// If the user has a Proposal, then show the Proposal icon.
			if (user.getProposal() != null) {
				holder.imageViewProposalIcon.setVisibility(View.VISIBLE);
			} else {
				holder.imageViewProposalIcon.setVisibility(View.INVISIBLE);
				convertView.setOnClickListener(null);
			}

			// Set the user's status icon based on his Availability.
			if (user.getAvailability() == null
					|| user.getAvailability().getStatus() == null) {
				holder.imageViewAvailability.setImageDrawable(getResources()
						.getDrawable(R.drawable.status_grey));
			} else if (user.getAvailability().getStatus() == Status.FREE) {
				holder.imageViewAvailability.setImageDrawable(getResources()
						.getDrawable(R.drawable.status_green));
			} else if (user.getAvailability().getStatus() == Status.BUSY) {
				holder.imageViewAvailability.setImageDrawable(getResources()
						.getDrawable(R.drawable.status_red));
			} else {
				Log.e("FeedFragment.getView",
						"Unknown user availability status: "
								+ user.getAvailability().getStatus());
			}

			// Make Facebook icon function as the NUDGE button.
			holder.profilePictureView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					restClient.sendNudge(user.getJid());
					Toast.makeText(context,
							"Sending a nudge to " + user.getFirstName(),
							Toast.LENGTH_SHORT).show();
				}
			});

			return convertView;
		}

		class ViewHolder {
			ProfilePictureView profilePictureView;
			TextView textViewFriendName;
			ImageView imageViewProposalIcon;
			ImageView imageViewAvailability;
		}
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		this.incomingBroadcasts.clear();
		this.incomingBroadcasts.addAll(incomingBroadcasts);
		Collections.sort(this.incomingBroadcasts);
		adapter.notifyDataSetChanged();
	}

}

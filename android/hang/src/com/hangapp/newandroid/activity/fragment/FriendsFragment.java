package com.hangapp.newandroid.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.activity.ProfileActivity;
import com.hangapp.newandroid.database.UserDatabase;
import com.hangapp.newandroid.model.Availability;
import com.hangapp.newandroid.model.Availability.Color;
import com.hangapp.newandroid.model.User;
import com.hangapp.newandroid.model.callback.IncomingBroadcastsListener;
import com.hangapp.newandroid.util.Keys;

public final class FriendsFragment extends SherlockFragment implements
		IncomingBroadcastsListener {

	private StickyListHeadersListView listViewFriends;

	private ArrayList<User> incomingBroadcasts = new ArrayList<User>();
	private FriendsAdapter adapter;

	private UserDatabase database;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate dependencies.
		database = UserDatabase.getInstance();

		// Setup listener.
		database.addIncomingBroadcastsListener(this);

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
		View view = inflater.inflate(R.layout.fragment_friends, container,
				false);

		// Reference Views.
		listViewFriends = (StickyListHeadersListView) view
				.findViewById(R.id.listViewFriendsFragment);

		// Set up the Adapter.
		adapter = new FriendsAdapter(getActivity(), incomingBroadcasts);
		listViewFriends.setAdapter(adapter);
		listViewFriends.setEmptyView(view.findViewById(android.R.id.empty));

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

	private static class FriendsAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {
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

				holder.profilePictureView = (ProfilePictureView) convertView
						.findViewById(R.id.profilePictureViewIcon);
				holder.textView1 = (TextView) convertView
						.findViewById(R.id.textViewFriendName);
				// holder.textView2 = (TextView) convertView
				// .findViewById(R.id.textViewFriendStatus);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.buttonFriendProposal);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Populate the Views.
			holder.profilePictureView.setProfileId(user.getJid());
			holder.textView1.setText(user.getFullName());

			// Set the OnClickListener
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent proposalLeechIntent = new Intent(context,
							ProfileActivity.class);
					proposalLeechIntent.putExtra(Keys.HOST_JID, user.getJid());
					context.startActivity(proposalLeechIntent);
				}
			});

			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			HeaderViewHolder holder;
			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = inflater.inflate(
						R.layout.cell_friends_list_header, parent, false);
				holder.text1 = (TextView) convertView
						.findViewById(R.id.textViewFriendsListHeader);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}

			Availability availability = friends.get(position).getAvailability();
			String headerText = null;

			if (availability != null && availability.getColor() == Color.FREE) {
				headerText = "Free to hang";
				holder.text1.setTextColor(android.graphics.Color.GREEN);
			} else if (availability != null
					&& availability.getColor() == Color.BUSY) {
				headerText = "Busy, can't hang";
				holder.text1.setTextColor(android.graphics.Color.RED);
			} else {
				headerText = "No availability";
				holder.text1.setTextColor(android.graphics.Color.GRAY);
			}
			holder.text1.setText(headerText);
			return convertView;
		}

		@Override
		public long getHeaderId(int position) {
			Availability availability = friends.get(position).getAvailability();

			if (availability != null && availability.getColor() == Color.FREE) {
				return 0;
			} else if (availability != null
					&& availability.getColor() == Color.BUSY) {
				return 1;
			} else {
				return 2;
			}
		}

		class HeaderViewHolder {
			TextView text1;
		}

		class ViewHolder {
			ProfilePictureView profilePictureView;
			TextView textView1;
			TextView textView2;
			ImageView imageView;
		}
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		this.incomingBroadcasts.clear();
		this.incomingBroadcasts.addAll(incomingBroadcasts);
		adapter.notifyDataSetChanged();
	}
}

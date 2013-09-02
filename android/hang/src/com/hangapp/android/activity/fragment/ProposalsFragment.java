package com.hangapp.android.activity.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.ProfileActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.model.callback.SeenProposalsListener;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;

public class ProposalsFragment extends SherlockFragment implements
		IncomingBroadcastsListener, SeenProposalsListener {

	// UI stuff
	private ListView listViewFriends;
	private ProposalsAdapter adapter;

	// Member datum.
	private ArrayList<UserWithSeenState> incomingBroadcasts = new ArrayList<UserWithSeenState>();

	// Dependencies.
	private Database database;

	private static class UserWithSeenState extends User {

		public UserWithSeenState(User user) {
			super(user.getJid(), user.getFirstName(), user.getLastName());
			super.setAvailability(user.getAvailability());
			super.setProposal(user.getProposal());
		}

		public UserWithSeenState(String jid, String firstName, String lastName) {
			super(jid, firstName, lastName);
		}

		public UserWithSeenState(Parcel in) {
			super(in);
		}

		public boolean seen = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate dependencies.
		database = Database.getInstance();

		// Reload the incoming broadcasts from savedInstanceState.
		if (savedInstanceState != null) {
			ArrayList<UserWithSeenState> savedIncomingBroadcasts = savedInstanceState
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
		View view = inflater.inflate(R.layout.fragment_proposals, container,
				false);

		// Reference Views.
		listViewFriends = (ListView) view
				.findViewById(R.id.listViewProposalsFragment);

		// Set up the Adapter.
		adapter = new ProposalsAdapter(getActivity(), incomingBroadcasts,
				database);

		listViewFriends.setAdapter(adapter);
		listViewFriends.setEmptyView(view.findViewById(android.R.id.empty));

		// Set OnClickListeners.
		listViewFriends.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Context context = ProposalsFragment.this.getActivity();
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
	public void onResume() {
		super.onResume();

		database.addIncomingBroadcastsListener(this);
		database.addSeenProposalListener(this);

		List<User> incomingBroadcasts = database.getMyIncomingBroadcasts();
		onIncomingBroadcastsUpdate(incomingBroadcasts);

		List<String> seenJids = database.getMySeenProposals();
		onMySeenProposalsUpdate(seenJids);
	}

	@Override
	public void onPause() {
		super.onPause();

		database.removeIncomingBroadcastsListener(this);
		database.removeSeenProposalListener(this);
	}

	private static class ProposalsAdapter extends BaseAdapter {
		private List<UserWithSeenState> friends;
		private LayoutInflater inflater;
		private Context context;
		private Database database;

		public ProposalsAdapter(Context context,
				List<UserWithSeenState> friends, Database database) {
			inflater = LayoutInflater.from(context);
			this.context = context;
			this.friends = friends;
			this.database = database;
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

			final UserWithSeenState seenUser = friends.get(position);

			// Inflate the View if necessary.
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.cell_proposals_fragment, null);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// If this user's proposal hasn't been seen, color in cell bg
			if (!seenUser.seen) {
				Log.d("ProposalsAdapter.getView",
						"coloring " + seenUser.getFirstName() + "'s cell");
				Log.i("getMySeenProposals looks like...", ""
						+ database.getMySeenProposals().toString());
				convertView.setBackgroundColor(context.getResources().getColor(
						R.color.teal));
			} else {
				convertView.setBackgroundColor(context.getResources().getColor(
						android.R.color.white));
			}

			// Reference views
			holder.profilePictureView = (ProfilePictureView) convertView
					.findViewById(R.id.profilePictureView);
			holder.textViewFriendName = (TextView) convertView
					.findViewById(R.id.textViewFriendName);
			holder.textViewProposalDescription = (TextView) convertView
					.findViewById(R.id.textViewProposalsCellDescription);
			holder.textViewProposalLocation = (TextView) convertView
					.findViewById(R.id.textViewProposalsCellLocation);
			holder.textViewProposalStartTime = (TextView) convertView
					.findViewById(R.id.textViewProposalsCellStartTime);
			holder.textViewProposalInterested = (TextView) convertView
					.findViewById(R.id.textViewProposalsCellInterested);

			// Get the fonts used in this cell.
			Typeface champagneLimousinesBold = Typeface.createFromAsset(
					context.getAssets(), Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
			Typeface champagneLimousines = Typeface.createFromAsset(
					context.getAssets(), Fonts.CHAMPAGNE_LIMOUSINES);

			// Set the fonts of this cell's TextViews.
			holder.textViewFriendName.setTypeface(champagneLimousinesBold);
			holder.textViewProposalDescription
					.setTypeface(champagneLimousinesBold);
			holder.textViewProposalLocation
					.setTypeface(champagneLimousinesBold);
			holder.textViewProposalStartTime.setTypeface(champagneLimousines);
			holder.textViewProposalInterested
					.setTypeface(champagneLimousinesBold);

			// Save the newly generated convertView into the "holder"
			// object.
			convertView.setTag(holder);

			// Populate the Views with correct data.
			holder.profilePictureView.setProfileId(seenUser.getJid());
			holder.textViewFriendName.setText(seenUser.getFullName());
			holder.textViewProposalDescription.setText(seenUser.getProposal()
					.getDescription());
			holder.textViewProposalLocation.setText(seenUser.getProposal()
					.getLocation());
			holder.textViewProposalStartTime.setText(seenUser.getProposal()
					.getStartTime().toString("h:mm aa"));

			// Construct the internationalized string for the number of users
			// interested in this Proposal. Then use it to populate the
			// TextView.
			final int numberOfUsersInterested = seenUser.getProposal()
					.getInterested().size();
			final String interestedString = String.format(context
					.getResources().getString(R.string.count_interested),
					numberOfUsersInterested);
			holder.textViewProposalInterested.setText(interestedString);

			return convertView;
		}

		class ViewHolder {
			ProfilePictureView profilePictureView;
			TextView textViewFriendName;
			TextView textViewProposalDescription;
			TextView textViewProposalLocation;
			TextView textViewProposalStartTime;
			TextView textViewProposalInterested;

		}

	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		this.incomingBroadcasts.clear();

		if (incomingBroadcasts != null) {
			// Remove users who have no Proposal from the new List<User>.
			for (Iterator<User> iterator = incomingBroadcasts.iterator(); iterator
					.hasNext();) {
				User user = iterator.next();
				if (user.getProposal() == null) {
					iterator.remove();
				}
			}

			// Convert Users to SeenUsers to give them seen "state" booleans
			for (User user : incomingBroadcasts) {
				UserWithSeenState seenUser = new UserWithSeenState(user);
				this.incomingBroadcasts.add(seenUser);
			}

			// Add the ones that are left to the internal List<User>.
			// this.incomingBroadcasts.addAll(incomingBroadcasts);
			Collections.sort(this.incomingBroadcasts);
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onMySeenProposalsUpdate(List<String> seenJids) {
		Set<String> seenJidsSet = new HashSet<String>(seenJids);

		for (UserWithSeenState seeableUser : this.incomingBroadcasts) {
			if (seenJidsSet.contains(seeableUser.getJid())) {
				seeableUser.seen = true;
			} else {
				seeableUser.seen = false;
			}
		}
		adapter.notifyDataSetChanged();
	}

}

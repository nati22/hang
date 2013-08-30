package com.hangapp.android.activity.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.HomeActivity;
import com.hangapp.android.activity.ProfileActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability.Status;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.StatusIcon;
import com.hangapp.android.util.Utils;

/**
 * The leftmost tab inside {@link HomeActivity}.
 */
public final class FeedFragment extends SherlockFragment implements
		IncomingBroadcastsListener {

	// UI stuff
	private ListView listViewFriends;
	private FriendsAdapter adapter;
	private Button buttonRefresh;

	// Member datum.
	private ArrayList<User> incomingBroadcasts = new ArrayList<User>();

	// Dependencies.
	private Database database;
	private RestClient restClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate dependencies.
		database = Database.getInstance();
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
		buttonRefresh = (Button) view.findViewById(R.id.buttonRefresh);

		// Set up the Adapter.
		adapter = new FriendsAdapter(getActivity(), incomingBroadcasts);
		listViewFriends.setAdapter(adapter);
		listViewFriends.setEmptyView(view.findViewById(android.R.id.empty));

		// Set OnClickListeners.
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
		buttonRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				restClient.getMyData();
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
	}

	@Override
	public void onPause() {
		super.onPause();

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
				convertView = inflater.inflate(R.layout.cell_friend_fragment, null);

				// Reference views
				holder.profilePictureView = (ProfilePictureView) convertView
						.findViewById(R.id.profilePictureView);
				holder.textViewFriendName = (TextView) convertView
						.findViewById(R.id.textViewFriendName);
				holder.textViewAvailabilityDescription = (TextView) convertView
						.findViewById(R.id.textViewAvailabilityDescription);
				/*
				 * holder.imageViewAvailability = (ImageView) convertView
				 * .findViewById(R.id.imageButtonAvailability);
				 */
				holder.statusIcon = (StatusIcon) convertView
						.findViewById(R.id.imageButtonAvailability);
				/*
				 * holder.textViewAvailabilityExpirationDate = (TextView)
				 * convertView
				 * .findViewById(R.id.textViewAvailabilityExpirationDate);
				 */

				Typeface champagneLimousinesBold = Typeface.createFromAsset(
						getActivity().getApplicationContext().getAssets(),
						Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
				Typeface champagneLimousines = Typeface.createFromAsset(
						getActivity().getApplicationContext().getAssets(),
						Fonts.CHAMPAGNE_LIMOUSINES);
				/*
				 * Typeface coolvetica = Typeface.createFromAsset(getActivity()
				 * .getApplicationContext().getAssets(), Fonts.COOLVETICA);
				 */

				holder.textViewFriendName.setTypeface(champagneLimousinesBold);
				holder.textViewAvailabilityDescription
						.setTypeface(champagneLimousines);
				/*
				 * holder.textViewAvailabilityExpirationDate.setTypeface(coolvetica);
				 */
				convertView.setTag(holder);
				Log.i("holder", "cell for " + user.getFirstName() + " == null");
			} else {
				holder = (ViewHolder) convertView.getTag();
				Log.i("holder", "cell for " + user.getFirstName() + " != null");
			}

			holder.profilePictureView.setProfileId(user.getJid());
			holder.textViewFriendName.setText(user.getFullName());

			// Reset the description
			holder.textViewAvailabilityDescription.setText("");
			
			// TODO: These sanity checks are already done in the StatusIcon...we should
			// find a way to consolidate the code (set the desc from the StatusIcon???)
			if (user.getAvailability() != null) {
				if (user.getAvailability().isActive()) {
					// Set description if it's there
					if (user.getAvailability().getDescription() != null
							&& !user.getAvailability().getDescription().equals("null")) {
						holder.textViewAvailabilityDescription.setText(user
								.getAvailability().getDescription());
					}
				}
			}

			holder.statusIcon.initialize(context, database, restClient, user,
					convertView);
			holder.statusIcon.setAvailabilityColor(user.getAvailability());
			holder.statusIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!user.getAvailability().isActive()) {
						restClient.sendNudge(user.getJid());
						Toast.makeText(context,
								"Sending a nudge to " + user.getFirstName(),
								Toast.LENGTH_SHORT).show();
					} else {
						// Determine hrs and min left
						DateTime expDateTime = user.getAvailability()
								.getExpirationDate();
						DateTime currentDateTime = new DateTime();
						int min = Minutes
								.minutesBetween(currentDateTime, expDateTime)
								.getMinutes();
						int hrs = 0;
						while (min >= 60) {
							hrs++;
							min = min - 60;
						}

						// Display remaining time to user
						Toast.makeText(
								context,
								hrs + " hr" + ((hrs > 1) ? "s " : " ") + min
										+ " min remaining", Toast.LENGTH_SHORT).show();
					}
				}
			});

			/*
			 * // Check if the user has an Availability.. if
			 * (user.getAvailability() != null) {
			 * 
			 * // Check if their status is active if
			 * (user.getAvailability().isActive()) {
			 * 
			 * // Set description if it's there if
			 * (user.getAvailability().getDescription() != null &&
			 * !user.getAvailability().getDescription().equals("null")) {
			 * holder.textViewAvailabilityDescription.setText(user
			 * .getAvailability().getDescription()); }
			 * 
			 * // Set availability button if (user.getAvailability().getStatus() ==
			 * Status.FREE) {
			 * holder.imageViewAvailability.setImageDrawable(getResources()
			 * .getDrawable(R.drawable.status_green));
			 * 
			 * String remainingTime = Utils.getAbbvRemainingTimeString(user
			 * .getAvailability().getExpirationDate());
			 * holder.textViewAvailabilityExpirationDate .setText(remainingTime); }
			 * else if (user.getAvailability().getStatus() == Status.BUSY) {
			 * holder.imageViewAvailability.setImageDrawable(getResources()
			 * .getDrawable(R.drawable.status_red));
			 * 
			 * String remainingTime = Utils.getAbbvRemainingTimeString(user
			 * .getAvailability().getExpirationDate());
			 * holder.textViewAvailabilityExpirationDate .setText(remainingTime); }
			 * else { // Then Status is neither FREE or BUSY
			 * Log.e("FeedFragment.getView", "Unknown user availability status: " +
			 * user.getAvailability().getStatus()); }
			 * 
			 * } else { // Then status has expired
			 * holder.imageViewAvailability.setImageDrawable(getResources()
			 * .getDrawable(R.drawable.status_grey)); }
			 * 
			 * } else { // Then the user has a null availability
			 * holder.imageViewAvailability.setImageDrawable(getResources()
			 * .getDrawable(R.drawable.status_grey));
			 * 
			 * Log.e("FeedFragment", user.getFirstName() +
			 * "'s getAvailability == null"); }
			 * 
			 * // Make availability icon function as the NUDGE button.
			 * holder.imageViewAvailability.setOnClickListener(new
			 * OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { if
			 * (!user.getAvailability().isActive()) {
			 * restClient.sendNudge(user.getJid()); Toast.makeText(context,
			 * "Sending a nudge to " + user.getFirstName(),
			 * Toast.LENGTH_SHORT).show(); } else { // Determine hrs and min left
			 * DateTime expDateTime = user.getAvailability() .getExpirationDate();
			 * DateTime currentDateTime = new DateTime(); int min =
			 * Minutes.minutesBetween(currentDateTime, expDateTime).getMinutes();
			 * int hrs = 0; while (min >= 60) { hrs++; min = min - 60; }
			 * 
			 * // Display remaining time to user Toast.makeText( context, hrs +
			 * " hr" + ((hrs > 1) ? "s " : " ") + min + " min remaining",
			 * Toast.LENGTH_SHORT).show(); } } });
			 */

			return convertView;
		}

		class ViewHolder {
			ProfilePictureView profilePictureView;
			TextView textViewFriendName;
			TextView textViewAvailabilityDescription;
			// ImageView imageViewAvailability;
			StatusIcon statusIcon;
			// TextView textViewAvailabilityExpirationDate;
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

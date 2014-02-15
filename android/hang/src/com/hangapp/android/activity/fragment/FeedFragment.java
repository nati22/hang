package com.hangapp.android.activity.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.model.callback.MyUserDataListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.StatusIcon;

/**
 * The leftmost tab inside {@link HomeActivity}.
 */
public final class FeedFragment extends SherlockFragment implements
		IncomingBroadcastsListener, MyUserDataListener {

	// UI stuff
	private ListView listViewFriends;
	private FriendsAdapter adapter;
	private Button buttonRefresh;

	private ProfilePictureView invisFBpic;
	private View visibleFBpic;
	private TextView textViewUserName;

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
		textViewUserName = (TextView) view.findViewById(R.id.userName);
		invisFBpic = (ProfilePictureView) view
				.findViewById(R.id.user_invis_fb_icon);
		visibleFBpic = (View) view.findViewById(R.id.user_real_fb_icon);

		// TODO should be pulling image and text out of cache
		textViewUserName.setText(database.getMyFullName());

		// Set up the Adapter.
		adapter = new FriendsAdapter(getActivity(), incomingBroadcasts);
		listViewFriends.setAdapter(adapter);
		listViewFriends.setEmptyView(view.findViewById(android.R.id.empty));

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
		database.addMyUserDataListener(this);

		// TODO instead of reconstructing this image each time it should be cached
		if (invisFBpic != null)
			if (invisFBpic.getChildAt(0) != null)
				setupCircularFacebookIcon();

	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void setupCircularFacebookIcon() {
		// this gets the profilepicture
		ImageView fbImage = (ImageView) invisFBpic.getChildAt(0);

		if (fbImage != null) {

			// Convert the fb profile pic into a bitmap
			BitmapDrawable fbDrawable = (BitmapDrawable) fbImage.getDrawable();
			if (fbDrawable == null) {
				Log.d("setupFacebookIcon",
						"Failed to get a drawable from the invis fbpic");
				return;
			}
			Bitmap bitmap = fbDrawable.getBitmap();

			// Create a blank bitmap
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);

			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
					bitmap.getWidth() / 2, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				visibleFBpic.setBackgroundDrawable(new BitmapDrawable(
						getResources(), output));
			} else {
				visibleFBpic.setBackground(new BitmapDrawable(getResources(),
						output));
			}
		} else {
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		database.removeIncomingBroadcastsListener(this);
		database.removeMyUserDataListener(this);
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
				holder.statusIcon = (StatusIcon) convertView
						.findViewById(R.id.imageButtonAvailability);

				Typeface champagneLimousinesBold = Typeface.createFromAsset(
						getActivity().getApplicationContext().getAssets(),
						Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
				Typeface champagneLimousines = Typeface.createFromAsset(
						getActivity().getApplicationContext().getAssets(),
						Fonts.CHAMPAGNE_LIMOUSINES);

				holder.textViewFriendName.setTypeface(champagneLimousinesBold);
				holder.textViewAvailabilityDescription
						.setTypeface(champagneLimousines);

				convertView.setTag(holder);
				// Log.i("holder", "cell for " + user.getFirstName() + " == null");
			} else {
				holder = (ViewHolder) convertView.getTag();
				// Log.i("holder", "cell for " + user.getFirstName() + " != null");
			}

			holder.profilePictureView.setProfileId(user.getJid());
			holder.textViewFriendName.setText(user.getFullName());

			// Reset the description
			holder.textViewAvailabilityDescription.setText("");

			// TODO: These sanity checks are already done in the StatusIcon...we
			// should
			// find a way to consolidate the code (set the desc from the
			// StatusIcon???)
			if (user.getAvailability() != null
					&& user.getAvailability().isActive()) {
				// Set description if it's there
				if (user.getAvailability().getDescription() != null
						&& !user.getAvailability().getDescription().equals("null")) {
					holder.textViewAvailabilityDescription.setText(user
							.getAvailability().getDescription());
				}
			}

			holder.statusIcon.initialize(context, user, convertView);
			holder.statusIcon.setAvailabilityColor(user.getAvailability());
			holder.statusIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!user.getAvailability().isActive()) {
						restClient.sendNudge(user.getJid());
						Toast.makeText(context,
								"Sending a nudge to " + user.getFirstName(),
								Toast.LENGTH_SHORT).show();
						((StatusIcon) v).setPressed(true);
					} else {
						// Determine hrs and min left
						int min = Minutes.minutesBetween(new DateTime(),
								user.getAvailability().getExpirationDate())
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

			final int pos = position;
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (user.getProposal() == null) {
						Toast.makeText(context, "User has no proposal",
								Toast.LENGTH_SHORT).show();
						return;
					}
					Context context = FeedFragment.this.getActivity();
					User user = incomingBroadcasts.get(pos);

					Intent proposalLeechIntent = new Intent(context,
							ProfileActivity.class);
					proposalLeechIntent.putExtra(Keys.HOST_JID, user.getJid());
					context.startActivity(proposalLeechIntent);

				}
			});

			return convertView;
		}

		class ViewHolder {
			ProfilePictureView profilePictureView;
			// ImageView greenRing;
			// ImageView redRing;
			// ImageView greyRing;
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

	@Override
	public void onMyUserDataUpdate(User me) {
		// TODO Setup caching
		invisFBpic.setProfileId(database.getMyJid());
		setupCircularFacebookIcon();
		textViewUserName.setText(me.getFullName());
	}

}

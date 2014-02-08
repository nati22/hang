package com.hangapp.android.activity;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.FeedFragment;
import com.hangapp.android.activity.fragment.YouFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Availability;
import com.hangapp.android.model.Availability.Status;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;
import com.hangapp.android.model.callback.IncomingBroadcastsListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.network.xmpp.XMPP;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.Utils;

/**
 * Get to this activity by clicking on a user in {@link FeedFragment}. <br />
 * <br />
 * This Activity shows a target user's availability (similar to how
 * {@link YouFragment} shows your own Availability). It implements
 * {@link IncomingBroadcastsActivity} in order to subscribe itself to any
 * changes in state for this target user.
 */
public final class ProfileActivity extends BaseActivity implements
		IncomingBroadcastsListener {

	// UI widgets.
	private ProfilePictureView profilePictureViewFriendIcon;
	private TextView textViewFriendsName;
	private ImageButton imageButtonFriendsAvailability;
	private TextView textViewFriendsAvailabilityExpirationDate;
	private TextView textViewStatus;
	private RelativeLayout relativeLayoutFriendsProposal;
	private ImageView imageViewOpenChat;
	private TextView textViewProposalDescription;
	private TextView textViewProposalLocation;
	private TextView textViewProposalStartTime;
	private CheckBox checkBoxInterested;
	private TextView textViewProposalInterestedCount;
	private LinearLayout linLayoutInterested;

	/** testing circle icon **/
	private ImageView circleView;

	// Member datum.
	private List<String> listInterestedJids = new ArrayList<String>();
	private User friend;

	// Dependencies.
	private Database database;
	private RestClient restClient;
	private XMPP xmpp;

	private boolean allowedToClickChatButton = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		// Instantiate dependencies
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getApplicationContext());
		xmpp = XMPP.getInstance();

		// Setup listener
		database.addIncomingBroadcastsListener(this);

		// Set who the friend is.
		String hostJid = getIntent().getStringExtra(Keys.HOST_JID);
		friend = database.getIncomingUser(hostJid);

		// Friend not in Database sanity check.
		if (friend == null) {
			Log.e("ProfileActivity.onCreate", "Host with jid: " + hostJid
					+ " was null in the Database.");
			finish();
		}

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Reference Views.
		profilePictureViewFriendIcon = (ProfilePictureView) findViewById(R.id.profilePictureViewFriendsIcon);
		textViewFriendsName = (TextView) findViewById(R.id.textViewFriendsName);
		imageButtonFriendsAvailability = (ImageButton) findViewById(R.id.imageButtonFriendsAvailability);
		textViewFriendsAvailabilityExpirationDate = (TextView) findViewById(R.id.textViewFriendsAvailabilityExpirationDate);
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		relativeLayoutFriendsProposal = (RelativeLayout) findViewById(R.id.relativeLayoutFriendsProposal);
		imageViewOpenChat = (ImageView) findViewById(R.id.imageViewChat);
		textViewProposalDescription = (TextView) findViewById(R.id.textViewMyProposalDescription);
		textViewProposalLocation = (TextView) findViewById(R.id.textViewMyProposalLocation);
		textViewProposalStartTime = (TextView) findViewById(R.id.textViewMyProposalStartTime);
		textViewProposalInterestedCount = (TextView) findViewById(R.id.textViewMyProposalInterestedCount);
		checkBoxInterested = (CheckBox) findViewById(R.id.checkBoxInterested);
		linLayoutInterested = (LinearLayout) findViewById(R.id.linearLayoutInterested);

		/** testing circle icon **/
		circleView = (ImageView) findViewById(R.id.circleView);

		// Set OnClickListeners.
		imageViewOpenChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "clicked",
				// Toast.LENGTH_SHORT).show();

				// TODO: Temporary fix to allow user in after logging in
				friend = database.getIncomingUser(getIntent().getStringExtra(
						Keys.HOST_JID));

				if (friend == null) {
					Log.e("ProfileActivity.imageViewOpenChat.setOnClick",
							"friend == null");
					return;
				}

				Proposal proposal = friend.getProposal();

				if (proposal == null) {
					Log.i("ProfileActivity.imageViewOpenChat.setOnClick",
							"proposal == null");
					return;
				}

				List<String> interestedList = proposal.getInterested();

				if ((interestedList.contains(database.getMyJid()) || friend
						.getJid().equals(database.getMyJid()))
						&& allowedToClickChatButton) {

					Log.i("ProfileActivity.imageViewOpenChat.onClick",
							"interested: "
									+ friend.getProposal().getInterested().toString());
					Intent chatActivityIntent = new Intent(ProfileActivity.this,
							FirebaseChatActivity.class);
					chatActivityIntent.putExtra(Keys.HOST_JID, friend.getJid());
					chatActivityIntent.putExtra(Keys.IS_HOST, false);
					startActivity(chatActivityIntent);
				} else {
					Toast.makeText(getApplicationContext(),
							"Sorry, you don't have permission to enter the chat.",
							Toast.LENGTH_SHORT).show();
					Log.i("ProfileActivity", "interested list: "
							+ friend.getProposal().getInterested().toString());
				}

			}
		});

		// If User is Interested/Confirmed, check the appropriate ToggleButton
		if (friend.getProposal() != null) {
			if (friend.getProposal().getInterested() != null) {
				if (friend.getProposal().getInterested()
						.contains(database.getMyJid()))
					checkBoxInterested.setChecked(true);
			}
		}

		// Set CheckBox.
		checkBoxInterested
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						// imageViewOpenChat.setEnabled(isChecked);

						// Add yourself to the Interested list of this user.
						if (isChecked) {
							restClient.setInterested(friend.getJid());
						}
						// Remove yourself from the Interested list of this user.
						else {
							restClient.deleteInterested(xmpp, friend.getJid());
							allowedToClickChatButton = false;
						}
					}
				});

		// Set fonts
		Typeface champagneLimousinesFontBold = Typeface.createFromAsset(
				getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
		Typeface champagneLimousinesFont = Typeface.createFromAsset(
				getApplicationContext().getAssets(), Fonts.CHAMPAGNE_LIMOUSINES);
		textViewStatus.setTypeface(champagneLimousinesFont);
		textViewProposalDescription.setTypeface(champagneLimousinesFontBold);
		textViewProposalLocation.setTypeface(champagneLimousinesFontBold);
		textViewProposalStartTime.setTypeface(champagneLimousinesFont);
		textViewProposalInterestedCount.setTypeface(champagneLimousinesFontBold);
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {

		allowedToClickChatButton = true;

		String TAG = "ProfileActivity.OnIncomingBroadcastsUpdate";
		Log.e(TAG, "ONINCOMINGBROADCASTSUPDATECALLED");

		// If friend is still broadcasting to you
		User broadcastingFriend = database.getIncomingUser(friend.getJid());

		if (broadcastingFriend == null) {
			Log.e("ProfileActivity.onIncomingBroadcastsUpdate", "friend == null");
		} else
			Log.e(TAG, "friend = " + friend.getFirstName());

		Proposal friendProposal = broadcastingFriend.getProposal();

		// Make sure they still have a proposal
		if (friendProposal == null) {
			Log.i("ProfileActivity.onIncomingBroadcastsUpdate", "Proposal == null");
			this.finish();
		} else
			Log.e(TAG, "proposal = " + friendProposal.getDescription());

		// Find out if User's Interested was updated
		if (!friendProposal.getInterested().equals(listInterestedJids)) {

			Log.e(TAG,
					"Interested was updated from " + listInterestedJids.toString()
							+ " to " + friendProposal.getInterested().toString());

			listInterestedJids.clear();
			listInterestedJids.addAll(database.getIncomingUser(friend.getJid())
					.getProposal().getInterested());

			updateHorizontalList(listInterestedJids, linLayoutInterested);

			// Modify my checkbox and chat accessibility
			Log.w("setting checkbox", " INTERESTED: "
					+ friendProposal.getInterested().contains(database.getMyJid()));
			checkBoxInterested.setChecked(friendProposal.getInterested().contains(
					database.getMyJid()));
		} else {
			Log.e(TAG,
					"Interested was NOTupdated from "
							+ listInterestedJids.toString() + " to "
							+ friendProposal.getInterested().toString());
		}

		updateAvailabilityIcon(database.getIncomingUser(friend.getJid())
				.getAvailability());

	}

	public static Bitmap getFacebookProfilePicture(String userID) throws IOException{
	    URL imageURL = new URL("http://graph.facebook.com/" + userID + "/picture?type=large");
	    Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

	    return bitmap;
	}
	
	public static Bitmap getCroppedBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	            bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
	            bitmap.getWidth() / 2, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
	    //return _bmp;
	    return output;
	}
	
	class GetCroppedCircleIcon extends AsyncTask<String, Void, Bitmap> {

	    private Exception exception;

	    protected Bitmap doInBackground(String... urls) {
	        try {
	      	  URL imageURL = new URL("http://graph.facebook.com/" + friend.getJid() + "/picture?type=large");
	    	    Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

	    	    return bitmap;
	        } catch (Exception e) {
	            this.exception = e;
	            return null;
	        }
	    }

	    protected void onPostExecute(Bitmap bitmap) {
	       
	   	 // Code to turn bitmap into circle (not always successfully)
	   	 
	   	 Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
		            bitmap.getHeight(), Config.ARGB_8888);
		    Canvas canvas = new Canvas(output);

		    final int color = 0xff424242;
		    final Paint paint = new Paint();
		    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		    paint.setAntiAlias(true);
		    canvas.drawARGB(0, 0, 0, 0);
		    paint.setColor(color);
		    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
		            bitmap.getWidth() / 2, paint);
		    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		    canvas.drawBitmap(bitmap, rect, rect, paint);
	   	
		    
		    circleView.setImageBitmap(output);
	    }
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// Populate Views.
		profilePictureViewFriendIcon.setProfileId(friend.getJid());

		new GetCroppedCircleIcon().execute();
	/*	Bitmap theBitmap = null;
		try {
			theBitmap = getFacebookProfilePicture(friend.getJid());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (theBitmap != null)
			circleView.setImageBitmap(theBitmap);
		else
			Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT)
					.show();*/
		textViewFriendsName.setText(friend.getFullName());

		updateAvailabilityIcon(database.getIncomingUser(friend.getJid())
				.getAvailability());

		if (friend.getProposal() == null) {
			Log.e("ProfileActivity.onResume", friend.getFirstName()
					+ "'s proposal was null");
			relativeLayoutFriendsProposal.setVisibility(View.GONE);
		} else {
			relativeLayoutFriendsProposal.setVisibility(View.VISIBLE);
			textViewProposalDescription.setText(friend.getProposal()
					.getDescription());
			textViewProposalLocation.setText(friend.getProposal().getLocation());
			textViewProposalStartTime.setText(friend.getProposal().getStartTime()
					.toString("h:mm aa"));

			// Refresh list
			onIncomingBroadcastsUpdate(database.getMyIncomingBroadcasts());

			// Add this proposal to the Users list of "seen proposals" //TODO:
			// optimize O(n)
			if (friend.getProposal() != null
					&& !database.getMySeenProposals().contains(friend.getJid())) {
				database.addSeenProposal(friend.getJid());
				restClient.setSeenProposal(friend.getJid());
				Log.i("ProfileActivity",
						"first time seeing " + friend.getFirstName() + "'s prop");
			} else {
				Log.i("ProfileActivity", friend.getFirstName()
						+ "'s proposal already seen");
			}
		}
		
		// REMOVE: let's try to get the profile picture bitmap here
		
	}

	private void updateAvailabilityIcon(Availability availability) {

		// If they have an Availability
		if (availability == null || availability.getStatus() == null
				|| !availability.isActive()) {
			imageButtonFriendsAvailability.setImageDrawable(getResources()
					.getDrawable(R.drawable.imagebutton_status_grey));
			String remainingHours = Utils.getAbbvRemainingTimeString(availability
					.getExpirationDate());
			textViewFriendsAvailabilityExpirationDate.setText(remainingHours);
			textViewStatus.setVisibility(View.INVISIBLE);
		}
		// Availability is FREE.
		else if (availability.getStatus() == Status.FREE) {
			imageButtonFriendsAvailability.setImageDrawable(getResources()
					.getDrawable(R.drawable.imagebutton_status_green));
			textViewStatus.setVisibility(View.VISIBLE);
			textViewStatus.setText(availability.getDescription());

			String remainingHours = Utils.getAbbvRemainingTimeString(availability
					.getExpirationDate());
			textViewFriendsAvailabilityExpirationDate.setText(remainingHours);
		}
		// Availability is BUSY.
		else if (availability.getStatus() == Status.BUSY) {
			imageButtonFriendsAvailability.setImageDrawable(getResources()
					.getDrawable(R.drawable.imagebutton_status_red));
			textViewStatus.setVisibility(View.VISIBLE);
			textViewStatus.setText(availability.getDescription());

			String remainingHours = Utils.getAbbvRemainingTimeString(availability
					.getExpirationDate());
			textViewFriendsAvailabilityExpirationDate.setText(remainingHours);
		}
		// Error state.
		else {
			Log.e("YouFragment.onMyAvailabilityUpdate",
					"Unknown availability state: " + availability);
			return;
		}
	}

	public void updateHorizontalList(List<String> jids, LinearLayout linLayout) {
		linLayout.removeAllViews();

		for (int i = 0; i < jids.size(); i++) {
			String jid = jids.get(i);

			// Get the cell
			View view = LayoutInflater.from(this).inflate(
					R.layout.cell_profile_icon, null);

			// Set the FB Profile pic
			ProfilePictureView icon = (ProfilePictureView) view
					.findViewById(R.id.profilePictureIcon);
			icon.setProfileId(jid);

			String name;
			if (database.getIncomingUser(jid) != null) {
				name = database.getIncomingUser(jid).getFirstName();
			} else if (database.getOutgoingUser(jid) != null) {
				name = database.getOutgoingUser(jid).getFirstName();
			} else
				name = "";
			final String toastName = name;
			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(), toastName,
							Toast.LENGTH_SHORT).show();
				}
			});

			linLayout.addView(view);

		}

		textViewProposalInterestedCount.setText(jids.size() + " interested");

	}

}

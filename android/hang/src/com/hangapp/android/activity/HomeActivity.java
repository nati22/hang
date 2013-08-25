package com.hangapp.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.FeedFragment;
import com.hangapp.android.activity.fragment.MyProposalFragment;
import com.hangapp.android.activity.fragment.ProposalsFragment;
import com.hangapp.android.activity.fragment.YouFragment;
import com.hangapp.android.activity.fragment.YouFragment.ProposalChangedListener;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.User;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.network.xmpp.XMPP;
import com.hangapp.android.util.BaseActivity;
import com.hangapp.android.util.Fonts;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.NoSlideViewPager;
import com.hangapp.android.util.TabsAdapter;

/**
 * This is our main Activity. This Activity manages two possible "fragments".
 * First, it is responsible for holding the main tabs of the app (we currently
 * use {@link FeedFragment} and {@link YouFragment} as our tabs). <br />
 * <br />
 * Second, it uses Facebook's {@link UiLifecycleHelper} class to "listen" to
 * changes in Facebook session state (it listens to whether or not you're logged
 * into Facebook). It handles changes in onSessionStateChange(). Several
 * activities "listen" to Facebook session state this way. This particular
 * Activity listens to state changes and hides / shows its tabs based on that.
 * If the user is logged out of Facebook, it shows {@link LoginFragment} and
 * hides its tabs.
 */
public final class HomeActivity extends BaseActivity implements
		ProposalChangedListener {

	// UI stuff.
	private NoSlideViewPager mViewPager;
	private TabsAdapter mTabsAdapter;

	// Dependencies.
	private SharedPreferences prefs;
	private Database database;
	private RestClient restClient;
	private XMPP xmpp;

	// Facebook SDK member variables.
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize dependencies.
		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		database = Database.getInstance();
		restClient = new RestClientImpl(Database.getInstance(),
				getApplicationContext());
		xmpp = XMPP.getInstance();
		LayoutInflater inflater = LayoutInflater.from(this);

		// Initialize the ViewPager and set it to be the ContentView of this
		// Activity.
		mViewPager = new NoSlideViewPager(this);
		mViewPager.setId(R.id.viewpager);
		setContentView(mViewPager);

		// Throw the two tabs into the ActionBar.
		final ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(bar.newTab(), FeedFragment.class, null);
		mTabsAdapter.addTab(bar.newTab(), YouFragment.class, null);
		mTabsAdapter.addTab(bar.newTab(), ProposalsFragment.class, null);

		// Style the Action Bar tabs.
		String[] tabNames = { "FEED", "YOU", "PROPOSALS" };
		Typeface champagneLimousinesFont = Typeface.createFromAsset(
				getApplicationContext().getAssets(),
				Fonts.CHAMPAGNE_LIMOUSINES_BOLD);
		for (int i = 0; i < bar.getTabCount(); i++) {
			View customView = inflater.inflate(R.layout.tab_title, null);

			TextView titleTV = (TextView) customView
					.findViewById(R.id.action_custom_title);
			titleTV.setText(tabNames[i]);
			titleTV.setTypeface(champagneLimousinesFont);

			bar.getTabAt(i).setCustomView(customView);
		}

		// Setup Facebook SDK.
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		// Reload the tab you had selected from savedInstanceState, if it was
		// saved.
		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			restClient.getMyData();
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			Log.e("HomeActivity.onOptionsItemSelected",
					"Unknown item selected: " + item.getAlphabeticShortcut());
			return true;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save which tab we had selected into savedInstanceState.
		outState.putInt("tab", getSupportActionBar()
				.getSelectedNavigationIndex());
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();

		// First thing's first: check to see if the user has Google Play
		// services installed. If he doesn't, let the Google SDK show
		// the Dialog that redirects him to the Google Play store to install
		// it.
		checkPlayServices();

		boolean userIsRegistered = prefs.getBoolean(Keys.REGISTERED, false);

		// If the user hasn't registered yet, then show the LoginFragment.
		if (!userIsRegistered) {
			setContentView(R.layout.login);

			getSupportActionBar().hide();
		}
		// Otherwise, show the regular tabbed ActionBar.
		else {
			setContentView(mViewPager);
			getSupportActionBar().show();

			restClient.getMyData();
		}

		// (Facebook SDK) For scenarios where the main activity is launched and
		// user session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		// Request "my" user data from Facebook and save the results.
		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser graphUser, Response response) {
				if (graphUser != null) {
					Log.v("HomeActivity.onResume",
							"Retrieved Facebook MeRequest, saving data internally"
									+ " for Facebook user "
									+ graphUser.getName());

					// You've officially "registered."
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean(Keys.REGISTERED, true);
					editor.commit();

					User me = new User(graphUser.getId(), graphUser
							.getFirstName(), graphUser.getLastName());

					// Save the "me" User object into the database.
					database.setMyUserData(me.getJid(), me.getFirstName(),
							me.getLastName());

					// Register the "me" User object into our server.
					restClient.registerNewUser(me);

					// Attempt to connect to XMPP using the new "me" JID.
					xmpp.connect(me.getJid(), getApplicationContext());
				}
			}
		});
		
		// Check if a certain tab should be opened (especially from a notification)
		int initialTab = getIntent().getIntExtra(Keys.TAB_INTENT, -1);
		if (initialTab!= -1) 
			getSupportActionBar().setSelectedNavigationItem(initialTab);
		
		
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("onResume", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);
		Log.e("******************",
				"attached Fragment with tag = " + fragment.getTag());
	}

	/**
	 * HomeActivity "watches" for Facebook session validity in order to show the
	 * LoginFragment if a logout occurs for any reason.
	 */
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i("HomeActivity.onSessionStateChange",
					"Logged in to Facebook...");
			// Since Facebook successfully logged in, show the regular tabbed
			// ActionBar.
			setContentView(mViewPager);
			getSupportActionBar().show();
		} else if (state.isClosed()) {
			Log.i("HomeActivity.onSessionStateChange",
					"Logged out of Facebook...");
			// Since Facebook isn't logged in, show the LoginFragment.
			setContentView(R.layout.login);
			getSupportActionBar().hide();

			if (exception != null) {
				Log.e("HomeActivity.onSessionStateChange",
						"Facebook exception: " + exception.getMessage());
			} else {
				Log.i("HomeActivity.onSessionStateChange",
						"Facebook logged out without exception");
			}
		}
	}

	/**
	 * XML onClickListener for the Empty View "Add Outgoing Broadcasts" button.
	 * This appears here, even though FeedFragment manages the Empty View
	 * itself... TODO we should change this so that FeedFragment explicitly
	 * implements this OnClickListener for the button.
	 */
	public void addMoreOutgoingBroadcasts(View v) {
		Intent intent = new Intent();
		intent.setData(AddOutgoingBroadcastActivity.FRIEND_PICKER);
		intent.setClass(this, AddOutgoingBroadcastActivity.class);
		startActivityForResult(intent, RESULT_OK);
	}

	public void notifyAboutProposalChange(Proposal proposal) {

		Log.d("HomeActivity.onProposalChangedListenerNotified", "" + proposal);
		if (proposal != null)
			Log.d("proposal desc", proposal.getDescription());
		else
			Log.d("proposal desc", "proposal == NULL");
		MyProposalFragment myProposalFragment = (MyProposalFragment) getSupportFragmentManager()
				.findFragmentByTag(Keys.MY_PROPOSAL_FRAGMENT_TAG);

		if (myProposalFragment == null) {
			Log.i("HomeActivity.onProposalChangedListenerNotified",
					"MyProposalFragment was null");
		} else if (myProposalFragment.isVisible()) {
			Log.i("HomeActivity.onProposalChangedListenerNotified",
					"MyProposalFragment is visible");

			Log.i("HomeActivity.onProposalChangedListenerNotified",
					"going to pass prop desc of " + proposal.getDescription());
			myProposalFragment.updateProposal(proposal);
		} else {
			Log.i("HomeActivity.onProposalChangedListenerNotified",
					"MyProposalFragment is there but not visible");
		}
	}
}

package com.hangapp.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.hangapp.android.R;
import com.hangapp.android.activity.fragment.FeedFragment;
import com.hangapp.android.activity.fragment.MyAvailabilityFragment;
import com.hangapp.android.activity.fragment.MyProposalFragment;
import com.hangapp.android.database.Database;
import com.hangapp.android.model.User;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.network.xmpp.XMPP;
import com.hangapp.android.util.Keys;
import com.hangapp.android.util.NoSlideViewPager;
import com.hangapp.android.util.TabsAdapter;
import com.hangapp.android.util.TypefaceSpan;

public final class HomeActivity extends SherlockFragmentActivity {

	private NoSlideViewPager mViewPager;
	private TabsAdapter mTabsAdapter;

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

		// Initialize the ViewPager and set it to be the ContentView of this
		// Activity.
		mViewPager = new NoSlideViewPager(this);
		mViewPager.setId(R.id.viewpager);
		setContentView(mViewPager);

		// Setup the ActionBar
		final ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowTitleEnabled(true);
		bar.setIcon(null);
		bar.setDisplayShowHomeEnabled(true);
		SpannableString s = new SpannableString(getString(R.string.app_name));
		s.setSpan(new TypefaceSpan(this, "coolvetica.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		bar.setTitle(s);
		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.action_bar_background));

		// Throw the three tabs into the ActionBar.
		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(bar.newTab().setText("Feed"),
				FeedFragment.class, null);
		mTabsAdapter.addTab(bar.newTab().setText("You"),
				MyAvailabilityFragment.class, null);
		mTabsAdapter.addTab(bar.newTab().setText("Proposals"),
				MyProposalFragment.class, null);

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
			return super.onOptionsItemSelected(item);
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

		restClient.getMyData();

		// If the user hasn't registered yet, then show the LoginFragment.
		if (!prefs.getBoolean(Keys.REGISTERED, false)) {
			setContentView(R.layout.fragment_login);
			getSupportActionBar().hide();
		}
		// Otherwise, show the regular tabbed ActionBar.
		else {
			setContentView(mViewPager);
			getSupportActionBar().show();
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
			setContentView(R.layout.fragment_login);
			getSupportActionBar().hide();
		}
	}

	public void addMoreOutgoingBroadcasts(View v) {
		Intent intent = new Intent();
		intent.setData(AddOutgoingBroadcastActivity.FRIEND_PICKER);
		intent.setClass(this, AddOutgoingBroadcastActivity.class);
		startActivityForResult(intent, RESULT_OK);
	}

}

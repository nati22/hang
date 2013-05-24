package com.hangapp.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.hangapp.android.R;
import com.hangapp.android.util.BaseFragmentActivity;

// TODO: Give our Action Bar a theme using Android Action Bar Style Generator
//			http://jgilfelt.github.io/android-actionbarstylegenerator/
// TODO: Facebook login Fragment (SplashFragment) should be contained in an
//			Activity.
// TODO: Reimplement RestClient usage for Facebook Login and Logout buttons.
// TODO: Reimplement showLoadingIndicator() and hideLoadingIndicator() from the 
//			SelectionFragment.
// TODO: Hide Action Bar when Settings Activity is showing.
// TODO: Delete SettingsActivity.
// TODO: Integrate Facebook SDK into ManageBroadcastsActivity: OutgoingBroadcasts
//			should simply be a list of your Facebook friends, with a button that says 
//			"Broadcasting" or "Broadcast" similar to Instagram's "Follow" "Following" 
//			model.
// TODO: Change all non-Activity code to inject their dependencies instead of
//			explicitly instantiating them themselves.
// TODO: Split up HomeActivity's ListView into sections, with headers:
//			1.) Free
//			2.) Stale
//			3.) Busy
// TODO: Use Android ViewPager for a sexy tab swipe GUI.
// TODO: Reimplement nudges.
// TODO: Reimplement Interested and Confirmed users.
// TODO: Improve Nudge GUI.
// TODO: Change DefaultUser so that Proposals are saved in SQLite. 
// TODO: Reimplement Proposal chatrooms (XMPP).
// TODO: Make the Action Bar's Indeterminate Progress Bar smaller.
// TODO: Change ChatFragment GUI to use speech bubbles:
//			http://adilsoomro.blogspot.com/2012/12/android-listview-with-speech-bubble.html
// TODO: Restrict "public"ness of most classes.
// TODO: Clean out the rest of the warnings in each file.
// TODO: Clean out the rest of the TODOs scattered around the source code.
// TODO: Make our XML code a little more DRY. For instance, buttons all have some of the
//			same properties (white text color, a little margin, faded edges, etc.)
public final class HomeActivity extends BaseFragmentActivity {

	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS + 1;

	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

	private boolean isResumed = false;
	// private MenuItem settings;

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
		setContentView(R.layout.activity_home);

		FragmentManager fm = getSupportFragmentManager();
		fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
		fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// // If this phone doesn't have a user registered to it, then open
		// // up RegistrationActivity. Otherwise, just call getUserData().
		// if (!prefs.getBoolean(Keys.REGISTERED, false)) {
		// startActivity(new Intent(this, RegistrationActivity.class));
		// } else {
		// // restClient.getUserData();
		// // showLoadingIndicator();
		// }
		isResumed = true;
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		Session session = Session.getActiveSession();

		if (session != null && session.isOpened()) {
			// if the session is already open,
			// try to show the selection fragment
			showFragment(SELECTION, false);
		} else {
			// otherwise present the splash screen
			// and ask the user to login.
			showFragment(SPLASH, false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_manage_broadcasts:
			startActivity(new Intent(this, ManageBroadcastsActivity.class));
			return true;
		case R.id.menu_settings:
			showFragment(SETTINGS, true);
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isResumed = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_home, menu);

		return super.onCreateOptionsMenu(menu);
	}

	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// Only make changes if the activity is visible
		if (isResumed) {
			FragmentManager manager = getSupportFragmentManager();
			// Get the number of entries in the back stack
			int backStackSize = manager.getBackStackEntryCount();
			// Clear the back stack
			for (int i = 0; i < backStackSize; i++) {
				manager.popBackStack();
			}
			if (state.isOpened()) {
				// If the session state is open:
				// Show the authenticated fragment
				showFragment(SELECTION, false);
			} else if (state.isClosed()) {
				// If the session state is closed:
				// Show the login fragment
				showFragment(SPLASH, false);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	public void openCreateProposalActivity(View v) {
		startActivity(new Intent(this, CreateProposalActivity.class));
	}

	public void openSetStatusActivity(View v) {
		startActivity(new Intent(this, SetStatusActivity.class));
	}
}

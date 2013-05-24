//package com.hangapp.android.activity;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.model.GraphUser;
//import com.hangapp.android.R;
//import com.hangapp.android.database.DefaultUser;
//import com.hangapp.android.model.User;
//import com.hangapp.android.network.rest.RestClient;
//import com.hangapp.android.network.rest.RestClientImpl;
//import com.hangapp.android.util.BaseFragmentActivity;
//import com.hangapp.android.util.Keys;
//
////@ContentView(R.layout.activity_registration)
//public class RegistrationActivity extends BaseFragmentActivity {
//
//	// @InjectView(R.id.welcome)
//	private TextView textViewWelcome;
//
//	// @Inject
//	private SharedPreferences prefs;
//	// @Inject
//	private RestClient restClient;
//	private DefaultUser defaultUser;
//
//	private static final int SPLASH = 0;
//	private static final int SELECTION = 1;
//	private static final int FRAGMENT_COUNT = SELECTION + 1;
//
//	private boolean isResumed = false;
//
//	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
//
//	private UiLifecycleHelper uiHelper;
//
//	private Session.StatusCallback callback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state,
//				Exception exception) {
//			onSessionStateChange(session, state, exception);
//		}
//	};
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_registration);
//
//		// Reference GUI views.
//		textViewWelcome = (TextView) findViewById(R.id.welcome);
//
//		// Instantiate dependencies.
//		prefs = PreferenceManager
//				.getDefaultSharedPreferences(getApplicationContext());
//		restClient = new RestClientImpl(getApplicationContext());
//		defaultUser = DefaultUser.getInstance();
//
//		// Facebook SDK stuff
//		uiHelper = new UiLifecycleHelper(this, callback);
//		uiHelper.onCreate(savedInstanceState);
//
//		FragmentManager fm = getSupportFragmentManager();
//		fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
//		fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
//
//		FragmentTransaction transaction = fm.beginTransaction();
//		for (int i = 0; i < fragments.length; i++) {
//			transaction.hide(fragments[i]);
//		}
//		transaction.commit();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		uiHelper.onResume();
//		isResumed = true;
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		uiHelper.onPause();
//		isResumed = false;
//	}
//
//	public void connectWithFacebook(View v) {
//		// start Facebook Login
//		Session.openActiveSession(this, true, new Session.StatusCallback() {
//
//			// callback when session changes state
//			@Override
//			public void call(Session session, SessionState state,
//					Exception exception) {
//
//				if (session.isOpened()) {
//					// make request to the /me API
//					Request.executeMeRequestAsync(session,
//							new Request.GraphUserCallback() {
//
//								// callback after Graph API response with user
//								// object
//								@Override
//								public void onCompleted(GraphUser user,
//										Response response) {
//									if (user != null) {
//										SharedPreferences.Editor editor = prefs
//												.edit();
//										editor.putString(Keys.JID, user.getId());
//										editor.commit();
//										Log.d("RegistrationActivity.connectWithFacebook",
//												"Saved JID: " + user.getId());
//
//										User newUser = new User(user.getId(),
//												user.getFirstName(), user
//														.getLastName(), null,
//												null);
//
//										restClient.registerUser(newUser);
//
//										textViewWelcome.setText("Logged in as "
//												+ user.getFirstName());
//									}
//								}
//							});
//				}
//			}
//		});
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		uiHelper.onActivityResult(requestCode, resultCode, data);
//		//
//		// // Facebook SDK wiring:
//		// //
//		// https://developers.facebook.com/docs/getting-started/facebook-sdk-for-android/3.0/
//		// Session.getActiveSession().onActivityResult(this, requestCode,
//		// resultCode, data);
//
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		uiHelper.onDestroy();
//	}
//
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		uiHelper.onSaveInstanceState(outState);
//	};
//
//	public void finished(View v) {
//		SharedPreferences.Editor editor = prefs.edit();
//
//		editor.putBoolean(Keys.REGISTERED, true);
//		editor.commit();
//
//		defaultUser.deleteMyProposal();
//
//		finish();
//	}
//
//	private void showFragment(int fragmentIndex, boolean addToBackStack) {
//		FragmentManager fm = getSupportFragmentManager();
//		FragmentTransaction transaction = fm.beginTransaction();
//		for (int i = 0; i < fragments.length; i++) {
//			if (i == fragmentIndex) {
//				transaction.show(fragments[i]);
//			} else {
//				transaction.hide(fragments[i]);
//			}
//		}
//		if (addToBackStack) {
//			transaction.addToBackStack(null);
//		}
//		transaction.commit();
//	}
//
//	private void onSessionStateChange(Session session, SessionState state,
//			Exception exception) {
//		// Only make changes if the activity is visible
//		if (isResumed) {
//			FragmentManager manager = getSupportFragmentManager();
//			// Get the number of entries in the back stack
//			int backStackSize = manager.getBackStackEntryCount();
//			// Clear the back stack
//			for (int i = 0; i < backStackSize; i++) {
//				manager.popBackStack();
//			}
//			if (state.isOpened()) {
//				// If the session state is open:
//				// Show the authenticated fragment
//				showFragment(SELECTION, false);
//			} else if (state.isClosed()) {
//				// If the session state is closed:
//				// Show the login fragment
//				showFragment(SPLASH, false);
//			}
//		}
//	}
//
//	@Override
//	protected void onPostResume() {
//		super.onPostResume();
//		Session session = Session.getActiveSession();
//
//		if (session != null && session.isOpened()) {
//			// if the session is already open,
//			// try to show the selection fragment
//			showFragment(SELECTION, false);
//		} else {
//			// otherwise present the splash screen
//			// and ask the user to login.
//			showFragment(SPLASH, false);
//		}
//	}
//
//}

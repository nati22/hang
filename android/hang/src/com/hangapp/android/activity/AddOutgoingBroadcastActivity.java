package com.hangapp.android.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.network.xmpp.XMPP;
import com.hangapp.android.util.BaseActivity;

/**
 * Get to this Activity from {@link OutgoingBroadcastsActivity}, or
 * alternatively from the Empty ListView of {@link FriendsFragment}. <br />
 * <br />
 * This Activity shows one thing: the Facebook Friend Picker showing all of your
 * Facebook friends, where you can check them off. When the user clicks "Done",
 * the Activity calls the {@code saveSelection()} method and closes itself. <br />
 * <br />
 * Most of the code from this class was lifted straight from the <a href=
 * "https://developers.facebook.com/docs/tutorials/androidsdk/3.0/scrumptious/show-friends/"
 * >Facebook Android SDK tutorial</a>.
 */
public final class AddOutgoingBroadcastActivity extends BaseActivity {

	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	private FriendPickerFragment friendPickerFragment;

	private Database database;
	private RestClient restClient;
	private XMPP xmpp;

	// Helper interface for Facebook FriendPicker widget. It uses
	// this interface to query whether or not the friend with that
	// Facebook ID has your Facebook app installed.
	private interface GraphUserWithInstalled extends GraphUser {
		Boolean getInstalled();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_login);

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Instantiate dependencies.
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getApplicationContext());
		xmpp = XMPP.getInstance();

		// (lifted from Facebook tutorial above)
		Bundle args = getIntent().getExtras();
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragmentToShow = null;
		Uri intentUri = getIntent().getData();

		// (lifted from Facebook tutorial above)
		if (FRIEND_PICKER.equals(intentUri)) {
			if (savedInstanceState == null) {
				friendPickerFragment = new FriendPickerFragment(args);
			} else {
				friendPickerFragment = (FriendPickerFragment) manager
						.findFragmentById(R.id.picker_fragment);
			}
			// Set the listener to handle errors
			friendPickerFragment
					.setOnErrorListener(new PickerFragment.OnErrorListener() {
						@Override
						public void onError(PickerFragment<?> fragment,
								FacebookException error) {
							AddOutgoingBroadcastActivity.this.onError(error);
						}
					});
			// Set the listener to handle button clicks
			friendPickerFragment
					.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
						@Override
						public void onDoneButtonClicked(
								PickerFragment<?> fragment) {
							finishActivity();
						}
					});
			// Set the Friend picker to filter only friends that have the Hang
			// app installed on their device (Facebook GraphUser query handles
			// this for us).
			friendPickerFragment.setExtraFields(Arrays.asList("installed"));
			friendPickerFragment
					.setFilter(new PickerFragment.GraphObjectFilter<GraphUser>() {
						@Override
						public boolean includeItem(GraphUser graphObject) {
							Boolean installed = graphObject.cast(
									GraphUserWithInstalled.class)
									.getInstalled();
							return (installed != null)
									&& installed.booleanValue();
						}
					});

			fragmentToShow = friendPickerFragment;
		} else {
			// Nothing to do, finish
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		manager.beginTransaction()
				.replace(R.id.picker_fragment, fragmentToShow).commit();
	}

	private void onError(Exception error) {
		onError(error.getLocalizedMessage(), false);
	}

	private void onError(String error, final boolean finishActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error_dialog_title)
				.setMessage(error)
				.setPositiveButton(R.string.error_dialog_button_text,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								if (finishActivity) {
									finishActivity();
								}
							}
						});
		builder.show();
	}

	private void finishActivity() {
		if (FRIEND_PICKER.equals(getIntent().getData())) {
			if (friendPickerFragment != null) {
				saveSelection();
			} else {
				Log.e("AddOutgoingBroadcastActivity.finishActivity",
						"Finished activity with null friendPickerFragment");
			}
		}

		setResult(RESULT_OK, null);
		finish();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (FRIEND_PICKER.equals(getIntent().getData())) {
			try {
				friendPickerFragment.loadData(false);
			} catch (Exception ex) {
				onError(ex);
			}
		}
	}

	private void saveSelection() {
		if (friendPickerFragment.getSelection().size() == 1) {

			restClient.addBroadcastee(xmpp, friendPickerFragment.getSelection()
					.get(0).getId());

		} else if (friendPickerFragment.getSelection().size() > 1) {

			List<String> parameters = new ArrayList<String>();

			for (GraphUser graphUser : friendPickerFragment.getSelection()) {
				Log.i("AddOutgoingBroadcastActivity.finishActivity",
						"Adding broadcastee: " + graphUser.getName());

				parameters.add(graphUser.getId());
			}

			restClient.addBroadcastees(xmpp, parameters);

		} else {
			Log.e("AddOutgoingBroadcastActivity.saveSelection",
					"friendPickerFragment.getSelection().size() = "
							+ friendPickerFragment.getSelection().size());

			Toast.makeText(getApplicationContext(), "Error...see LogCat",
					Toast.LENGTH_SHORT).show();
		}
	}
}

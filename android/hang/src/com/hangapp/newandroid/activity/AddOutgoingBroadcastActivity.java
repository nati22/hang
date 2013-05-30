package com.hangapp.newandroid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.facebook.FacebookException;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.network.rest.RestClient;
import com.hangapp.newandroid.network.rest.RestClientImpl;
import com.hangapp.newandroid.util.BaseFragmentActivity;
import com.hangapp.newandroid.util.HangLog;

public class AddOutgoingBroadcastActivity extends BaseFragmentActivity {

	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	private FriendPickerFragment friendPickerFragment;

	private Database database;
	private RestClient restClient;

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

		Bundle args = getIntent().getExtras();
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragmentToShow = null;
		Uri intentUri = getIntent().getData();

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
		for (GraphUser graphUser : friendPickerFragment.getSelection()) {
			HangLog.toastD(getApplicationContext(),
					"AddOutgoingBroadcastActivity.finishActivity",
					"Adding broadcastee: " + graphUser.getName());
			restClient.addBroadcastee(graphUser.getId());
		}
	}
}

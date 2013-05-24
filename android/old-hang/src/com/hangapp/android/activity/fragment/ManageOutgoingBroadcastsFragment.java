package com.hangapp.android.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.android.R;
import com.hangapp.android.activity.PickerActivity;
import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.User;
import com.hangapp.android.model.listener.OutgoingBroadcastsListener;
import com.hangapp.android.util.BaseArrayAdapter;

public class ManageOutgoingBroadcastsFragment extends SherlockFragment
		implements OutgoingBroadcastsListener {

	// @Inject
	private ArrayList<User> outgoingBroadcasts = new ArrayList<User>();
	private OutgoingBroadcastsArrayAdapter outgoingBroadcastsAdapter;

	// @Inject
	private DefaultUser defaultUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_outgoing_broadcasts,
				container, false);

		// ListView listViewOutgoing = (ListView) view
		// .findViewById(R.id.listViewOutgoingBroadcasts);

		Button buttonPickFriends = (Button) view
				.findViewById(R.id.buttonPickFriends);
		buttonPickFriends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pickFriends(null);
			}
		});

		// Instantiate dependencies.
		defaultUser = DefaultUser.getInstance();

		outgoingBroadcastsAdapter = new OutgoingBroadcastsArrayAdapter(
				getActivity().getApplicationContext(),
				R.layout.cell_outgoing_broadcast, outgoingBroadcasts);

		// listViewOutgoing.setAdapter(outgoingBroadcastsAdapter);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		defaultUser.addOutgoingBroadcastsListener(this);

		defaultUser.notifyAllListeners();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		defaultUser.removeOutgoingBroadcastsListener(this);
	}

	static class OutgoingBroadcastsArrayAdapter extends BaseArrayAdapter<User> {

		public OutgoingBroadcastsArrayAdapter(Context context,
				int textViewResourceId, List<User> outgoingBroadcasts) {
			super(context, textViewResourceId, outgoingBroadcasts);
		}

		@Override
		public View getViewEnhanced(final User object, View convertedView) {
			TextView username = (TextView) convertedView
					.findViewById(R.id.textViewUserName);
			username.setText(object.getFullName());

			Button broadcasting = (Button) convertedView
					.findViewById(R.id.buttonStopBroadcasting);
			broadcasting.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(getContext(), "Currently disabled",
							Toast.LENGTH_SHORT).show();
				}
			});

			convertedView.setLayoutParams(new ListView.LayoutParams(
					ListView.LayoutParams.MATCH_PARENT,
					ListView.LayoutParams.WRAP_CONTENT));

			return convertedView;
		}
	}

	@Override
	public void onOutgoingBroadcastsUpdate(List<User> outgoingBroadcasts) {
		this.outgoingBroadcasts.clear();
		this.outgoingBroadcasts.addAll(outgoingBroadcasts);

		outgoingBroadcastsAdapter.notifyDataSetChanged();
	}

	private void startPickerActivity(Uri data, int requestCode) {
		Intent intent = new Intent();
		intent.setData(data);
		intent.setClass(getActivity(), PickerActivity.class);
		startActivityForResult(intent, requestCode);
	}

	public void pickFriends(View v) {
		startPickerActivity(PickerActivity.FRIEND_PICKER,
				getTargetRequestCode());
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	//
	// if (requestCode == REAUTH_ACTIVITY_CODE) {
	// uiHelper.onActivityResult(requestCode, resultCode, data);
	// } else if (resultCode == Activity.RESULT_OK) {
	// // Do nothing for now
	// }
	// }

}

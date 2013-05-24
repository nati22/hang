package com.hangapp.android.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.android.R;
import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.User;
import com.hangapp.android.model.listener.IncomingBroadcastsListener;
import com.hangapp.android.util.BaseArrayAdapter;

public class ManageIncomingBroadcastsFragment extends SherlockFragment implements
		IncomingBroadcastsListener {

	// @Inject
	private ArrayList<User> incomingBroadcasts = new ArrayList<User>();

	private IncomingBroadcastsArrayAdapter incomingBroadcastsAdapter;

	// @Inject
	private DefaultUser defaultUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_incoming_broadcasts,
				container, false);

		ListView listViewIncoming = (ListView) view
				.findViewById(R.id.listViewIncomingBroadcasts);

		// Instantiate dependencies
		defaultUser = DefaultUser.getInstance();

		// attach ArrayAdapters
		incomingBroadcastsAdapter = new IncomingBroadcastsArrayAdapter(
				getActivity().getApplicationContext(),
				R.layout.cell_incoming_broadcast, incomingBroadcasts);
		listViewIncoming.setAdapter(incomingBroadcastsAdapter);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Subscribe this Activity to changes it cares about
		defaultUser.addIncomingBroadcastsListener(this);

		defaultUser.notifyAllListeners();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		defaultUser.removeIncomingBroadcastsListener(this);
	}

	static class IncomingBroadcastsArrayAdapter extends BaseArrayAdapter<User> {
		public IncomingBroadcastsArrayAdapter(Context context,
				int textViewResourceId, List<User> incomingBroadcasts) {
			super(context, textViewResourceId, incomingBroadcasts);
		}

		@Override
		public View getViewEnhanced(final User object, View convertedView) {
			TextView username = (TextView) convertedView
					.findViewById(R.id.textViewUserName);
			username.setText(object.getFullName());

			Button broadcasting = (Button) convertedView
					.findViewById(R.id.button);

			broadcasting.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) { // If we want to START filtering

					if (((Button) v).getText().equals(
							getContext().getResources().getString(
									R.string.filter_broadcast))) {

						// Make Toast
						Toast.makeText(
								getContext(),
								"Filtering broadcasts from "
										+ object.getFirstName(),
								Toast.LENGTH_SHORT).show();

						((Button) v).setText(getContext().getResources()
								.getString(R.string.unfilter_broadcast));

					} else { // If we want to STOP filtering

						String name = (String) ((TextView) ((LinearLayout) ((Button) v)
								.getParent())
								.findViewById(R.id.textViewUserName)).getText();

						Toast.makeText(getContext(),
								"Removing filter on broadcasts from " + name,
								Toast.LENGTH_SHORT).show();

						((Button) v).setText(getContext().getResources()
								.getString(R.string.filter_broadcast));
					}
				}
			});

			return convertedView;
		}
	}

	@Override
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts) {
		this.incomingBroadcasts.clear();
		this.incomingBroadcasts.addAll(incomingBroadcasts);

		incomingBroadcastsAdapter.notifyDataSetChanged();
	}

}

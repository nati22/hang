package com.hangapp.android.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hangapp.android.R;
import com.hangapp.android.activity.CreateProposalActivity;
import com.hangapp.android.activity.ProposalHostActivity;
import com.hangapp.android.activity.ProposalLeechActivity;
import com.hangapp.android.activity.SetStatusActivity;
import com.hangapp.android.database.DefaultUser;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.Status;
import com.hangapp.android.model.User;
import com.hangapp.android.model.listener.IncomingBroadcastsListener;
import com.hangapp.android.model.listener.MyProposalListener;
import com.hangapp.android.model.listener.MyStatusListener;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.BaseArrayAdapter;
import com.hangapp.android.util.HangLog;
import com.hangapp.android.util.Keys;

public class SelectionFragment extends SherlockFragment implements
		IncomingBroadcastsListener, MyStatusListener, MyProposalListener {

	private static final String TAG = "SelectionFragment";

	private Button buttonSetStatus;
	private Button buttonCreateNewProposal;
	private PullToRefreshListView listViewHomeActivity;

	private RestClient restClient;
	private DefaultUser defaultUser;
	private SharedPreferences prefs;

	private ArrayList<User> incomingBroadcasts = new ArrayList<User>();
	private IncomingBroadcastsArrayAdapter incomingBroadcastsAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.selection, container, false);

		// Set GUI references.
		buttonSetStatus = (Button) view.findViewById(R.id.buttonSetStatus);
		buttonCreateNewProposal = (Button) view
				.findViewById(R.id.buttonCreateNewProposal);
		listViewHomeActivity = (PullToRefreshListView) view
				.findViewById(R.id.pull_to_refresh_listview);

		// Instantiate dependencies.
		restClient = new RestClientImpl(getActivity().getApplicationContext());
		defaultUser = DefaultUser.getInstance();
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		// Attaching adapter to list.
		incomingBroadcastsAdapter = new IncomingBroadcastsArrayAdapter(
				getActivity(), R.layout.cell_home_broadcast, incomingBroadcasts);
		listViewHomeActivity.setAdapter(incomingBroadcastsAdapter);
		listViewHomeActivity
				.setEmptyView(view.findViewById(android.R.id.empty));
		listViewHomeActivity
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						restClient.getUserData();
						// getActivity().showLoadingIndicator();
					}
				});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Subscribe this Activity to the DefaultUser.
		defaultUser.addIncomingBroadcastsListener(this);
		defaultUser.addMyStatusListener(this);
		defaultUser.addMyProposalListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Unsubscribe this Activity from the DefaultUser.
		defaultUser.removeIncomingBroadcastsListener(this);
		defaultUser.removeMyStatusListener(this);
		defaultUser.removeMyProposalListner(this);
	}

	static class IncomingBroadcastsArrayAdapter extends BaseArrayAdapter<User> {

		public IncomingBroadcastsArrayAdapter(Context context,
				int textViewResourceId, List<User> incomingBroadcasts) {
			super(context, textViewResourceId, incomingBroadcasts);
		}

		@Override
		public View getViewEnhanced(final User object, View convertedView) {

			TextView textViewUserName = (TextView) convertedView
					.findViewById(R.id.textViewUserName);
			TextView textViewProposalDescription = (TextView) convertedView
					.findViewById(R.id.textViewProposalDescription);
			Button imageViewStatusIcon = (Button) convertedView
					.findViewById(R.id.imageViewStatusIcon);

			textViewUserName.setText(object.getFullName());

			if (object.getProposal().getDescription() == null
					|| object.getProposal().getDescription().trim()
							.equals("null")) {
				textViewProposalDescription.setText("(no proposal)");
				convertedView.setOnClickListener(null);
			} else {
				textViewProposalDescription.setText(object.getProposal()
						.getDescription());
				convertedView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent leechProposal = new Intent(getContext(),
								ProposalLeechActivity.class);

						leechProposal.putExtra(Keys.HOST_JID_KEY,
								object.getJid());
						getContext().startActivity(leechProposal);
					}
				});

			}

			imageViewStatusIcon.setBackgroundResource(object.getStatus()
					.getColor().getIcon());

			imageViewStatusIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					HangLog.toastD(getContext(),
							"HomeActivity.IncomingBroadcastsArrayAdapter."
									+ "imageViewStatusIcon.onClickListener",
							"NOT sending nudge to " + object.getFirstName());
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
		// hideLoadingIndicator();
		// listViewHomeActivity.onRefreshComplete();
	}

	@Override
	public void onMyProposalUpdate(Proposal proposal) {
		// TODO: Move this business logic into Proposal.
		if (proposal == null) {
			buttonCreateNewProposal
					.setText(getString(R.string.create_new_proposal));
			buttonCreateNewProposal.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(getActivity()
							.getApplicationContext(),
							CreateProposalActivity.class));
				}
			});
			buttonCreateNewProposal
					.setBackgroundResource(R.drawable.button_black);
		} else {
			buttonCreateNewProposal.setText("My Proposal: "
					+ proposal.getDescription());
			buttonCreateNewProposal.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(getActivity()
							.getApplicationContext(),
							ProposalHostActivity.class));
				}
			});
			buttonCreateNewProposal
					.setBackgroundResource(R.drawable.button_blue);
		}

		// hideLoadingIndicator();
	}

	@Override
	public void onMyStatusUpdate(Status status) {
		buttonSetStatus.setBackgroundResource(status.getColor().getIcon());
		buttonSetStatus.setText(status.getDescription());
		buttonSetStatus.setTextColor(Color.WHITE);

		// hideLoadingIndicator();
	}

}

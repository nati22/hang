package com.hangapp.android.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hangapp.android.R;
import com.hangapp.android.database.Database;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;

public class SetStatusDialogFragment extends DialogFragment {

	private Database database;
	private RestClient restClient;

	public SetStatusDialogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, 0); // remove title from dialogfragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_set_status, container);

		// Instantiate dependencies
		database = Database.getInstance();
		restClient = new RestClientImpl(database, getActivity()
				.getApplicationContext());

		return view;
	}

}

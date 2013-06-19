package com.hangapp.android.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.hangapp.android.R;

public class CreateProposalFragment extends SherlockFragment {

	private TextView textViewCreateProposal;
	private RadioButton radioButtonNow;
	private RadioButton radioButtonLater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_create_proposal,
				container, false);

		return view;
	}
}

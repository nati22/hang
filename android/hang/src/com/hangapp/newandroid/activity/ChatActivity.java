package com.hangapp.newandroid.activity;

import android.os.Bundle;

import com.hangapp.newandroid.R;
import com.hangapp.newandroid.util.BaseFragmentActivity;

public class ChatActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		// Enable the "Up" button.
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}

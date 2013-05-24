package com.hangapp.android.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.hangapp.android.R;
import com.hangapp.android.util.BaseActivity;

public class SettingsActivity extends BaseActivity {

	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		// Instantiate dependencies.
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		// Turn on the "Up" button
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void unregister(View v) {
		SharedPreferences.Editor editor = prefs.edit();

		editor.clear();
		editor.commit();

		finish();
	}
}

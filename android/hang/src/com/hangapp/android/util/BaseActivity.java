package com.hangapp.android.util;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.hangapp.android.R;

public class BaseActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SpannableString s = new SpannableString(getTitle());
		s.setSpan(new TypefaceSpan(this, "coolvetica.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.action_bar_background));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			Log.e("BaseActivity.onOptionsItemSelected",
					"Unknown MenuItem pressed: " + item.getTitle());
			return super.onOptionsItemSelected(item);
		}
	}
}

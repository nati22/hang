package com.hangapp.android.util;

import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.hangapp.android.R;

public class BaseActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		getSupportActionBar().setIcon(R.drawable.hang_logo_icon);

		hideLoadingIndicator();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void showLoadingIndicator() {
		Log.v("BaseActivity.showLoadingIndicator",
				"Turning on Loading indicator");
		setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
	}

	protected void hideLoadingIndicator() {
		Log.v("BaseActivity.hideLoadingIndicator",
				"Turning off Loading indicator");
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
	}

}

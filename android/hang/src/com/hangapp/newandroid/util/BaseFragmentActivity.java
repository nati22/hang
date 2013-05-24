package com.hangapp.newandroid.util;

import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class BaseFragmentActivity extends SherlockFragmentActivity {

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

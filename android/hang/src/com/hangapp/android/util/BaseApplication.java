package com.hangapp.android.util;

import android.app.Application;

import com.hangapp.android.database.Database;

/**
 * This is the very root of the whole app.
 * @author NATI
 *
 */
public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the Database.
		Database database = Database.getInstance();
		database.initialize(getApplicationContext());

	}
}

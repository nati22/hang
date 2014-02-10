package com.hangapp.android.util;

import android.app.Application;

import com.hangapp.android.database.Database;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the Database.
		Database database = Database.getInstance();
		database.initialize(getApplicationContext());

	}
}

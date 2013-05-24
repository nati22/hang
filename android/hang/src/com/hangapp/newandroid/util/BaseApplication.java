package com.hangapp.newandroid.util;

import android.app.Application;

import com.hangapp.newandroid.database.Database;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the Database.
		Database.getInstance().initialize(getApplicationContext());
	}
}

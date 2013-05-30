package com.hangapp.newandroid.util;

import android.app.Application;

import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.network.xmpp.XMPP;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the Database.
		Database.getInstance().initialize(getApplicationContext());

		XMPP.getInstance().initialize(getApplicationContext());
	}
}

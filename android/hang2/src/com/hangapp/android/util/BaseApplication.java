package com.hangapp.android.util;

import android.app.Application;

import com.hangapp.android.database.Database;
import com.hangapp.android.network.xmpp.XMPP;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the Database.
		Database.getInstance().initialize(getApplicationContext());

		// Initialize the XMPP object.
		XMPP.getInstance().initialize(getApplicationContext());
	}
}

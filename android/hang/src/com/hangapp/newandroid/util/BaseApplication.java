package com.hangapp.newandroid.util;

import android.app.Application;
import android.content.Intent;

import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.network.xmpp.XMPPService;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the Database.
		Database.getInstance().initialize(getApplicationContext());
	}
}

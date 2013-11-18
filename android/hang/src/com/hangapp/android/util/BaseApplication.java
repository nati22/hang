package com.hangapp.android.util;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.hangapp.android.database.Database;
import com.hangapp.android.network.xmpp.XMPP;

@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=4c0ada3e", formKey = "")
public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Enable ACRA error reporting.
//		ACRA.init(this);

		// Initialize the Database.
		Database database = Database.getInstance();
		database.initialize(getApplicationContext());

		// Initialize the XMPP object.
//		XMPP.getInstance().initialize(database, getApplicationContext());
	}
}

package com.hangapp.android.util;

import com.hangapp.android.database.DefaultUser;

import android.app.Application;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		
		// Initialize the DefaultUser. 
		DefaultUser.getInstance().init(getApplicationContext());

		// Inject the fields of this class.
		// RoboGuice.injectMembers(this, this);

		// Parse.initialize(this, "LbQCxi6ilczGOv7C3WlCy8bHw16GL7nUENeNznol",
		// "JDMOxxkWnWMaEg0031kXLPR1Pf8xDZ6lmfB7RnOo");

		// Attempt to connect to XMPP
		// xmpp.connect();
	}
}

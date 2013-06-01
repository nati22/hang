package com.hangapp.newandroid.util;

import android.app.IntentService;
import android.content.Intent;

public class MyIntentService extends IntentService {

	public MyIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		HangLog.toastD(getApplicationContext(), "MyIntentService", "Intent Received");
	}

}

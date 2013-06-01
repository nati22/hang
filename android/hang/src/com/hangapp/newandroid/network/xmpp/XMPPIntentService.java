package com.hangapp.newandroid.network.xmpp;

import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.Keys;

import android.app.IntentService;
import android.content.Intent;

public class XMPPIntentService extends IntentService {

	public XMPPIntentService() {
		super("XMPPIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int message = intent.getIntExtra(Keys.MESSAGE, 0);

		switch (message) {
		case Keys.XMPP_CONNECT:
			
			return;
		case Keys.XMPP_REGISTER:

			return;
		case Keys.XMPP_LOGIN:

			return;
		case Keys.XMPP_LOGOUT:

			return;
		case Keys.XMPP_JOIN_MUC:

			return;
		case Keys.XMPP_SEND_MUC_MESSAGE:

			return;
		default:
			HangLog.toastE(getApplicationContext(),
					"XMPPPIntentService.onHandleIntent",
					"Unknown intent message code: " + message);
			return;
		}
	}
}

package com.hangapp.newandroid.network.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.Keys;

public class MucBroadcastReceiver extends BroadcastReceiver {

	public static final String PROCESS_RESPONSE = "com.hangapp.android.intent.action.PROCESS_RESPONSE";

	@Override
	public void onReceive(Context context, Intent intent) {

		// Figure out which message the Intent is sending.
		int message = intent.getIntExtra(Keys.MESSAGE, 0);

		String mucMessage = intent.getStringExtra(Keys.MUC_MESSAGE);
		String mucName = intent.getStringExtra(Keys.MUC_NAME);

		switch (message) {
		case Keys.MUC_JOIN_ROOM:
			HangLog.toastD(context, "XMPPIntentService.joinMuc()",
					"Joined XMPP muc: " + mucName
							+ ". Did NOT add message listener.");
			// muc.addMessageListener(new MyPacketListener(mucName,
			// getApplicationContext()));
			break;
		case Keys.MUC_SEND_MESSAGE:
			HangLog.toastD(context, "MucBroadcastReceiver",
					"Received muc message: " + mucMessage);
			break;
		default:
			Log.e("XMPPPIntentService.onHandleIntent",
					"Unknown intent message code: " + message);
			return;
		}

	}
}

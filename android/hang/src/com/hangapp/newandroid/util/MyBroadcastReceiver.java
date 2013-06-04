package com.hangapp.newandroid.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MyBroadcastReceiver extends BroadcastReceiver {

	static final String TAG = "GCMDemo";
	public static final int NOTIFICATION_ID = 1;

	// private NotificationManager mNotificationManager;
	// NotificationCompat.Builder builder;
	// Context ctx;

	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		// ctx = context;
		String messageType = gcm.getMessageType(intent);
		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			HangLog.toastE(context, TAG, "Send error: " + intent.getExtras().toString());
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
				.equals(messageType)) {
			HangLog.toastD(context, TAG, "Deleted messages on server: "
					+ intent.getExtras().toString());
		} else {
			HangLog.toastD(context, TAG, "Received: "
					+ intent.getExtras().toString());
		}
		setResultCode(Activity.RESULT_OK);
	}

	// // Put the GCM message into a notification and post it.
	// private void sendNotification(String msg) {
	// mNotificationManager = (NotificationManager) ctx
	// .getSystemService(Context.NOTIFICATION_SERVICE);
	//
	// PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
	// new Intent(ctx, DemoActivity.class), 0);
	//
	// NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
	// ctx).setSmallIcon(R.drawable.ic_stat_notification)
	// .setContentTitle("GCM Notification")
	// .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
	// .setContentText(msg);
	//
	// mBuilder.setContentIntent(contentIntent);
	// mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	// }

}

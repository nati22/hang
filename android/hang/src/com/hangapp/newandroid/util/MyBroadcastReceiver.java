package com.hangapp.newandroid.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hangapp.newandroid.R;
import com.hangapp.newandroid.database.Database;
import com.hangapp.newandroid.network.rest.RestClient;
import com.hangapp.newandroid.network.rest.RestClientImpl;

public class MyBroadcastReceiver extends BroadcastReceiver {

	static final String TAG = "GCMDemo";
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager notifMgr;
	private RestClient restClient;
	private Database database;

	// NotificationCompat.Builder builder;

	@Override
	public void onReceive(Context context, Intent intent) {

		notifMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		database = Database.getInstance();
		restClient = new RestClientImpl(database, context);

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

		String messageType = gcm.getMessageType(intent);
		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			HangLog.toastE(context, TAG, "Send error: "
					+ intent.getExtras().toString());
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
			HangLog.toastD(context, TAG, "Deleted messages on server: "
					+ intent.getExtras().toString());
		} else {

			String type = intent.getExtras().getString(Keys.FromServer.TYPE);
			String senderFn = intent.getExtras().getString(Keys.FromServer.NUDGER);

			if (type.equals(Keys.FromServer.TYPE_NUDGE)) {
				Notification notif = new NotificationCompat.Builder(context)
						.setContentTitle("You got a nudge!")
						.setContentText(
								senderFn + " wants to know what you're up to!")
						.setSmallIcon(R.drawable.ic_launcher)
						.setLargeIcon(
								BitmapFactory.decodeResource(context.getResources(),
										R.drawable.ic_launcher_huge))
						.setContentIntent(
								PendingIntent.getActivity(context, 0, new Intent(), 0))
						.build();
				
				
				notifMgr.notify(1, notif);
				
				Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(400);
			//	long[] pattern = {0, 250, 400, 200, 125, 200, 75, 100, 25, 100, 200, 100, 25, 100, 10, 100};
			//	v.vibrate(pattern, -1);

			} else if (type.equals(Keys.FromServer.TYPE_TICKLE)) {
				restClient.getMyData();
		//		HangLog.toastD(context, "Received tickle", "Teehee, that tickles!");

			} else {
				HangLog.toastE(context, TAG, "Nudge type \"" + type
						+ "\" is unrecognizable.");
			}

		}
		setResultCode(Activity.RESULT_OK);
	}

}

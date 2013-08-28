package com.hangapp.android.util;

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
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hangapp.android.R;
import com.hangapp.android.activity.HomeActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;

public class GCMBroadcastReceiver extends BroadcastReceiver {

	static final String TAG = "GCMDemo";
	public static final int NUDGE_NOTIFY_ID = 1;
	public static final int BROADCAST_NOTIFY_ID = 2;
	private NotificationManager notifMgr;
	private RestClient restClient;
	private Database database;

	@Override
	public void onReceive(Context context, Intent intent) {

		// Set dependencies
		notifMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		database = Database.getInstance();
		restClient = new RestClientImpl(database, context);

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

		String messageType = gcm.getMessageType(intent);

		Log.i("GCMBroadcastReceiver", "NUDGE RECEIVED: " + messageType);

		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			Log.e(TAG, "Send error: " + intent.getExtras().toString());
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
			Log.d(TAG, "Deleted messages on server: "
					+ intent.getExtras().toString());
		} else {
			// Get message type and sender
			String type = intent.getExtras().getString(Keys.FromServer.TYPE);
			String senderFn = intent.getExtras().getString(Keys.FromServer.NUDGER);

			if (type != null && type.equals(Keys.FromServer.TYPE_NUDGE)) {

				Intent nudgeIntent = new Intent(context, HomeActivity.class);

				Notification notif = new NotificationCompat.Builder(context)
						.setContentTitle("You got a nudge!")
						.setContentText(
								senderFn + " wants to know what you're up to!")
						.setSmallIcon(R.drawable.ic_launcher)
						.setLargeIcon(
								BitmapFactory.decodeResource(context.getResources(),
										R.drawable.ic_launcher))
						.setContentIntent(
								PendingIntent.getActivity(context, 0, nudgeIntent, 0))
						.build();

				notif.flags = Notification.FLAG_AUTO_CANCEL;
				notifMgr.notify(NUDGE_NOTIFY_ID, notif);

				Vibrator v = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(400);
				// long[] pattern = {0, 250, 400, 200, 125, 200, 75, 100, 25,
				// 100,
				// 200, 100, 25, 100, 10, 100};
				// v.vibrate(pattern, -1);

			} else if (type != null && type.equals(Keys.FromServer.TYPE_TICKLE)) {
				Log.d("GCMBroadcastReceiver", "Received a TICKLE");
				restClient.getMyData();
			} else if (type != null
					&& type.equals(Keys.FromServer.TYPE_NEW_BROADCAST)) {
				Log.d("GCMBroadcastReceiver", "Received a NEW BROADCAST");

				Intent newBroadcastIntent = new Intent(context, HomeActivity.class);
				newBroadcastIntent.putExtra(Keys.TAB_INTENT, 0);

				Notification notif = new NotificationCompat.Builder(context)
						.setContentTitle("You have a new Broadcast!")
						.setContentText(senderFn + " is now broadcasting to you!")
						.setSmallIcon(R.drawable.ic_launcher)
						.setLargeIcon(
								BitmapFactory.decodeResource(context.getResources(),
										R.drawable.ic_launcher))
						.setContentIntent(
								PendingIntent.getActivity(context, 0,
										newBroadcastIntent, 0))
										
						.build();
				notif.flags = Notification.FLAG_AUTO_CANCEL;
				
				notifMgr.notify(BROADCAST_NOTIFY_ID, notif);

				
				Vibrator v = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(400);

			} else {
				Log.e(TAG, "Nudge type \"" + type + "\" is unrecognizable.");
			}

		}
		setResultCode(Activity.RESULT_OK);
	}
}

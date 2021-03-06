package com.hangapp.android.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hangapp.android.R;
import com.hangapp.android.activity.FirebaseChatActivity;
import com.hangapp.android.activity.HomeActivity;
import com.hangapp.android.database.Database;
import com.hangapp.android.network.rest.RestClient;
import com.hangapp.android.network.rest.RestClientImpl;
import com.hangapp.android.util.Keys.FromServer;

// TODO: Should this class be in the "utils" package?
public class GCMBroadcastReceiver extends BroadcastReceiver {

	static final String TAG = "GCMDemo";
	public static final int NUDGE_NOTIFY_ID = 1;
	public static final int BROADCAST_NOTIFY_ID = 2;
	private NotificationManager notifMgr;
	
	public static final int VIBRATE_LENGTH_SHORT = 100;
	public static final long[] VIBRATE_PATTERN_DOUBLE = new long[] {0, 70, 100, 75};

	// Dependencies.
	private Database database;
	private RestClient restClient;

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

			if (messageType == null)
				return;

			// Get message type and sender
			String type = intent.getExtras().getString(Keys.FromServer.TYPE);
			String senderFn = intent.getExtras().getString(Keys.FromServer.NUDGER);

			// TODO: Need to make sure gcm ids are removed properly anytime
			// a User uninstalls the app, but until then I'll use this sanity
			// check
			String targetJid = intent.getExtras()
					.getString(Keys.FromServer.TARGET);

			if (!targetJid.equals(database.getMyJid())) {
				Log.e(TAG, "THIS NUDGE ISN'T INTENDED FOR ME!! (JID " + targetJid
						+ ")");
				return;
			}

			if (type != null && type.equals(Keys.FromServer.TYPE_NUDGE)) {

				Intent nudgeIntent = new Intent(context, HomeActivity.class);

				// ProfilePictureView
				Log.i(TAG, "" + intent.getExtras().getString(Keys.FromServer.FROM));
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

				AudioManager audioM = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				if (audioM.getRingerMode() != 0) {
					Vibrator v = (Vibrator) context
							.getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(VIBRATE_LENGTH_SHORT);
				}
				
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
										newBroadcastIntent, 0)).build();
				notif.flags = Notification.FLAG_AUTO_CANCEL;

				notifMgr.notify(BROADCAST_NOTIFY_ID, notif);

				Vibrator v = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(VIBRATE_LENGTH_SHORT);

			} else if (type != null && type.equals(Keys.FromServer.TYPE_NEW_CHAT)) {
				String hostJid = intent.getExtras().getString(FromServer.HOST);
				boolean isHost = hostJid.equals(database.getMyJid()) ? true : false;

				Intent newChatMessageIntent = new Intent(context,
						FirebaseChatActivity.class);

				newChatMessageIntent.putExtra(Keys.HOST_JID, hostJid);
				newChatMessageIntent.putExtra(Keys.IS_HOST, isHost);

				String proposalDesc = "";
				if (isHost) {
					proposalDesc = database.getMyProposal().getDescription();
				} else {
					proposalDesc = database.getIncomingUser(hostJid).getProposal()
							.getDescription();
				}

				Notification notif = new NotificationCompat.Builder(context)
						.setContentTitle(proposalDesc)
						.setContentText("You have a new message!")
						.setSmallIcon(R.drawable.ic_launcher)
						.setLargeIcon(
								BitmapFactory.decodeResource(context.getResources(),
										R.drawable.ic_launcher))
						.setContentIntent(
								PendingIntent.getActivity(context, 0,
										newChatMessageIntent, 0)).build();
				notif.flags = Notification.FLAG_AUTO_CANCEL;

				notifMgr.notify(BROADCAST_NOTIFY_ID, notif);

				AudioManager audioM = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				if (audioM.getRingerMode() != 0) {
					Vibrator v = (Vibrator) context
							.getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(VIBRATE_LENGTH_SHORT);
				}

			} else {
				Log.e(TAG, "Nudge type \"" + type + "\" is unrecognizable.");
				Log.e(TAG, "intent.toString() " + intent.getExtras().toString());

			}

		}
		setResultCode(Activity.RESULT_OK);
	}
}

package com.hangapp.android.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.Message;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.hangapp.android.R;
import com.hangapp.android.activity.ChatActivity;
import com.hangapp.android.database.Database;

public final class Utils {
	public static final String BASE_URL = "http://therealhangapp.appspot.com/rest";
	public static final String USERS_URL = BASE_URL + "/users";
	public static final String TAG = "Utils";
	public static final int MUC_MESSAGE_RECEIVED_NOTIFICATION_ID = 3;

	/**
	 * Returns true IFF this device is connected to the internet, either through
	 * WiFi, 3G or 4G.
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		return activeNetworkInfo != null;
	}

	public static String convertArrayToString(List<String> stringArray) {
		if (stringArray == null) {
			return null;
		}

		StringBuilder result = new StringBuilder();
		for (String string : stringArray) {
			result.append(string);
			result.append(",");
		}
		return result.length() > 0 ? result.substring(0, result.length() - 1)
				: "";
	}

	public static List<String> convertStringToArray(String str) {
		if (str == null) {
			return new ArrayList<String>();
		}

		String[] arr = str.split(",");

		// Arrays.asList alone returns a fixed-length list
		// so we need to return it as a new ArrayList
		return new ArrayList<String>(Arrays.asList(arr));
	}

	public static String getAbbvRemainingTimeString(DateTime expirationDate) {
		DateTime rightNow = new DateTime();

		if (expirationDate.isBefore(rightNow)) {
			return "0h";
		}

		int hrs = 0;
		int min = Minutes.minutesBetween(rightNow, expirationDate).getMinutes();

		while (min >= 60) {
			hrs++;
			min = min - 60;
		}

		Log.i(TAG, hrs + "hrs " + min + " min");
		if (hrs == 0) {
			// TODO: Eventually we should add a countdown so we can display
			if (min < 30) {
				return "<30m";
			}
			return "1h";
		} else if (min < 30) {
			Log.v(TAG, "< 30 min past the hr, posting " + hrs);
			return hrs + "h";
		} else if (min >= 30) {
			Log.v(TAG, "> 30 min past the hr, posting " + (hrs + 1));
			hrs++;
			return hrs + "h";
		} else {
			Log.e(TAG, hrs + " hrs " + min + " min");
			return null;
		}

	}

	public static int getRemainingHours(DateTime expirationDate) {
		DateTime rightNow = new DateTime();

		if (expirationDate.isBefore(rightNow)) {
			return 0;
		}

		return Hours.hoursBetween(rightNow, expirationDate).getHours();
	}

	public static void showChatNotification(Context context, String title,
			String notificationString, String hostJid) {
		Intent nudgeIntent = new Intent(context, ChatActivity.class);
		nudgeIntent.putExtra(Keys.HOST_JID, hostJid);

		// Open up ChatActivity, but put HomeActivity behind it on the
		// Android Activity back stack.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(ChatActivity.class);
		stackBuilder.addNextIntent(nudgeIntent);
		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager notifMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notif = new NotificationCompat.Builder(context)
				.setContentTitle(title)
				.setContentText(notificationString)
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.ic_launcher))
				.setContentIntent(pendingIntent).build();

		notif.flags = Notification.FLAG_AUTO_CANCEL;
		notifMgr.notify(MUC_MESSAGE_RECEIVED_NOTIFICATION_ID, notif);

		Vibrator v = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(400);
	}

	public static String parseJidFromMessage(Message message) {
		return message.getFrom().substring(
				message.getFrom().indexOf(".com/") + 5);
	}

	/**
	 * Takes a JID and converts it to the person's name. Will return the string
	 * "me" if the JID is equal to the current user's JID.
	 * 
	 * @return
	 */
	public static String convertJidToName(String fromJid, Database database) {
		final String myJid = database.getMyJid();
		String from;

		if (myJid != null && myJid.equals(fromJid)) {
			from = "Me"; // TODO: Internationalize.
		} else if (database.getOutgoingUser(fromJid) != null) {
			from = database.getOutgoingUser(fromJid).getFullName();
		} else {
			from = "User#" + fromJid;
		}

		return from;
	}

}

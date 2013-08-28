package com.hangapp.android.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import android.util.Log;

import com.hangapp.android.R;

public final class Utils {
	public static final String BASE_URL = "http://therealhangapp.appspot.com/rest";
	public static final String USERS_URL = BASE_URL + "/users";
	public static final String TAG = "Utils";

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

	public static String convertStringArrayToString(List<String> stringArray) {
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

		if (expirationDate.isBefore(rightNow))
			return null;

		int hrs = 0;
		int min = Minutes.minutesBetween(rightNow, expirationDate).getMinutes();
		
		while (min >= 60) {
			hrs++;
			min = min - 60;
		}

		Log.i(TAG, hrs + "hrs " + min + " min");
		if (hrs == 0) {
			// TODO: Eventually we should add a countdown so we can display
			if (min < 30) return "<30m";
			return "<1h";
		} else if (min < 30) {
			Log.i(TAG, "< 30 min past the hr, posting " + hrs);
			return hrs + "h";
		} else if (min >= 30) {
			Log.i(TAG, "> 30 min past the hr, posting " + (hrs + 1));
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

	public static void showNotification(Context context, String title,
			String notificationString, Class<?> activityToOpen, int notificationId) {
		Intent nudgeIntent = new Intent(context, activityToOpen);

		NotificationManager notifMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notif = new NotificationCompat.Builder(context)
				.setContentTitle(title)
				.setContentText(notificationString)
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.ic_launcher))
				.setContentIntent(
						PendingIntent.getActivity(context, 0, nudgeIntent, 0))
				.build();

		notif.flags = Notification.FLAG_AUTO_CANCEL;
		notifMgr.notify(notificationId, notif);

		Vibrator v = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(400);
	}

}

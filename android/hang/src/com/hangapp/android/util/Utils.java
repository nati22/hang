package com.hangapp.android.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Hours;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class Utils {
	public static final String BASE_URL = "http://therealhangapp.appspot.com/rest";
	public static final String USERS_URL = BASE_URL + "/users";

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
		return Arrays.asList(arr);
	}

	public static int getRemainingHours(DateTime expirationDate) {
		DateTime rightNow = new DateTime();

		if (expirationDate.isBefore(rightNow)) {
			return 0;
		}

		return Hours.hoursBetween(rightNow, expirationDate).getHours();
	}

}

package com.hangapp.newandroid.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Apparently calling HangLog during UI events will stop the event cold. Fml.
 */
@Deprecated
public class HangLog {

	public static void toastD(Context context, String tag, String message) {
		Log.d(tag, message);
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void toastE(Context context, String tag, String message) {
		Log.e(tag, message);
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void toastE(Context context, String tag, Exception exception) {
		Log.e(tag, exception.getLocalizedMessage(), exception);
		Toast.makeText(context, exception.getLocalizedMessage(),
				Toast.LENGTH_SHORT).show();
	}

}

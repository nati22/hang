package com.hangapp.android.network.rest;

import android.content.Context;
import android.util.Log;

import com.hangapp.android.util.SafeAsyncTask;
import com.hangapp.android.util.Utils;

/**
 * Never use an AsyncTask directly, in the whole codebase. Instead, extend this
 * class. This class bakes in several checks and GUI notifications related to
 * network calls right into it.
 * 
 * Don't forget to call "super(context)" in the constructor of your subclass.
 * 
 * @author girum
 * 
 */
abstract class BaseHttpRequest<ResultT> extends SafeAsyncTask<ResultT> {

	static final String BASE_URL = "http://hangapp2.appspot.com";

	protected String responseString = "";
	protected String uri = null;
	protected Context context;

	protected BaseHttpRequest(Context context, String uriSuffix) {
		super();
		this.uri = BASE_URL + uriSuffix;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		// Verify there is an Internet connection
		if (!Utils.isNetworkAvailable(context)) {
			// final String errorMessage = "No internet connection detected";
			// Log.e(errorMessage);
			// Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
			Log.e("BaseHttpRequestAsyncTask.onPreExecute",
					"No internet connection detected");

			// If there is no Internet connection, then don't run the AsyncTask.
			cancel(true);
		}
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);
		Log.e("BaseHttpRequestAsyncTask.onException", e.getMessage());
	}
}
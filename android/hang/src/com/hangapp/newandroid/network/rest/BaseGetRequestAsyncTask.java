package com.hangapp.newandroid.network.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.SafeAsyncTask;
import com.hangapp.newandroid.util.Utils;

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
public abstract class BaseGetRequestAsyncTask<ResultT> extends
		SafeAsyncTask<ResultT> {

	static final String BASE_URL = "http://hangapp2.appspot.com";

	// @Inject
	protected String responseString = "";
	private String uri = null;
	private Context context;

	protected BaseGetRequestAsyncTask(Context context, String uriSuffix) {
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
			HangLog.toastE(context, "BaseGetRequestAsyncTask.onPreExecute",
					"No internet connection detected");

			// If there is no Internet connection, then don't run the AsyncTask.
			cancel(true);
		}
	}

	// TODO: should that be ResultT or Result<T>?
	@Override
	public ResultT call() throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpUriRequest getRequest = new HttpGet(uri);

		Log.v("BaseGetRequestAsyncTask.call", "Sending GET request with URI: "
				+ uri);
		
		// The actual network call
		String responseString = EntityUtils.toString(client.execute(getRequest)
				.getEntity());

		if (responseString != null) {
			Log.v("BaseGetRequestAsyncTask.call", "Got HTTP result: "
					+ responseString);
		} else {
			throw new Exception("GET request receieved null response string.");
		}

		// Save the responseString internally, for inheriting classes to use
		// (e.g. most classes will parse this string).
		this.responseString = responseString;

		return null;
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);
		HangLog.toastE(context, "BaseGetRequestAsyncTask.onException", e);
	}
}
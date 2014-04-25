package com.hangapp.android.network.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

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
abstract class BaseGetRequestAsyncTask<ResultT> extends
		BaseHttpRequest<ResultT> {

	protected BaseGetRequestAsyncTask(Context context, String uriSuffix) {
		super(context, uriSuffix);
	}

	@Override
	public ResultT call() throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpUriRequest getRequest = new HttpGet(uri);

	/*	Log.v("BaseGetRequestAsyncTask.call", "Sending GET request with URI: "
				+ uri);
*/
		// The actual network call
		String responseString = EntityUtils.toString(client.execute(getRequest)
				.getEntity());

		if (responseString != null) {
	/*		Log.v("BaseGetRequestAsyncTask.call", "Got HTTP result: "
					+ responseString);*/
		} else {
			throw new Exception("GET request receieved null response string.");
		}

		// Save the responseString internally, for inheriting classes to use
		// (e.g. most classes will parse this string).
		this.responseString = responseString;

		return null;
	}

}
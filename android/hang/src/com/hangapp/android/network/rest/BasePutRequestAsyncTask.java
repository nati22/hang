package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
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
abstract class BasePutRequestAsyncTask<ResultT> extends
		BaseHttpRequest<ResultT> {

	protected List<NameValuePair> parameters = null;

	protected BasePutRequestAsyncTask(Context context, String uriSuffix,
			List<NameValuePair> parameters) {
		super(context, uriSuffix);
		this.parameters = parameters;
	}

	@Override
	public ResultT call() throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpPut putRequest = new HttpPut(uri);

		// Add the parameters in.
		if (parameters != null) {
			putRequest.setEntity(new UrlEncodedFormEntity(parameters));
		}

/*		Log.v("BasePutRequestAsyncTask.call", "Sending PUT request with URI: "
				+ uri);*/
		String responseString = EntityUtils.toString(client.execute(putRequest)
				.getEntity());

		if (responseString != null) {
/*			Log.v("BasePutRequestAsyncTask.call", "Got HTTP result: "
					+ responseString);*/
		} else {
			throw new Exception("PUT request receieved null response string.");
		}

		// Save the responseString internally, for inheriting classes to use
		// (e.g. most classes will parse this string).
		this.responseString = responseString;

		return null;
	}
}
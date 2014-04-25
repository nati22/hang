package com.hangapp.android.network.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

class BaseDeleteRequestAsyncTask<ResultT> extends BaseHttpRequest<ResultT> {

	protected BaseDeleteRequestAsyncTask(Context context, String uriSuffix) {
		super(context, uriSuffix);
	}

	@Override
	public ResultT call() throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpUriRequest deleteRequest = new HttpDelete(uri);

/*		Log.v("BaseDeleteRequestAsyncTask", "Sending DELETE request with URI: "
				+ uri);*/
		String responseString = EntityUtils.toString(client.execute(
				deleteRequest).getEntity());

		if (responseString != null) {
/*			Log.v("BaseDeleteRequestAsyncTask", "Got HTTP result: "
					+ responseString);*/
		} else {
			throw new Exception(
					"DELETE request receieved null response string.");
		}

		// Save the responseString internally, for inheriting classes to use
		// (e.g. most classes will parse this string).
		this.responseString = responseString;

		return null;
	}
}

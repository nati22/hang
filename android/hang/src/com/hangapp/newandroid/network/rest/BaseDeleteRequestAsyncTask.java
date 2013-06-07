package com.hangapp.newandroid.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.SafeAsyncTask;
import com.hangapp.newandroid.util.Utils;

class BaseDeleteRequestAsyncTask<ResultT> extends SafeAsyncTask<ResultT> {

	static final String BASE_URL = "http://hangapp2.appspot.com";

	protected String responseString = "";
	private String uri = null;
	private Context context;

	protected BaseDeleteRequestAsyncTask(Context context, String uriSuffix) {
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
			HangLog.toastE(context, "BaseDeleteRequestAsyncTask",
					"No internet connection detected");

			// If there is no Internet connection, then don't run the AsyncTask.
			cancel(true);
		}
	}

	// TODO: should that be ResultT or Result<T>?
	@Override
	public ResultT call() throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpUriRequest deleteRequest = new HttpDelete(uri);
		
		Log.v("BaseDeleteRequestAsyncTask", "Sending DELETE request with URI: "
				+ uri);
		String responseString = EntityUtils.toString(client
				.execute(deleteRequest).getEntity());

		if (responseString != null) {
			Log.v("BaseDeleteRequestAsyncTask", "Got HTTP result: "
					+ responseString);
		} else {
			throw new Exception("DELETE request receieved null response string.");
		}

		// Save the responseString internally, for inheriting classes to use
		// (e.g. most classes will parse this string).
		this.responseString = responseString;

		return null;
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);
		HangLog.toastE(context, "BaseDeleteRequestAsyncTask", e);
	}
}

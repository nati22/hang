package com.hangapp.newandroid.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
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
public abstract class BasePutRequestAsyncTask<ResultT> extends
		SafeAsyncTask<ResultT> {

	static final String BASE_URL = "http://hangapp2.appspot.com";

	// @Inject
	protected String responseString = "";

	protected List<NameValuePair> parameters = null;

	private String uri = null;

	protected Context context;

	// protected BasePutRequestAsyncTask(Context context, String uriSuffix) {
	// super(context);
	// this.uri = BASE_URL + uriSuffix;
	// }

	protected BasePutRequestAsyncTask(Context context, String uriSuffix,
			List<NameValuePair> parameters) {
		super();
		this.uri = BASE_URL + uriSuffix;
		this.parameters = parameters;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		// Verify there is an Internet connection
		if (!Utils.isNetworkAvailable(context)) {
			// final String errorMessage = "No internet connection detected";
			// Log.e(errorMessage);
			// Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
			HangLog.toastE(context, "BasePutRequestAsyncTask.onPreExecute",
					"No internet connection detected");

			// If there is no Internet connection, then don't run the AsyncTask.
			cancel(true);
		}
	}

	// TODO: should that be ResultT or Result<T>?
	@Override
	public ResultT call() throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpPut putRequest = new HttpPut(uri);

		// Add the parameters in.
		if (parameters != null) {
			putRequest.setEntity(new UrlEncodedFormEntity(parameters));
		}

		Log.v("BasePutRequestAsyncTask.call", "Sending PUT request with URI: "
				+ uri);
		String responseString = EntityUtils.toString(client.execute(putRequest)
				.getEntity());

		if (responseString != null) {
			Log.v("BasePutRequestAsyncTask.call", "Got HTTP result: "
					+ responseString);
		} else {
			throw new Exception("PUT request receieved null response string.");
		}

		// Save the responseString internally, for inheriting classes to use
		// (e.g. most classes will parse this string).
		this.responseString = responseString;

		return null;
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);
		HangLog.toastE(context, "BasePutRequestAsyncTask.onException", e);
	}
}
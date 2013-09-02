package com.hangapp.android.network.rest;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

public class AddMultipleBroadcastsAsyncTask extends BasePutRequestAsyncTask<String>{

	private RestClient restClient;
	
	protected AddMultipleBroadcastsAsyncTask(Context context, RestClient restClient, String uriSuffix,
			List<NameValuePair> parameters) {
		super(context, uriSuffix, parameters);
		
		this.restClient = restClient;
	}
	
	@Override
	public String call() throws Exception {
		// Execute the PUT request
		super.call();

		Log.i("AddMultipleBroadcasteesAsyncTask", responseString);

		return null;
	}
	
	@Override
	protected void onSuccess(String result) throws Exception {
		restClient.getMyData();
	}

}

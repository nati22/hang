//package com.hangapp.android.util;
//
//import android.content.Context;
//
//import com.google.inject.AbstractModule;
//import com.hangapp.android.network.rest.RestClient;
//import com.hangapp.android.network.rest.RestClientImpl;
//
//public final class MainModule extends AbstractModule {
//
//	@SuppressWarnings("unused") private Context context;
//
//	public MainModule(Context context) {
//		this.context = context;
//	}
//
//	@Override
//	protected void configure() {
//		bind(RestClient.class).to(RestClientImpl.class);
//	}
//}

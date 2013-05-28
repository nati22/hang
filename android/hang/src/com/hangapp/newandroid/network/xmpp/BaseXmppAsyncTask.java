package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

import com.hangapp.newandroid.util.HangLog;
import com.hangapp.newandroid.util.SafeAsyncTask;

public class BaseXmppAsyncTask<T> extends SafeAsyncTask<T> {

	protected String myJid;
	protected XMPPConnection xmppConnection;
	protected Context context;

	protected BaseXmppAsyncTask(String myJid, XMPPConnection xmppConnection,
			Context context) {
		this.myJid = myJid;
		this.xmppConnection = xmppConnection;
		this.context = context;
	}

	@Override
	public T call() throws Exception {
		return null; // do nothing.
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		HangLog.toastE(context, "BaseXmppAsyncTask.onException", e.getMessage());
	}

}

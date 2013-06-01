package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Context;

import com.hangapp.newandroid.util.HangLog;

public class SendMucMessageAsyncTask extends BaseXmppAsyncTask<MultiUserChat> {

//	private XMPP xmpp;
	private MultiUserChat muc;
	private String mucName;
	private String message;

	protected SendMucMessageAsyncTask(MultiUserChat muc, String mucName,
			String message, Context context) {
		super(null, null, context);
//		this.xmpp = XMPP.getInstance();
		this.muc = muc;
		this.mucName = mucName;
		this.message = message;
	}

	@Override
	public MultiUserChat call() throws Exception {
		if (muc == null) {
			throw new Exception("Won't send message: Muc was null");
		}

		muc.sendMessage(message);

		return muc;
	}

	@Override
	protected void onSuccess(MultiUserChat muc) throws Exception {
		super.onSuccess(muc);

		HangLog.toastD(context, "SendMucMessage.onSuccess", "Sent message: "
				+ message);
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		HangLog.toastE(context, "SendMucMessage.onException",
				"Did not send message: " + e.getMessage());
	}
}

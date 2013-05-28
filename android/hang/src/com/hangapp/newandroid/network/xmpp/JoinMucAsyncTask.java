package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Context;

import com.hangapp.newandroid.util.HangLog;

public class JoinMucAsyncTask extends BaseXmppAsyncTask<MultiUserChat> {

	private MultiUserChat muc;
	private String mucName;

	protected JoinMucAsyncTask(MultiUserChat muc, String myJid, String mucName,
			XMPPConnection xmppConnection, Context context) {
		super(myJid, xmppConnection, context);
		this.muc = muc;
		this.mucName = mucName;
	}

	@Override
	public MultiUserChat call() throws Exception {
		muc.join(myJid);

		return muc;
	}

	@Override
	protected void onSuccess(MultiUserChat muc) throws Exception {
		super.onSuccess(muc);

		HangLog.toastD(context, "JoinMuc.onSuccess",
				"Joined muc: " + muc.getRoom());

		muc.addMessageListener(new MessageListener(mucName));
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		HangLog.toastE(context, "JoinMuc.onException",
				"Did not join Muc: " + e.getMessage());
	}
}

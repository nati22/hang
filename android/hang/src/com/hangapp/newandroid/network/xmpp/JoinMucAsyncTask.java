package com.hangapp.newandroid.network.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Context;

import com.hangapp.newandroid.util.HangLog;

public class JoinMucAsyncTask extends BaseXmppAsyncTask<MultiUserChat> {

	private MultiUserChat muc;
	private PacketListener messageListener;

	protected JoinMucAsyncTask(MultiUserChat muc, String myJid,
			PacketListener messageListener, XMPPConnection xmppConnection,
			Context context) {
		super(myJid, xmppConnection, context);
		this.muc = muc;
		this.messageListener = messageListener;
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

		muc.addMessageListener(messageListener);
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		HangLog.toastE(context, "JoinMuc.onException",
				"Did not join Muc: " + e.getMessage());
	}
}

package com.hangapp.android.model.callback;

import java.util.List;

import com.hangapp.android.model.User;

public interface IncomingBroadcastsListener {
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts);
}

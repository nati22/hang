package com.hangapp.newandroid.model.callback;

import java.util.List;

import com.hangapp.newandroid.model.User;

public interface IncomingBroadcastsListener {
	public void onIncomingBroadcastsUpdate(List<User> incomingBroadcasts);
}

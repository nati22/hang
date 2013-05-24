package com.hangapp.newandroid.model.callback;

import java.util.List;

import com.hangapp.newandroid.model.User;

public interface OutgoingBroadcastsListener {
	public void onOutgoingBroadcastsUpdate(List<User> outgoingBroadcasts);
}

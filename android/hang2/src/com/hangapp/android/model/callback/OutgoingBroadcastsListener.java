package com.hangapp.android.model.callback;

import java.util.List;

import com.hangapp.android.model.User;

public interface OutgoingBroadcastsListener {
	public void onOutgoingBroadcastsUpdate(List<User> outgoingBroadcasts);
}

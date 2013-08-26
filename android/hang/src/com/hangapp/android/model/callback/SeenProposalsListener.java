package com.hangapp.android.model.callback;

import java.util.List;

import com.hangapp.android.model.User;

public interface SeenProposalsListener {

	public void onMySeenProposalsUpdate(List<String> seenJids);
	
}

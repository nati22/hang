package com.hangapp.android.model.callback;

import java.util.List;

public interface SeenProposalsListener {

	public void onMySeenProposalsUpdate(List<String> seenJids);
	
}

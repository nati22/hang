package com.hangapp.android.util;

public final class FlurryEventHandler {
	
	private static FlurryEventHandler instance = new FlurryEventHandler();
	
	/** Private constructor */
	private FlurryEventHandler() { }

	public static final synchronized FlurryEventHandler getInstance() {
		return instance;
	}
	
/*	public static void proposalEntered() {
		
	}*/
	
}

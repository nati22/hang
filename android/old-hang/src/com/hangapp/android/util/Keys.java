package com.hangapp.android.util;

public final class Keys {

	public static final String REGISTERED = "regisetered";
	public static final String JID = "jid";
	public static final String TARGET = "target";
	public static final String FIRST_NAME = "fn";
	public static final String LAST_NAME = "ln";
	public static final String STATUS_TEXT = "text";
	public static final String STATUS_COLOR = "color";
	public static final String STATUS_EXPIRATION_DATE = "exp";
	public static final String PROPOSAL_DESCRIPTION = "des";
	public static final String PROPOSAL_LOCATION = "loc";
	public static final String PROPOSAL_TIME = "time";
	public static final String PROPOSAL_INTERESTED = "int";
	public static final String PROPOSAL_CONFIRMED = "conf";
	public static final String OUTGOING = "out";
	public static final String INCOMING = "inc";

	public static final String PROPOSAL_PARCEL_KEY = "parcel";
	public static final String HOST_JID_KEY = "host";
	
	/**
	 * Instead of converting directly from Date objects to Strings and vice
	 * versa, Android wants us to use DateFormat objects which require a
	 * SimpleDateFormat (string) like this.
	 * 
	 * I think.
	 */
	public static final String SPECIFIED_DATE_FORMAT = "MM/dd/yyyy hh:mm:ss aa";
	public static final String SERVER_ADDRESS = "@conference.ec2-54-242-9-67.compute-1.amazonaws.com/";
}

package com.hangapp.newandroid.util;

public final class Keys {

	public static final String REGISTERED = "registered";
	public static final String JID = "jid";
	public static final String TARGET = "target";
	public static final String FIRST_NAME = "fn";
	public static final String LAST_NAME = "ln";
	public static final String STATUS_TEXT = "text";
	public static final String AVAILABILITY_COLOR = "color";
	public static final String AVAILABILITY_EXPIRATION_DATE = "exp";
	public static final String PROPOSAL_DESCRIPTION = "des";
	public static final String PROPOSAL_LOCATION = "loc";
	public static final String PROPOSAL_TIME = "time";
	public static final String PROPOSAL_INTERESTED = "int";
	public static final String PROPOSAL_CONFIRMED = "conf";
	public static final String OUTGOING = "out";
	public static final String INCOMING = "inc";
	public static final String LIBRARY = "lib";
	public static final String REGISTRATION_ID = "regid";
	public static final String FRIENDS = "friends";
	public static final String HOST_JID = "host_jid";
	public static final String FREE = "free";
	public static final String BUSY = "busy";
	public static final String MUC_NAME = "muc_name";
	public static final String MUC_MESSAGE = "muc_message";

	/*
	 * IntentService message codes.
	 */
	public static final String MESSAGE = "msg";
	public static final int XMPP_CONNECT = 100;
	public static final int XMPP_REGISTER = 101;
	public static final int XMPP_LOGIN = 102;
	public static final int XMPP_LOGOUT = 103;
	public static final int XMPP_JOIN_MUC = 104;
	public static final int XMPP_LEAVE_MUC = 105;
	public static final int XMPP_SEND_MUC_MESSAGE = 106;

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

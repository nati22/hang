package com.hangapp.android.util;

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
	public static final String PROPOSAL_START_TIME = "time";
	public static final String PROPOSAL_INTERESTED = "int";
	public static final String PROPOSAL_CONFIRMED = "conf";
	public static final String OUTGOING = "out";
	public static final String INCOMING = "inc";
	public static final String JIDS_IM_INTERESTED_IN = "jids_im_interested_in";
	public static final String LIBRARY = "lib";
	public static final String PROPOSAL_SEEN = "seen";
	public static final String REGISTRATION_ID = "regid";
	public static final String FRIENDS = "friends";
	public static final String HOST_JID = "host_jid";
	public static final String IS_HOST = "is_host";
	public static final String FREE = "free";
	public static final String BUSY = "busy";
	public static final String MUC_NAME = "muc_name";
	public static final String MUC_MESSAGE = "muc_message";
	public static final String MESSAGE_PACKET_ID = "message_packet_id";
	public static final String MESSAGE_FROM = "message_from";
	public static final String MESSAGE_BODY = "message_body";

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
	public static final int XMPP_JOIN_ALL_MUCS = 107;

	/*
	 * BroadcastReceiver message codes.
	 */
	public static final int MUC_JOIN_ROOM = 200;
	public static final int MUC_SEND_MESSAGE = 201;

	/**
	 * Instead of converting directly from Date objects to Strings and vice
	 * versa, Android wants us to use DateFormat objects which require a
	 * SimpleDateFormat (string) like this.
	 * 
	 * I think.
	 */
	public static final String SPECIFIED_DATE_FORMAT = "MM/dd/yyyy hh:mm:ss aa";
	
	
	public static final String CONFERENCE_SERVER_ADDRESS = "@conference.ec2-54-242-9-67.compute-1.amazonaws.com/";

	/**
	 * I'm sure this is a terrible, terrible idea but I wanted to show that
	 * these Keys are specifically for receiving nudges from GCM.
	 * 
	 * TODO: I actually kind of like this idea of organizing our keys into static inner classes.
	 */
	public static class FromServer {
		public static final String FROM = "from";
		public static final String COLLAPSE_KEY = "collapse_key";
		public static final String NUDGER = "nudger";
		public static final String TYPE = "type";
		public static final String SENDER = "sender_jid";
		public static final String HOST = "host_jid";
		
		public static final String TYPE_NUDGE = "nudge";
		public static final String TYPE_TICKLE = "tickle";
		public static final String TYPE_NEW_CHAT = "new_chat";
		public static final String TYPE_NEW_BROADCAST = "new_broadcast";
	}

	public static final String TAB_INTENT = "whichtab";

	public static final String YOU_FRAGMENT_TAG = "#dontbelievemechrisbosh";
	public static final String MY_PROPOSAL_FRAGMENT_TAG = "myProposalFragmentTAG";
	public static final String CREATE_PROPOSAL_FRAGMENT_TAG = "createProposalFragmentTAG";

	// Firebase keys
	public static final String FIREBASE_HOST_ID = "host_id";
	public static final String FIREBASE_MEMBERS = "members";
	public static final String FIREBASE_MESSAGES = "messages";
	public static final String FIREBASE_MEMBERS_PRESENT = "present";
	
	/**
	 * Flurry Keys
	 */
	public static final String FLURRY_KEY = "NKR88TW7NKXYS5XDZ2DC";

	public static class FlurryEvent {

		/* Tracking page views */
		public static final String VIEW_FEED_FRAGMENT = "VIEW_FEED_FRAGMENT";
		public static final String VIEW_YOU_FRAGMENT = "VIEW_YOU_FRAGMENT";
		public static final String VIEW_PROPOSALS_FRAGMENT = "VIEW_PROPOSALS_FRAGMENT";
		public static final String VIEW_SETTINGS_PAGE = "VIEW_SETTINGS_PAGE";
		public static final String VIEW_INCOMING_BROADCASTS = "VIEW_INCOMING_BROADCASTS";
		public static final String VIEW_OUTGOING_BROADCASTS = "VIEW_OUTGOING_BROADCASTS";
		public static final String VIEW_PROPOSAL = "VIEW_PROPOSAL";
		public static final String VIEW_CHATROOM = "VIEW_CHATROOM";

		public static final String MANUAL_REFRESH = "MANUAL_REFRESH";
		public static final String NUDGE_USER = "NUDGE_USER";
		public static final String PROPOSAL_CREATED = "PROPOSAL_CREATED";
		public static final String STATUS_UPDATED_WITH_TEXT = "STATUS_UPDATED_WITH_TEXT";
		public static final String STATUS_UPDATED_WITHOUT_TEXT = "STATUS_UPDATED_WITHOUT_TEXT";
		public static final String PROPOSAL_VIEWED = "PROPOSAL_VIEWED";

		public static class params {
			public static final String USER_JID = "USER_JID";
			public static final String USER_FULL_NAME = "USER_FULL_NAME";
		}

	}

	/**
	 * Google Analytics Keys
	 */
	public static final String GOOGLE_ANALYTICS_TRACKING_ID = "UA-43852319-1";

}

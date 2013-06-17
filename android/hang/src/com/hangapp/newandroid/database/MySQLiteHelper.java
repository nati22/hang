package com.hangapp.newandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class MySQLiteHelper extends SQLiteOpenHelper {

	static final String TABLE_MESSAGES = "messages";
	static final String COLUMN_MESSAGE_PACKET_ID = "packet_id";
	static final String COLUMN_MESSAGE_MUC_NAME = "muc_name";
	static final String COLUMN_MESSAGE_FROM = "message_from";
	static final String COLUMN_MESSAGE_BODY = "message_body";

	static final String TABLE_USERS = "users";
	static final String COLUMN_JID = "jid";
	static final String COLUMN_FIRST_NAME = "first_name";
	static final String COLUMN_LAST_NAME = "last_name";
	static final String COLUMN_AVAILABILITY_STATUS = "availability_status";
	static final String COLUMN_AVAILABILITY_EXPIRATION_DATE = "availability_expiration_date";
	static final String COLUMN_PROPOSAL_DESCRIPTION = "proposal_description";
	static final String COLUMN_PROPOSAL_LOCATION = "proposal_location";
	static final String COLUMN_PROPOSAL_START_TIME = "proposal_start_time";
	static final String COLUMN_PROPOSAL_INTERESTED = "proposal_interested";
	static final String COLUMN_PROPOSAL_CONFIRMED = "proposal_confirmed";

	private static final String DATABASE_NAME = "com.hangapp.android.database";
	private static final int DATABASE_VERSION = 1;

	// Database table creation SQL statement
	private static final String CREATE_MESSAGES_TABLE_SQL_STATEMENT = "create table "
			+ TABLE_MESSAGES
			+ "("
			+ COLUMN_MESSAGE_PACKET_ID
			+ " text primary key not null, "
			+ COLUMN_MESSAGE_MUC_NAME
			+ " text not null, "
			+ COLUMN_MESSAGE_FROM
			+ " text not null, "
			+ COLUMN_MESSAGE_BODY + " text);";
	private static final String CREATE_USERS_TABLE_SQL_STATEMENT = "create table "
			+ TABLE_USERS
			+ "("
			+ COLUMN_JID
			+ " text primary key not null, "
			+ COLUMN_FIRST_NAME
			+ " text not null, "
			+ COLUMN_LAST_NAME
			+ " text not null, "
			+ COLUMN_AVAILABILITY_STATUS
			+ " text, "
			+ COLUMN_AVAILABILITY_EXPIRATION_DATE
			+ " text, "
			+ COLUMN_PROPOSAL_DESCRIPTION
			+ " text, "
			+ COLUMN_PROPOSAL_LOCATION
			+ " text, "
			+ COLUMN_PROPOSAL_START_TIME
			+ " text, "
			+ COLUMN_PROPOSAL_INTERESTED
			+ " text, "
			+ COLUMN_PROPOSAL_CONFIRMED + " text); ";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_MESSAGES_TABLE_SQL_STATEMENT);
		database.execSQL(CREATE_USERS_TABLE_SQL_STATEMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		onCreate(db);
	}

}
package com.hangapp.newandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_MESSAGES = "messages";
	public static final String COLUMN_PACKET_ID = "packet_id";
	public static final String COLUMN_MUC_NAME = "muc_name";
	public static final String COLUMN_MESSAGE_FROM = "message_from";
	public static final String COLUMN_MESSAGE_BODY = "message_body";

	private static final String DATABASE_NAME = "messages.db";
	private static final int DATABASE_VERSION = 1;

	// UserDatabase creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MESSAGES + "(" + COLUMN_PACKET_ID
			+ " text primary key not null, " + COLUMN_MUC_NAME
			+ " text not null, " + COLUMN_MESSAGE_FROM + " text not null, "
			+ COLUMN_MESSAGE_BODY + " text);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
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
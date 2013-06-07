package com.hangapp.newandroid.database;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public final class MessagesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_PACKET_ID,
			MySQLiteHelper.COLUMN_MUC_NAME, MySQLiteHelper.COLUMN_MESSAGE_FROM,
			MySQLiteHelper.COLUMN_MESSAGE_BODY };

	public MessagesDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public boolean createMessage(String mucName, Message message) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PACKET_ID, message.getPacketID());
		values.put(MySQLiteHelper.COLUMN_MUC_NAME, mucName);
		values.put(MySQLiteHelper.COLUMN_MESSAGE_FROM, message.getFrom());
		values.put(MySQLiteHelper.COLUMN_MESSAGE_BODY, message.getBody());

		boolean success = false;
		try {
			success = database.insert(MySQLiteHelper.TABLE_MESSAGES, null,
					values) != -1;
		} catch (SQLiteConstraintException e) {
			Log.e("MessagesDataSource.createMessage", e.getMessage());
		}

		return success;
	}

	public List<Message> getAllMessages(String mucName) {
		List<Message> messages = new ArrayList<Message>();

		final String WHERE_CLAUSE = MySQLiteHelper.COLUMN_MUC_NAME + " = '"
				+ mucName + "'";

		Cursor cursor = database.query(MySQLiteHelper.TABLE_MESSAGES,
				allColumns, WHERE_CLAUSE, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Message message = retrieveMessageFromCursor(cursor);
			messages.add(message);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return messages;
	}

	private Message retrieveMessageFromCursor(Cursor cursor) {
		Message message = new Message();

		message.setPacketID(cursor.getString(0));
		message.setFrom(cursor.getString(2));
		message.setBody(cursor.getString(3));

		return message;
	}
}
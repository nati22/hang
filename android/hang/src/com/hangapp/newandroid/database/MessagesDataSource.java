package com.hangapp.newandroid.database;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MessagesDataSource {

	// UserDatabase fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_PACKET_ID, MySQLiteHelper.COLUMN_MUC_NAME,
			MySQLiteHelper.COLUMN_MESSAGE_FROM, MySQLiteHelper.COLUMN_MESSAGE_BODY };

	public MessagesDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Message createMessage(String mucName, Message message) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PACKET_ID, message.getPacketID());
		values.put(MySQLiteHelper.COLUMN_MUC_NAME, mucName);
		values.put(MySQLiteHelper.COLUMN_MESSAGE_FROM, message.getFrom());
		values.put(MySQLiteHelper.COLUMN_MESSAGE_BODY, message.getBody());

		long insertId = database.insert(MySQLiteHelper.TABLE_MESSAGES, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_MESSAGES,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Message newMessage = cursorToMessage(cursor);
		cursor.close();

		return newMessage;
	}

	public List<Message> getAllMessages(String mucName) {
		List<Message> messages = new ArrayList<Message>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_MESSAGES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Message message = cursorToMessage(cursor);
			messages.add(message);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return messages;
	}

	private Message cursorToMessage(Cursor cursor) {
		Message message = new Message();

		message.setPacketID(cursor.getString(1));
		message.setFrom(cursor.getString(3));
		message.setBody(cursor.getString(4));

		return message;
	}
}
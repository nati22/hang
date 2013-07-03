package com.hangapp.android.database;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Helper <a href="http://en.wikipedia.org/wiki/Data_access_object">DAO</a> that
 * abstracts away XMPP Message SQLite usage. <br />
 * <br />
 * Basically, you should never explicitly use SQLite; you should only use this
 * interface.
 */
public final class MessagesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_MESSAGE_PACKET_ID,
			MySQLiteHelper.COLUMN_MESSAGE_MUC_NAME,
			MySQLiteHelper.COLUMN_MESSAGE_FROM,
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

	/**
	 * Saves an XMPP {@link Message} into SQLite.
	 * 
	 * @param mucName
	 * @param message
	 * @return
	 */
	public boolean createMessage(String mucName, Message message) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_MESSAGE_PACKET_ID,
				message.getPacketID());
		values.put(MySQLiteHelper.COLUMN_MESSAGE_MUC_NAME, mucName);
		values.put(MySQLiteHelper.COLUMN_MESSAGE_FROM, message.getFrom());
		values.put(MySQLiteHelper.COLUMN_MESSAGE_BODY, message.getBody());

		boolean success = false;
		try {
			success = database.insert(MySQLiteHelper.TABLE_MESSAGES, null,
					values) != -1;
		} catch (Exception e) {
			Log.e("MessagesDataSource.createMessage", e.getMessage());
		}

		return success;
	}

	/**
	 * Given an MUC name, pulls all XMPP {@link Message}s for an MUC.
	 * 
	 * @param mucName
	 * @return
	 */
	public List<Message> getAllMessages(String mucName) {
		List<Message> messages = new ArrayList<Message>();

		final String WHERE_CLAUSE = MySQLiteHelper.COLUMN_MESSAGE_MUC_NAME
				+ " = '" + mucName + "'";

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

	/**
	 * Helper method that retrieves individual {@link Message} objects.
	 * 
	 * @param cursor
	 * @return
	 */
	private Message retrieveMessageFromCursor(Cursor cursor) {
		Message message = new Message();

		message.setPacketID(cursor.getString(0));
		message.setFrom(cursor.getString(2));
		message.setBody(cursor.getString(3));

		return message;
	}
}
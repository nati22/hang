package com.hangapp.android.database;
//package com.hangapp.android.database;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//import com.hangapp.android.model.User;
//import com.hangapp.android.util.Utils;
//
//public final class UsersDataSource {
//
//	// Database fields
//	private SQLiteDatabase database;
//	private MySQLiteHelper dbHelper;
//
//	private String[] allColumns = { MySQLiteHelper.COLUMN_JID,
//			MySQLiteHelper.COLUMN_FIRST_NAME, MySQLiteHelper.COLUMN_LAST_NAME,
//			MySQLiteHelper.COLUMN_AVAILABILITY_STATUS,
//			MySQLiteHelper.COLUMN_AVAILABILITY_EXPIRATION_DATE,
//			MySQLiteHelper.COLUMN_PROPOSAL_DESCRIPTION,
//			MySQLiteHelper.COLUMN_PROPOSAL_LOCATION,
//			MySQLiteHelper.COLUMN_PROPOSAL_START_TIME,
//			MySQLiteHelper.COLUMN_PROPOSAL_INTERESTED,
//			MySQLiteHelper.COLUMN_PROPOSAL_CONFIRMED };
//
//	public UsersDataSource(Context context) {
//		dbHelper = new MySQLiteHelper(context);
//	}
//
//	public void open() throws SQLException {
//		database = dbHelper.getWritableDatabase();
//	}
//
//	public void close() {
//		dbHelper.close();
//	}
//
//	public void clearUsersTable() {
//		database.delete(MySQLiteHelper.TABLE_USERS, null, null);
//	}
//
//	public boolean saveUserInDatabase(User user) {
//		String interestedStringArray = Utils.convertStringArrayToString(user
//				.getProposal().getInterested());
//		String confirmedStringArray = Utils.convertStringArrayToString(user
//				.getProposal().getConfirmed());
//
//		ContentValues values = new ContentValues();
//		values.put(MySQLiteHelper.COLUMN_JID, user.getJid());
//		values.put(MySQLiteHelper.COLUMN_FIRST_NAME, user.getFirstName());
//		values.put(MySQLiteHelper.COLUMN_LAST_NAME, user.getLastName());
//
//		// If the user has an Availability, save it in the SQLite row.
//		if (user.getAvailability() != null
//				&& user.getAvailability().getStatus() != null
//				&& user.getAvailability().getExpirationDate() != null) {
//			values.put(MySQLiteHelper.COLUMN_AVAILABILITY_STATUS, user
//					.getAvailability().getStatus().toString());
//			values.put(MySQLiteHelper.COLUMN_AVAILABILITY_EXPIRATION_DATE, user
//					.getAvailability().getExpirationDate().toString());
//		} else {
//			values.put(MySQLiteHelper.COLUMN_AVAILABILITY_STATUS, (String) null);
//			values.put(MySQLiteHelper.COLUMN_AVAILABILITY_EXPIRATION_DATE,
//					(String) null);
//		}
//
//		// If the user has a Proposal, save it in the SQLite row.
//		if (user.getProposal() != null) {
//			values.put(MySQLiteHelper.COLUMN_PROPOSAL_DESCRIPTION, user
//					.getProposal().getDescription());
//			values.put(MySQLiteHelper.COLUMN_PROPOSAL_LOCATION, user
//					.getProposal().getLocation());
//			values.put(MySQLiteHelper.COLUMN_PROPOSAL_START_TIME, user
//					.getProposal().getStartTime().toString());
//			values.put(MySQLiteHelper.COLUMN_PROPOSAL_INTERESTED,
//					interestedStringArray);
//			values.put(MySQLiteHelper.COLUMN_PROPOSAL_CONFIRMED,
//					confirmedStringArray);
//		}
//
//		boolean success = false;
//		try {
//			success = database.insert(MySQLiteHelper.TABLE_USERS, null, values) != -1;
//		} catch (Exception e) {
//			Log.e("MessagesDataSource.createMessage", e.getMessage());
//		}
//
//		return success;
//	}
//
//	public List<User> getMyIncomingBroadcastsFromSQLite(
//			List<String> incomingBroadcastJids) {
//		List<User> incomingBroadcasts = new ArrayList<User>();
//
//		for (String incomingBroadcastJid : incomingBroadcastJids) {
//			incomingBroadcasts.add(getUser(incomingBroadcastJid));
//		}
//
//		return incomingBroadcasts;
//	}
//
//	public List<User> getMyOutgoingBroadcastsFromSQLite(
//			List<String> outgoingBroadcastJids) {
//		List<User> outgoingBroadcasts = new ArrayList<User>();
//
//		for (String outgoingBroadcastJid : outgoingBroadcastJids) {
//			outgoingBroadcasts.add(getUser(outgoingBroadcastJid));
//		}
//
//		return outgoingBroadcasts;
//	}
//
//	protected User getUser(String jid) {
//		User user = null;
//
//		final String WHERE_CLAUSE = MySQLiteHelper.COLUMN_JID + " = '" + jid
//				+ "'";
//
//		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, allColumns,
//				WHERE_CLAUSE, null, null, null, null);
//
//		cursor.moveToFirst();
//
//		// FIXME: Change this query to simply return one User row.
//		while (!cursor.isAfterLast()) {
//			user = retrieveUserFromCursor(cursor);
//			cursor.moveToNext();
//		}
//
//		// Make sure to close the cursor
//		cursor.close();
//
//		return user;
//	}
//
//	private User retrieveUserFromCursor(Cursor cursor) {
//		String jid = cursor.getString(0);
//		String firstName = cursor.getString(1);
//		String lastName = cursor.getString(2);
//
//		User user = new User(jid, firstName, lastName);
//
//		return user;
//	}
//}
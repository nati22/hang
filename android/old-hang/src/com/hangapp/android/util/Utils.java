package com.hangapp.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

/**
 * This class extends Application so that we can statically retrieve the Context
 * for this app.
 */
public final class Utils {
	public static final String BASE_URL = "http://therealhangapp.appspot.com/rest";
	public static final String USERS_URL = BASE_URL + "/users";

	/**
	 * Returns true IFF this device is connected to the internet, either through
	 * WiFi, 3G or 4G.
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		return activeNetworkInfo != null;
	}

	/**
	 * Returns the Default User's JID from {@link SharedPreferences}, or null if
	 * not set.
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultUserJID(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		return prefs.getString(Keys.JID, null);
	}

	/*
	 * public static User parseDefaultUserJSON(String userJSONString, Context
	 * context) { User user = null; Status status = null; Proposal proposal =
	 * null; List<User> outgoing = null, incoming = null;
	 * 
	 * // Parse the resulting JSON try { // Reference each of the JSON objects
	 * JSONObject userJSON = new JSONObject(userJSONString); JSONObject
	 * userDataJSON = userJSON.getJSONObject(Keys.DATA);
	 * 
	 * // Parse the JID, first name, last name int jid =
	 * userJSON.getInt(Keys.JID); if (jid != getDefaultUserJID(context)) throw
	 * new Exception("Parsed JID and existing JID don't match"); String
	 * firstName = userDataJSON.getString(Keys.FIRST_NAME); String lastName =
	 * userDataJSON.getString(Keys.LAST_NAME);
	 * 
	 * // Parse the Status, if there is one try { JSONObject statusJSON =
	 * userDataJSON.getJSONObject(Keys.STATUS); String statusText =
	 * statusJSON.getString(Keys.STATUS_TEXT); Color statusColor =
	 * Status.parseColor(statusJSON .getString(Keys.STATUS_COLOR));
	 * 
	 * // Retrieve and parse the Date from the server's String version String
	 * statusExpDateString = statusJSON .getString(Keys.STATUS_EXPIRATION_DATE);
	 * Date statusExpDate = new Date(Date.parse(statusExpDateString));
	 * 
	 * // Construct the Status object status = new Status(statusText,
	 * statusColor, statusExpDate); } catch (JSONException e) { Log.i(TAG,
	 * "Parsed user has no Status"); status = null; }
	 * 
	 * // Parse the Proposal, if there is one try { JSONObject propJSON =
	 * userDataJSON.getJSONObject(Keys.PROPOSAL); String proposalDesc =
	 * propJSON.getString(Keys.PROPOSAL_DESC); String proposalLoc =
	 * propJSON.getString(Keys.PROPOSAL_LOC); List<User> proposalIntList = null;
	 * List<User> proposalConfList = null;
	 * 
	 * // Retrieve and parse the Date from the server's String version String
	 * proposalTimeString = propJSON.getString(Keys.PROPOSAL_TIME); Date
	 * proposalTime = new Date(Date.parse(proposalTimeString));
	 * 
	 * // TODO: Try to parse the Interested and Confirmed lists from JSON //
	 * JSONArray interestedJSON = proposalJSON //
	 * .getJSONArray(Keys.PROPOSAL_INTERESTED); // JSONArray confirmedJSON =
	 * proposalJSON // .getJSONArray(Keys.PROPOSAL_CONFIRMED);
	 * 
	 * // Construct the proposal proposal = new Proposal(proposalDesc,
	 * proposalLoc, proposalTime, proposalIntList, proposalConfList); } catch
	 * (JSONException e) { Log.i(TAG, "Parsed user has no Proposal"); proposal =
	 * null; }
	 * 
	 * // Try to parse the User's Outgoing and Incoming lists only if it's the
	 * // Default user (this was already checked above) try { outgoing =
	 * parseListFromUser(userJSON, Keys.OUTGOING);
	 * defaultUser.setOutgoingBroadcasts(outgoing); } catch (JSONException e) {
	 * Log.i(TAG, "Parsed user has no Outgoing list"); outgoing = null; } //
	 * Don't forget to parse Outgoing and Incoming and throw them into // the
	 * DefaultUser try { incoming = parseListFromUser(userJSON, Keys.INCOMING);
	 * defaultUser.setIncomingBroadcasts(incoming); } catch (JSONException e) {
	 * Log.i(TAG, "Parsed user has no Incoming list"); incoming = null; }
	 * 
	 * // Finally, construct the User from the parsed user = new
	 * User(getDefaultUserJID(context), firstName, lastName, status, proposal);
	 * 
	 * } catch (Exception e) { final String errorMessage = e.getMessage() ==
	 * null ? "JSON Parsing failed" : e.getMessage(); Log.e(TAG, errorMessage);
	 * Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show(); }
	 * 
	 * return user; }
	 *//**
	 * When the DB returns a JSONArray of size 1, it doesn't return an array
	 * of JSONObjects, but rather a single JSONObject. This small helper
	 * function accounts for that.
	 * 
	 * @param userJSON
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	/*
	 * private static List<User> parseListFromUser(JSONObject userJSON, String
	 * key) throws JSONException { List<User> list = new ArrayList<User>();
	 * JSONObject listJSON = null;
	 * 
	 * // Assume the JSONArray you want to parse is actually a JSONArray try {
	 * JSONArray listJSONArray = userJSON.getJSONArray(key); for (int i = 0; i <
	 * listJSONArray.length(); i++) { listJSON = listJSONArray.getJSONObject(i);
	 * list.add(new User(listJSON.getInt(Keys.JID), listJSON
	 * .getString(Keys.FIRST_NAME), listJSON .getString(Keys.LAST_NAME), null,
	 * null)); } } // Catch the event where Jersey hands you a single JSONObject
	 * instead // of an actual JSONArray catch (JSONException e) { listJSON =
	 * userJSON.getJSONObject(key); list.add(new User(listJSON.getInt(Keys.JID),
	 * listJSON .getString(Keys.FIRST_NAME), listJSON.getString(Keys.LAST_NAME),
	 * null, null)); }
	 * 
	 * return list.size() != 0 ? list : null; }
	 */

}

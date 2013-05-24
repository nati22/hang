package com.hangapp.android.network.rest;
//package com.hangapp.android.network;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPut;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.jivesoftware.smack.sasl.SASLMechanism.Success;
//
//import android.content.Context;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.hangapp.android.model.DefaultUser;
//import com.hangapp.android.model.Proposal;
//import com.hangapp.android.model.User;
//import com.hangapp.android.util.BaseAsyncTask;
//import com.hangapp.android.util.Keys;
//import com.hangapp.android.util.Utils;
//
///**
// * This class should be a collection of useful AsyncTasks that connect to GAE.
// * 
// * Each AsyncTask should mirror exactly one REST call.
// * 
// * @author girum
// * 
// */
//public class DBTasks {
//
//	/**
//	 * GETs the User from the server, along with his Status, Proposal, Incoming
//	 * and Outgoing broadcasts.
//	 * 
//	 * @param context
//	 *           needed to retrieve Default user's JID from SharedPrefs
//	 */
//	public static class GetUserTask extends BaseAsyncTask {
//
//		public GetUserTask(Context context) {
//			super(context);
//		}
//
//		@Override
//		protected String doInBackground(Void... params) {
//			// Setup the GET request.
//			HttpClient http = new DefaultHttpClient();
//			HttpGet get = new HttpGet(Utils.USERS_URL + "/"
//					+ Utils.getDefaultUserJID(getContext()));
//			String responseString = null;
//
//			// Send the GET request
//			try {
//				responseString = EntityUtils
//						.toString(http.execute(get).getEntity());
//				Log.i(TAG, responseString);
//			} catch (Exception e) {
//				Log.e(TAG, e.getMessage());
//			}
//			return responseString;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//
//			// Parse the JSON into a User
//			User parsedUser = Utils.parseDefaultUserJSON(result, getContext());
//
//			// Error if the parsing failed.
//			if (parsedUser == null) {
//				final String errorMessage = "Error parsing user";
//				Log.e(TAG, errorMessage + ": " + result);
//				Toast.makeText(getContext(), "Error: " + errorMessage,
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//
//			// Copy the parsed User's Status and Proposal into the DefaultUser
//			defaultUser.setStatus(parsedUser.getStatus());
//			defaultUser.setProposal(parsedUser.getProposal());
//
//			Toast.makeText(getContext(), "GetUserTask: " + result,
//					Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	public static class RegisterNewUserTask extends BaseAsyncTask {
//		private User desiredUserData;
//
//		public RegisterNewUserTask(Context context, User newUser) {
//			super(context);
//			this.desiredUserData = newUser;
//		}
//
//		@Override
//		protected String doInBackground(Void... params) {
//			// Setup the PUT request.
//			HttpClient http = new DefaultHttpClient();
//			HttpPut put = new HttpPut(Utils.USERS_URL + "/"
//					+ desiredUserData.getJid());
//			String responseString = null;
//
//			// Assemble the data you need to send in the PUT request
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair(Keys.FIRST_NAME,
//					desiredUserData.getFirstName()));
//			nameValuePairs.add(new BasicNameValuePair(Keys.LAST_NAME,
//					desiredUserData.getLastName()));
//
//			// Send the PUT request.
//			try {
//				put.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//				responseString = EntityUtils
//						.toString(http.execute(put).getEntity());
//				Log.i(TAG, "Response: " + responseString);
//			} catch (Exception e) {
//				Log.e(TAG, e.getMessage());
//			}
//
//			return responseString;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//			final String successMessage = "GAE Registration: " + result;
//			Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT)
//					.show();
//			
//			// Register the user in Jabber
//			Log.d(TAG, successMessage + ", registering in Jabber");
//			new XMPP.RegisterNewUserTask(getContext(), desiredUserData.getJid()
//					.toString()).execute();
//		}
//	}
//
//	public static class AddNewOutgoingBroadcastTask extends BaseAsyncTask {
//		private Integer broadcasteeJID;
//
//		public AddNewOutgoingBroadcastTask(Context context, Integer broadcasteeJID) {
//			super(context);
//			this.broadcasteeJID = broadcasteeJID;
//		}
//
//		@Override
//		protected String doInBackground(Void... params) {
//			// Setup the PUT request.
//			HttpClient http = new DefaultHttpClient();
//			HttpPut put = new HttpPut(Utils.USERS_URL + "/"
//					+ Utils.getDefaultUserJID(getContext()) + "/broadcast");
//			String responseString = null;
//
//			// Assemble the data you need to send in the PUT request
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair(Keys.TARGET, broadcasteeJID
//					.toString()));
//
//			// Send the PUT request.
//			try {
//				put.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//				responseString = EntityUtils
//						.toString(http.execute(put).getEntity());
//				Log.i(TAG, "Response: " + responseString);
//			} catch (Exception e) {
//				Log.e(TAG, e.getMessage());
//			}
//
//			return responseString;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//
//			// Parse the JSON into a User
//			User parsedUser = Utils.parseDefaultUserJSON(result, getContext());
//
//			// Error if the parsing failed.
//			if (parsedUser == null) {
//				final String errorMessage = "Error parsing user";
//				Log.e(TAG, errorMessage + ": " + result);
//				Toast.makeText(getContext(), "Error: " + errorMessage,
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//
//			// Copy the parsed User's Status and Proposal into the DefaultUser
//			defaultUser.setStatus(parsedUser.getStatus());
//			defaultUser.setProposal(parsedUser.getProposal());
//
//			Toast.makeText(getContext(), "New outgoing broadcast: " + result,
//					Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	public static class SetUserStatusTask extends BaseAsyncTask {
//		private com.hangapp.android.model.Status status;
//
//		public SetUserStatusTask(Context context,
//				com.hangapp.android.model.Status newStatus) {
//			super(context);
//			this.status = newStatus;
//		}
//
//		@Override
//		protected String doInBackground(Void... params) {
//			// Setup the PUT request.
//			HttpClient http = new DefaultHttpClient();
//			HttpPut put = new HttpPut(Utils.USERS_URL + "/"
//					+ Utils.getDefaultUserJID(getContext()) + "/status");
//			String responseString = null;
//
//			// Assemble the data you need to send in the PUT request
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair(Keys.STATUS_TEXT, status
//					.getDescription()));
//			nameValuePairs.add(new BasicNameValuePair(Keys.STATUS_COLOR, status
//					.getColor().toString()));
//			nameValuePairs.add(new BasicNameValuePair(Keys.STATUS_EXPIRATION_DATE,
//					status.getExpirationDate().toGMTString()));
//
//			// Send the PUT request.
//			try {
//				put.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//				responseString = EntityUtils
//						.toString(http.execute(put).getEntity());
//				Log.i(TAG, "Response: " + responseString);
//			} catch (Exception e) {
//				Log.e(TAG, e.getMessage());
//			}
//
//			return responseString;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//			Toast.makeText(getContext(), "Set Status: " + result,
//					Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	public static class SetUserProposalTask extends BaseAsyncTask {
//		private Proposal prop;
//
//		public SetUserProposalTask(Context context, Proposal newProposal) {
//			super(context);
//			this.prop = newProposal;
//		}
//
//		@Override
//		protected String doInBackground(Void... params) {
//			// Setup the PUT request.
//			HttpClient http = new DefaultHttpClient();
//			HttpPut put = new HttpPut(Utils.USERS_URL + "/"
//					+ Utils.getDefaultUserJID(getContext()) + "/prop");
//			String responseString = null;
//
//			// Assemble the data you need to send in the PUT request
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair(Keys.PROPOSAL_DESC, prop
//					.getDescription()));
//			nameValuePairs.add(new BasicNameValuePair(Keys.PROPOSAL_LOC, prop
//					.getLocation()));
//			nameValuePairs.add(new BasicNameValuePair(Keys.PROPOSAL_TIME, prop
//					.getTime().toGMTString()));
//
//			// Send the PUT request.
//			try {
//				put.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//				responseString = EntityUtils
//						.toString(http.execute(put).getEntity());
//				Log.i(TAG, "Response: " + responseString);
//			} catch (Exception e) {
//				Log.e(TAG, e.getMessage());
//			}
//
//			return responseString;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//			Toast.makeText(getContext(), "Set Proposal: " + result,
//					Toast.LENGTH_SHORT).show();
//		}
//
//	}
//
//}

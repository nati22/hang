//package com.hangapp.android.network.xmpp;
//
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smackx.muc.DiscussionHistory;
//import org.jivesoftware.smackx.muc.MultiUserChat;
//
//import android.content.Context;
//
//import com.hangapp.android.model.User;
//import com.hangapp.android.network.rest.parse.BaseParseAsyncTask;
//import com.hangapp.android.util.Log;
//
//public class JoinMUCAsyncTask extends BaseParseAsyncTask<String> {
//
//	private MultiUserChat muc;
//	private Integer hostJID;
//	private User joinee;
//	private XMPPConnection xmppConnection;
//
//	public JoinMUCAsyncTask(Context context, MultiUserChat muc, User joinee,
//			Integer hostJID, XMPPConnection xmppConnection) {
//		super(context);
//		this.muc = muc;
//		this.joinee = joinee;
//		this.hostJID = hostJID;
//		this.xmppConnection = xmppConnection;
//	}
//
//	@Override
//	public String call() throws Exception {
//		// XMPPConnection sanity check
//		if (!xmppConnection.isAuthenticated()) {
//			final String errorMessage = "Will not create MUC: Not authenticated/connected";
//			Log.e(errorMessage);
//
//			// ////////////////////////////////////////////////////////////////////
//			// Question: (02/09/13 - 11:41pm) Why are we attempting to
//			// register a
//			// new user in JoinMUCTask? Would there ever be a scenario when
//			// a user
//			// is able to see (let alone join) an MUC without already being
//			// registered with Jabber?
//
//			// Attempt to register/login
//			// new XMPP.RegisterNewUserTask(getContext(), hostJID).execute();
//			new RegisterNewUserAsyncTask(context, hostJID, xmppConnection)
//					.execute();
//
//			return null;
//		}
//
//		try {
//			// Specify the amount of history to receive.
//			DiscussionHistory history = new DiscussionHistory();
//			history.setMaxStanzas(50);
//
//			Log.d("Joining muc: " + hostJID.toString());
//			muc.join(joinee.getFirstName());
//		} catch (XMPPException e) {
//			Log.e("Joining MUC failed: " + e.getMessage());
//
//			// We shouldn't need this anymore, since muc.join()
//			// automatically creates the room if it doesn't exist (and our
//			// server has all MUCs as persistent by default)
//			// // If you couldn't join the MUC, then the MUC doesn't exist.
//			// // In that case, just try and create the MUC.
//			// try {
//			// // Create the room
//			// Log.d( "Attempting to create Persistent MUC: "
//			// + hostJID);
//			// muc.create(joinee.getFirstName());
//			//
//			// // Get the the room's configuration form
//			// Form form = muc.getConfigurationForm();
//			// // Create a new form to submit based on the original form
//			// Form submitForm = form.createAnswerForm();
//			// // Add default answers to the form to submit
//			// for (Iterator<FormField> fields = form.getFields(); fields
//			// .hasNext();) {
//			// FormField field = (FormField) fields.next();
//			// if (!FormField.TYPE_HIDDEN.equals(field.getType())
//			// && field.getVariable() != null) {
//			// // Sets the default value as the answer
//			// submitForm.setDefaultAnswer(field.getVariable());
//			// }
//			// }
//			// submitForm.setAnswer("muc#roomconfig_persistentroom", true);
//			// // Send the completed form (with default values)
//			// // to the server to configure the room
//			// muc.sendConfigurationForm(submitForm);
//			//
//			// Log.d( "Created MUC: " + hostJID);
//			// } catch (XMPPException e2) {
//			// Log.e( "Creating MUC failed: " + e2.getMessage());
//			// }
//
//		}
//
//		return null;
//	}
//
//	@Override
//	protected void onSuccess(String result) {
//		// super.onPostExecute(result);
//	}
//
//}

package com.hangapp.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.hangapp.android.util.Keys;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Comparable<User>, Parcelable {
	/**
	 * A {@link User}'s JID is his Facebook ID.
	 */
	protected String jid;
	protected String firstName;
	protected String lastName;
	protected Availability availability;
	protected Proposal proposal;

	public User(String jid, String firstName, String lastName) {
		super();
		this.jid = jid;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getJid() {
		return jid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public Availability getAvailability() {
		return availability;
	}

	public Proposal getProposal() {
		return proposal;
	}

	public void setAvailability(Availability status) {
		this.availability = status;
	}

	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
	}

	public static User parseUser(String userJsonString) throws JSONException {
		User user = null;

		JSONObject userJsonObject = new JSONObject(userJsonString);

		String jid = userJsonObject.getString(Keys.JID);
		String firstName = userJsonObject.getString(Keys.FIRST_NAME);
		String lastName = userJsonObject.getString(Keys.LAST_NAME);

		user = new User(jid, firstName, lastName);

		return user;
	}

	@Override
	public String toString() {
		return "User {jid=" + jid + ", firstName=" + firstName + ", lastName="
				+ lastName + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jid == null) ? 0 : jid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (jid == null) {
			if (other.jid != null)
				return false;
		} else if (!jid.equals(other.jid))
			return false;
		return true;
	}

	@Override
	public int compareTo(User another) {
		if (this.availability == null && another.availability != null) {
			return 1;
		} else if (this.availability != null && another.availability == null) {
			return -1;
		} else if (this.availability == null && another.availability == null) {
			return 0;
		} else {
			return this.availability.compareTo(another.availability);
		}
	}

	/*
	 * Parcelable.
	 */
	public User(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(jid);
		out.writeString(firstName);
		out.writeString(lastName);
	}

	protected void readFromParcel(Parcel in) {
		jid = in.readString();
		firstName = in.readString();
		lastName = in.readString();
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
}

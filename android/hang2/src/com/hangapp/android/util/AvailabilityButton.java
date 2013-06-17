package com.hangapp.android.util;

import org.joda.time.DateTime;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import com.hangapp.android.R;
import com.hangapp.android.model.Availability;

public class AvailabilityButton extends Button {

	private int id;
	private DateTime time;
	private Availability.Status state;

	public AvailabilityButton(Context context) {
		super(context);
	}

	public AvailabilityButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AvailabilityButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setState(Availability.Status newState) {
		this.state = newState;

		if (newState == null) {
			setBackgroundResource(R.drawable.button_grey);
			return;
		}

		switch (newState) {
		case FREE:
			setBackgroundResource(R.drawable.button_green);
			return;
		case BUSY:
			setBackgroundResource(R.drawable.button_red);
			return;
		default:
			Log.e("MyAvailabilityFragment",
					"Unknown new state: " + newState.toString());
			return;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Availability.Status getState() {
		return state;
	}

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime dateTime) {
		this.time = dateTime;
		setText(dateTime.toString("h aa"));
	}
}

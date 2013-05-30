package com.hangapp.newandroid.util;

import org.joda.time.DateTime;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import com.hangapp.newandroid.R;
import com.hangapp.newandroid.model.NewAvailability;

public class AvailabilityButton extends Button {

	private DateTime dateTime;
	private NewAvailability.Status state;

	public AvailabilityButton(Context context) {
		super(context);
	}

	public AvailabilityButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AvailabilityButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setState(NewAvailability.Status newState) {
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
			Log.e("AvailabilityFragment",
					"Unknown new state: " + newState.toString());
			return;
		}
	}

	public NewAvailability.Status getState() {
		return state;
	}
}

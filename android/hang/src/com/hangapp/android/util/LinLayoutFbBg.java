package com.hangapp.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LinLayoutFbBg extends LinearLayout {

	private boolean backgroundSet = false;

	public LinLayoutFbBg(Context context) {
		super(context);
	}

	public LinLayoutFbBg(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean isBackgroundSet() {
		return backgroundSet;
	}

	public void backgroundIsSet(boolean x) {
		backgroundSet = x;
	}

}

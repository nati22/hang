package com.hangapp.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ChatMessageRelativeLayout extends RelativeLayout {

	public ChatMessageRelativeLayout(Context context) {
		super(context);

		Toast.makeText(context, "ChatMessageRelativeLayout with 1 constructor",
				Toast.LENGTH_SHORT).show();
	}

	public ChatMessageRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		Toast.makeText(context, "ChatMessageRelativeLayout with 2 constructors",
				Toast.LENGTH_SHORT).show();
	}

	public ChatMessageRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		Toast.makeText(context, "ChatMessageRelativeLayout with 3 constructors",
				Toast.LENGTH_SHORT).show();
	}

}

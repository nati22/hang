package com.hangapp.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewFbBg extends ImageView {

	private boolean backgroundSet = false;
	
	public ImageViewFbBg(Context context) {
		super(context);
	}

	public ImageViewFbBg(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public boolean isBackgroundSet() {
		return backgroundSet;
	}
	
	public void backgroundIsSet(boolean x) {
		backgroundSet = x;
	}

}

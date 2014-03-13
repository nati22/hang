package com.hangapp.android.util;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation that will resize a view
 */
public class ResizeAnimation extends Animation {
	private View mView;
	private float mToHeight;
	private float mFromHeight;

	private float mToWidth;
	private float mFromWidth;

	public ResizeAnimation(View v, float fromWidth, float fromHeight,
			float toWidth, float toHeight, int duration) {
		mToHeight = toHeight;
		mToWidth = toWidth;
		mFromHeight = fromHeight;
		mFromWidth = fromWidth;
		mView = v;
		setDuration(duration);
	}

	@Override
	protected void applyTransformation(float interpolatedTime,
			Transformation t) {
		float height = (mToHeight - mFromHeight) * interpolatedTime
				+ mFromHeight;
		float width = (mToWidth - mFromWidth) * interpolatedTime
				+ mFromWidth;
		LayoutParams p = mView.getLayoutParams();
		p.height = (int) height;
		p.width = (int) width;
		mView.requestLayout();
	}
}
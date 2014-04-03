package com.hangapp.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hangapp.android.R;

public class MyExpandableViewGroup extends RelativeLayout {

	private static final String TAG = MyExpandableViewGroup.class
			.getSimpleName();
	private ResizeAnimation anim;
	private OnClickListener onClick;
	private static boolean isExpanded = false;
	private int originalWidth;
	private int originalHeight;

	private int shrunkHeight;
	private int expandedHeight;

	private Animation animOut = AnimationUtils.loadAnimation(getContext(),
			R.anim.fade_out);
	private Animation animIn = AnimationUtils.loadAnimation(getContext(),
			R.anim.fade_in);

	private final static int DURATION_OF_ANIMATION = 500;

	public MyExpandableViewGroup(Context context) {
		super(context);
		Toast.makeText(context, "one param constructor called",
				Toast.LENGTH_SHORT).show();
		// initialize(context);
	}

	public MyExpandableViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		Toast.makeText(context, "two param constructor called",
				Toast.LENGTH_SHORT).show();
		// initialize(context);
	}

	public MyExpandableViewGroup(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		Toast.makeText(context, "three param constructor called",
				Toast.LENGTH_SHORT).show();
		// initialize(context);
	}

	@SuppressWarnings("deprecation")
	public void initialize(Context context) {

		// Set initial values.
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		expandedHeight = (int) (display.getHeight() * 0.55);

		originalWidth = display.getWidth();
		originalHeight = display.getHeight() / 7;
		Log.d(TAG, "originalWidth = " + originalWidth);
		Log.d(TAG, "originalHeight = " + originalHeight);

		// Get Views
		onClick = new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(TAG, "onClick called");
				Log.d(TAG, "isExpanded = " + isExpanded);

				if (!isExpanded) {

					MyExpandableViewGroup view = (MyExpandableViewGroup) v;

					Log.d(TAG, "from (" + originalWidth + ", " + originalHeight
							+ ")\nto (" + originalWidth + ", " + expandedHeight + ")");
					expand(view);
				} else {

					/*
					 * We don't want to do anything if the view is clicked when it's
					 * expanded
					 */
				}

			}
		};

		this.setOnClickListener(onClick);
	}

	public void setResizeAnimation(ResizeAnimation anim) {
		this.anim = anim;
	}

	public void expand(MyExpandableViewGroup view) {
		findViewById(R.id.red_box).startAnimation(animIn);
		findViewById(R.id.blue_box).startAnimation(animOut);
		findViewById(R.id.blue_box).setVisibility(INVISIBLE);

		view.setResizeAnimation(new ResizeAnimation((View) view, originalWidth,
				originalHeight, originalWidth, expandedHeight,
				DURATION_OF_ANIMATION));

		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				isExpanded = true;

			}
		});
		this.startAnimation(anim);

		Log.d(TAG, "expand() called");
	}

	public void collapse(MyExpandableViewGroup view) {
		findViewById(R.id.red_box).startAnimation(animOut);
		findViewById(R.id.red_box).setVisibility(INVISIBLE);
		findViewById(R.id.blue_box).startAnimation(animIn);

		view.setResizeAnimation(new ResizeAnimation((View) view, originalWidth,
				expandedHeight, originalWidth, originalHeight,
				DURATION_OF_ANIMATION));

		this.startAnimation(anim);

		this.isExpanded = false;
		Log.d(TAG, "collapse() called");
	}

	public boolean isExpanded() {
		return isExpanded;
	}

}

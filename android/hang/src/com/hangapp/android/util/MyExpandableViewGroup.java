package com.hangapp.android.util;

import com.hangapp.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MyExpandableViewGroup extends RelativeLayout {

	private ResizeAnimation anim;
	private OnClickListener onClick;
	private static boolean isExpanded = false;
	private int originalWidth;
	private int originalHeight;

	public MyExpandableViewGroup(Context context) {
		super(context);

		Toast.makeText(context, "one param constructor called",
				Toast.LENGTH_SHORT).show();

		// Set initial values.
		originalWidth = this.getWidth();
		originalHeight = this.getHeight();

		Toast.makeText(context, this.getChildAt(0).toString(), Toast.LENGTH_SHORT)
				.show();

		anim = new ResizeAnimation(this, this.getWidth(), this.getHeight(),
				this.getWidth(), this.getHeight(), 750);

		onClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyExpandableViewGroup view = (MyExpandableViewGroup) v;
				if (isExpanded) {
					Log.e("view", "isExpanded");
					collapse();
				} else {
					Log.e("view", "isCollapsed");
					expand();
				}

			}
		};
		this.setOnClickListener(onClick);

	}

	public MyExpandableViewGroup(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		Toast.makeText(context, "three param constructor called",
				Toast.LENGTH_SHORT).show();

		// TODO Auto-generated constructor stub
	}

	public MyExpandableViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		Toast.makeText(context, "two param constructor called",
				Toast.LENGTH_SHORT).show();

		// Set initial values.
		originalWidth = this.getWidth();
		originalHeight = this.getHeight();

		anim = new ResizeAnimation(this, this.getWidth(), this.getHeight(),
				this.getWidth(), this.getHeight(), 750);

		onClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(),
						((MyExpandableViewGroup) v).getChildCount() + "",
						Toast.LENGTH_SHORT).show();

				Animation animOut = AnimationUtils.loadAnimation(getContext(),
						R.anim.fade_out);
				Animation animIn = AnimationUtils.loadAnimation(getContext(),
						R.anim.fade_in);

				MyExpandableViewGroup view = (MyExpandableViewGroup) v;

				if (isExpanded) {
					findViewById(R.id.red_box).startAnimation(animOut);
					findViewById(R.id.red_box).setVisibility(INVISIBLE);
					findViewById(R.id.blue_box).startAnimation(animIn);
					view.setResizeAnimation(new ResizeAnimation(v, v.getWidth(), v
							.getHeight(), v.getWidth(), v.getHeight() / 2, 600));
					collapse();
				} else {
					findViewById(R.id.red_box).startAnimation(animIn);
					findViewById(R.id.blue_box).startAnimation(animOut);
					findViewById(R.id.blue_box).setVisibility(INVISIBLE);
					view.setResizeAnimation(new ResizeAnimation(v, v.getWidth(), v
							.getHeight(), v.getWidth(), v.getHeight() * 2, 600));
					expand();
				}

			}
		};
		this.setOnClickListener(onClick);

	}

	public void setResizeAnimation(ResizeAnimation anim) {
		this.anim = anim;
	}

	public void expand() {
		this.startAnimation(anim);
		this.isExpanded = true;
	}

	public void collapse() {
		this.startAnimation(anim);
		this.isExpanded = false;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

}

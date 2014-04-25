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
import com.hangapp.android.database.Database;
import com.hangapp.android.model.Proposal;
import com.hangapp.android.model.callback.MyProposalListener;

public class MyExpandableViewGroup extends RelativeLayout implements
		MyProposalListener {

	private static final String TAG = MyExpandableViewGroup.class
			.getSimpleName() + " yello";
	private ResizeAnimation anim;
	private OnClickListener onClick;
	private static boolean isExpanded = false;
	private int originalWidth;
	private int originalHeight;

	private int shrunkHeight;
	private int expandedHeight;

	private double createProposalHeightRatio = 0.75;
	private double existingProposalHeightRatio = 0.65;

	private Animation animOut = AnimationUtils.loadAnimation(getContext(),
			R.anim.fade_out);
	private Animation animIn = AnimationUtils.loadAnimation(getContext(),
			R.anim.fade_in);

	private final static int DURATION_OF_ANIMATION = 500;

	private Database database;

	public MyExpandableViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "two param constructor called");
		database = Database.getInstance();
		initialize(context, database);
	}

/*	public MyExpandableViewGroup(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		Log.d(TAG, "two param constructor called");
		Toast.makeText(context, "3 PARAM CONSTRUCTOR CALLED",
				Toast.LENGTH_SHORT).show();
		database = Database.getInstance();
		initialize(context, database);
		
	}*/

	@SuppressWarnings("deprecation")
	public void initialize(Context context, Database database) {

		// Set initial values.
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		// programmatically get the expanded height
		if (database.getMyProposal() == null) {
			Log.d(TAG,
					"Setting expanded height to be for one with NEW proposal.");
			expandedHeight = (int) (display.getHeight() * createProposalHeightRatio);
		} else {
			Log.d(TAG,
					"Setting expanded height to be for one with EXISTING proposal.");
			expandedHeight = (int) (display.getHeight() * existingProposalHeightRatio);
		}
		
//		expand(this);

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
							+ ")\nto (" + originalWidth + ", " + expandedHeight
							+ ")");
					expand(view);
				} else {

					/*
					 * We don't want to do anything if the view is clicked when
					 * it's expanded
					 */
				}
			}
		};

		this.setOnClickListener(onClick);
	}

	public void setResizeAnimation(ResizeAnimation anim) {
		this.anim = anim;
	}

	public boolean expand(MyExpandableViewGroup view) {
		/*
		 * findViewById(R.id.red_box).startAnimation(animIn);
		 * findViewById(R.id.blue_box).startAnimation(animOut);
		 * findViewById(R.id.blue_box).setVisibility(INVISIBLE);
		 */
		Log.d(TAG, "expand() called");

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

		return true;
	}

	public boolean collapse(MyExpandableViewGroup view) {
		/*
		 * findViewById(R.id.red_box).startAnimation(animOut);
		 * findViewById(R.id.red_box).setVisibility(INVISIBLE);
		 * findViewById(R.id.blue_box).startAnimation(animIn);
		 */
		Log.d(TAG, "collapse() called");

		view.setResizeAnimation(new ResizeAnimation((View) view, originalWidth,
				expandedHeight, originalWidth, originalHeight,
				DURATION_OF_ANIMATION));

		this.startAnimation(anim);

		this.isExpanded = false;
		
		return true;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	@Override
	public void onMyProposalUpdate(Proposal proposal) {
		// TODO Auto-generated method stub
		Toast.makeText(getContext(), "I should expand", Toast.LENGTH_SHORT)
				.show();
	}

}

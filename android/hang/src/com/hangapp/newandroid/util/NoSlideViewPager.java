package com.hangapp.newandroid.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoSlideViewPager extends ViewPager {

	public NoSlideViewPager(Context context) {
		super(context);	
	}
	
	public NoSlideViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
   public boolean onInterceptTouchEvent(MotionEvent arg0) {
       // Never allow swiping to switch between pages
       return false;
   }
	
	@Override
   public boolean onTouchEvent(MotionEvent event) {
       // Never allow swiping to switch between pages
       return false;
   }

}

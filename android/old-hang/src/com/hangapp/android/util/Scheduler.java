//package com.hangapp.android.util;
//
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.content.Context;
//
///**
// * Utility class that schedules refreshes of the {@link GetUserTask} to be run
// * once per Status expiration.
// * 
// * @author girum
// */
//public final class Scheduler {
//	private static Timer timer = new Timer();
//	
//	private Scheduler() {
//		// Do nothing (
//	}
//
//	public static void scheduleNewGetUserTask(Date refreshDate, Context context) {
//		Log.d("Scheduling new GetUserTask at: " + refreshDate.toString());
//		timer.schedule(new GetUserTimerTask(context), refreshDate);
//	}
//
//	private static class GetUserTimerTask extends TimerTask {
//		private Context context;
//
//		private GetUserTimerTask(Context context) {
//			this.context = context;
//		}
//
//		@Override
//		public void run() {
//		}
//	}
//}

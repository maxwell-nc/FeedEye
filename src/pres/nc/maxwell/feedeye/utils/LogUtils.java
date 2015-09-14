package pres.nc.maxwell.feedeye.utils;

import android.util.Log;

public class LogUtils {

	/**
	 * 控制日志打印等级，-1代表不打印任何日志，5代表打印所有级别的日志
	 */
	private static int mlogLevel = 5;

	public static void syso(String msg) {
		if (mlogLevel >= 0) {
			System.out.println(msg);
		}
	}
	
	public static void v(String tag, String msg) {
		if (mlogLevel >= 1) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (mlogLevel >= 2) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (mlogLevel >= 3) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (mlogLevel >= 4) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (mlogLevel >= 5) {
			Log.e(tag, msg);
		}
	}
}

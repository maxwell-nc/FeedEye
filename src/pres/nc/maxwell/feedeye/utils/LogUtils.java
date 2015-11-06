package pres.nc.maxwell.feedeye.utils;

import android.util.Log;

/**
 * 日志管理工具类
 */
public class LogUtils {

	/**
	 * 控制日志打印等级，-1代表不打印任何日志，5代表打印所有级别的日志
	 */
	private final static int mlogLevel = -1;

	/**
	 * 获取对象的类名
	 * 
	 * @param obj
	 *            要获取的对象
	 * @return 对象的类名
	 */
	private static String getClassName(Object obj) {

		String fullPathClassName = obj.getClass().getName();

		String className = fullPathClassName.substring(
				fullPathClassName.lastIndexOf(".") + 1,
				fullPathClassName.length());

		return className;
	}

	/**
	 * 调用System.out.println打印日志
	 * 
	 * @param msg
	 *            日志信息
	 */
	public static void syso(String msg) {
		if (mlogLevel >= 0) {
			System.out.println(msg);
		}
	}

	/**
	 * verbose级别日志
	 * 
	 * @param obj
	 *            调用对象，一般传入this
	 * @param msg
	 *            日志信息
	 */
	public static void v(Object obj, String msg) {
		if (mlogLevel >= 1) {
			Log.v(getClassName(obj), msg);
		}
	}

	/**
	 * debug级别日志
	 * 
	 * @param obj
	 *            调用对象，一般传入this
	 * @param msg
	 *            日志信息
	 */
	public static void d(Object obj, String msg) {
		if (mlogLevel >= 2) {
			Log.d(getClassName(obj), msg);
		}
	}

	/**
	 * info级别日志
	 * 
	 * @param obj
	 *            调用对象，一般传入this
	 * @param msg
	 *            日志信息
	 */
	public static void i(Object obj, String msg) {
		if (mlogLevel >= 3) {
			Log.i(getClassName(obj), msg);
		}
	}

	/**
	 * warning级别日志
	 * 
	 * @param obj
	 *            调用对象，一般传入this
	 * @param msg
	 *            日志信息
	 */
	public static void w(Object obj, String msg) {
		if (mlogLevel >= 4) {
			Log.w(getClassName(obj), msg);
		}
	}

	/**
	 * error级别日志
	 * 
	 * @param obj
	 *            调用对象，一般传入this
	 * @param msg
	 *            日志信息
	 */
	public static void e(Object obj, String msg) {
		if (mlogLevel >= 5) {
			Log.e(getClassName(obj), msg);
		}
	}

	/**
	 * verbose级别日志
	 * 
	 * @param tag
	 *            日志标记
	 * @param msg
	 *            日志信息
	 */
	public static void v(String tag, String msg) {
		if (mlogLevel >= 1) {
			Log.v(tag, msg);
		}
	}

	/**
	 * debug级别日志
	 * 
	 * @param tag
	 *            日志标记
	 * @param msg
	 *            日志信息
	 */
	public static void d(String tag, String msg) {
		if (mlogLevel >= 2) {
			Log.d(tag, msg);
		}
	}

	/**
	 * info级别日志
	 * 
	 * @param tag
	 *            日志标记
	 * @param msg
	 *            日志信息
	 */
	public static void i(String tag, String msg) {
		if (mlogLevel >= 3) {
			Log.i(tag, msg);
		}
	}

	/**
	 * warning级别日志
	 * 
	 * @param tag
	 *            日志标记
	 * @param msg
	 *            日志信息
	 */
	public static void w(String tag, String msg) {
		if (mlogLevel >= 4) {
			Log.w(tag, msg);
		}
	}

	/**
	 * error级别日志
	 * 
	 * @param tag
	 *            日志标记
	 * @param msg
	 *            日志信息
	 */
	public static void e(String tag, String msg) {
		if (mlogLevel >= 5) {
			Log.e(tag, msg);
		}
	}
}

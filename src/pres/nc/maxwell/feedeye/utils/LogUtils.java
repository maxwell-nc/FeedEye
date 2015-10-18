package pres.nc.maxwell.feedeye.utils;

import android.util.Log;

/**
 * ��־��������
 */
public class LogUtils {

	/**
	 * ������־��ӡ�ȼ���-1������ӡ�κ���־��5�����ӡ���м������־
	 */
	private final static int mlogLevel = 5;

	/**
	 * ��ȡ���������
	 * 
	 * @param obj
	 *            Ҫ��ȡ�Ķ���
	 * @return ���������
	 */
	private static String getClassName(Object obj) {

		String fullPathClassName = obj.getClass().getName();

		String className = fullPathClassName.substring(
				fullPathClassName.lastIndexOf(".") + 1,
				fullPathClassName.length());

		return className;
	}

	/**
	 * ����System.out.println��ӡ��־
	 * 
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void syso(String msg) {
		if (mlogLevel >= 0) {
			System.out.println(msg);
		}
	}

	/**
	 * verbose������־
	 * 
	 * @param obj
	 *            ���ö���һ�㴫��this
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void v(Object obj, String msg) {
		if (mlogLevel >= 1) {
			Log.v(getClassName(obj), msg);
		}
	}

	/**
	 * debug������־
	 * 
	 * @param obj
	 *            ���ö���һ�㴫��this
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void d(Object obj, String msg) {
		if (mlogLevel >= 2) {
			Log.d(getClassName(obj), msg);
		}
	}

	/**
	 * info������־
	 * 
	 * @param obj
	 *            ���ö���һ�㴫��this
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void i(Object obj, String msg) {
		if (mlogLevel >= 3) {
			Log.i(getClassName(obj), msg);
		}
	}

	/**
	 * warning������־
	 * 
	 * @param obj
	 *            ���ö���һ�㴫��this
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void w(Object obj, String msg) {
		if (mlogLevel >= 4) {
			Log.w(getClassName(obj), msg);
		}
	}

	/**
	 * error������־
	 * 
	 * @param obj
	 *            ���ö���һ�㴫��this
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void e(Object obj, String msg) {
		if (mlogLevel >= 5) {
			Log.e(getClassName(obj), msg);
		}
	}

	/**
	 * verbose������־
	 * 
	 * @param tag
	 *            ��־���
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void v(String tag, String msg) {
		if (mlogLevel >= 1) {
			Log.v(tag, msg);
		}
	}

	/**
	 * debug������־
	 * 
	 * @param tag
	 *            ��־���
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void d(String tag, String msg) {
		if (mlogLevel >= 2) {
			Log.d(tag, msg);
		}
	}

	/**
	 * info������־
	 * 
	 * @param tag
	 *            ��־���
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void i(String tag, String msg) {
		if (mlogLevel >= 3) {
			Log.i(tag, msg);
		}
	}

	/**
	 * warning������־
	 * 
	 * @param tag
	 *            ��־���
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void w(String tag, String msg) {
		if (mlogLevel >= 4) {
			Log.w(tag, msg);
		}
	}

	/**
	 * error������־
	 * 
	 * @param tag
	 *            ��־���
	 * @param msg
	 *            ��־��Ϣ
	 */
	public static void e(String tag, String msg) {
		if (mlogLevel >= 5) {
			Log.e(tag, msg);
		}
	}
}

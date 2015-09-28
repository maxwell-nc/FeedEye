package pres.nc.maxwell.feedeye.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 系统信息获取工具类
 */
public class SystemInfoUtils {

	/**
	 * 获取当前系统时间文本
	 * 
	 * @return 时间文本，格式如：15-09-16 22:19:49
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	/**
	 * 获得状态栏的高度，失败返回-1
	 * 
	 * @param context
	 *            上下文
	 * @return 状态栏高度
	 */
	public static int getStatusBarHeight(Context context) {
		int statusBarHeight = -1;
		int resId = context.getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resId > 0) {
			statusBarHeight = context.getResources().getDimensionPixelSize(
					resId);
		}
		return statusBarHeight;
	}

}

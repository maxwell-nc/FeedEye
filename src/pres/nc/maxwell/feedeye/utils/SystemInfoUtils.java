package pres.nc.maxwell.feedeye.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ϵͳ��Ϣ��ȡ������
 */
public class SystemInfoUtils {

	/**
	 * ��ȡ��ǰϵͳʱ���ı�
	 * 
	 * @return ʱ���ı�����ʽ�磺15-09-16 22:19:49
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	/**
	 * ���״̬���ĸ߶ȣ�ʧ�ܷ���-1
	 * 
	 * @param context
	 *            ������
	 * @return ״̬���߶�
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

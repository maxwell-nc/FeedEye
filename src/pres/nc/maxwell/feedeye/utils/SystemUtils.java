package pres.nc.maxwell.feedeye.utils;

import android.content.Context;
import android.text.ClipboardManager;
import android.widget.Toast;

/**
 * 系统信息获取工具类
 */
@SuppressWarnings("deprecation")
public class SystemUtils {

	/**
	 * 复制文本到粘贴板
	 * @param text 文本
	 */
	public static void copyTextToClipBoard(Context context,String text) {
		
		ClipboardManager clipManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipManager.setText(text);
		Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
		
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

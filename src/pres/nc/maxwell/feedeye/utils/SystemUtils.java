package pres.nc.maxwell.feedeye.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.widget.Toast;

/**
 * 系统信息获取工具类
 */
@SuppressWarnings("deprecation")
public class SystemUtils {

	/**
	 * 打开发送意图的
	 * @param activity 用于启动的Activity
	 * @param text 发送的文本
	 */
	public static void startShareIntentActivity(Activity activity, String text){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,text);
		activity.startActivity(intent);
	}
	
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

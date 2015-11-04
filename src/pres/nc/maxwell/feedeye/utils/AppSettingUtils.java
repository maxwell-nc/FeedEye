package pres.nc.maxwell.feedeye.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 应用设置管理工具类
 */
public class AppSettingUtils {

	/**
	 * 是否开启夜间模式
	 */
	public final static String KEY_DAY_NIGHT = "day_night";
	
	/**
	 * 是否开启程序时自动检查更新
	 */
	public final static String KEY_UPDATE_SETTING = "auto_update";

	/**
	 * 是否使用网络加载图片
	 */
	public final static String KEY_NO_IMAGE_SETTING = "no_img";

	/**
	 * 配置文件名
	 */
	private final static String CONFIG_FILE_NAME = "app_config";

	/**
	 * 获取设置
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            设置信息名
	 * @param defValue
	 *            默认值
	 * @return 值
	 */
	public static String get(Context context, String key, String defValue) {
		SharedPreferences preferences = context.getSharedPreferences(
				CONFIG_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getString(key, defValue);
	}

	/**
	 * 设置
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            设置信息名
	 * @param value
	 *            值
	 */
	public static void set(Context context, String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences(
				CONFIG_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

}

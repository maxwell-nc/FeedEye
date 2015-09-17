package pres.nc.maxwell.feedeye.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 系统信息获取工具类 
 */
public class SystemInfoUtils {
	
	/**
	 * 获取当前系统时间文本
	 * @return 时间文本，格式如：15-09-16 22:19:49
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime(){
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		return format.format(new Date());
	} 
	
}

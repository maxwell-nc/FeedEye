package pres.nc.maxwell.feedeye.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ϵͳ��Ϣ��ȡ������ 
 */
public class SystemInfoUtils {
	
	/**
	 * ��ȡ��ǰϵͳʱ���ı�
	 * @return ʱ���ı�����ʽ�磺15-09-16 22:19:49
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime(){
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		return format.format(new Date());
	} 
	
}

package pres.nc.maxwell.feedeye.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 处理时间转换的工具类
 */
public class TimeUtils {

	/**
	 * Timestamp转换成特定格式的字符串
	 * 
	 * @param timestamp
	 *            时间戳
	 * @param pattern
	 *            时间的格式
	 * @return 本地的时间字符串
	 */
	public static String timestamp2String(Timestamp timestamp, String pattern) {

		String retStr = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
				Locale.getDefault());
		retStr = simpleDateFormat.format(timestamp);

		return retStr;
	}

	/**
	 * Date转换成特定格式的字符串
	 * 
	 * @param date
	 *            时间
	 * @param pattern
	 *            时间的格式
	 * @return 本地的时间字符串
	 */
	public static String date2String(Date date, String pattern) {

		String retStr = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
				Locale.getDefault());
		retStr = simpleDateFormat.format(date);

		return retStr;
	}

	/**
	 * String转Timestamp
	 * 
	 * @param timeString
	 *            时间文本
	 * @return Timestamp类型
	 */
	public static Timestamp string2Timestamp(String timeString) {

		return Timestamp.valueOf(timeString);

	}

	/**
	 * GMTString转Timestamp
	 * 
	 * @param timeString
	 *            GMT时间文本
	 * @return Timestamp类型
	 */
	public static Timestamp gmt2Timestamp(String timeString) {
		
		Date newDate = null;
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
				Locale.US);//注意由于时间采用的US格式
		try {
			newDate = format.parse(timeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return string2Timestamp(date2String(newDate, "yyyy-MM-dd HH:mm:ss"));
	}
}

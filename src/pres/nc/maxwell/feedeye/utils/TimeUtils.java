package pres.nc.maxwell.feedeye.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 通用的简单时间处理工具类
 */
public class TimeUtils {

	/**
	 * 本应用使用的标准格式
	 */
	public static final String STANDARD_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 任意时间转换为本地事件
	 * 
	 * @param orgTime
	 *            原来的时间文本
	 * @param orgPattern
	 *            原来的时间文本格式
	 * @param orgLocale
	 *            原来的时间语言
	 * @param orgTimeZone
	 *            原来的时间的地区，如GMT、GMT-8:00，可以自动确定时区的格式可以传入null
	 * @param outPattern
	 *            输出的模式
	 * @return 成功转换则返回本地时间，否则返回null
	 * @throws ParseException
	 *             解析异常，模式不匹配
	 */
	public static String formatTimeToLocal(String orgTime, String orgPattern,
			Locale orgLocale, TimeZone orgTimeZone, String outPattern) {

		String outTime = null;
		try {
			// 格式化原来的时间
			SimpleDateFormat format = new SimpleDateFormat(orgPattern,
					orgLocale);
			if (orgTimeZone != null) {
				format.setTimeZone(orgTimeZone);
			}
			Date orginDate = format.parse(orgTime);

			// 转换为本地的时间
			format = new SimpleDateFormat(outPattern, Locale.getDefault());
			format.setTimeZone(TimeZone.getDefault());
			outTime = format.format(orginDate);

		} catch (ParseException e) {
			// 忽略错误，不打日志
		}
		return outTime;
	}

	
	/**
	 * 任意文本转本地时间，匹配不到则返回系统时间
	 * @param orgTime 时间文本，传入"getCurrentTime"为获取当前时间
	 * @return "yyyy-MM-dd HH:mm:ss"格式的时间
	 */
	public static String LoopToTransTime(String orgTime) {
		
		return LoopToTransTime(orgTime,STANDARD_TIME_PATTERN);
		
	}
	
	/**
	 * 任意文本转本地时间，匹配不到则返回系统时间
	 * @param orgTime 时间文本，传入"getCurrentTime"为获取当前时间
	 * @param pattern 输出的时间格式
	 * @return 时间
	 */
	public static String LoopToTransTime(String orgTime,String pattern) {

		if (orgTime == null) {
			return null;
		}

		String resultTime = null;

		if (orgTime!="getCurrentTime") {//不是取当前时间
			
			/**
			 * UTC/GMT时间
			 */

			// ATOM 1.0标准，如：2003-12-13T18:30:02Z
			resultTime = formatTimeToLocal(orgTime, "yyyy-MM-dd'T'HH:mm:ss'Z'",
					Locale.US, TimeZone.getTimeZone("GMT"), pattern);
			if (resultTime != null) {
				return resultTime;
			}

			// 自带时区的格式，如：Sat, 03 Oct 2005 12:58:04 GMT
			resultTime = formatTimeToLocal(orgTime, "EEE, dd MMM yyyy HH:mm:ss z",
					Locale.US, null, pattern);
			if (resultTime != null) {
				return resultTime;
			}

			// RSS 2.0标准，如：Mon, 06 Sep 2010 00:01:00 +0000
			resultTime = formatTimeToLocal(orgTime,
					"EEE, dd MMM yyyy HH:mm:ss ZZZZ", Locale.US,
					TimeZone.getTimeZone("GMT"), pattern);
			if (resultTime != null) {
				return resultTime;
			}

			/**
			 * 本地时间
			 */
			// 常用格式，如：2005/12/25 11:08:04
			resultTime = formatTimeToLocal(orgTime, "yyyy/MM/dd HH:mm:ss",
					Locale.US, TimeZone.getDefault(), pattern);
			if (resultTime != null) {
				return resultTime;
			}
		}

		/**
		 * 上述不存在则返回当前时间
		 */
		resultTime = formatTimeToLocal(
				new Date(System.currentTimeMillis()).toString(),
				"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US, null,
				pattern);

		return resultTime;

	}

	/**
	 * 时间戳转换成特定格式的字符串
	 * 
	 * @param timestamp
	 *            时间戳
	 * @param pattern
	 *            时间的格式
	 * @param locale
	 *            时间的语言
	 * @return 本地时间字符串
	 */
	public static String timestamp2String(Timestamp timestamp, String pattern,
			Locale locale) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
				locale);
		simpleDateFormat.setTimeZone(TimeZone.getDefault());

		return simpleDateFormat.format(timestamp);
	}

	/**
	 * String转Timestamp
	 * 
	 * @param timeString
	 *            时间文本
	 * @return Timestamp类型，失败返回null
	 */
	public static Timestamp string2Timestamp(String timeString) {

		return Timestamp.valueOf(timeString);

	}

}

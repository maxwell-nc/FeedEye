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

		newDate = string2Date(timeString, "EEE, dd MMM yyyy HH:mm:ss z",
				Locale.US);// 注意由于时间采用的US格式
		if (newDate != null) {
			// 转换为标准时间
			String standardString = date2String(newDate, "yyyy-MM-dd HH:mm:ss");
			return string2Timestamp(standardString);
		} else {
			return null;
		}

	}

	/**
	 * 文本转日期
	 * 
	 * @param timeString
	 *            文本
	 * @param pattern
	 *            时间格式
	 * @param locate
	 *            地区
	 * @return 日期对象
	 */
	public static Date string2Date(String timeString, String pattern,
			Locale locate) {

		Date newDate = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern, locate);// 注意由于时间采用的US格式
		try {
			newDate = format.parse(timeString);
		} catch (ParseException e) {

			e.printStackTrace();
			// 非GMT
			return null;
		}

		return newDate;
	}

	/**
	 * 任意时间文本转TimeStamp
	 * 
	 * @param timeString
	 *            时间文本
	 * @return TimeStamp对象
	 */
	public static Timestamp varString2Timestamp(String timeString) {

		// 先判断是否为GMT文本
		Timestamp ret = gmt2Timestamp(timeString);

		if (ret != null) {// GMT

			return ret;

		} else {// 非GMT

			Date newDate = null;
			// 情况1： 2015/9/29 20:13:00

			newDate = string2Date(timeString, "yyyy/MM/dd HH:mm:ss",
					Locale.getDefault());

			if (newDate != null) {
				String standardString = date2String(newDate,
						"yyyy-MM-dd HH:mm:ss");// 转换为标准时间
				return string2Timestamp(standardString);
			}

			// 情况2： 待添加

		}

		// 标准时间文本
		return string2Timestamp(timeString);

	}

}

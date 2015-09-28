package pres.nc.maxwell.feedeye.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ����ʱ��ת���Ĺ�����
 */
public class TimeUtils {

	/**
	 * Timestampת�����ض���ʽ���ַ���
	 * 
	 * @param timestamp
	 *            ʱ���
	 * @param pattern
	 *            ʱ��ĸ�ʽ
	 * @return ���ص�ʱ���ַ���
	 */
	public static String timestamp2String(Timestamp timestamp, String pattern) {

		String retStr = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
				Locale.getDefault());
		retStr = simpleDateFormat.format(timestamp);

		return retStr;
	}

	/**
	 * Dateת�����ض���ʽ���ַ���
	 * 
	 * @param date
	 *            ʱ��
	 * @param pattern
	 *            ʱ��ĸ�ʽ
	 * @return ���ص�ʱ���ַ���
	 */
	public static String date2String(Date date, String pattern) {

		String retStr = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
				Locale.getDefault());
		retStr = simpleDateFormat.format(date);

		return retStr;
	}

	/**
	 * StringתTimestamp
	 * 
	 * @param timeString
	 *            ʱ���ı�
	 * @return Timestamp����
	 */
	public static Timestamp string2Timestamp(String timeString) {

		return Timestamp.valueOf(timeString);

	}

	/**
	 * GMTStringתTimestamp
	 * 
	 * @param timeString
	 *            GMTʱ���ı�
	 * @return Timestamp����
	 */
	public static Timestamp gmt2Timestamp(String timeString) {
		
		Date newDate = null;
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
				Locale.US);//ע������ʱ����õ�US��ʽ
		try {
			newDate = format.parse(timeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return string2Timestamp(date2String(newDate, "yyyy-MM-dd HH:mm:ss"));
	}
}

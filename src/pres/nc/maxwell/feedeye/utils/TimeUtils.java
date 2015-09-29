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

		newDate = string2Date(timeString, "EEE, dd MMM yyyy HH:mm:ss z",
				Locale.US);// ע������ʱ����õ�US��ʽ
		if (newDate != null) {
			// ת��Ϊ��׼ʱ��
			String standardString = date2String(newDate, "yyyy-MM-dd HH:mm:ss");
			return string2Timestamp(standardString);
		} else {
			return null;
		}

	}

	/**
	 * �ı�ת����
	 * 
	 * @param timeString
	 *            �ı�
	 * @param pattern
	 *            ʱ���ʽ
	 * @param locate
	 *            ����
	 * @return ���ڶ���
	 */
	public static Date string2Date(String timeString, String pattern,
			Locale locate) {

		Date newDate = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern, locate);// ע������ʱ����õ�US��ʽ
		try {
			newDate = format.parse(timeString);
		} catch (ParseException e) {

			e.printStackTrace();
			// ��GMT
			return null;
		}

		return newDate;
	}

	/**
	 * ����ʱ���ı�תTimeStamp
	 * 
	 * @param timeString
	 *            ʱ���ı�
	 * @return TimeStamp����
	 */
	public static Timestamp varString2Timestamp(String timeString) {

		// ���ж��Ƿ�ΪGMT�ı�
		Timestamp ret = gmt2Timestamp(timeString);

		if (ret != null) {// GMT

			return ret;

		} else {// ��GMT

			Date newDate = null;
			// ���1�� 2015/9/29 20:13:00

			newDate = string2Date(timeString, "yyyy/MM/dd HH:mm:ss",
					Locale.getDefault());

			if (newDate != null) {
				String standardString = date2String(newDate,
						"yyyy-MM-dd HH:mm:ss");// ת��Ϊ��׼ʱ��
				return string2Timestamp(standardString);
			}

			// ���2�� �����

		}

		// ��׼ʱ���ı�
		return string2Timestamp(timeString);

	}

}

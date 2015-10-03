package pres.nc.maxwell.feedeye.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ͨ�õļ�ʱ�䴦������
 */
public class TimeUtils {

	/**
	 * ��Ӧ��ʹ�õı�׼��ʽ
	 */
	public static final String STANDARD_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * ����ʱ��ת��Ϊ�����¼�
	 * 
	 * @param orgTime
	 *            ԭ����ʱ���ı�
	 * @param orgPattern
	 *            ԭ����ʱ���ı���ʽ
	 * @param orgLocale
	 *            ԭ����ʱ������
	 * @param orgTimeZone
	 *            ԭ����ʱ��ĵ�������GMT��GMT-8:00�������Զ�ȷ��ʱ���ĸ�ʽ���Դ���null
	 * @param outPattern
	 *            �����ģʽ
	 * @return �ɹ�ת���򷵻ر���ʱ�䣬���򷵻�null
	 * @throws ParseException
	 *             �����쳣��ģʽ��ƥ��
	 */
	public static String formatTimeToLocal(String orgTime, String orgPattern,
			Locale orgLocale, TimeZone orgTimeZone, String outPattern) {

		String outTime = null;
		try {
			// ��ʽ��ԭ����ʱ��
			SimpleDateFormat format = new SimpleDateFormat(orgPattern,
					orgLocale);
			if (orgTimeZone != null) {
				format.setTimeZone(orgTimeZone);
			}
			Date orginDate = format.parse(orgTime);

			// ת��Ϊ���ص�ʱ��
			format = new SimpleDateFormat(outPattern, Locale.getDefault());
			format.setTimeZone(TimeZone.getDefault());
			outTime = format.format(orginDate);

		} catch (ParseException e) {
			// ���Դ��󣬲�����־
		}
		return outTime;
	}

	
	/**
	 * �����ı�ת����ʱ�䣬ƥ�䲻���򷵻�ϵͳʱ��
	 * @param orgTime ʱ���ı�������"getCurrentTime"Ϊ��ȡ��ǰʱ��
	 * @return "yyyy-MM-dd HH:mm:ss"��ʽ��ʱ��
	 */
	public static String LoopToTransTime(String orgTime) {
		
		return LoopToTransTime(orgTime,STANDARD_TIME_PATTERN);
		
	}
	
	/**
	 * �����ı�ת����ʱ�䣬ƥ�䲻���򷵻�ϵͳʱ��
	 * @param orgTime ʱ���ı�������"getCurrentTime"Ϊ��ȡ��ǰʱ��
	 * @param pattern �����ʱ���ʽ
	 * @return ʱ��
	 */
	public static String LoopToTransTime(String orgTime,String pattern) {

		if (orgTime == null) {
			return null;
		}

		String resultTime = null;

		if (orgTime!="getCurrentTime") {//����ȡ��ǰʱ��
			
			/**
			 * UTC/GMTʱ��
			 */

			// ATOM 1.0��׼���磺2003-12-13T18:30:02Z
			resultTime = formatTimeToLocal(orgTime, "yyyy-MM-dd'T'HH:mm:ss'Z'",
					Locale.US, TimeZone.getTimeZone("GMT"), pattern);
			if (resultTime != null) {
				return resultTime;
			}

			// �Դ�ʱ���ĸ�ʽ���磺Sat, 03 Oct 2005 12:58:04 GMT
			resultTime = formatTimeToLocal(orgTime, "EEE, dd MMM yyyy HH:mm:ss z",
					Locale.US, null, pattern);
			if (resultTime != null) {
				return resultTime;
			}

			// RSS 2.0��׼���磺Mon, 06 Sep 2010 00:01:00 +0000
			resultTime = formatTimeToLocal(orgTime,
					"EEE, dd MMM yyyy HH:mm:ss ZZZZ", Locale.US,
					TimeZone.getTimeZone("GMT"), pattern);
			if (resultTime != null) {
				return resultTime;
			}

			/**
			 * ����ʱ��
			 */
			// ���ø�ʽ���磺2005/12/25 11:08:04
			resultTime = formatTimeToLocal(orgTime, "yyyy/MM/dd HH:mm:ss",
					Locale.US, TimeZone.getDefault(), pattern);
			if (resultTime != null) {
				return resultTime;
			}
		}

		/**
		 * �����������򷵻ص�ǰʱ��
		 */
		resultTime = formatTimeToLocal(
				new Date(System.currentTimeMillis()).toString(),
				"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US, null,
				pattern);

		return resultTime;

	}

	/**
	 * ʱ���ת�����ض���ʽ���ַ���
	 * 
	 * @param timestamp
	 *            ʱ���
	 * @param pattern
	 *            ʱ��ĸ�ʽ
	 * @param locale
	 *            ʱ�������
	 * @return ����ʱ���ַ���
	 */
	public static String timestamp2String(Timestamp timestamp, String pattern,
			Locale locale) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
				locale);
		simpleDateFormat.setTimeZone(TimeZone.getDefault());

		return simpleDateFormat.format(timestamp);
	}

	/**
	 * StringתTimestamp
	 * 
	 * @param timeString
	 *            ʱ���ı�
	 * @return Timestamp���ͣ�ʧ�ܷ���null
	 */
	public static Timestamp string2Timestamp(String timeString) {

		return Timestamp.valueOf(timeString);

	}

}

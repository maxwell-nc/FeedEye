package pres.nc.maxwell.feedeye.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * ����ʱ��ת���Ĺ�����
 */
public class TimeUtils {

	/**
	 * Timestampת�����ض���ʽ���ַ���
	 * @param timestamp ʱ���
	 * @param pattern ʱ��ĸ�ʽ
	 * @return ���ص�ʱ���ַ���
	 */
	public static String timestamp2String(Timestamp timestamp,String pattern){

		String retStr = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,Locale.getDefault());
		retStr = simpleDateFormat.format(timestamp);
		
		return retStr;
	}
	
	/**
	 * StringתTimestamp
	 * @param timeString ʱ���ı�
	 * @return Timestamp����
	 */
	public static Timestamp string2Timestamp(String timeString){
		
		return Timestamp.valueOf(timeString);
		
	}
}

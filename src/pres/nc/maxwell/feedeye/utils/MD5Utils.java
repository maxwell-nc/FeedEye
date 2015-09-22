package pres.nc.maxwell.feedeye.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5����ժҪ����
 */
public class MD5Utils {

	/**
	 * ���32λ��MD5ժҪֵ
	 * @param content Ҫ������ı����� 
	 * @return MD5ֵ
	 */
	public static String getMD5String(String content) {
		byte[] digestBytes = null;
		try {
			digestBytes = MessageDigest.getInstance("md5").digest(
					content.getBytes());
		} catch (NoSuchAlgorithmException e) {
			//can not reach
		}
		String md5Code = new BigInteger(1, digestBytes).toString(16);
		//��ȫ����λ��
		for (int i = 0; i < 32 - md5Code.length(); i++) {
			md5Code = "0" + md5Code;
		}
		return md5Code;
	}
}

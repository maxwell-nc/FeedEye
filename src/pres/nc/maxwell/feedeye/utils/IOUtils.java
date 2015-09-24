package pres.nc.maxwell.feedeye.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ����IO������
 */
public class IOUtils {

	/**
	 * �ر�������������IO�쳣
	 * 
	 * @param inputStream
	 *            ������
	 */
	public static void closeQuietly(InputStream inputStream) {

		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				inputStream = null;
			}
		}

	}

	/**
	 * �ر������������IO�쳣
	 * 
	 * @param outputStream
	 *            �����
	 */
	public static void closeQuietly(OutputStream outputStream) {

		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				outputStream = null;
			}
		}
	}

	
	/**
	 * ������д�뵽�����
	 * @param inputStream ������
	 * @param outputStream �����
	 */
	public static void writeStream(InputStream inputStream,
			OutputStream outputStream) {
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
